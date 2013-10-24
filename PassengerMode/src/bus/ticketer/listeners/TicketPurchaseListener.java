package bus.ticketer.listeners;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class TicketPurchaseListener implements OnClickListener {

	private String function;
	private TextView text;
	
	public TicketPurchaseListener(String func, TextView text) {
		this.function = func;
		this.text = text;
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
			if(current == 10)
				return;
			current++;			
		}
		
		text.setText(current + "");
	}

}
