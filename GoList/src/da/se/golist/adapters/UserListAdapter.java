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
import da.se.golist.objects.User;

public class UserListAdapter extends BaseAdapter{
	private final Context context;
	private ArrayList<User> user = new ArrayList<User>();

	public UserListAdapter(Context context, ArrayList<User> user) {
		this.context = context;
		this.user = user;
	}
	
	public void updateMails(ArrayList<User> user){
		this.user = user;
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
		nameText.setText(user.get(position).getName());
		secondLineText.setText("neuer User");
		return rowView;
	}

	@Override
	public int getCount() {
		return user.size();
	}

	@Override
	public Object getItem(int pos) {
		return user.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
}
