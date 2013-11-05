package bus.ticketer.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import bus.ticketer.fragments.BuyTicketsFragment;
import bus.ticketer.fragments.ShowTicketsFragment;
import bus.ticketer.passenger.R;

public class CentralPagerAdapter extends FragmentStatePagerAdapter {

	private int nSwipes = 2;
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	
	public CentralPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int i) {
		Fragment fragment;
		
		fragment = i == 0 ? new ShowTicketsFragment() : new BuyTicketsFragment();
		
		fragments.add(fragment);
		return fragment;
	}

	@Override
	public int getCount() {
		return nSwipes;
	}
	
	 @Override
	 public Object instantiateItem(View collection, int position) {
         LayoutInflater inflater = (LayoutInflater) collection.getContext()
                 .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         int resId = 0;
         switch (position) {
	         case 0:
	             resId = R.layout.fragment_show_tickets;
	             break;
	         case 1:
	             resId = R.layout.fragment_buy_tickets;
	             break;
	         case 2:
	             resId = R.layout.fragment_history_tickets;
	             break;
         }
         
         View view = inflater.inflate(resId, null);
         ((ViewPager) collection).addView(view, 0);
         return view;
     }
	 
	 @Override
	 public void destroyItem(View collection, int position, Object view) {
		 fragments.remove(position);
		 ((ViewPager) collection).removeViewAt(position);
	 }

	 @Override
	 public Parcelable saveState() {
	     return null;
	 }
	
}
