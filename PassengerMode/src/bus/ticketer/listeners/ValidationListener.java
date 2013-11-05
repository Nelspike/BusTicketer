package bus.ticketer.listeners;

import java.util.ArrayList;

import bus.ticketer.objects.Ticket;
import bus.ticketer.passenger.BeamActivity;
import bus.ticketer.passenger.BusTicketer;
import bus.ticketer.passenger.R;
import bus.ticketer.utils.FileHandler;
import bus.ticketer.utils.PDFWriter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ValidationListener implements OnClickListener {

	private SparseArray<ArrayList<Ticket>> tickets;
	private Context context;
	private RadioGroup radio;
	
	public ValidationListener(RadioGroup radio, Context context) {
		this.context = context;
		this.tickets = ((BusTicketer) context.getApplicationContext()).getTickets();
		this.radio = radio;
	}
	
	@SuppressLint("ShowToast")
	@Override
	public void onClick(View v) {
		
		final Button btn = (Button) v;
		String type = "";
		String filename = "";
		int pos = -1;
		
		switch(radio.getCheckedRadioButtonId()) {
			case R.id.t1_radio:
				type = "T1";
				break;
			case R.id.t2_radio:
				type = "T2";
				break;
			case R.id.t3_radio:
				type = "T3";
				break;
			default:
				break;
		}
				
		if(type.equals("T1")) {
			ArrayList<Ticket> t1Tickets = tickets.get(1);
			filename = "t1Ticket-";
			
			for(Ticket t : t1Tickets) {
				int id = t.getTicketID();
				if(FileHandler.checkFileExistance(filename+id+".pdf")) {
					FileHandler fh = new FileHandler(filename+id+".pdf", "");
					fh.deleteFile();
					new PDFWriter(filename+id+".pdf", type, new FileHandler().getUsername(), null, true).createFile();
					pos = id;
					break;
				}
			}
		}
		else if(type.equals("T2")) {
			ArrayList<Ticket> t2Tickets = tickets.get(2);
			filename = "t2Ticket-";
			
			for(Ticket t : t2Tickets) {
				int id = t.getTicketID();
				if(FileHandler.checkFileExistance(filename+id+".pdf")) {
					FileHandler fh = new FileHandler(filename+id+".pdf", "");
					fh.deleteFile();
					new PDFWriter(filename+id+".pdf", type, new FileHandler().getUsername(), null, true).createFile();
					pos = id;
					break;
				}
			}
		}
		else {
			ArrayList<Ticket> t3Tickets = tickets.get(3);
			filename = "t3Ticket-";
			
			for(Ticket t : t3Tickets) {
				int id = t.getTicketID();
				if(FileHandler.checkFileExistance(filename+id+".pdf")) {
					FileHandler fh = new FileHandler(filename+id+".pdf", "");
					fh.deleteFile();
					new PDFWriter(filename+id+".pdf", type, new FileHandler().getUsername(), null, true).createFile();
					pos = id;
					break;
				}
			}
		}
		
		if(pos != -1) {
			btn.setEnabled(false);
			for (int i = 0; i < radio.getChildCount(); i++)
				radio.getChildAt(i).setEnabled(false);
			
			((BusTicketer) context.getApplicationContext()).setTicketType(type);

			Intent intent = new Intent(context, BeamActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("ID", pos);
			context.startActivity(intent);
			((Activity) context).finish();
		}
		else {
			Toast.makeText(context, "You have no tickets, please buy some!", Toast.LENGTH_SHORT);
		}
	}
}
