package bus.ticketer.fragments;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import bus.ticketer.adapters.DialogAdapter;
import bus.ticketer.connection.ConnectionThread;
import bus.ticketer.listeners.TicketPurchaseListener;
import bus.ticketer.objects.Ticket;
import bus.ticketer.passenger.BusTicketer;
import bus.ticketer.passenger.R;
import bus.ticketer.utils.BusUtils;
import bus.ticketer.utils.FileHandler;
import bus.ticketer.utils.Method;
import bus.ticketer.utils.RESTFunction;

public class BuyTicketsFragment extends Fragment {
	private View rootView;
	private RESTFunction currentFunction;
	private SparseArray<ArrayList<Ticket>> tickets;
	private int transactionCost;
	private int[] boughtTickets = new int[3];
	private String confirmationToken = "", IPAddress = "";
	private BusTicketer app;

	@SuppressLint("HandlerLeak")
	private Handler threadConnectionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (currentFunction) {
				case BUY_CLIENT_TICKETS:
					tickets = app.getTickets();
					break;			
				case BUY_CLIENT_TICKETS_CLICK:
					handlePurchase(msg);
					break;
				case BUY_CONFIRMATION_CLIENT:
					handleBuyPayload(msg);
					break;
				default:
					break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		
		setRetainInstance(true);
		rootView = inflater.inflate(R.layout.fragment_buy_tickets,
					container, false);

		app = (BusTicketer) getActivity().getApplication();
		IPAddress = app.getIPAddress();
		getTicketInfo();
		buyTicketsHandler();

		return rootView;
	}
	
	public void refresh() {
		app = (BusTicketer) getActivity().getApplication();
		getTicketInfo();
		buyTicketsHandler();	
	}
	
	public void getTicketInfo() {
		FileHandler fHandler = new FileHandler(app.getClientFilename(), "");
		ArrayList<String> fileContents = fHandler.readFromFile();

		
		if(!app.isNetworkAvailable()) {
			SparseArray<ArrayList<Ticket>> tickets = FileHandler.getTicketCount();
			app.setTickets(tickets);
			quantityHandler();			
		}
		else {
			currentFunction = RESTFunction.BUY_CLIENT_TICKETS;
			
			ConnectionThread dataThread = new ConnectionThread(
					IPAddress+"list/" + fileContents.get(2),
					Method.GET, null, threadConnectionHandler, null,
					currentFunction, rootView, getActivity());
			dataThread.start();
		}
	}

	public void buyTicketsHandler() {

		Button t1Minus = (Button) rootView.findViewById(R.id.t1_ticket_minus);
		Button t2Minus = (Button) rootView.findViewById(R.id.t2_ticket_minus);
		Button t3Minus = (Button) rootView.findViewById(R.id.t3_ticket_minus);
		Button t1Plus = (Button) rootView.findViewById(R.id.t1_ticket_plus);
		Button t2Plus = (Button) rootView.findViewById(R.id.t2_ticket_plus);
		Button t3Plus = (Button) rootView.findViewById(R.id.t3_ticket_plus);

		Button buyTickets = (Button) rootView
				.findViewById(R.id.buy_tickets_button);

		final TextView t1Tickets = (TextView) rootView
				.findViewById(R.id.t1_ticket_quantity_buy);
		final TextView t2Tickets = (TextView) rootView
				.findViewById(R.id.t2_ticket_quantity_buy);
		final TextView t3Tickets = (TextView) rootView
				.findViewById(R.id.t3_ticket_quantity_buy);
		
		t1Tickets.setText("0");
		t2Tickets.setText("0");
		t3Tickets.setText("0");

		t1Minus.setOnClickListener(new TicketPurchaseListener("Minus", t1Tickets));
		t2Minus.setOnClickListener(new TicketPurchaseListener("Minus", t2Tickets));
		t3Minus.setOnClickListener(new TicketPurchaseListener("Minus", t3Tickets));
		t1Plus.setOnClickListener(new TicketPurchaseListener("Plus", t1Tickets));
		t2Plus.setOnClickListener(new TicketPurchaseListener("Plus", t2Tickets));
		t3Plus.setOnClickListener(new TicketPurchaseListener("Plus", t3Tickets));

		buyTickets.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				FileHandler fHandler = new FileHandler(app.getClientFilename(), "");
				ArrayList<String> fileContents = fHandler.readFromFile();
				
				final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("cid", fileContents.get(2)));
				params.add(new BasicNameValuePair("t1", t1Tickets.getText().toString()));
				params.add(new BasicNameValuePair("t2", t2Tickets.getText().toString()));
				params.add(new BasicNameValuePair("t3", t3Tickets.getText().toString()));

				ProgressDialog progDialog = ProgressDialog.show(getActivity(),
						"", "Loading, please wait!",
						true);

				progDialog.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
						
						ProgressDialog pDiag = ProgressDialog.show(getActivity(),
								"", "Loading, please wait!",
								true);

						pDiag.setOnDismissListener(new OnDismissListener() {

							@Override
							public void onDismiss(DialogInterface dialog) {
								BusUtils.purchaseProcess(getActivity());
								DialogAdapter.purchaseSuccess(boughtTickets[0], boughtTickets[1], boughtTickets[2], transactionCost, getActivity());
							}
						});
						
						int[] data = {boughtTickets[0], boughtTickets[1], boughtTickets[2], transactionCost};
						currentFunction = RESTFunction.BUY_CONFIRMATION_CLIENT;
						DialogAdapter.confirmPurchase(params, pDiag, data, getActivity(), confirmationToken, threadConnectionHandler, rootView);
					}
				});
				
				currentFunction = RESTFunction.BUY_CLIENT_TICKETS_CLICK;
				ConnectionThread dataThread = new ConnectionThread(
						IPAddress+"buy/", Method.POST,
						params, threadConnectionHandler, progDialog,
						currentFunction, rootView, getActivity());
				
				dataThread.start();
			}

		});
		
		if(app.isTimerOn() || !app.isNetworkAvailable() || app.isWaitingValidation()) {
			t1Minus.setEnabled(false);
			t2Minus.setEnabled(false);
			t3Minus.setEnabled(false);
			t1Plus.setEnabled(false);
			t2Plus.setEnabled(false);
			t3Plus.setEnabled(false);
			buyTickets.setEnabled(false);
		}
		else {
			t1Minus.setEnabled(true);
			t2Minus.setEnabled(true);
			t3Minus.setEnabled(true);
			t1Plus.setEnabled(true);
			t2Plus.setEnabled(true);
			t3Plus.setEnabled(true);
			buyTickets.setEnabled(true);			
		}
	}
	
	private void handleBuyPayload(Message msg) {
        JSONObject ticketListing = (JSONObject) msg.obj;
        try {
        	for(int i = 1; i <= 3; i++) {
        		JSONArray ticketsArray = ticketListing.getJSONArray("t"+i);
        		for(int t = 0; t < ticketsArray.length(); t++)
        			tickets.get(i).add(new Ticket(ticketsArray.getInt(t)));
        		
        		boughtTickets[i-1] = ticketsArray.length();
        	}
        } catch (JSONException e) {
            e.printStackTrace();
        }

        app.setTickets(tickets);
	}
	
	private void handlePurchase(Message msg) {
        JSONObject ticketListing = (JSONObject) msg.obj;
        try {
        	for(int i = 0; i < 3; i++)
        		boughtTickets[i] = ticketListing.getInt("t"+(i+1));
        	
            transactionCost = ticketListing.getInt("cost");
            confirmationToken = ticketListing.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}
	
	private void quantityHandler() {
		TextView t1TicketsQuantity = (TextView) rootView
				.findViewById(R.id.t1_ticket_quantity);
		TextView t2TicketsQuantity = (TextView) rootView
				.findViewById(R.id.t2_ticket_quantity);
		TextView t3TicketsQuantity = (TextView) rootView
				.findViewById(R.id.t3_ticket_quantity);

		SparseArray<ArrayList<Ticket>> tickets = app.getTickets();
		
		t1TicketsQuantity.setText(tickets.get(1).size() + "");
		t2TicketsQuantity.setText(tickets.get(2).size() + "");
		t3TicketsQuantity.setText(tickets.get(3).size() + "");
	}	
}