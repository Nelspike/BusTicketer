package bus.ticketer.inspector;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.nfc.*;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.widget.Button;

public class MainActivity extends Activity implements CreateNdefMessageCallback,OnNdefPushCompleteCallback  {
	  NfcAdapter mNfcAdapter;
	  String revisorID;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button scanId=(Button) findViewById(R.id.button1);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
        	scanId.setText("NFC n�o suportado");
            return;
        }
        else{
        	 mNfcAdapter.setNdefPushMessageCallback(this, this);
        	 mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
             
             scanId.setOnClickListener(new OnClickListener() {

     			@Override
     			public void onClick(View arg0) {
     				
     			
     				
     				
     			}

     		});
        }
       
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


	@Override
	public NdefMessage createNdefMessage(NfcEvent event)
	{
		NdefMessage msg = new NdefMessage(new NdefRecord[]{NdefRecord.createMime("BusTicketer", "getInfo".getBytes())});
		return msg;
	}


	@Override
	public void onNdefPushComplete(NfcEvent arg0) {
		//fazer pedido HTTP ao server 
		
	}
    
}
