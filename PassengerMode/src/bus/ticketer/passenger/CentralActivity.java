package bus.ticketer.passenger;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import bus.ticketer.adapters.CentralPagerAdapter;
import bus.ticketer.connection.ConnectionThread;
import bus.ticketer.utils.FileHandler;
import bus.ticketer.utils.Method;
import bus.ticketer.utils.RESTFunction;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.app.ActionBar.Tab;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.*;
import android.support.v4.view.*;

/*
 * http://192.168.0.136:81/client/create/
 */

public class CentralActivity extends FragmentActivity {
	CentralPagerAdapter mCentralActivity;
	ViewPager mViewPager;
	private RESTFunction currentFunction;
	private ArrayList<Integer> tickets = new ArrayList<Integer>();
	ProgressDialog progDialog;
	
	@SuppressLint("HandlerLeak")
	private Handler threadConnectionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			switch(currentFunction) {
				case GET_CLIENT_TICKETS:
					handleGetTickets(msg);
					break;
				default:
					break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_central);

		progDialog = ProgressDialog.show(
				CentralActivity.this, "",
				"Loading, please wait!", true);
		
		progDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				System.out.println("This here - " + tickets.get(0));
				tabHandler();
				mViewPager.getAdapter().notifyDataSetChanged();
			}
		});
		
		getTicketInfo();
	}

	public void tabHandler() {
		mCentralActivity = new CentralPagerAdapter(getSupportFragmentManager(), tickets);
		mCentralActivity.setTickets(tickets);
		mViewPager = (ViewPager) findViewById(R.id.CentralPager);
		mViewPager.setAdapter(mCentralActivity);

		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						getActionBar().setSelectedNavigationItem(position);
					}
				});

		final ActionBar actionBar = getActionBar();

		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabReselected(Tab arg0,
					android.app.FragmentTransaction arg1) {
				// probably ignore, eh?

			}

			@Override
			public void onTabSelected(Tab arg0,
					android.app.FragmentTransaction arg1) {
				mViewPager.setCurrentItem(arg0.getPosition());
			}

			@Override
			public void onTabUnselected(Tab arg0,
					android.app.FragmentTransaction arg1) {
				// hide the tab

			}
		};

		actionBar.addTab(actionBar.newTab().setText("Validate")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("Buy Tickets")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("History")
				.setTabListener(tabListener));

	}

	public void getTicketInfo() {
		FileHandler fHandler = new FileHandler("client.txt", "");
		ArrayList<String> fileContents = fHandler.readFromFile();
		
		currentFunction = RESTFunction.GET_CLIENT_TICKETS;
		ConnectionThread dataThread = new ConnectionThread("http://192.168.0.136:81/list/"+fileContents.get(2), Method.GET, null, threadConnectionHandler, progDialog);
		dataThread.start();
	}
	
	private void handleGetTickets(Message msg) {
		JSONObject ticketListing = (JSONObject) msg.obj;
		try {
			tickets.add(ticketListing.getInt("t1"));
			tickets.add(ticketListing.getInt("t2"));
			tickets.add(ticketListing.getInt("t3"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
