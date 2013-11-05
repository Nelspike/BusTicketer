package bus.ticketer.utils;

import bus.ticketer.adapters.CentralPagerAdapter;
import bus.ticketer.fragments.ShowTicketsFragment;
import bus.ticketer.passenger.BusTicketer;
import bus.ticketer.runnable.TimerRunnable;
import android.app.Activity;
import android.os.CountDownTimer;
import android.view.View;

public class BusTimer extends CountDownTimer {

	private View view;
	private BusTicketer app;
	private String finalTicketFile;
	
	public BusTimer(long millisInFuture, long countDownInterval, View view, Activity context, String finalFile) {
		super(millisInFuture, countDownInterval);
		this.view = view;
		this.finalTicketFile = finalFile;
		app = (BusTicketer) context.getApplicationContext();
		
	}

	@Override
	public void onFinish() {
		view.post(new TimerRunnable(view, "Finish", 0));
		app.setTimerOn(false);
		app.setSuccessValidity(false);
		app.setWaitingValidation(false);
		FileHandler fh = new FileHandler(finalTicketFile, "");
		fh.deleteFile();
		
		((ShowTicketsFragment)((CentralPagerAdapter) app.getAppViewPager().getAdapter()).instantiateItem(app.getAppViewPager(), 0)).refresh();
	}

	@Override
	public void onTick(long millisUntilFinished) {
		view.post(new TimerRunnable(view, "Update", millisUntilFinished));
	}
	
	public void setView(View view) {
		this.view = view;
	}
}
