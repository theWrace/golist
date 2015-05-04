package da.se.golist.activities;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;

import da.se.golist.R;
import da.se.golist.adapters.MenuListAdapter;
import da.se.golist.adapters.MyListsAdapter;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.LogoView;
import da.se.golist.objects.ShoppingList;

@SuppressLint("RtlHardcoded")
public class MyListsActivity<AnalyticsSampleApp> extends BaseActivity{
	
	private ArrayList<GoListObject> myLists = new ArrayList<GoListObject>();
	private boolean isLoading = false;
	private MyListsAdapter listAdapter;	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private final String[] drawerTitles = {"Favorite Items", "Change Password", "Delete Account", "Logout"};
	private final int[] drawerIcons = {R.drawable.menu_icon_favorite, R.drawable.menu_icon_settings, R.drawable.menu_icon_delete, R.drawable.menu_icon_logout};
	private final int CODE_ACC_DELETED = 0, CODE_LIST_UPDATED = 1;
	private LinearLayout linearLayoutBackground;
	private TextView textViewEmpty;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mylistslayout);
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
	    linearLayoutBackground = (LinearLayout) findViewById(R.id.linerLayoutBackground);
	    textViewEmpty = (TextView) findViewById(R.id.textViewEmpty);	    
		TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);		
		textViewTitle.setText(getString(R.string.mylists));
		setTypeface("deluxe", textViewTitle, textViewEmpty);
		
		//Button für neue Liste
		((ImageButton) findViewById(R.id.buttonNewList)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MyListsActivity.this, CreateNewListActivity.class));
			}
		});
		
		//Refresh Button mit Animation
		logoView = (LogoView) findViewById(R.id.logoView);
		
		logoView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateLists();
			}
		});
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navList);

        MenuListAdapter mMenuAdapter = new MenuListAdapter(this, drawerTitles, drawerIcons);
        mDrawerList.setAdapter(mMenuAdapter);
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(isLoading){
					return;
				}
				switch(position){
				case 0:		//Favorite Items
					startActivity(new Intent(MyListsActivity.this, ShowFavoriteItemsActivity.class));
					break;
				case 1:		//Change password
					startActivity(new Intent(MyListsActivity.this, ChangePasswordActivity.class));
					break;
				case 2:	//Delete Account
					startActivityForResult(new Intent(MyListsActivity.this, DeleteAccountActivity.class), CODE_ACC_DELETED);
					break;				
				case 3:	//Logout
					logout();
					break;
				}
				mDrawerLayout.closeDrawer(Gravity.LEFT);	
			}
		});
        
        ImageButton imageButtonMenu = (ImageButton) findViewById(R.id.imageButtonMenu);
        
        imageButtonMenu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDrawerLayout.openDrawer(Gravity.LEFT);				
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
	
	/**
	 * Beim Start alle Listen des Users laden
	 */
	@Override
	protected void onStart() {
		updateLists();
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
		super.onStop();
	}
	
	private void updateLists(){
		if(!isLoading){
			logoView.startDrawing();
			if((LoginActivity.NAME.length() == 0 || LoginActivity.NAME == null) && getPreferences(MODE_PRIVATE).contains("name")){
				LoginActivity.NAME = getPreferences(MODE_PRIVATE).getString("name", "");
			}
			if(LoginActivity.NAME.length() == 0){
				logout();
				return;
			}
			new LoadDataTask(new String[]{"name"},new String[]{LoginActivity.NAME}, "loadlists.php").execute();
		}
	}
	
	@Override
	protected void preExcecute() {
		isLoading = true;
	}
	
	private void logout(){
		Intent startLoginActivity = new Intent(MyListsActivity.this, LoginActivity.class);
		startLoginActivity.putExtra("logout", 1);
		startActivity(startLoginActivity);
		finish();
	}
	
	private void updateVisibility(){
		if(myLists.size() == 0){
			textViewEmpty.setVisibility(View.VISIBLE);
			linearLayoutBackground.setVisibility(View.INVISIBLE);
		}else{
			textViewEmpty.setVisibility(View.INVISIBLE);
			linearLayoutBackground.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * Alle geladenen Listen auslesen und anzeigen
	 */
	@Override
	protected void postExcecute(JSONObject json) {		
		try {
			ArrayList<ShoppingList> updatedList = new ArrayList<ShoppingList>();
			
			try {
				JSONArray dataArray = json.getJSONArray("data");			
				for (int i = 0; i < dataArray.length(); i++) {
					updatedList.add((ShoppingList) objectFromString(dataArray.getString(i)));
					updatedList.get(i).setDescription(updatedList.get(i).getItems().size() + " Items");
				}
				
				JSONArray dataArrayInvitations = json.getJSONArray("datainvitations");				
				for (int i = 0; i < dataArrayInvitations.length(); i++) {
					updatedList.add((ShoppingList) objectFromString(dataArrayInvitations.getString(i)));
					updatedList.get(updatedList.size()-1).setDescription("Invitation");
				}
			} catch (JSONException e) {	//tritt auf wenn keine Listen vorhanden
				e.printStackTrace();
			}
			
			myLists.clear();
			myLists.addAll(updatedList);
			
			if(listAdapter == null){
				//Liste mit allen ShoppingLists
				ListView myListsView = (ListView) findViewById(R.id.listView1);
				myListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	
					@Override
					public void onItemClick(AdapterView<?> parent, final View view,	int position, long id) {
						if(myLists.get(position).getDescription().equals("Invitation")){
							Intent intent = new Intent(MyListsActivity.this, AnswerInvitationActivity.class);
							intent.putExtra("id", ((ShoppingList) myLists.get(position)).getID());
							startActivityForResult(intent, CODE_LIST_UPDATED);
							return;
						}
						Intent intent = new Intent(MyListsActivity.this, ListActivity.class);
						intent.putExtra("id", ((ShoppingList) myLists.get(position)).getID());
						startActivity(intent);
					}
	
				});
				listAdapter = new MyListsAdapter(this, myLists);
				myListsView.setAdapter(listAdapter);
			}else{
				listAdapter.updateListObjects(myLists);
			}
			
			updateVisibility();			
			isLoading = false;
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int arg1, Intent data) {
		//Wird nur aufgerufen wenn Account gelöscht
		if(requestCode == CODE_ACC_DELETED && data != null && data.getBooleanExtra("accdeleted", false)){
			logout();
			return;
		}
		if(requestCode == CODE_LIST_UPDATED && data != null && data.getBooleanExtra("listupdated", false)){
			updateLists();
			return;
		}
		super.onActivityResult(requestCode, arg1, data);
	}
	
}
