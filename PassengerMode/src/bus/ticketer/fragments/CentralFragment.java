package bus.ticketer.fragments;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import bus.ticketer.connection.ConnectionThread;
import bus.ticketer.passenger.R;
import bus.ticketer.utils.FileHandler;
import bus.ticketer.utils.Method;
import bus.ticketer.utils.RESTFunction;

public class CentralFragment extends Fragment {
	public static final String ARG_OBJECT = "object";
	public static final String SPARSE = "sparse";
	private View rootView;
	private RESTFunction currentFunction;
	private ArrayList<Integer> tickets = new ArrayList<Integer>();

	@SuppressLint("HandlerLeak")
	private Handler threadConnectionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			System.out.println(msg.obj.toString());
			switch (currentFunction) {
			case GET_CLIENT_TICKETS:
				handleGetTickets(msg);
				break;
			case BUY_CLIENT_TICKETS:
				//handlePurchase(msg);
				break;
			default:
				break;
			}
		}
	};

	@SuppressLint("UseSparseArrays")
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			Bundle savedInstanceState) {

		/*ProgressDialog progDialog = ProgressDialog.show(getActivity(),
				"", "Loading, please wait!",
				false);

		progDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {*/
				handleFragments(inflater, container);
		/*	}
		});
		
		getTicketInfo(progDialog);*/
		return rootView;
	}

	private void handleFragments(LayoutInflater inflater, ViewGroup container) {
		
		Bundle args = getArguments();
		tickets = args.getIntegerArrayList(SPARSE);
		
		if (args.getInt(ARG_OBJECT) == 1) {
			rootView = inflater.inflate(R.layout.fragment_show_tickets,
					container, false);
			showTicketsHandler();

		} else if (args.getInt(ARG_OBJECT) == 2) {
			rootView = inflater.inflate(R.layout.fragment_buy_tickets,
					container, false);

			buyTicketsHandler();
		} else {
			rootView = inflater.inflate(R.layout.fragment_history_tickets,
					container, false);
		}
	}
	
	public void showTicketsHandler() {
		RadioGroup radioGroup = (RadioGroup) rootView
				.findViewById(R.id.ticket_radio);
		radioGroup.check(R.id.t1_radio);
		final TextView ticketsText = (TextView) rootView
				.findViewById(R.id.show_ticket_amount);

		ticketsText.setText(tickets.get(0) + " tickets");
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.t1_radio:
					ticketsText.setText(tickets.get(0) + " tickets");
					break;
				case R.id.t2_radio:
					ticketsText.setText(tickets.get(1) + " tickets");
					break;
				case R.id.t3_radio:
					ticketsText.setText(tickets.get(2) + " tickets");
					break;
				default:
					break;
				}

			}
		});
	}

	public void buyTicketsHandler() {

		TextView t1TicketsQuantity = (TextView) rootView
				.findViewById(R.id.t1_ticket_quantity);
		TextView t2TicketsQuantity = (TextView) rootView
				.findViewById(R.id.t2_ticket_quantity);
		TextView t3TicketsQuantity = (TextView) rootView
				.findViewById(R.id.t3_ticket_quantity);

		t1TicketsQuantity.setText(tickets.get(0) + "");
		t2TicketsQuantity.setText(tickets.get(1) + "");
		t3TicketsQuantity.setText(tickets.get(2) + "");

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

		t1Minus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int current = Integer.parseInt(t1Tickets.getText().toString());

				if (current == 0)
					return;

				current--;
				t1Tickets.setText(current + "");
			}

		});

		t2Minus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int current = Integer.parseInt(t2Tickets.getText().toString());

				if (current == 0)
					return;

				current--;
				t2Tickets.setText(current + "");
			}

		});

		t3Minus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int current = Integer.parseInt(t3Tickets.getText().toString());

				if (current == 0)
					return;

				current--;
				t3Tickets.setText(current + "");
			}

		});

		t1Plus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int current = Integer.parseInt(t1Tickets.getText().toString());
				current++;
				t1Tickets.setText(current + "");
			}

		});

		t2Plus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int current = Integer.parseInt(t2Tickets.getText().toString());
				current++;
				t2Tickets.setText(current + "");
			}

		});

		t3Plus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int current = Integer.parseInt(t3Tickets.getText().toString());
				current++;
				t3Tickets.setText(current + "");
			}

		});

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
						getActivity().finish();
					}
				});
				
				currentFunction = RESTFunction.BUY_CLIENT_TICKETS;
				ConnectionThread dataThread = new ConnectionThread(
						"http://192.168.0.136:81/buy/", Method.POST,
						params, threadConnectionHandler, progDialog);
				dataThread.start();
			}

		});
	}
	
	public void getTicketInfo(ProgressDialog progDialog) {
		FileHandler fHandler = new FileHandler("client.txt", "");
		ArrayList<String> fileContents = fHandler.readFromFile();

		currentFunction = RESTFunction.GET_CLIENT_TICKETS;
		ConnectionThread dataThread = new ConnectionThread("http://192.168.0.136:81/list/"+fileContents.get(2), Method.GET, null, threadConnectionHandler, progDialog);
		dataThread.start();
	}

	private void handleGetTickets(Message msg) {
		JSONObject ticketListing = (JSONObject) msg.obj;
		try {
			tickets.add(ticketListing.getInt("t1"));
			tickets.add(ticketListing.getInt("t2"));
			tickets.add(ticketListing.getInt("t3"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/*private void handlePurchase(Message msg) {
		JSONObject ticketListing = (JSONObject) msg.obj;
		
		try {
			t1Bought = ticketListing.getInt("t1");
			t2Bought = ticketListing.getInt("t2");
			t3Bought = ticketListing.getInt("t3");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}*/

}