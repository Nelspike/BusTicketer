package bus.ticketer.listeners;

import bus.ticketer.passenger.BusTicketer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class TicketPurchaseListener implements OnClickListener {

	private String function;
	private TextView text;
	private int tickets;
	
	public TicketPurchaseListener(String func, String type, TextView text, BusTicketer app) {
		this.function = func;
		this.text = text;
		tickets = app.getTickets().get(Integer.parseInt(type.charAt(1)+"")).size();
	}
	
	@Override
	public void onClick(View v) {
		int current = Integer.parseInt(text.getText().toString());

		if(function.equals("Minus")) {
			if(current == 0)
				return;
			current--;
		}
		else {
			if(current == (10-tickets) || tickets >= 11)
				return;
			current++;			
		}
		
		text.setText(current + "");
	}

}
