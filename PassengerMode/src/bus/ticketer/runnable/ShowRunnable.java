package bus.ticketer.runnable;

import java.util.ArrayList;

import android.app.Activity;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import bus.ticketer.listeners.RadioGroupListener;
import bus.ticketer.listeners.ValidationListener;
import bus.ticketer.objects.Ticket;
import bus.ticketer.passenger.BusTicketer;
import bus.ticketer.passenger.R;
import bus.ticketer.utils.BusTimer;
import bus.ticketer.utils.BusUtils;

public class ShowRunnable implements Runnable {

	private BusTimer cTimer;
	private View view;
	private BusTicketer app;
	private Activity context;
	
	public ShowRunnable(BusTicketer app, Activity context, View view) {
		this.app = app;
		this.context = context;
		this.view = view;
	}
	
	@Override
	public void run() {
		final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.ticket_radio);
		final TextView ticketsText = (TextView) view.findViewById(R.id.show_ticket_amount);
		final TextView timerText = (TextView) view.findViewById(R.id.ticket_timer);
		final SparseArray<ArrayList<Ticket>> tickets = ((BusTicketer) context.getApplicationContext()).getTickets();
		final Button validationButton = (Button) view.findViewById(R.id.ticket_validate);

		if(app.isSuccessValidity())
			cTimer = BusUtils.initializeTimer(context, view);
		
		if(app.isWaitingValidation()) {
			for (int i = 0; i < radioGroup.getChildCount(); i++)
				radioGroup.getChildAt(i).setEnabled(false);
			timerText.setText("Waiting validation...");
			validationButton.setEnabled(false);
		}
		else if(app.isSuccessValidity()) {
			if(app.getTimerThread() == null) {
				TimerThread timer = new TimerThread(cTimer);
				app.setTimerThread(timer);
				timer.start();
				Toast.makeText(context, "Your ticket is valid! Enjoy your trip!", Toast.LENGTH_SHORT).show();
			}
			else app.getTimerThread().setView(view);

			validationButton.setEnabled(false);
			app.setTimerOn(true);
		}
		else {
			radioGroup.check(R.id.t1_radio);
			radioGroup.setOnCheckedChangeListener(new RadioGroupListener(context.getApplicationContext(),ticketsText));
			timerText.setText("No ticket Validated");	
			ticketsText.setText(tickets.get(1).size() + " tickets");
			validationButton.setOnClickListener(new ValidationListener(radioGroup, context));
		}		
	}
}
