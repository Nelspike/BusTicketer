package bus.ticketer.runnable;

import bus.ticketer.passenger.R;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

public class TimerRunnable implements Runnable {

	private View view;
	private String type;
	private long millis;
	
	public TimerRunnable(View view, String type, long millis) {
		this.view = view;
		this.type = type;
		this.millis = millis;
	}
	
	@Override
	public void run() {
		if(type.equals("Update")) {
			TextView timerText = (TextView) view.findViewById(R.id.ticket_timer);
			long seconds = (millis/1000)%60;
			timerText.setText("" + millis/(1000*60) + ":" + 
					(seconds < 10 ? "0"+seconds : seconds)
					+ " minutes left");
		}
		else if(type.equals("Finish")) {
			TextView timerText = (TextView) view.findViewById(R.id.ticket_timer);
			RadioGroup radio = (RadioGroup) view.findViewById(R.id.ticket_radio);
			Button valButton = (Button) view.findViewById(R.id.ticket_validate);
			for (int i = 0; i < radio.getChildCount(); i++)
				radio.getChildAt(i).setEnabled(true);
			
			timerText.setText("No ticket Validated");
			valButton.setEnabled(true);
		}
	}

}
