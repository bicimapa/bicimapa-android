package fr.ylecuyer.colazo.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import fr.ylecuyer.colazo.world.Event;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BiciEventosAdapter extends BaseAdapter {



	private Context context;
	private ArrayList<Event> events;

	public BiciEventosAdapter(Context context, ArrayList<Event> events) {

		this.context = context;
		this.events = events;

	}

	@Override
	public int getCount() {
		return events.size();
	}

	@Override
	public Object getItem(int position) {

		Event event = events.get(position);

		return event;
	}

	@Override
	public long getItemId(int position) {

		Event event = events.get(position);

		return event.getID();
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

		Event event = events.get(position);
		
		String formated_date = new SimpleDateFormat("dd MMM yyyy - HH:mm", new Locale("es_ES")).format(new Date(event.getWhen()));

		
		description.setText(Html.fromHtml("<b>" + event.getName() + "</b><br/><small>" + event.getShortDescription() + "</small><br/>" + formated_date + "<br/>" + event.getWhere() ));

		return view;
	}



}
