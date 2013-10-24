package bus.ticketer.listeners;

import java.util.ArrayList;

import bus.ticketer.objects.Ticket;
import bus.ticketer.passenger.BusTicketer;
import bus.ticketer.passenger.R;
import android.content.Context;
import android.util.SparseArray;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class RadioGroupListener implements OnCheckedChangeListener {

	private SparseArray<ArrayList<Ticket>> tickets;
	private TextView text;
	
	public RadioGroupListener(Context context, TextView text) {
		this.tickets = ((BusTicketer) context.getApplicationContext()).getTickets();
		this.text = text;
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
			case R.id.t1_radio:
				text.setText(tickets.get(1).size() + " tickets");
				break;
			case R.id.t2_radio:
				text.setText(tickets.get(2).size() + " tickets");
				break;
			case R.id.t3_radio:
				text.setText(tickets.get(3).size() + " tickets");
				break;
			default:
				break;
		}
	}
}
