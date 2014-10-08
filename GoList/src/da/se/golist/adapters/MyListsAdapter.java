package da.se.golist.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import da.se.golist.R;
import da.se.golist.objects.ShoppingList;

public class MyListsAdapter extends BaseAdapter{
	private final Context context;
	private ArrayList<ShoppingList> lists = new ArrayList<ShoppingList>();

	public MyListsAdapter(Context context, ArrayList<ShoppingList> lists) {
		this.context = context;
		this.lists = lists;
	}
	
	public void updateMails(ArrayList<ShoppingList> lists){
		this.lists = lists;
		this.notifyDataSetChanged();		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.listlistitem, parent,	false);
		TextView nameText = (TextView) rowView.findViewById(R.id.title);		
		TextView secondLineText = (TextView) rowView.findViewById(R.id.secondLine);
//		if(lists.get(position).getImage() != null){
//			ImageView iv = (ImageView) rowView.findViewById(R.id.icon);
//			iv.setImageBitmap(lists.get(position).getImage());
//		}
		nameText.setText(lists.get(position).getName());
		secondLineText.setText(lists.get(position).getDescription());
		return rowView;
	}

	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public Object getItem(int pos) {
		return lists.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
}
