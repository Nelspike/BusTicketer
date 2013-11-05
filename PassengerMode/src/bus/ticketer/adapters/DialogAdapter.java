package bus.ticketer.adapters;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import bus.ticketer.connection.ConnectionThread;
import bus.ticketer.fragments.BuyTicketsFragment;
import bus.ticketer.passenger.BusTicketer;
import bus.ticketer.passenger.CentralActivity;
import bus.ticketer.utils.FileHandler;
import bus.ticketer.utils.Method;
import bus.ticketer.utils.RESTFunction;

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
	
	public static void connectionIssues(final Context context) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		alertDialogBuilder.setTitle("Connection Expired");

		alertDialogBuilder
		.setMessage("Can't reach the server at the moment. Restart the app, and try again.")
		.setCancelable(false)
		.setPositiveButton("OK",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
		        System.exit(0);
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();		
	}
	
	public static void purchaseSuccess(int t1Bought, int t2Bought, int t3Bought, int transactionCost, Activity activity) {
		BusTicketer app = (BusTicketer) activity.getApplicationContext();
		final ViewPager mViewPager = app.getAppViewPager();
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

		alertDialogBuilder.setTitle("Purchase succeeded!");

		alertDialogBuilder
		.setMessage("You have bought: " + t1Bought + " T1 Tickets, " + t2Bought + " T2 Tickets and " + t3Bought + " T3 Tickets, for " + transactionCost + "€.")
		.setCancelable(false)
		.setPositiveButton("OK",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				((BuyTicketsFragment)((CentralPagerAdapter) mViewPager.getAdapter()).instantiateItem(mViewPager, 1)).refresh();
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();		
	}
	
	public static void confirmPurchase(final ArrayList<NameValuePair> params, final ProgressDialog pDiag, 
			int[] data, final Activity activity, final String confirmationToken, final Handler handler, final View rootView) {
		final BusTicketer app = (BusTicketer) activity.getApplicationContext();
		final ViewPager mViewPager = app.getAppViewPager();
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

		alertDialogBuilder.setTitle("Confirm your purchase");

		alertDialogBuilder
		.setMessage(buildConfirmationMessage(data[0], data[1], data[2], data[3]))
		.setCancelable(false)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				params.add(new BasicNameValuePair("token", confirmationToken));
				
				//currentFunction = RESTFunction.BUY_CONFIRMATION_CLIENT;
				ConnectionThread dataThread = new ConnectionThread(
						app.getIPAddress()+"buy/", Method.POST,
						params, handler, pDiag,
						RESTFunction.BUY_CONFIRMATION_CLIENT, rootView, activity);
				dataThread.start();
			}
		})
		.setNegativeButton("No",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				((BuyTicketsFragment)((CentralPagerAdapter) mViewPager.getAdapter()).instantiateItem(mViewPager, 1)).refresh();
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();		
	}
	
	private static String buildConfirmationMessage(int t1Bought, int t2Bought, int t3Bought, int transactionCost) {
		String ret = "";
		
		if(t1Bought != 0) {
			ret += t1Bought + "t1 tickets";
			if(t2Bought != 0) {
				ret += ", " + t2Bought + "t2 tickets";
				if(t3Bought != 0) ret += " and " + t3Bought + "t3 tickets";
			}
			else
				if(t3Bought != 0) ret += " and " + t3Bought + "t3 tickets";
		}
		else {
			if(t2Bought != 0) {
				ret += t2Bought + "t2 tickets";
				if(t3Bought != 0) ret += " and " + t3Bought + "t3 tickets";
			}
			else {
				if(t3Bought != 0) ret += t3Bought + "t3 tickets";
				ret = "(no tickets)";
			}
		}
		
		return "You will get a bonus of: " + ret + " and it will cost you " + transactionCost + " €.";
	}
}
