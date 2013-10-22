package bus.ticketer.connection;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import bus.ticketer.passenger.R;
import bus.ticketer.utils.Method;
import bus.ticketer.utils.RESTFunction;

public class ConnectionThread extends Thread {

	private ConnectionRunnable runConnection;
	private Handler mHandler;
	private ProgressDialog progDialog;
	private RESTFunction currentFunction;
	private View view;
	private ArrayList<Integer> tickets = new ArrayList<Integer>();

	public ConnectionThread(String link, Method method,
			ArrayList<NameValuePair> payload, Handler handler,
			ProgressDialog progDialog, RESTFunction function,
			View view) {
		runConnection = new ConnectionRunnable(link, method.toString(), payload);
		mHandler = handler;
		this.progDialog = progDialog;
		this.currentFunction = function;
		this.view = view;
	}

	@Override
	public void run() {				
		runConnection.run();
		threadMsg();
		fillList();
		handleView();

		if (progDialog != null)
			progDialog.dismiss();
	}

	public JSONObject getJSON() {
		return runConnection.getResultObject();
	}
	
	private void fillList() {
        JSONObject ticketListing = getJSON();
        try {
                tickets.add(ticketListing.getInt("t1"));
                tickets.add(ticketListing.getInt("t2"));
                tickets.add(ticketListing.getInt("t3"));
        } catch (JSONException e) {
                e.printStackTrace();
        }
	}
	
	private void handleView() {
		switch(currentFunction) {
			case GET_CLIENT_TICKETS:
				view.post(new Runnable() {
					@Override
					public void run() {
						showTicketsHandler();
					}
				});
				break;
			case BUY_CLIENT_TICKETS:
				view.post(new Runnable() {
					@Override
					public void run() {
						buyTicketsHandler();
					}
				});				
				break;
			case BUY_CLIENT_TICKETS_CLICK:
				break;
			default:
				break;
		}
	}

	private void showTicketsHandler() {
		RadioGroup radioGroup = (RadioGroup) view
				.findViewById(R.id.ticket_radio);
		radioGroup.check(R.id.t1_radio);
		final TextView ticketsText = (TextView) view
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
	
	private void buyTicketsHandler() {
		TextView t1TicketsQuantity = (TextView) view
				.findViewById(R.id.t1_ticket_quantity);
		TextView t2TicketsQuantity = (TextView) view
				.findViewById(R.id.t2_ticket_quantity);
		TextView t3TicketsQuantity = (TextView) view
				.findViewById(R.id.t3_ticket_quantity);

		t1TicketsQuantity.setText(tickets.get(0) + "");
		t2TicketsQuantity.setText(tickets.get(1) + "");
		t3TicketsQuantity.setText(tickets.get(2) + "");
	}
	
	private void threadMsg() {
		Message msgObj = mHandler.obtainMessage();
		msgObj.obj = getJSON();
		mHandler.sendMessage(msgObj);
	}

}
