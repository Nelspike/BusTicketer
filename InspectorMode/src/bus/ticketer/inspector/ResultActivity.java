package bus.ticketer.inspector;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends Activity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback {

	private NfcAdapter myNFC;
	private boolean status;
	private final int MESSAGE_SENT = 1;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case MESSAGE_SENT:
					Intent inte = new Intent(ResultActivity.this, ScanActivity.class);
					startActivity(inte);
					break;
			}
		}
	};
	
	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
		status = getIntent().getExtras().getBoolean("status");
		TextView text = (TextView) findViewById(R.id.result_box);
		TextView box = (TextView) findViewById(R.id.instructions_box);
		ImageView mark = (ImageView) findViewById(R.id.check_box_spot);
		
		if(status) {
			text.setText("Valid Ticket");
			text.setTextColor(0xff007500);
			box.setText(getResources().getText(R.string.success_validation));
			mark.setImageDrawable(getResources().getDrawable(R.drawable.check_button));
		}
		else{
			text.setText("Invalid Ticket");
			text.setTextColor(0xff750000);
			box.setText(getResources().getText(R.string.success_validation));
			mark.setImageDrawable(getResources().getDrawable(R.drawable.cross_button));
		}
		
		myNFC = NfcAdapter.getDefaultAdapter(this);
		if(myNFC == null) {
			Toast.makeText(this, "You have no NFC, please try with another device that has NFC!", Toast.LENGTH_SHORT).show();
		}
		
		myNFC.setNdefPushMessageCallback(this, this);
		myNFC.setOnNdefPushCompleteCallback(this, this);
	}

	@Override
	public void onNdefPushComplete(NfcEvent event) {
		mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		String test = "";
		
		if(status) test = "Success";
		else test = "Fail";
		
		NdefMessage msg = new NdefMessage(new NdefRecord[] {
			NdefRecord.createMime("application/bus.ticketer.message", test.getBytes())
		});
		return msg;
	}

}
