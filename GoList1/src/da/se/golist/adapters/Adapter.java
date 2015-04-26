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
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(layoutResource, parent, false);
		TextView nameText = (TextView) rowView.findViewById(R.id.title);		
		TextView secondLineText = (TextView) rowView.findViewById(R.id.secondLine);
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/geosanslight.ttf");
		nameText.setTypeface(tf);
		secondLineText.setTypeface(tf);
		nameText.setText(listObjects.get(position).getName());
		secondLineText.setText(listObjects.get(position).getDescription());
		return rowView;
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
