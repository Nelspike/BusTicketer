package bus.ticketer.fragments;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import bus.ticketer.connection.ConnectionThread;
import bus.ticketer.listeners.TicketPurchaseListener;
import bus.ticketer.objects.Ticket;
import bus.ticketer.passenger.BusTicketer;
import bus.ticketer.passenger.R;
import bus.ticketer.utils.FileHandler;
import bus.ticketer.utils.Method;
import bus.ticketer.utils.PDFWriter;
import bus.ticketer.utils.RESTFunction;

public class BuyTicketsFragment extends Fragment {
	public static final String ARG_OBJECT = "object";
	private View rootView;
	private RESTFunction currentFunction;
	private SparseArray<ArrayList<Ticket>> tickets;
	private int t1Bought, t2Bought, t3Bought;
	

	@SuppressLint("HandlerLeak")
	private Handler threadConnectionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (currentFunction) {
				case BUY_CLIENT_TICKETS:
					tickets = ((BusTicketer) getActivity().getApplicationContext()).getTickets();
					break;			
				case BUY_CLIENT_TICKETS_CLICK:
					handlePurchase(msg);
					break;
				default:
					break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_buy_tickets,
					container, false);

		getTicketInfo();
		buyTicketsHandler();

		return rootView;
	}
	
	public void refresh() {
		getTicketInfo();
		buyTicketsHandler();	
	}
	
	public void getTicketInfo() {
		FileHandler fHandler = new FileHandler("client.txt", "");
		ArrayList<String> fileContents = fHandler.readFromFile();

		currentFunction = RESTFunction.BUY_CLIENT_TICKETS;
		
		ConnectionThread dataThread = new ConnectionThread(
				"http://192.168.0.136:81/list/" + fileContents.get(2),
				Method.GET, null, threadConnectionHandler, null,
				currentFunction, rootView, getActivity());
		dataThread.start();
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

				FileHandler fHandler = new FileHandler("client.txt", "");
				ArrayList<String> fileContents = fHandler.readFromFile();
				
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("cid",
						fileContents.get(2)));
				params.add(new BasicNameValuePair("t1", t1Tickets.getText().toString()));
				params.add(new BasicNameValuePair("t2", t2Tickets.getText().toString()));
				params.add(new BasicNameValuePair("t3", t3Tickets.getText().toString()));

				ProgressDialog progDialog = ProgressDialog.show(getActivity(),
						"", "Loading, please wait!",
						true);

				progDialog.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
						purchaseProcess();
						purchaseSuccess();
					}
				});
				
				currentFunction = RESTFunction.BUY_CLIENT_TICKETS_CLICK;
				ConnectionThread dataThread = new ConnectionThread(
						"http://192.168.0.136:81/buy/", Method.POST,
						params, threadConnectionHandler, progDialog,
						currentFunction, rootView, getActivity());
				dataThread.start();
			}

		});
		
		if(((BusTicketer) getActivity().getApplication()).isTimerOn()) {
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
	
	private void purchaseSuccess() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

		alertDialogBuilder.setTitle("Purchase succeeded!");

		alertDialogBuilder
		.setMessage("You now have: " + t1Bought + " T1 Tickets, " + t2Bought + " T2 Tickets and " + t3Bought + " T3 Tickets")
		.setCancelable(false)
		.setPositiveButton("OK",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
		        refresh();
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();		
	}

	
	private void handlePurchase(Message msg) {
        JSONObject ticketListing = (JSONObject) msg.obj;
        
        try {
            t1Bought = ticketListing.getInt("t1");
            t2Bought = ticketListing.getInt("t2");
            t3Bought = ticketListing.getInt("t3");
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}
	
	private void purchaseProcess() {
		FileHandler fHandler = new FileHandler("client.txt", "");
		ArrayList<String> fileContents = fHandler.readFromFile();
		int currentT1 = tickets.get(1).size();
		int currentT2 = tickets.get(2).size();
		int currentT3 = tickets.get(3).size();

		for(int i = currentT1; i < t1Bought; i++)
			new PDFWriter("t1-ticket"+i+".pdf", "T1", fileContents.get(0), null, false).createFile();
		
		for(int i = currentT2; i < t2Bought; i++)
			new PDFWriter("t2-ticket"+i+".pdf", "T2", fileContents.get(0), null, false).createFile();
		
		for(int i = currentT3; i < t3Bought; i++)
			new PDFWriter("t3-ticket"+i+".pdf", "T3", fileContents.get(0), null, false).createFile();
	}
}