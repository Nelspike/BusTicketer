package bus.ticketer.utils;

import java.util.ArrayList;

import android.app.Activity;
import android.util.SparseArray;
import android.view.View;
import bus.ticketer.objects.Ticket;
import bus.ticketer.passenger.BusTicketer;

public class BusUtils {
	
	public static BusTimer initializeTimer(Activity context, final View view) {
		BusTicketer app = ((BusTicketer) context.getApplicationContext());
		SparseArray<ArrayList<Ticket>> tickets = app.getTickets();
		int type = Integer.parseInt(app.getTicketType().charAt(1)+"");
		long span = 1000;
		String finalTicketFile = "";
		
		long timeInMinutes = app.getMinutes(type-1) * 1000 * 60;
		ArrayList<Ticket> tXTickets = tickets.get(type);
		String filename = "t"+type+"Ticket-";
		
		for(Ticket t : tXTickets) {
			int id = t.getTicketID();
			if(FileHandler.checkFileExistance(filename+id+".txt")) {
				finalTicketFile = filename+id+".txt";
				new FileWriter(finalTicketFile, new FileHandler().getUsername()).writeToFile();
				break;
			}
		}
		
		return new BusTimer(timeInMinutes, span, view, context, finalTicketFile);
	}
	
	public static void purchaseProcess(Activity context) {
		BusTicketer app = (BusTicketer) context.getApplication();
		FileHandler fHandler = new FileHandler(app.getClientFilename(), "");
		ArrayList<String> fileContents = fHandler.readFromFile();
		SparseArray<ArrayList<Ticket>> tickets = app.getTickets();
		
		for(int i = 1; i <= 3; i++) {
			ArrayList<Ticket> typeTickets = tickets.get(i);
			for(Ticket t : typeTickets) {
				if(!FileHandler.checkFileExistance("t"+i+"Ticket-"+t.getTicketID()+".txt"))
					new FileWriter("t"+i+"Ticket-"+t.getTicketID()+".txt", fileContents.get(0)).createFile();
			}
		}
	}
	
}
