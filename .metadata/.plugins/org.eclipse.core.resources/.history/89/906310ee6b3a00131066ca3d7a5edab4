package bus.ticketer.utils;

import bus.ticketer.passenger.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SpinnerAdapter extends ArrayAdapter<String> {

	private String[] strings;
	private int[] images;
	private Context context;
	
	public SpinnerAdapter(Context context, int textViewResourceId,
			String[] objects, int[] images) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.strings = objects;
		this.images = images;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View row = inflater.inflate(R.layout.spinner_cc_choice_box, parent, false);
		TextView label = (TextView) row.findViewById(R.id.company);
		label.setText(strings[position]);

		ImageView icon = (ImageView) row.findViewById(R.id.spinner_image);
		icon.setImageResource(images[position]);

		return row;
	}
}
