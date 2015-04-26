package da.se.golist.adapters;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import da.se.golist.R;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.Item;

public class ItemListAdapter extends Adapter{
		
	public ItemListAdapter(Context context, ArrayList<GoListObject> listObjects) {
		this.context = context;
		this.listObjects = listObjects;
		layoutResource = R.layout.itemlistitem;
		
		//Nach Kategorie sortieren
		Collections.sort(this.listObjects);
	}
	
	public void updateListObjects(ArrayList<GoListObject> listObjects){
		this.listObjects = listObjects;
		Collections.sort(this.listObjects);
		notifyDataSetChanged();
	}
	
	private static class ViewHolder {
		private ImageView imageViewCategory;
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
			mViewHolder.imageViewCategory = (ImageView) convertView.findViewById(R.id.icon);		
			convertView.setTag(mViewHolder);
		}else{
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		
		Item item = (Item) listObjects.get(position);
		mViewHolder.textViewName.setText(item.getName());
		mViewHolder.textViewDescription.setText(item.getDescription());
		
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/geosanslight.ttf");
		mViewHolder.textViewName.setTypeface(tf);
		mViewHolder.textViewDescription.setTypeface(tf);
		
		if(item.getState() == Item.STATE_BOUGHT){
			mViewHolder.textViewName.setPaintFlags(mViewHolder.textViewName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
			mViewHolder.textViewDescription.setPaintFlags(mViewHolder.textViewDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
		}else{
			mViewHolder.textViewName.setPaintFlags(0);
			mViewHolder.textViewDescription.setPaintFlags(0);
		}
		
		TypedArray imgs = context.getResources().obtainTypedArray(R.array.category_icons);
		mViewHolder.imageViewCategory.setImageDrawable(imgs.getDrawable(item.getCategory()));
		imgs.recycle();
		
		return convertView;
	}
}
