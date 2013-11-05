package bus.ticketer.connection;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import bus.ticketer.adapters.DialogAdapter;
import bus.ticketer.objects.Ticket;
import bus.ticketer.passenger.BusTicketer;
import bus.ticketer.runnable.BuyRunnable;
import bus.ticketer.runnable.ShowRunnable;
import bus.ticketer.utils.Method;
import bus.ticketer.utils.RESTFunction;

public class ConnectionThread extends Thread {

	private ConnectionRunnable runConnection;
	private Handler mHandler;
	private ProgressDialog progDialog;
	private RESTFunction currentFunction;
	private View view;
	private Activity context;
	private BusTicketer app;
	
	public ConnectionThread(String link, Method method,
			ArrayList<NameValuePair> payload, Handler handler,
			ProgressDialog progDialog, RESTFunction function,
			View view, Activity context) {
		runConnection = new ConnectionRunnable(link, method.toString(), payload);
		mHandler = handler;
		this.progDialog = progDialog;
		this.currentFunction = function;
		this.view = view;
		this.context = context;
		this.app = ((BusTicketer) context.getApplicationContext());
	}

	@Override
	public void run() {
		Looper.prepare();
		runConnection.run();
		threadMsg();
		
		if(!currentFunction.toString().equals(RESTFunction.BUY_CLIENT_TICKETS_CLICK.toString()) 
				&& !currentFunction.toString().equals(RESTFunction.BUY_CONFIRMATION_CLIENT.toString()))
			fillList();
		
		handleView();

		if (progDialog != null)
			progDialog.dismiss();
		
		Looper.loop();
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
        
        if(ticketListing == null) 
        	DialogAdapter.connectionIssues(context);
        else {
	        SparseArray<ArrayList<Ticket>> tickets = app.getTickets();
	     
	        try {
	        	if(tickets.size() == 0) {
	        		tickets.put(1, new ArrayList<Ticket>());
	        		tickets.put(2, new ArrayList<Ticket>());
	        		tickets.put(3, new ArrayList<Ticket>());
	        	}
	        	
	        	for(int i = 1; i <= 3; i++) {
	        		JSONArray typeTickets = ticketListing.getJSONArray("t"+i);
	        		ArrayList<Ticket> currentTickets = tickets.get(i);
	        		for(int j = 0; j < typeTickets.length(); j++) {
	        			Ticket t = new Ticket(typeTickets.getInt(j));
	        			if(!currentTickets.contains(t))
	        				currentTickets.add(t);
	        		}
	        	}
	        	        	
	            app.setTickets(tickets);
	        } catch (JSONException e) {
                e.printStackTrace();
	        }
        }
	}
	
	private void handleView() {
		switch(currentFunction) {
			case GET_CLIENT_TICKETS:
				view.post(new ShowRunnable(app, context, view));
				break;
			case BUY_CLIENT_TICKETS:
				view.post(new BuyRunnable(app, view));				
				break;
			default:
				break;
		}
	}

}
