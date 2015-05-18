package da.se.golist.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.adapters.ExpandableMenuListAdapter;
import da.se.golist.adapters.ItemListAdapter;
import da.se.golist.objects.Item;
import da.se.golist.objects.ShoppingList;
import da.se.interfaces.AfterRefresh;
import da.se.otherclasses.DeleteAllItems;
import da.se.otherclasses.DeleteBoughtItems;
import da.se.otherclasses.LeaveList;
import da.se.otherclasses.LogoView;
import da.se.otherclasses.MarkAllItems;

@SuppressLint("RtlHardcoded")
public class ListActivity extends ReadNFCActivity{
	
	private ShoppingList list = null;
	private int id;
	private ItemListAdapter listAdapter = null;
	private ListView itemListView;
	private TextView textViewTitleList, textViewEmpty;
	private DrawerLayout mDrawerLayout;
	private ExpandableListView mDrawerList;
	private ExpandableMenuListAdapter mMenuAdapter;	
	private RelativeLayout relativeLayoutList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listlayout);
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    
	    initAdapter();
	    		
		textViewEmpty = (TextView)findViewById(R.id.textViewEmpty);
		textViewTitleList = (TextView) findViewById(R.id.textViewTitle);
		relativeLayoutList = (RelativeLayout) findViewById(R.id.relativeLayoutList);
	    setTypeface("deluxe", textViewEmpty, textViewTitleList);
		textViewTitleList.setText("List");
		
		ImageButton imageButtonMenu = (ImageButton) findViewById(R.id.imageButtonMenu);
        
        imageButtonMenu.setOnClickListener(new OnClickListener() {
			
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
		
		itemListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(ListActivity.this, EditItemActivity.class);
				intent.putExtra("list", list);
				intent.putExtra("itemid", ((Item) list.getItems().get(position)).getId());
				startActivityForResult(intent, 0);
			}
		});
		
		itemListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
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
					}
				}, id);
				return true;
			}
		});		
		
		//### Menu ###		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ExpandableListView) findViewById(R.id.navList);
       
        mDrawerList.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				Intent intent = new Intent(ListActivity.this, ManageListActivity.class);
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
							intent.putExtra("managelistfunction", new LeaveList());
							startActivityForResult(intent, ManageListActivity.CODE_LIST_LEFT);
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
						startActivityForResult(intent, ManageListActivity.CODE_LIST_DELETED);
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
						intent.putExtra("managelistfunction", new MarkAllItems(true));
						startActivityForResult(intent, 0);						
						break;
					case 2:	//Mark Items not bought
						intent.putExtra("managelistfunction", new MarkAllItems(false));
						startActivityForResult(intent, 0);			
						break;
					case 3:	//Delete bought Items
						intent.putExtra("managelistfunction", new DeleteBoughtItems());
						startActivityForResult(intent, 0);
						break;
					default:	//Delete all Items
						intent.putExtra("managelistfunction", new DeleteAllItems());
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

	    id = getIntent().getExtras().getInt("id");
		refreshList(null, id);
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
	protected void preExcecute() {
		updateViews(false, itemListView, mDrawerList);
	}
		
	@Override
	protected void postExcecute(JSONObject json) {		        
		//Daten von Liste erfolgreich geladen
		if(getListFromJson(json) != null){
			list = getListFromJson(json);
			updateMenuItems(isAdmin(list, LoginActivity.NAME));
			
			//Button für neues Item
			((ImageButton) findViewById(R.id.buttonNewItem)).setOnClickListener(new OnClickListener() {				
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ListActivity.this, CreateNewItemActivity.class);
					intent.putExtra("list", list);
					startActivityForResult(intent, 0);
				}
			});
			
			if(listAdapter == null){
				listAdapter = new ItemListAdapter(ListActivity.this, list.getItems());
				itemListView.setAdapter(listAdapter);
			}else{				
				listAdapter.updateListObjects(list.getItems());
			}
		    
			updateTextViews();
		}
		
		runAfterRefresh();
		updateViews(true, itemListView, mDrawerList);
	}
	
	private void updateTextViews(){
		textViewTitleList.setText(list.getName());
		
		if(list.getItems().size() == 0){
			textViewEmpty.setVisibility(View.VISIBLE);
			relativeLayoutList.setBackgroundColor(Color.parseColor("#007abb"));
		}else{
			textViewEmpty.setVisibility(View.INVISIBLE);
			
			//Bug in android 5.0: radial gradient geht nicht
			if(android.os.Build.VERSION.SDK_INT != 21){
				relativeLayoutList.setBackgroundResource(R.drawable.listviewbackground);
			}
		}
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
			
			menuListData.add(new String[]{getString(R.string.list), getString(R.string.history), getString(R.string.changename), getString(R.string.deletelist)});
	        menuListData.add(new String[]{getString(R.string.items), getString(R.string.importitems), getString(R.string.markitemsbought), getString(R.string.markitemsunbought),
	        		getString(R.string.deleteboughtitems), getString(R.string.deleteallitems)});
	        menuListData.add(new String[]{getString(R.string.user), getString(R.string.inviteauser), getString(R.string.showuser)});	        
		}else{
			menuListIcons.add(new Integer[]{R.drawable.menu_icon_list, R.drawable.menu_icon_history, R.drawable.menu_icon_delete});
			
			menuListData.add(new String[]{getString(R.string.list), getString(R.string.history), getString(R.string.leavelist)});
	        menuListData.add(new String[]{getString(R.string.items), getString(R.string.importitems), getString(R.string.markitemsbought), getString(R.string.markitemsunbought)});
	        menuListData.add(new String[]{getString(R.string.user), getString(R.string.inviteauser), getString(R.string.showuser)});
		}
		
		menuListIcons.add(new Integer[]{R.drawable.menu_icon_items, R.drawable.menu_icon_import, R.drawable.menu_icon_bought, R.drawable.menu_icon_not_bought, R.drawable.menu_icon_delete, R.drawable.menu_icon_delete});
	    menuListIcons.add(new Integer[]{R.drawable.menu_icon_usergroup, R.drawable.menu_icon_add_user, R.drawable.menu_icon_usergroup});

        mMenuAdapter = new ExpandableMenuListAdapter(this, menuListData, menuListIcons);
        mDrawerList.setAdapter(mMenuAdapter);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Activity beenden falls liste gelöscht oder verlassen wurde
		if((requestCode == ManageListActivity.CODE_LIST_LEFT || requestCode == ManageListActivity.CODE_LIST_DELETED) && resultCode != ManageListActivity.CODE_CANCELED){
			finish();
			return;
		}
		
		if(data != null && data.getExtras().containsKey("list")){
			list = (ShoppingList) data.getExtras().get("list");
			listAdapter.updateListObjects(list.getItems());
			updateTextViews();
		}
	}

	/**
	 * Text von NFC Tag gelesen
	 */
	@Override
	protected void nfcTagRead(String result) {
		if (result == null || !result.contains(";;")) {
        	return;
        }
        
        String[] resultArray = result.split(";;");
        if(resultArray.length != 4){
        	Toast.makeText(getApplicationContext(), getString(R.string.errorincorrectdata), Toast.LENGTH_SHORT).show();
        	return;
        }
        
        list.addItem(new Item(list.getFreeId(), resultArray[0], resultArray[2], resultArray[3], 
        		Integer.parseInt(resultArray[1]), LoginActivity.NAME, new Date()));
        list.setDescription(list.getItems().size() + " Items");
        listAdapter.notifyDataSetChanged();
        textViewEmpty.setVisibility(View.INVISIBLE);
        String infoText = getString(R.string.infofromnfc).replace("username", LoginActivity.NAME);
        infoText = infoText.replace("itemname", resultArray[0]);
        uploadList(list, false, infoText);
	}
	
}
