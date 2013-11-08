package bus.ticketer.fragments;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import bus.ticketer.connection.ConnectionThread;
import bus.ticketer.objects.Ticket;
import bus.ticketer.passenger.BusTicketer;
import bus.ticketer.passenger.R;
import bus.ticketer.runnable.ShowRunnable;
import bus.ticketer.utils.FileHandler;
import bus.ticketer.utils.Method;
import bus.ticketer.utils.RESTFunction;

public class ShowTicketsFragment extends Fragment implements CreateNdefMessageCallback, OnNdefPushCompleteCallback {

	private NfcAdapter myNFC;
	private View rootView;
	private RESTFunction currentFunction;
	private String IPAddress = "";
	private BusTicketer app;
	private final int MESSAGE_SENT = 1;

	@SuppressLint("HandlerLeak")
	private Handler threadConnectionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
		}
	};
	
	@SuppressLint("HandlerLeak")
	private Handler nfcHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case MESSAGE_SENT:
					break;
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setRetainInstance(true);
		rootView = inflater.inflate(R.layout.fragment_show_tickets, container, false);
		app = (BusTicketer) getActivity().getApplication();
		IPAddress = app.getIPAddress();
		
		myNFC = NfcAdapter.getDefaultAdapter(getActivity());
		if(myNFC == null) {
			Toast.makeText(getActivity(), "You have no NFC, please try with another device that has NFC!", Toast.LENGTH_SHORT).show();
		}		
		
		myNFC.setNdefPushMessageCallback(this, getActivity());
		myNFC.setOnNdefPushCompleteCallback(this, getActivity());
		
		getTicketInfo();
		
		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getActivity().getIntent().getAction()))
			processIntent(getActivity().getIntent());
	}
	
	private void processIntent(Intent intent) {

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

	@Override
	public void onNdefPushComplete(NfcEvent event) {
		nfcHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		String test = "";
		if(app.getTicketID() != -1) {
			test = "ID:"+app.getTicketID();
		}
		else return null;
		
		NdefMessage msg = new NdefMessage(new NdefRecord[] {
			NdefRecord.createMime("application/bus.ticketer.message", test.getBytes())
		});
		return msg;
	}
}
