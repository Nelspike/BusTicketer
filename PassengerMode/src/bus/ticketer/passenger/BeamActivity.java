package bus.ticketer.passenger;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
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
import android.content.Intent;
import android.view.Menu;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class BeamActivity extends Activity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback{

	private NfcAdapter myNFC;
	private int ticketID;
	private final int MESSAGE_SENT = 1;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case MESSAGE_SENT:
					System.out.println("LAWL");
					break;
			}
		}
	};
	
	@Override
	public void onNewIntent(Intent intent) {
		setIntent(intent);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			processIntent(getIntent());
		}
	}
	
	private void processIntent(Intent intent) {
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		String payload = new String(msg.getRecords()[0].getPayload());
		System.out.println("I got: "  + payload);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beam);
		
		myNFC = NfcAdapter.getDefaultAdapter(this);
		if(myNFC == null) {
			Toast.makeText(this, "You have no NFC, please try with another device that has NFC!", Toast.LENGTH_SHORT);
		}
		
		myNFC.setNdefPushMessageCallback(this, this);
		myNFC.setOnNdefPushCompleteCallback(this, this);
		
		ticketID = getIntent().getIntExtra("ID", -1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.beam, menu);
		return true;
	}

	@Override
	public void onNdefPushComplete(NfcEvent event) {
		mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		String test = "ID:"+ticketID;
		
		NdefMessage msg = new NdefMessage(new NdefRecord[] {
			NdefRecord.createMime("application/bus.ticketer.message", test.getBytes())
		});
		return msg;
	}

}
