package bus.ticketer.inspector;

import java.util.ArrayList;

import android.app.Application;

public class BusInspector extends Application {

	public final String IPAddress = "http://192.168.178.24:81";
	
	private ArrayList<Integer> ticketiList;
	
	public boolean isTicketValidated(int client) {
		return ticketiList.contains(client);
	}
	
	public void saveTicketList(ArrayList<Integer> clientList) {
		this.ticketiList=clientList;
	}
}
