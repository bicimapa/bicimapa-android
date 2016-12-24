package fr.ylecuyer.colazo.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import fr.ylecuyer.colazo.R;
import fr.ylecuyer.colazo.world.LogEntry;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LogsAdapter extends BaseAdapter {



	private Context context;
	private ArrayList<LogEntry> logs;

	public LogsAdapter(Context context, ArrayList<LogEntry> logs) {

		this.context = context;
		this.logs = logs;
	}

	@Override
	public int getCount() {
		return logs.size();
	}

	@Override
	public Object getItem(int position) {

		LogEntry log = logs.get(position);

		return log;
	}

	@Override
	public long getItemId(int position) {


		LogEntry log = logs.get(position);
		
		return log.getID();
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

		LogEntry log = logs.get(position);
				
		String formated_date = new SimpleDateFormat("dd MMM yyyy", new Locale("es_ES")).format(new Date(log.getTimestamp()));
		
		description.setText(context.getString(R.string.lbl_description) + "\n" + log.getDescription() + "\n" + context.getString(R.string.lbl_reason) + "\n" + log.getReason() + "\n" + context.getString(R.string.lbl_cost) + " " + log.getCost() + "\n" + context.getString(R.string.lbl_date) + " " + formated_date);
		
		return view;

	}

	public void setLogs(ArrayList<LogEntry> logs) {
		
		this.logs = logs;
		
	}

}
