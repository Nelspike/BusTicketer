package bus.ticketer.listeners;

import java.util.ArrayList;

import bus.ticketer.objects.Ticket;
import bus.ticketer.passenger.BeamActivity;
import bus.ticketer.passenger.BusTicketer;
import bus.ticketer.passenger.R;
import bus.ticketer.utils.FileHandler;
import bus.ticketer.utils.FileWriter;
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
	
	@Override
	public void onClick(View v) {
		
		final Button btn = (Button) v;
		int type = 0;
		
		switch(radio.getCheckedRadioButtonId()) {
			case R.id.t1_radio:
				type = 1;
				break;
			case R.id.t2_radio:
				type = 2;
				break;
			case R.id.t3_radio:
				type = 3;
				break;
			default:
				break;
		}

		ArrayList<Ticket> tXTickets = tickets.get(type);
		String filename = "t"+type+"Ticket-";
		
		for(Ticket t : tXTickets) {
			int id = t.getTicketID();
			if(FileHandler.checkFileExistance(filename+id+".txt")) {
				String finalTicketFile = filename+id+".txt";
				new FileWriter(finalTicketFile, new FileHandler().getUsername()).writeToFile();
				btn.setEnabled(false);
				
				for (int i = 0; i < radio.getChildCount(); i++)
					radio.getChildAt(i).setEnabled(false);
				
				((BusTicketer) context.getApplicationContext()).setTicketType("T"+type);

				Intent intent = new Intent(context, BeamActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("ID", id);
				

				((BusTicketer) context.getApplicationContext()).setTicketID(id);
				
				context.startActivity(intent);
				((Activity) context).finish();
				break;
			}
		}
		
		if(tXTickets.isEmpty())
			Toast.makeText(context, "You have no tickets, please buy some!", Toast.LENGTH_SHORT).show();
	}
}
