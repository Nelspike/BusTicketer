package bus.ticketer.listeners;

import java.util.ArrayList;

import bus.ticketer.objects.Ticket;
import bus.ticketer.passenger.BusTicketer;
import bus.ticketer.passenger.R;
import bus.ticketer.utils.FileHandler;
import bus.ticketer.utils.PDFWriter;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ValidationListener implements OnClickListener {

	private SparseArray<ArrayList<Ticket>> tickets;
	private Context context;
	private RadioGroup radio;
	private TextView text;
	
	public ValidationListener(RadioGroup radio, Context context, TextView text) {
		this.text = text;
		this.context = context;
		this.tickets = ((BusTicketer) context.getApplicationContext()).getTickets();
		this.radio = radio;
	}
	
	@Override
	public void onClick(View v) {
		final Button btn = (Button) v;
		btn.setEnabled(false);
		for (int i = 0; i < radio.getChildCount(); i++)
			radio.getChildAt(i).setEnabled(false);
		
		String type = "";
		String filename = "";
		int pos = 0;
		long timeInMinutes = 0;
		
		switch(radio.getCheckedRadioButtonId()) {
			case R.id.t1_radio:
				type = "T1";
				timeInMinutes = 15*1000*60;
				break;
			case R.id.t2_radio:
				type = "T2";
				timeInMinutes = 30*1000*60;
				break;
			case R.id.t3_radio:
				type = "T3";
				timeInMinutes = 60*1000*60;
				break;
			default:
				break;
		}
		
		int sizeToCheck = 0;
		
		if(type.equals("T1")) {
			sizeToCheck = tickets.get(1).size();
			filename = "t1-ticket";
		}
		else if(type.equals("T2")) {
			sizeToCheck = tickets.get(2).size();
			filename = "t2-ticket";
		}
		else {
			sizeToCheck = tickets.get(3).size();
			filename = "t3-ticket";
		}
		
		for(int i = 0; i < sizeToCheck; i++) {
			if(FileHandler.checkFileExistance(filename+i+".pdf")) {
				FileHandler fh = new FileHandler(filename+i+".pdf", "");
				fh.deleteFile();
				new PDFWriter(filename+i+".pdf", type, new FileHandler().getUsername(), null, true).createFile();
				pos = i;
				break;
			}
		}
		
		final String finalTicketFile = filename+pos+".pdf";
		
		new CountDownTimer(timeInMinutes, 1000*60) {
			
			public void onTick(long millisUntilFinished) {
				text.setText("" + millisUntilFinished/(1000*60) + " minutes left");
			}
			
			public void onFinish() {
				for (int i = 0; i < radio.getChildCount(); i++)
					radio.getChildAt(i).setEnabled(true);
				
				text.setText("No ticket Validated");
				((BusTicketer) context.getApplicationContext()).setTimerOn(false);
				btn.setEnabled(true);
				FileHandler fh = new FileHandler(finalTicketFile, "");
				fh.deleteFile();
			}
		}.start();
		
		((BusTicketer) context.getApplicationContext()).setTimerOn(true);
	}
}
