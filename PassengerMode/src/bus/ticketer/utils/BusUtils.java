package bus.ticketer.utils;

import java.util.ArrayList;

import android.app.Activity;
import android.util.SparseArray;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import bus.ticketer.objects.Ticket;
import bus.ticketer.passenger.BusTicketer;

public class BusUtils {
	
	public static BusTimer initializeTimer(Activity context, final TextView text, final Button btn, final RadioGroup radio) {
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
			if(FileHandler.checkFileExistance(filename+id+".pdf")) {
				finalTicketFile = filename+id+".pdf";
				FileHandler fh = new FileHandler(finalTicketFile, "");
				fh.deleteFile();
				new PDFWriter(finalTicketFile, "T"+type, new FileHandler().getUsername(), null, true).createFile();
				break;
			}
		}
		
		return new BusTimer(timeInMinutes, span, text, btn, radio, context, finalTicketFile);
	}
	
	public static void purchaseProcess(Activity context) {
		BusTicketer app = (BusTicketer) context.getApplication();
		FileHandler fHandler = new FileHandler(app.getClientFilename(), "");
		ArrayList<String> fileContents = fHandler.readFromFile();
		SparseArray<ArrayList<Ticket>> tickets = app.getTickets();
		
		for(int i = 1; i <= 3; i++) {
			ArrayList<Ticket> typeTickets = tickets.get(i);
			for(Ticket t : typeTickets) {
				if(!FileHandler.checkFileExistance("t"+i+"Ticket-"+t.getTicketID()+".pdf"))
					new PDFWriter("t"+i+"Ticket-"+t.getTicketID()+".pdf", "T"+i, fileContents.get(0), null, false).createFile();
			}
		}
		
		/*for(Ticket t : t1Tickets) {
			if(!FileHandler.checkFileExistance(t.getTicketID()+".pdf"))
				new PDFWriter("t1Ticket-"+t.getTicketID()+".pdf", "T1", fileContents.get(0), null, false).createFile();
		}

		for(Ticket t : t2Tickets) {
			if(!FileHandler.checkFileExistance(t.getTicketID()+".pdf"))
				new PDFWriter("t2Ticket-"+t.getTicketID()+".pdf", "T1", fileContents.get(0), null, false).createFile();
		}
		
		for(Ticket t : t3Tickets) {
			if(!FileHandler.checkFileExistance(t.getTicketID()+".pdf"))
				new PDFWriter("t3Ticket-"+t.getTicketID()+".pdf", "T1", fileContents.get(0), null, false).createFile();
		}*/
	}
	
}
