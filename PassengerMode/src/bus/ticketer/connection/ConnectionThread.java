package bus.ticketer.connection;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import bus.ticketer.listeners.RadioGroupListener;
import bus.ticketer.listeners.ValidationListener;
import bus.ticketer.objects.Ticket;
import bus.ticketer.passenger.BusTicketer;
import bus.ticketer.passenger.R;
import bus.ticketer.utils.Method;
import bus.ticketer.utils.RESTFunction;

public class ConnectionThread extends Thread {

	private ConnectionRunnable runConnection;
	private Handler mHandler;
	private ProgressDialog progDialog;
	private RESTFunction currentFunction;
	private View view;
	private Context context;

	public ConnectionThread(String link, Method method,
			ArrayList<NameValuePair> payload, Handler handler,
			ProgressDialog progDialog, RESTFunction function,
			View view, Context context) {
		runConnection = new ConnectionRunnable(link, method.toString(), payload);
		mHandler = handler;
		this.progDialog = progDialog;
		this.currentFunction = function;
		this.view = view;
		this.context = context;
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
	
	private void threadMsg() {
		Message msgObj = mHandler.obtainMessage();
		msgObj.obj = getJSON();
		mHandler.sendMessage(msgObj);
	}
	
	private void fillList() {
        JSONObject ticketListing = getJSON();
        SparseArray<ArrayList<Ticket>> tickets = ((BusTicketer) context.getApplicationContext()).getTickets();
        
        try {
        	
        	if(tickets.size() == 0) {
        		tickets.put(1, new ArrayList<Ticket>());
        		tickets.put(2, new ArrayList<Ticket>());
        		tickets.put(3, new ArrayList<Ticket>());
        	}
        	
            int t1TicketsQuantity = ticketListing.getInt("t1");
            int t2TicketsQuantity = ticketListing.getInt("t2");
            int t3TicketsQuantity = ticketListing.getInt("t3");
            
            ArrayList<Ticket> t1Tickets = tickets.get(1);
            ArrayList<Ticket> t2Tickets = tickets.get(2);
            ArrayList<Ticket> t3Tickets = tickets.get(3);
            
            for(int i = tickets.get(1).size(); i < t1TicketsQuantity; i++)
            	t1Tickets.add(new Ticket());
            
            for(int i = tickets.get(2).size(); i < t2TicketsQuantity; i++)
            	t2Tickets.add(new Ticket());
            
            for(int i = tickets.get(3).size(); i < t3TicketsQuantity; i++)
            	t3Tickets.add(new Ticket());
            
            ((BusTicketer) context.getApplicationContext()).setTickets(tickets);
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
			default:
				break;
		}
	}

	private void showTicketsHandler() {
		final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.ticket_radio);
		final TextView ticketsText = (TextView) view.findViewById(R.id.show_ticket_amount);
		final TextView timerText = (TextView) view.findViewById(R.id.ticket_timer);
		final SparseArray<ArrayList<Ticket>> tickets = ((BusTicketer) context.getApplicationContext()).getTickets();
		final Button validationButton = (Button) view.findViewById(R.id.ticket_validate);
		
		radioGroup.check(R.id.t1_radio);
		radioGroup.setOnCheckedChangeListener(new RadioGroupListener(context.getApplicationContext(),ticketsText));
		timerText.setText("No ticket Validated");	
		ticketsText.setText(tickets.get(1).size() + " tickets");
		validationButton.setOnClickListener(new ValidationListener(radioGroup, context.getApplicationContext(), timerText));
	}
	
	private void buyTicketsHandler() {
		TextView t1TicketsQuantity = (TextView) view
				.findViewById(R.id.t1_ticket_quantity);
		TextView t2TicketsQuantity = (TextView) view
				.findViewById(R.id.t2_ticket_quantity);
		TextView t3TicketsQuantity = (TextView) view
				.findViewById(R.id.t3_ticket_quantity);

		SparseArray<ArrayList<Ticket>> tickets = ((BusTicketer) context.getApplicationContext()).getTickets();
		
		t1TicketsQuantity.setText(tickets.get(1).size() + "");
		t2TicketsQuantity.setText(tickets.get(2).size() + "");
		t3TicketsQuantity.setText(tickets.get(3).size() + "");
	}

}
