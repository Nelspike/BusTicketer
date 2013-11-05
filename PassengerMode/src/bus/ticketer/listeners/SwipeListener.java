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
	private BusTicketer app;
	
	public SwipeListener(ViewPager mViewPager, Context context) {
		this.mViewPager = mViewPager;
		this.context = context;
	}
	
	@Override
	public void onPageSelected(int position) {
		this.app = (BusTicketer) context.getApplicationContext();
		CentralPagerAdapter adapter = (CentralPagerAdapter) mViewPager.getAdapter();
		if(app.isWaitingValidation()) {
			((Activity) context).getActionBar().setSelectedNavigationItem(0);
			((ShowTicketsFragment) adapter.instantiateItem(mViewPager, 0)).refresh();
		}
		else {
			if(position == 0) {
				if(!app.isSuccessValidity())
					((ShowTicketsFragment) adapter.instantiateItem(mViewPager, position)).refresh();
			}
			else ((BuyTicketsFragment) adapter.instantiateItem(mViewPager, position)).refresh();
		
			((Activity) context).getActionBar().setSelectedNavigationItem(position);
		}
	}
}
