package bus.ticketer.utils;

import bus.ticketer.passenger.BusTicketer;
import android.app.Activity;
import android.os.CountDownTimer;
import android.widget.*;

public class BusTimer extends CountDownTimer {

	private TextView timerText;
	private Button valButton;
	private RadioGroup radio;
	private BusTicketer app;
	private String finalTicketFile;
	
	public BusTimer(long millisInFuture, long countDownInterval,
			TextView text, Button btn, RadioGroup radio, Activity context, String finalFile) {
		super(millisInFuture, countDownInterval);
		timerText = text;
		valButton = btn;
		this.radio = radio;
		this.finalTicketFile = finalFile;
		app = (BusTicketer) context.getApplicationContext();
		
	}

	@Override
	public void onFinish() {
		for (int i = 0; i < radio.getChildCount(); i++)
			radio.getChildAt(i).setEnabled(true);
		
		timerText.setText("No ticket Validated");
		app.setTimerOn(false);
		valButton.setEnabled(true);
		FileHandler fh = new FileHandler(finalTicketFile, "");
		fh.deleteFile();
	}

	@Override
	public void onTick(long millisUntilFinished) {
		timerText.setText("" + millisUntilFinished/(1000*60) + ":" + (millisUntilFinished/1000)%60 + " minutes left");
	}
}
