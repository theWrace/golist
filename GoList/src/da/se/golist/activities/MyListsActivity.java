package da.se.golist.activities;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.adapters.MenuListAdapter;
import da.se.golist.adapters.MyListsAdapter;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.LogoView;
import da.se.golist.objects.ShoppingList;
import da.se.golist.objects.User;

public class MyListsActivity extends BaseActivity{
	
	private ArrayList<GoListObject> myLists = new ArrayList<GoListObject>();
	private boolean isLoading = false, deleteacc = false, invitationanswered = false;
	private MyListsAdapter listAdapter;	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private String[] drawerTitles = {"Settings", "Favorite Items", "Delete Account", "Logout"};
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
				if(myLists.get(position).getDescription().equals("Invitation")){
					AlertDialog.Builder alert = new AlertDialog.Builder(MyListsActivity.this);

					alert.setTitle("Invitation");
					alert.setMessage("Do you want to join this list?");
					
					final ShoppingList list = (ShoppingList) myLists.get(position);

					alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int whichButton) {							
							for(int i = 0; i < list.getInvitedUser().size(); i++){
								if(list.getInvitedUser().get(i).getName().equals(LoginActivity.NAME)){
									list.getInvitedUser().remove(i);
									list.addUser(new User(LoginActivity.NAME));
									break;
								}
							}
							invitationanswered = true;
							String infoText = getString(R.string.infoinvitationaccepted);
							infoText = infoText.replace("username", LoginActivity.NAME);
							uploadList(list, true, infoText);			
						}
					});

					alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int whichButton) {
							for(int i = 0; i < list.getInvitedUser().size(); i++){
								if(list.getInvitedUser().get(i).getName().equals(LoginActivity.NAME)){
									list.getInvitedUser().remove(i);
									break;
								}
							}
							invitationanswered = true;
							String infoText = getString(R.string.infoinvitationnotaccepted);
							infoText = infoText.replace("username", LoginActivity.NAME);
							uploadList(list, true, infoText);
						}
					});
					
					alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int whichButton) {
							dialog.dismiss();
						}
					});

					alert.show();
					return;
				}
				Intent intent = new Intent(MyListsActivity.this, ListActivity.class);
				intent.putExtra("id", ((ShoppingList) myLists.get(position)).getID());
				startActivity(intent);
			}

		});
		listAdapter = new MyListsAdapter(this, myLists);
		myListsView.setAdapter(listAdapter);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navList);

        drawerIcons = new int[] {R.drawable.menu_icon_settings, R.drawable.menu_icon_favorite, R.drawable.menu_icon_delete, R.drawable.menu_icon_logout};

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
					startActivity(new Intent(MyListsActivity.this, ShowFavoriteItemsActivity.class));
					break;
				case 2:
					if(isLoading){
						break;
					}
					AlertDialog.Builder alert = new AlertDialog.Builder(MyListsActivity.this);

					alert.setTitle("Enter your password to delete Account!");

					final EditText input = new EditText(MyListsActivity.this);
					alert.setView(input);

					alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					  if(input.getText().toString().length() != 0){
						  deleteacc = true;
						  new LoadDataTask(new String[]{"name", "password"},new String[]{LoginActivity.NAME, input.getText().toString()}, "deleteuser.php").execute();
						  return;
					  }
					  Toast.makeText(getApplicationContext(), "Please enter your password!", Toast.LENGTH_SHORT).show();
					  }
					});

					alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					  public void onClick(DialogInterface dialog, int whichButton) {}
					});

					alert.show();
					break;				
				case 3:
					logout();
					break;
				}
				mDrawerLayout.closeDrawer(Gravity.LEFT);	
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
	
	/**
	 * Alle geladenen Listen auslesen und anzeigen
	 */
	@Override
	protected void postExcecute(JSONObject json) {
		if(invitationanswered){
			invitationanswered = false;
			isLoading = false;
			updateLists();
			return;
		}
		if(deleteacc){
			String message = "Failed to delete Account!";
			try {
				message = json.getString("message");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			deleteacc = false;
			if(message.equals("succes")){
				Toast.makeText(getApplicationContext(), "Account deleted!", Toast.LENGTH_SHORT).show();
				logout();
				return;
			}
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
			return;
		}
		
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
			
			listAdapter.notifyDataSetChanged();
			isLoading = false;
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
