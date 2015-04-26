package da.se.golist.adapters;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import da.se.golist.R;
import da.se.golist.activities.ShowFavoriteItemsActivity;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.Item;

public class FavoriteItemListAdapter extends Adapter{
	
	private ShowFavoriteItemsActivity activity;
		
	public FavoriteItemListAdapter(Context context, ArrayList<GoListObject> listObjects, ShowFavoriteItemsActivity activity) {
		this.context = context;
		this.listObjects = listObjects;
		this.activity = activity;
		layoutResource = R.layout.favoriteitemlistitem;
		
		//Nach Kategorie sortieren
		Collections.sort(this.listObjects);
	}
	
	public void updateListObjects(ArrayList<GoListObject> listObjects){
		this.listObjects = listObjects;
		Collections.sort(this.listObjects);
		this.notifyDataSetChanged();
	}
	
	private static class ViewHolder {
		private ImageView imageViewCategory, imageViewDelete;
		private TextView textViewName, textViewDescription;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder mViewHolder = null;
		if(convertView == null){
			mViewHolder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(layoutResource, parent, false);
			mViewHolder.textViewName = (TextView) convertView.findViewById(R.id.title);		
			mViewHolder.textViewDescription  = (TextView) convertView.findViewById(R.id.secondLine);
			mViewHolder.imageViewCategory = (ImageView) convertView.findViewById(R.id.icon);
			mViewHolder.imageViewDelete = (ImageView) convertView.findViewById(R.id.delete);			
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
		}
		TypedArray imgs = context.getResources().obtainTypedArray(R.array.category_icons);
		mViewHolder.imageViewCategory.setImageDrawable(imgs.getDrawable(item.getCategory()));
		imgs.recycle();
		mViewHolder.imageViewDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				activity.deleteFavoriteItem((Item)listObjects.get(position));				
			}
		});
		
		return convertView;
	}
}
