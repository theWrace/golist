package da.se.golist.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import da.se.golist.R;
import da.se.golist.objects.GoListObject;

public abstract class Adapter extends BaseAdapter{
	
	protected Context context = null;
	protected ArrayList<GoListObject> listObjects;
	protected int layoutResource;
				
	public void updateListObjects(ArrayList<GoListObject> listObjects){
		this.listObjects = listObjects;
		this.notifyDataSetChanged();
	}
	
	private static class ViewHolder {
		private TextView textViewName, textViewDescription;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mViewHolder = null;
		if(convertView == null){
			mViewHolder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(layoutResource, parent, false);
			mViewHolder.textViewName = (TextView) convertView.findViewById(R.id.title);		
			mViewHolder.textViewDescription  = (TextView) convertView.findViewById(R.id.secondLine);
			convertView.setTag(mViewHolder);
		}else{
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/geosanslight.ttf");
		mViewHolder.textViewName.setTypeface(tf);
		mViewHolder.textViewDescription.setTypeface(tf);
		mViewHolder.textViewName.setText(listObjects.get(position).getName());
		mViewHolder.textViewDescription.setText(listObjects.get(position).getDescription());
		
		return convertView;
	}

	@Override
	public int getCount() {
		return listObjects.size();
	}

	@Override
	public Object getItem(int pos) {
		return listObjects.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

}
