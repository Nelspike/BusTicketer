package bus.ticketer.listeners;

import bus.ticketer.adapters.CentralPagerAdapter;
import bus.ticketer.fragments.BuyTicketsFragment;
import bus.ticketer.fragments.ShowTicketsFragment;
import bus.ticketer.passenger.BusTicketer;
import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;

public class SwipeListener extends ViewPager.SimpleOnPageChangeListener {
	
	private ViewPager mViewPager;
	private Context context;
	
	public SwipeListener(ViewPager mViewPager, Context context) {
		this.mViewPager = mViewPager;
		this.context = context;
	}
	
	@Override
	public void onPageSelected(int position) {
		if(position == 0) {
			if(!((BusTicketer) context.getApplicationContext()).isTimerOn()) {
				((ShowTicketsFragment)((CentralPagerAdapter) mViewPager.getAdapter()).instantiateItem(mViewPager, position)).refresh();
			}
		}
		else {
			((BuyTicketsFragment)((CentralPagerAdapter) mViewPager.getAdapter()).instantiateItem(mViewPager, position)).refresh();
		}

		((Activity) context).getActionBar().setSelectedNavigationItem(position);
	}
}
