package bus.ticketer.runnable;

import android.view.View;
import bus.ticketer.utils.BusTimer;

public class TimerThread extends Thread {
	
	BusTimer timer;
	
	public TimerThread() {
		
	}
	
	public TimerThread(BusTimer t) {
		this.timer = t;
	}
	
	@Override
	public void run() {
		timer.start();
	}
	
	public void setTimer(BusTimer t) {
		this.timer = t;
	}
	
	public void setView(View view) {
		timer.setView(view);
	}
}
