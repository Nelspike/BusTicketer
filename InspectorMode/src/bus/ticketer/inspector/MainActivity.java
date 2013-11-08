package bus.ticketer.inspector;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bus.ticketer.connection.ConnectionThread;
import bus.ticketer.utils.Method;
import bus.ticketer.utils.RESTFunction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.*;
import android.widget.Toast;

public class MainActivity extends Activity {
	public NfcAdapter mNfcAdapter;
	public String IPAddress;
	private RESTFunction currentFunction;
	private BusInspector app;
	private Context context;
	
	@SuppressLint("HandlerLeak")
	private Handler threadConnectionHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(currentFunction) {
				case GET_VALIDATED_TICKETS:
					JSONObject object = (JSONObject) msg.obj;
					JSONArray list = null;
					try {
						list = object.getJSONArray("list");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					parseArray(list);
					Intent intent = new Intent(context, ScanActivity.class);
					context.startActivity(intent);
					((Activity) context).finish();
					break;
				default:
					break;
			}
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (BusInspector) getApplicationContext();
        IPAddress = app.IPAddress;
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        
        if (mNfcAdapter == null)
        	Toast.makeText(this, "You have no NFC, please try with another device that has NFC!", Toast.LENGTH_SHORT).show();
        
        context = this;
    }

	@Override
	public void onResume() {
		super.onResume();
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			processIntent(getIntent());
		}
	}
	
	@Override
	public void onNewIntent(Intent intent) {
		setIntent(intent);
	}

	private void processIntent(Intent intent) {
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		String payload = new String(msg.getRecords()[0].getPayload());
		
		//String is: ID:x
		String id = payload.split("-")[0].split(":")[1];
		
		if(id.equals("-1"))
			return;
		
		currentFunction = RESTFunction.GET_VALIDATED_TICKETS;
		String busId = id;
    	ConnectionThread dataThread = new ConnectionThread(IPAddress + "/validated/"+busId, Method.GET, null, threadConnectionHandler, null);
		dataThread.start();
	}
	
	private void parseArray(JSONArray list) {
		ArrayList<Integer> tickets = new ArrayList<Integer>();
		for(int i = 0; i < list.length(); i++) {
			try {
				tickets.add((Integer) list.get(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		app.saveTicketList(tickets);
	}
}
