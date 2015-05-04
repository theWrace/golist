package da.se.golist.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import da.se.golist.R;

public class MenuListAdapter extends BaseAdapter {  
    private Context context;
    private String[] mTitle;
    private int[] mIcon;

    public MenuListAdapter(Context pContext, String[] pTitle, int[] pIcon) {
        context = pContext;
        mTitle = pTitle;
        mIcon = pIcon;
    }
    
    private static class ViewHolder {
		private ImageView imageViewIcon;
		private TextView textViewTitle;
	}

    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder mViewHolder = null;
    	if(convertView == null){
    		mViewHolder = new ViewHolder();
    		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.drawer_list_item, parent, false);
			mViewHolder.textViewTitle = (TextView) convertView.findViewById(R.id.title);
			mViewHolder.imageViewIcon = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(mViewHolder);
    	}else{
			mViewHolder = (ViewHolder) convertView.getTag();
		}

        mViewHolder.textViewTitle.setText(mTitle[position]);
        mViewHolder.imageViewIcon.setImageResource(mIcon[position]);
        
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/geosanslight.ttf");
        mViewHolder.textViewTitle.setTypeface(tf);

        return convertView;
    }

    @Override
    public int getCount() {
        return mTitle.length;
    }

    @Override
    public Object getItem(int position) {
        return mTitle[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
