package da.se.golist.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import da.se.golist.R;

public class HistoryItemListAdapter extends BaseAdapter{
	
	private List<String> history;
	private Context context;
		
	public HistoryItemListAdapter(Context context, List<String> history) {
		this.context = context;
		this.history = history;
	}
	
	private static class ViewHolder {
		private TextView textViewTitle, textViewDate;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder mViewHolder = null;
		if(convertView == null){
			mViewHolder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.historylistitem, parent, false);
			mViewHolder.textViewTitle = (TextView) convertView.findViewById(R.id.title);		
			mViewHolder.textViewDate  = (TextView) convertView.findViewById(R.id.secondLine);
			convertView.setTag(mViewHolder);
		}else{
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/geosanslight.ttf");
		mViewHolder.textViewTitle.setTypeface(tf);
		mViewHolder.textViewDate.setTypeface(tf);
		
		String[] data = history.get(position).split("::");
		mViewHolder.textViewDate.setText(data[1]);
		mViewHolder.textViewTitle.setText(data[0]);
		
		return convertView;
	}

	@Override
	public int getCount() {
		return history.size();
	}

	@Override
	public String getItem(int position) {
		return history.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
}
