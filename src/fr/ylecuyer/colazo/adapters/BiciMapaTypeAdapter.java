package fr.ylecuyer.colazo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BiciMapaTypeAdapter extends BaseAdapter {

	private Context context;
	private String[] types;
	
	public BiciMapaTypeAdapter(Context context, String[] types) {

		this.context = context;
		this.types = types;
	}

	@Override
	public int getCount() {

		return types.length;
	}

	@Override
	public Object getItem(int position) {
		
		return types[position];
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = null;

		if (convertView == null) {

			LayoutInflater layout_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = layout_inflater.inflate(android.R.layout.simple_spinner_item, null);

		}
		else 
			view = convertView;


		TextView type = (TextView)view.findViewById(android.R.id.text1);

		type.setText(types[position]);
		
		return view;
	}

}
