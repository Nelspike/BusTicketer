package bus.ticketer.validation;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import bus.ticketer.connection.ConnectionThread;
import bus.ticketer.utils.Method;
import bus.ticketer.utils.RESTFunction;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback {

	private NfcAdapter myNFC;
	private RESTFunction currentFunction;
	private int busID = 1;
    private String IPAddress = "http://192.168.178.24:81/";
	private boolean status = false;
	private Context context;
	
	@SuppressLint("HandlerLeak")
	private Handler threadConnectionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (currentFunction) {
				case VALIDATE_TICKET:
					try {
						status = new JSONObject(msg.obj.toString()).getBoolean("status");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				default:
					break;
			}
		}
	};
	
    @SuppressLint("ShowToast")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        TextView text = (TextView) findViewById(R.id.validation_title);
        text.setText("Welcome aboard!");
        
		myNFC = NfcAdapter.getDefaultAdapter(this);
		if(myNFC == null) {
			Toast.makeText(this, "You have no NFC, please try with another device that has NFC!", Toast.LENGTH_SHORT);
		}
		
		myNFC.setNdefPushMessageCallback(this, this);
		myNFC.setOnNdefPushCompleteCallback(this, this);
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
	
	@SuppressLint("ShowToast")
	private void processIntent(Intent intent) {
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		String payload = new String(msg.getRecords()[0].getPayload());
		
		//String is: ID:x
		String id = payload.split("-")[0].split(":")[1];
		
		if(id.equals("-1"))
			return;
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tid", id));
		params.add(new BasicNameValuePair("bid", busID+""));
		
		ProgressDialog progDialog = ProgressDialog.show(this,
				"", "Validating ticket..",
				true);
		
		progDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				Intent intent = new Intent(context, ResultActivity.class);
				intent.putExtra("status", status);
				context.startActivity(intent);
			}
		});
		
		currentFunction = RESTFunction.VALIDATE_TICKET;
		ConnectionThread dataThread = new ConnectionThread(
				IPAddress+"validate/", Method.POST,
				params, threadConnectionHandler, progDialog);
		
		dataThread.start();
	}


	@Override
	public void onNdefPushComplete(NfcEvent event) {
	}


	@Override
	public NdefMessage createNdefMessage(NfcEvent arg0) {
		return null;
	}
    
}
