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
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import bus.ticketer.connection.ConnectionThread;
import bus.ticketer.listeners.RadioGroupListener;
import bus.ticketer.listeners.ValidationListener;
import bus.ticketer.objects.Ticket;
import bus.ticketer.passenger.BusTicketer;
import bus.ticketer.passenger.R;
import bus.ticketer.utils.FileHandler;
import bus.ticketer.utils.Method;
import bus.ticketer.utils.RESTFunction;

public class ShowTicketsFragment extends Fragment {

	private View rootView;
	private RESTFunction currentFunction;
	private String IPAddress = "";

	@SuppressLint("HandlerLeak")
	private Handler threadConnectionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_show_tickets, container, false);
		IPAddress = ((BusTicketer) getActivity().getApplication()).getIPAddress();
		getTicketInfo();
		
		return rootView;
	}
	
	public void refresh() {
		getTicketInfo();
	}
	
	private void getTicketInfo() {
		FileHandler fHandler = new FileHandler(((BusTicketer) getActivity().getApplication()).getClientFilename(), "");
		ArrayList<String> fileContents = fHandler.readFromFile();

		if(!((BusTicketer) getActivity().getApplication()).isNetworkAvailable()) {
			SparseArray<ArrayList<Ticket>> tickets = FileHandler.getTicketCount();
			((BusTicketer) getActivity().getApplicationContext()).setTickets(tickets);
			showTicketsHandler();
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

	private void showTicketsHandler() {
		RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.ticket_radio);
		TextView ticketsText = (TextView) rootView.findViewById(R.id.show_ticket_amount);
		TextView timerText = (TextView) rootView.findViewById(R.id.ticket_timer);
		SparseArray<ArrayList<Ticket>> tickets = ((BusTicketer) getActivity().getApplicationContext()).getTickets();
		Button validationButton = (Button) rootView.findViewById(R.id.ticket_validate);
		
		radioGroup.check(R.id.t1_radio);
		radioGroup.setOnCheckedChangeListener(new RadioGroupListener(getActivity().getApplicationContext(),ticketsText));
		timerText.setText("No ticket Validated");	
		ticketsText.setText(tickets.get(1).size() + " tickets");
		validationButton.setOnClickListener(new ValidationListener(radioGroup, getActivity().getApplicationContext(), timerText));
	}
}
