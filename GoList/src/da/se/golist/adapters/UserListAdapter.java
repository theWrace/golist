package da.se.golist.adapters;

import java.util.ArrayList;

import android.content.Context;
import da.se.golist.R;
import da.se.golist.objects.GoListObject;

public class UserListAdapter extends Adapter{
	
	public UserListAdapter(Context context, ArrayList<GoListObject> listObjects) {
		this.context = context;
		this.listObjects = listObjects;
		layoutResource = R.layout.userlistitem;
	}
	
}
