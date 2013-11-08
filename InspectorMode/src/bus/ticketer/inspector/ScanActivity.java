package bus.ticketer.inspector;

import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class ScanActivity extends Activity {

	public NfcAdapter mNfcAdapter;
	public String IPAddress;
	private BusInspector app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		
        app = (BusInspector) getApplicationContext();
        IPAddress = app.IPAddress;
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        
        if (mNfcAdapter == null)
        	Toast.makeText(this, "You have no NFC, please try with another device that has NFC!", Toast.LENGTH_SHORT).show();
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
		
		Intent inte = new Intent(this, ResultActivity.class);
		
		if(app.isTicketValidated(Integer.parseInt(id)))
			inte.putExtra("status", true);
		else {
			inte.putExtra("status", false);
		}
	
		startActivity(inte);
		finish();
	}    
	
//	public void showResult(boolean res) {
//		String resultmsg="x";
//		String resulttitle="x";
//		if (res)
//		{
//			resultmsg="Bilhete válido";
//			resulttitle="Ok";
//		}
//		else
//		{
//			resultmsg="Cliente sem bilhete válido";
//			resulttitle="Infrator";
//		}
//		
//		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//				context);
// 
//			
//		alertDialogBuilder.setTitle(resulttitle)
//			.setMessage(resultmsg)
//			.setCancelable(false)
//			.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog,int id) {
//
//					dialog.cancel();
//				}
//			});
// 
//		AlertDialog alertDialog = alertDialogBuilder.create();
//		alertDialog.show();
//	}

}
