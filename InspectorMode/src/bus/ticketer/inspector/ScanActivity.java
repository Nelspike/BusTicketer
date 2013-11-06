package bus.ticketer.inspector;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Menu;

public class ScanActivity extends Activity {

	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scan, menu);
		return true;
	}
	
	
	public void showResult(boolean res)
	{
		String resultmsg="x";
		String resulttitle="x";
		if (res)
		{
			resultmsg="Bilhete válido";
			resulttitle="Ok";
		}
		else
		{
			resultmsg="Cliente sem bilhete válido";
			resulttitle="Infrator";
		}
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
 
			
		alertDialogBuilder.setTitle(resulttitle)
			.setMessage(resultmsg)
			.setCancelable(false)
			.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {

					dialog.cancel();
				}
			});
 
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

}
