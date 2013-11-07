package bus.ticketer.inspector;

import java.util.ArrayList;

import android.app.Application;

public class BusInspector extends Application {

		public final String IPAddress="10.13.37.72:8080";
		private ArrayList<Integer> clientList;
		
		public boolean isClientValidated(Integer client)
		{
			return clientList.contains(client);
		}
		
		public void saveClientList(ArrayList<Integer> clientList)
		{
			this.clientList=clientList;
		}
}
