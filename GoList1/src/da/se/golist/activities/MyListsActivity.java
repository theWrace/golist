package da.se.golist.activities;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import da.se.golist.R;
import da.se.golist.adapters.MenuListAdapter;
import da.se.golist.adapters.MyListsAdapter;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.LogoView;
import da.se.golist.objects.ShoppingList;

public class MyListsActivity extends BaseActivity{
	
	private ArrayList<GoListObject> myLists = new ArrayList<GoListObject>();
	private boolean isLoading = false;
	private MyListsAdapter listAdapter;
	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private String[] drawerTitles;
	private int[] drawerIcons;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mylistslayout);
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/deluxe.ttf");
		TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
		textViewTitle.setTypeface(tf);
		textViewTitle.setText("My Lists");
		
		//Button für neue Liste
		((ImageButton) findViewById(R.id.buttonNewList)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent newListIntent = new Intent(MyListsActivity.this, CreateNewListActivity.class);
				startActivity(newListIntent);
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
		
		
		//Liste mit allen ShoppingLists
		ListView myListsView = (ListView) findViewById(R.id.listView1);
		myListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,	int position, long id) {
				Intent intent = new Intent(MyListsActivity.this, ListActivity.class);
				intent.putExtra("id", ((ShoppingList) myLists.get(position)).getID());
				startActivity(intent);
			}

		});
		listAdapter = new MyListsAdapter(this, myLists);
		myListsView.setAdapter(listAdapter);
		
		
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navList);

        drawerTitles = getResources().getStringArray(R.array.drawerTitles_array);
        drawerIcons = new int[] {R.drawable.logo5, R.drawable.logo5, R.drawable.logo5};

        MenuListAdapter mMenuAdapter = new MenuListAdapter(this, drawerTitles, drawerIcons);
        mDrawerList.setAdapter(mMenuAdapter);
        mDrawerList.setDivider(null);
        mDrawerList.setDividerHeight(50);
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mDrawerLayout.closeDrawer(mDrawerList);
				switch(position){
				case 0:
					//Settings
					break;
				case 1:
					//Favorite Items
					break;
				case 2:
					logout();
					break;
				}
			}
		});
        
        textViewTitle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDrawerLayout.openDrawer(Gravity.LEFT);				
			}
		});
		
	}
	
	/**
	 * Beim Start alle Listen des Users laden
	 */
	@Override
	protected void onStart() {
		updateLists();
		super.onStart();
	}
	
	private void updateLists(){
		if(!isLoading){
			logoView.startDrawing();
			if(LoginActivity.NAME.length() == 0 && getPreferences(MODE_PRIVATE).contains("name")){
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
			} catch (JSONException e) {	//tritt auf wenn keine Listen vorhanden
				e.printStackTrace();
			}
			
			myLists.clear();
			myLists.addAll(updatedList);
			
			listAdapter.notifyDataSetChanged();
			isLoading = false;
			
			//Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_LONG).show();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
