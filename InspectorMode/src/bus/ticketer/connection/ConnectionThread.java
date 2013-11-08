package bus.ticketer.connection;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import bus.ticketer.utils.Method;

public class ConnectionThread extends Thread {

	private ConnectionRunnable runConnection;
	private Handler mHandler;
	private ProgressDialog progDialog;
	
	public ConnectionThread(String link, Method method,
			ArrayList<NameValuePair> payload, Handler handler,
			ProgressDialog progDialog) {
		runConnection = new ConnectionRunnable(link, method.toString(), payload);
		mHandler = handler;
		this.progDialog = progDialog;
	}

	@Override
	public void run() {				
		runConnection.run();
		threadMsg();
		if (progDialog != null)
			progDialog.dismiss();
	}

	public JSONObject getJSON() {
		return runConnection.getResultObject();
	}
	
	private void threadMsg() {
		Message msgObj = mHandler.obtainMessage();
		msgObj.obj = getJSON();
		mHandler.sendMessage(msgObj);
	}
}
