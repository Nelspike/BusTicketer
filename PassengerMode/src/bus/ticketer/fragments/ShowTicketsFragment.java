package bus.ticketer.fragments;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import bus.ticketer.connection.ConnectionThread;
import bus.ticketer.objects.Ticket;
import bus.ticketer.passenger.BusTicketer;
import bus.ticketer.passenger.R;
import bus.ticketer.runnable.ShowRunnable;
import bus.ticketer.utils.FileHandler;
import bus.ticketer.utils.Method;
import bus.ticketer.utils.RESTFunction;

public class ShowTicketsFragment extends Fragment {

	private View rootView;
	private RESTFunction currentFunction;
	private String IPAddress = "";
	private BusTicketer app;

	@SuppressLint("HandlerLeak")
	private Handler threadConnectionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_show_tickets, container, false);
		app = (BusTicketer) getActivity().getApplication();
		IPAddress = app.getIPAddress();
		getTicketInfo();
		
		return rootView;
	}
	
	public void refresh() {
		getTicketInfo();
	}
		
	private void getTicketInfo() {
		FileHandler fHandler = new FileHandler(app.getClientFilename(), "");
		ArrayList<String> fileContents = fHandler.readFromFile();

		if(!app.isNetworkAvailable()) {
			SparseArray<ArrayList<Ticket>> tickets = FileHandler.getTicketCount();
			app.setTickets(tickets);
			//TODO: Check form files what is validated
			rootView.post(new ShowRunnable(app, getActivity(), rootView));
		}
		else {
			currentFunction = RESTFunction.GET_CLIENT_TICKETS;
			
			ConnectionThread dataThread = new ConnectionThread(
					IPAddress+"list/" + fileContents.get(2),
					Method.GET, null, threadConnectionHandler, null,
					currentFunction, rootView, getActivity());
			dataThread.start();
		}
	}
}
