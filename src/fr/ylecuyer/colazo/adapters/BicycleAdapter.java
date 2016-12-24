package fr.ylecuyer.colazo.adapters;

import java.util.ArrayList;

import fr.ylecuyer.colazo.R;
import fr.ylecuyer.colazo.world.Bicycle;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BicycleAdapter extends BaseAdapter {

	private Context context = null;
	private ArrayList<Bicycle> bicycles;
	
	public BicycleAdapter(Context context, ArrayList<Bicycle> bicycles) {
	
		this.context = context;
		this.bicycles = bicycles;
		
	}
	
	public void setBicycles(ArrayList<Bicycle> bicycles) {
		
		this.bicycles = bicycles;
		
	}

	@Override
	public int getCount() {

		return bicycles.size();
	}

	@Override
	public Object getItem(int position) {

		return bicycles.get(position);
		
	}

	@Override
	public long getItemId(int position) {
		
		Bicycle bicycle = bicycles.get(position);
		
		return bicycle.getID();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = null;
		
		if (convertView == null) {
			
			LayoutInflater layout_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			view = layout_inflater.inflate(R.layout.listitem_bicirevision, null);
			
		}
		else 
			view = convertView;
		
		
		TextView description = (TextView)view.findViewById(R.id.text1);
		
		Bicycle bicycle = bicycles.get(position);
		
		description.setText(bicycle.getDescription());
		
		ImageView image = (ImageView)view.findViewById(R.id.imageView1);
		image.setImageURI(Uri.parse(bicycle.getPhoto_URI()));
		
		return view;
	}


}
