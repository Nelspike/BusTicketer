package bus.ticketer.inspector;

import bus.ticketer.connection.ConnectionThread;
import bus.ticketer.utils.Method;
import bus.ticketer.utils.RESTFunction;
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
import android.view.Menu;
import android.nfc.*;
import android.widget.Button;

public class MainActivity extends Activity {
	public  NfcAdapter mNfcAdapter;
	public  String revisorID;
	private RESTFunction currentFunction;
	private Context context;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button scanId=(Button) findViewById(R.id.TextView1);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
        	scanId.setText("NFC não suportado");
            return;
        }
        context=this;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
		
		
		
		ProgressDialog progDialog = ProgressDialog.show(this,
				"", "Getting validated tickets..",
				true);
		
		progDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				Intent intent = new Intent(context, MainActivity.class);
				//intent.putExtra("status", status);
				context.startActivity(intent);
			}
		});
		//fazer pedido HTTP ao server 
		currentFunction=RESTFunction.GET_VALIDATED_TICKETS;
		String busId=id;
    	ConnectionThread dataThread = new ConnectionThread("http://192.168.0.136:81/getValidated/"+busId, Method.GET, null, threadConnectionHandler, null);
		dataThread.start();
		
	}

	
	@SuppressLint("HandlerLeak")
	private Handler threadConnectionHandler = new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			
			
		}
	};
    
}
