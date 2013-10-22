package bus.ticketer.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import bus.ticketer.fragments.CentralFragment;

public class CentralPagerAdapter extends FragmentStatePagerAdapter {

	private int nSwipes = 3;
	private ArrayList<Integer> tickets = new ArrayList<Integer>();
	
	public CentralPagerAdapter(FragmentManager fm, ArrayList<Integer> c) {
		super(fm);
		tickets = c;
	}

	@Override
	public Fragment getItem(int i) {
		Fragment fragment = new CentralFragment();		
		Bundle args = new Bundle();
		args.putInt(CentralFragment.ARG_OBJECT, i + 1);
		args.putIntegerArrayList(CentralFragment.SPARSE, tickets);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public int getItemPosition(Object object) {
	    return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return nSwipes;
	}
	
	public void setTickets(ArrayList<Integer> ti) {
		tickets = ti;
	}

}
