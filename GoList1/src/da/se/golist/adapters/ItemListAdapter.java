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
		
		Item item = (Item) listObjects.get(position);
		nameText.setText(item.getName());
		secondLineText.setText(item.getDescription());
		
		if(item.getState() == Item.STATE_BOUGHT){
			nameText.setPaintFlags(nameText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
			secondLineText.setPaintFlags(secondLineText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
		}
		
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		TypedArray imgs = context.getResources().obtainTypedArray(R.array.category_icons);
		imageView.setImageDrawable(imgs.getDrawable(item.getCategory()));
		
		return rowView;
	}
}
