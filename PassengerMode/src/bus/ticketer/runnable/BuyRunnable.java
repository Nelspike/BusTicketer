package bus.ticketer.runnable;

import java.util.ArrayList;

import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import bus.ticketer.objects.Ticket;
import bus.ticketer.passenger.BusTicketer;
import bus.ticketer.passenger.R;

public class BuyRunnable implements Runnable {

	private BusTicketer app;
	private View view;
	
	public BuyRunnable(BusTicketer app, View view) {
		this.app = app;
		this.view = view;
	}
	
	@Override
	public void run() {
		TextView t1TicketsQuantity = (TextView) view
				.findViewById(R.id.t1_ticket_quantity);
		TextView t2TicketsQuantity = (TextView) view
				.findViewById(R.id.t2_ticket_quantity);
		TextView t3TicketsQuantity = (TextView) view
				.findViewById(R.id.t3_ticket_quantity);

		SparseArray<ArrayList<Ticket>> tickets = app.getTickets();
		
		t1TicketsQuantity.setText(tickets.get(1).size() + "");
		t2TicketsQuantity.setText(tickets.get(2).size() + "");
		t3TicketsQuantity.setText(tickets.get(3).size() + "");
	}

}
