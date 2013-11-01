package bus.ticketer.passenger;

import java.util.ArrayList;

import bus.ticketer.objects.Ticket;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.SparseArray;

public class BusTicketer extends Application {

    private boolean timerOn = false, waitingValidation = false;
    private SparseArray<ArrayList<Ticket>> tickets = new SparseArray<ArrayList<Ticket>>();
    private String clientFilename = "client";
    private String IPAddress = "http://172.30.78.107:81/";
    
    public boolean isTimerOn() {
        return timerOn;
    }

    public void setTimerOn(boolean timerOn) {
        this.timerOn = timerOn;
    }

    public boolean isWaitingValdiation() {
        return waitingValidation;
    }

    public void setWaitingValidation(boolean waitingValidation) {
        this.waitingValidation = waitingValidation;
    }
    
	public SparseArray<ArrayList<Ticket>> getTickets() {
		return tickets;
	}

	public void setTickets(SparseArray<ArrayList<Ticket>> tickets) {
		this.tickets = tickets;
	}
	
	public void networkPrompt() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());

		alertDialogBuilder.setTitle("No Connection");

		alertDialogBuilder
		.setMessage("Did you turn on your WiFi/Internet connectivity?")
		.setCancelable(false)
		.setPositiveButton("OK",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
		        System.exit(0);
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();		
	}
	
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

	public String getClientFilename() {
		return clientFilename;
	}

	public void setClientFilename(String clientFilename) {
		this.clientFilename = clientFilename;
	}

	public String getIPAddress() {
		return IPAddress;
	}

	public void setIPAddress(String iPAddress) {
		IPAddress = iPAddress;
	}
	
	/*public Context getBusContext() {
		return getApplicationContext();
	}*/
}
