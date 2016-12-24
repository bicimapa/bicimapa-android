package fr.ylecuyer.colazo.adapters;

import java.util.ArrayList;

import fr.ylecuyer.colazo.world.BicyclePart;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BicyclePartAdapter extends BaseAdapter {

	private Context context = null;
	private ArrayList<BicyclePart> parts;
	
	public BicyclePartAdapter(Context context, ArrayList<BicyclePart> parts) {
	
		this.context = context;
		this.parts = parts;
		
	}
	
	public void setParts(ArrayList<BicyclePart> parts) {
		
		this.parts = parts;
		
	}

	@Override
	public int getCount() {

		return parts.size();
	}

	@Override
	public Object getItem(int position) {

		
		BicyclePart part = parts.get(position);
				
		return part;
	}

	@Override
	public long getItemId(int position) {
		
		BicyclePart part = parts.get(position);
				
		return part.getID();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = null;
		
		if (convertView == null) {
			
			LayoutInflater layout_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			view = layout_inflater.inflate(android.R.layout.simple_list_item_1, null);
			
		}
		else 
			view = convertView;
		
		
		TextView description = (TextView)view.findViewById(android.R.id.text1);
		
		BicyclePart part = parts.get(position);
		
		description.setText(part.getName());
		
		return view;
	}


}
