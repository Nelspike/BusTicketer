package bus.ticketer.connection;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import bus.ticketer.utils.Method;

public class ConnectionThread extends Thread{
	
	private ConnectionRunnable runConnection;
	private Handler mHandler;
	
	public ConnectionThread(String link, Method method, ArrayList<NameValuePair> payload, Handler handler) {
		runConnection = new ConnectionRunnable(link, method.toString(), payload);
		mHandler = handler;
	}

	@Override
	public void run() {
    	runConnection.run();
		threadMsg();
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
