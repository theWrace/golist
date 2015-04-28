package da.se.golist.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.adapters.ExpandableMenuListAdapter;
import da.se.golist.adapters.ItemListAdapter;
import da.se.golist.objects.Item;
import da.se.golist.objects.LogoView;
import da.se.golist.objects.ShoppingList;

@SuppressLint("RtlHardcoded")
public class ListActivity extends BaseActivity{
	
	private ShoppingList list = null;
	private int id;
	private ItemListAdapter listAdapter = null;
	private ListView itemListView;
	private TextView textViewTitleList;
	private DrawerLayout mDrawerLayout;
	private ExpandableListView mDrawerList;
	private ExpandableMenuListAdapter mMenuAdapter;
	public final static int CODE_LIST_DELETED = 0, TYPE_DELETE_ALL_ITEMS = 1, 
			TYPE_DELETE_BOUGHT_ITEMS = 2, TYPE_MARK_ALL_BOUGHT = 3, TYPE_MARK_ALL_NOT_BOUGHT = 4, TYPE_LEAVE_LIST = 5;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listlayout);
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/deluxe.ttf");
		textViewTitleList = (TextView) findViewById(R.id.textViewTitle);
		textViewTitleList.setTypeface(tf);
		textViewTitleList.setText("List");
		
		textViewTitleList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDrawerLayout.openDrawer(Gravity.LEFT);
			}
		});
		
		logoView = (LogoView) findViewById(R.id.logoView);
		logoView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				refreshList(null, id);
			}
		});
		
		itemListView = (ListView) findViewById(R.id.listViewArticles);
		
		itemListView.setDividerHeight(6);
		
		itemListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(ListActivity.this, EditItemActivity.class);
				intent.putExtra("list", list);
				intent.putExtra("itemid", ((Item) list.getItems().get(position)).getId());
				startActivity(intent);
			}
		});
		
		itemListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
				itemListView.setEnabled(false);
				final int itemid = ((Item) list.getItems().get(position)).getId();
				refreshList(new AfterRefresh() {
					
					@Override
					public void applyChanges() {
						Item item = list.getItemById(itemid);
						String infoText = "";
						if(item.getState() == Item.STATE_BOUGHT){
							item.setState(Item.STATE_NORMAL);
							infoText = getString(R.string.infoitemnotbought).replace("username", LoginActivity.NAME);
							infoText = infoText.replace("itemname", item.getName());
							Toast.makeText(ListActivity.this, R.string.markednotbought, Toast.LENGTH_SHORT).show();
						}else if(item.getState() == Item.STATE_NORMAL){
							item.setState(Item.STATE_BOUGHT);
							infoText = getString(R.string.infoitembought).replace("username", LoginActivity.NAME);
							infoText = infoText.replace("itemname", item.getName());
							Toast.makeText(ListActivity.this, R.string.markedbought, Toast.LENGTH_SHORT).show();
						}
						uploadList(list, false, infoText);
						itemListView.setEnabled(true);
						refreshList(null, id);
					}
				}, id);
				return true;
			}
		});
		
		
		//### Menu ###
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ExpandableListView) findViewById(R.id.navList);
        mDrawerList.setGroupIndicator(null);

       
        mDrawerList.setDivider(null);
        mDrawerList.setVerticalScrollBarEnabled(false);
        mDrawerList.setDividerHeight(50);
        mDrawerList.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				Intent intent = new Intent(ListActivity.this, ManageItemsActivity.class);
				intent.putExtra("id", ListActivity.this.id);
				
				switch(groupPosition){
				case 0:	//List
					switch(childPosition){
					case 0:	//History
						intent = new Intent(ListActivity.this, ShowHistoryActivity.class);
						intent.putExtra("id", ListActivity.this.id);
						startActivity(intent);
						break;
					case 1:
						//Leave List
						if(!isAdmin(list, LoginActivity.NAME)){							
							intent.putExtra("type", TYPE_LEAVE_LIST);
							startActivityForResult(intent, CODE_LIST_DELETED);
							break;
						}
						
						//Change List Name
						intent = new Intent(ListActivity.this, ChangeListNameActivity.class);
						intent.putExtra("id", ListActivity.this.id);
						startActivityForResult(intent, 0);
						break;
					default:	//Delete List
						intent = new Intent(ListActivity.this, DeleteListActivity.class);
						intent.putExtra("id", list.getID());
						startActivityForResult(intent, CODE_LIST_DELETED);
						break;
					}
					break;
				case 1:	//Items
					switch(childPosition){
					case 0:	//Import Favorite Items
						intent = new Intent(ListActivity.this, ImportFavoriteItemsActivity.class);
						intent.putExtra("id", ListActivity.this.id);
						startActivityForResult(intent, 0);
						break;
					case 1:	//Mark Items bought
						intent.putExtra("type", TYPE_MARK_ALL_BOUGHT);
						startActivityForResult(intent, 0);						
						break;
					case 2:	//Mark Items not bought
						intent.putExtra("type", TYPE_MARK_ALL_NOT_BOUGHT);
						startActivityForResult(intent, 0);			
						break;
					case 3:	//Delete bought Items
						intent.putExtra("type", TYPE_DELETE_BOUGHT_ITEMS);
						startActivityForResult(intent, 0);
						break;
					default:	//Delete all Items
						intent.putExtra("type", TYPE_DELETE_ALL_ITEMS);
						startActivityForResult(intent, 0);
						break;
					}
					break;
				case 2:	//User
					switch(childPosition){
					case 0:	//Invite a User
						Intent newListIntent = new Intent(ListActivity.this, InviteUserActivity.class);
						newListIntent.putExtra("list", list);
						startActivityForResult(newListIntent, 0);
						break;
					default:	//Show User
						intent = new Intent(ListActivity.this, ShowUserActivity.class);
						intent.putExtra("id", ListActivity.this.id);
						startActivityForResult(intent, 0);
						break;
					}
					break;
				}
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				return false;
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_MENU) {
	        if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
	            mDrawerLayout.openDrawer(Gravity.LEFT);
	        } else if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
	            mDrawerLayout.closeDrawer(Gravity.LEFT);
	        }
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onStart() {
		id = getIntent().getExtras().getInt("id");
		refreshList(null, id);
		super.onStart();
	}
	
	@Override
	protected void preExcecute() {
		itemListView.setEnabled(false);
		mDrawerList.setEnabled(false);
	}
		
	@Override
	protected void postExcecute(JSONObject json) {
		String message = "Loading failed!";
		try {
			message = json.getString("message");			
			if(message.equals("succes")){
				JSONArray dataArray = json.getJSONArray("data");
				list = (ShoppingList) objectFromString(dataArray.getString(0));
				updateMenuItems(isAdmin(list, LoginActivity.NAME));
			}
		} catch (JSONException e) {			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}			       
        
		//Daten von Liste erfolgreich geladen
		if(message.equals("succes")){
			
			//Button für neues Item
			((ImageButton) findViewById(R.id.buttonNewItem)).setOnClickListener(new OnClickListener() {				
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ListActivity.this, CreateNewItemActivity.class);
					intent.putExtra("list", list);
					startActivity(intent);
				}
			});
			
			if(listAdapter == null){
				listAdapter = new ItemListAdapter(ListActivity.this, list.getItems());
				itemListView.setAdapter(listAdapter);
			}else{				
				listAdapter.updateListObjects(list.getItems());
			}
		    
		    textViewTitleList.setText(list.getName());			
			
			TextView textViewEmpty = (TextView)findViewById(R.id.textViewEmpty);
			if(list.getItems().size() == 0){
				Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/deluxe.ttf");
				textViewEmpty.setVisibility(View.VISIBLE);
				textViewEmpty.setTypeface(tf);
			}
			else{
				textViewEmpty.setVisibility(View.INVISIBLE);
			}			
		}
		
		if(afterRefresh != null){
			afterRefresh.applyChanges();
			afterRefresh = null;
		}
		
		itemListView.setEnabled(true);
		mDrawerList.setEnabled(true);		
	}
	
	/**
	 * Zeigt Einstellungen für Liste an
	 * @param isAdmin
	 */
	private void updateMenuItems(boolean isAdmin){
		List<Integer[]> menuListIcons = new ArrayList<Integer[]>();
		List<String[]> menuListData = new ArrayList<String[]>();
        
		if(isAdmin){
			menuListIcons.add(new Integer[]{R.drawable.menu_icon_list, R.drawable.menu_icon_history, R.drawable.menu_icon_settings, R.drawable.menu_icon_delete});
			
			menuListData.add(new String[]{"List", "History", getString(R.string.changename), getString(R.string.deletelist)});
	        menuListData.add(new String[]{"Items", getString(R.string.importitems), getString(R.string.markitemsbought), getString(R.string.markitemsunbought),
	        		getString(R.string.deleteboughtitems), getString(R.string.deleteallitems)});
	        menuListData.add(new String[]{"User", getString(R.string.inviteauser), getString(R.string.showuser)});	        
		}else{
			menuListIcons.add(new Integer[]{R.drawable.menu_icon_list, R.drawable.menu_icon_history, R.drawable.menu_icon_delete});
			
			menuListData.add(new String[]{"List", "History", "Leave List"});
	        menuListData.add(new String[]{"Items", getString(R.string.importitems), getString(R.string.markitemsbought), getString(R.string.markitemsunbought)});
	        menuListData.add(new String[]{"User", getString(R.string.inviteauser), getString(R.string.showuser)});
		}
		
		menuListIcons.add(new Integer[]{R.drawable.menu_icon_items, R.drawable.menu_icon_import, R.drawable.menu_icon_bought, R.drawable.menu_icon_not_bought, R.drawable.menu_icon_delete, R.drawable.menu_icon_delete});
	    menuListIcons.add(new Integer[]{R.drawable.menu_icon_usergroup, R.drawable.menu_icon_add_user, R.drawable.menu_icon_usergroup});

        mMenuAdapter = new ExpandableMenuListAdapter(this, menuListData, menuListIcons);
        mDrawerList.setAdapter(mMenuAdapter);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Activity beenden falls liste gelöscht wurde
		if(requestCode == CODE_LIST_DELETED && data != null && data.getExtras().containsKey("listdeleted")){
			finish();
		}else{
			//Nach Show oder Invite User liste aktualisieren
			refreshList(null, id);
		}
	}
}
