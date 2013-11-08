package bus.ticketer.passenger;

import java.util.ArrayList;

import bus.ticketer.objects.Ticket;
import bus.ticketer.runnable.TimerThread;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;

public class BusTicketer extends Application {

    private boolean timerOn = false, waitingValidation = false, successValidity = false;
    private SparseArray<ArrayList<Ticket>> tickets = new SparseArray<ArrayList<Ticket>>();
    private long[] minutes = {15,30,60};
    private String clientFilename = "client", IPAddress = "http://192.168.178.24:81/", ticketType;
    private String fileExtension = ".txt";
    private ViewPager appViewPager;
    private TimerThread timerThread;
    private int ticketID=-1;
    
    public boolean isTimerOn() {
        return timerOn;
    }

    public void setTimerOn(boolean timerOn) {
        this.timerOn = timerOn;
    }

    public boolean isWaitingValidation() {
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

	public boolean isSuccessValidity() {
		return successValidity;
	}

	public void setSuccessValidity(boolean successValidity) {
		this.successValidity = successValidity;
	}

	public String getTicketType() {
		return ticketType;
	}

	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}

	public long getMinutes(int pos) {
		return pos >= 0 && pos <= 2 ? minutes[pos] : -1;		
	}

	public ViewPager getAppViewPager() {
		return appViewPager;
	}

	public void setAppViewPager(ViewPager appViewPager) {
		this.appViewPager = appViewPager;
	}

	public TimerThread getTimerThread() {
		return timerThread;
	}

	public void setTimerThread(TimerThread timerThread) {
		this.timerThread = timerThread;
	}

	public int getTicketID() {
		return ticketID;
	}

	public void setTicketID(int ticketID) {
		this.ticketID = ticketID;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
}
