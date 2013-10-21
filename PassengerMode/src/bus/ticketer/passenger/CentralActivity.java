package bus.ticketer.passenger;

import bus.ticketer.adapters.CentralPagerAdapter;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.*;

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

}
