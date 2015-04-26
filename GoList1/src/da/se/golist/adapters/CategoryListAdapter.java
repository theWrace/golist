package da.se.golist.adapters;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import da.se.golist.R;

public class CategoryListAdapter extends BaseAdapter {

    private final List<Drawable> list;
    private final Activity context;


    public CategoryListAdapter(Activity context, List<Drawable> list) {
        this.context = context;
        this.list = list;
    }

    @SuppressLint("ViewHolder")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.categorylistitem, parent, false);
        ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(list.get(position));
        return view;
    }

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
}
