package bus.ticketer.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import bus.ticketer.passenger.CentralActivity;
import bus.ticketer.utils.FileHandler;

public class DialogAdapter {
	
	public static void dialogYesNoShowing(String title, String text, final Context context, final FileHandler fHandler){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		alertDialogBuilder.setTitle(title);

		alertDialogBuilder
		.setMessage(text)
		.setCancelable(false)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				fHandler.deleteFiles();
				((Activity) context).recreate();
			}
		})
		.setNegativeButton("No",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
	            System.exit(0);
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	public static void registrationSuccess(final Context context) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		alertDialogBuilder.setTitle("Registration succeeded!");

		alertDialogBuilder
		.setMessage("Enjoy Bus Ticketer!")
		.setCancelable(false)
		.setPositiveButton("OK",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
		        Intent intent = new Intent(context, CentralActivity.class);
		        context.startActivity(intent);
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();		
	}
}
