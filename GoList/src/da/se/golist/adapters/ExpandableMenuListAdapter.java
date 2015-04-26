package da.se.golist.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import da.se.golist.R;

public class ExpandableMenuListAdapter extends BaseExpandableListAdapter {
	 
    private Context context;
    private List<String[]> menuListData;
    private List<Integer[]> iconResources;
 
    public ExpandableMenuListAdapter(Context context, List<String[]> menuListData, List<Integer[]> iconResources) {
        this.context = context;
        this.menuListData = menuListData;
        this.iconResources = iconResources;
    }
 
    @Override
    public String getChild(int groupPosition, int childPosititon) {
        return menuListData.get(groupPosition)[childPosititon+1];
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    
    public void updateMenuItems(List<String[]> menuListData){
        this.menuListData = menuListData;
        notifyDataSetChanged();
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
  
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.drawer_list_child_item, parent, false);
        }
 
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/geosanslight.ttf");
        TextView txtListChild = (TextView) convertView.findViewById(R.id.title);
        txtListChild.setTypeface(tf);
        txtListChild.setText(getChild(groupPosition, childPosition));
        
        ImageView imageViewIcon = (ImageView) convertView.findViewById(R.id.icon);
        imageViewIcon.setImageResource(iconResources.get(groupPosition)[childPosition+1]);
         
        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return menuListData.get(groupPosition).length-1;
    }
 
    @Override
    public String getGroup(int groupPosition) {
        return menuListData.get(groupPosition)[0];
    }
 
    @Override
    public int getGroupCount() {
        return menuListData.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.drawer_list_item, parent, false);
        }
 
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/geosanslight.ttf");
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.title);
        lblListHeader.setTypeface(tf, Typeface.BOLD);
        lblListHeader.setText(getGroup(groupPosition));
        
        ImageView imageViewIcon = (ImageView) convertView.findViewById(R.id.icon);
        imageViewIcon.setImageResource(iconResources.get(groupPosition)[0]);
 
        return convertView;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
