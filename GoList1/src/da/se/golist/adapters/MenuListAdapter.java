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
    private LayoutInflater inflater;

    public MenuListAdapter(Context pContext, String[] pTitle, int[] pIcon) {
        context = pContext;
        mTitle = pTitle;
        mIcon = pIcon;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.drawer_list_item, parent, false);

        TextView txtTitle = (TextView) itemView.findViewById(R.id.title);
        ImageView imgIcon = (ImageView) itemView.findViewById(R.id.icon);

        txtTitle.setText(mTitle[position]);
        imgIcon.setImageResource(mIcon[position]);
        
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/geosanslight.ttf");
        txtTitle.setTypeface(tf);

        return itemView;
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
