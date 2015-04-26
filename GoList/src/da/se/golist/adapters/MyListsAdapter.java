package da.se.golist.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import da.se.golist.R;
import da.se.golist.objects.GoListObject;

public class MyListsAdapter extends Adapter{

	public MyListsAdapter(Context context, ArrayList<GoListObject> listObjects) {
		this.context = context;
		this.listObjects = listObjects;
		layoutResource = R.layout.listlistitem;
	}
	
	private static class ViewHolder {
		private TextView textViewName, textViewDescription;
		private ImageView imageViewIcon;
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
			mViewHolder.imageViewIcon = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(mViewHolder);
		}else{
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/geosanslight.ttf");
		mViewHolder.textViewName.setTypeface(tf);
		mViewHolder.textViewDescription.setTypeface(tf);
		mViewHolder.textViewName.setText(listObjects.get(position).getName());
		mViewHolder.textViewDescription.setText(listObjects.get(position).getDescription());
		
		if(listObjects.get(position).getDescription().equals("Invitation")){
			RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.itemlayout);
			layout.setBackgroundResource(R.drawable.invitationitem);
			mViewHolder.imageViewIcon.setImageResource(R.drawable.icon_message);
		}
		return convertView;
	}

}
