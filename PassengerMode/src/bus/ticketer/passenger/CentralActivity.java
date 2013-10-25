package bus.ticketer.passenger;

import bus.ticketer.adapters.CentralPagerAdapter;
import bus.ticketer.listeners.BusTabListener;
import bus.ticketer.listeners.SwipeListener;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;

/*
 * http://192.168.0.136:81/client/create/
 */

public class CentralActivity extends FragmentActivity {
	CentralPagerAdapter mCentralActivity;
	ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_central);
		tabHandler();
	}

	public void tabHandler() {
		mCentralActivity = new CentralPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.CentralPager);
		mViewPager.setOffscreenPageLimit(0);
		mViewPager.setAdapter(mCentralActivity);
		mViewPager.setOnPageChangeListener(new SwipeListener(mViewPager, CentralActivity.this));

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		ActionBar.TabListener tabListener = new BusTabListener(mViewPager);

		actionBar.addTab(actionBar.newTab().setText("Validate")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("Buy Tickets")
				.setTabListener(tabListener));
		/*actionBar.addTab(actionBar.newTab().setText("History")
				.setTabListener(tabListener));*/
		
	}
	
}
