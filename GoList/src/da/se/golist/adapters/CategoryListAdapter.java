package da.se.golist.adapters;

import java.util.List;

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
    
    private static class ViewHolder {
    	 private ImageView imageView;
    }

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mViewHolder = null;
		if(convertView == null){
			mViewHolder = new ViewHolder();
	    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	convertView = inflater.inflate(R.layout.categorylistitem, parent, false);
	    	mViewHolder.imageView = (ImageView) convertView.findViewById(R.id.icon);
	    	convertView.setTag(mViewHolder);
		}else{
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		mViewHolder.imageView.setImageDrawable(list.get(position));
        return convertView;
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
