package da.se.golist.activities;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.adapters.ExpandableMenuListAdapter;
import da.se.golist.adapters.ItemListAdapter;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.Item;
import da.se.golist.objects.LogoView;
import da.se.golist.objects.ShoppingList;

public class ListActivity extends BaseActivity{
	
	private ShoppingList list = null;
	private int id;
	private ItemListAdapter listAdapter = null;
	private ListView itemListView;
	private TextView textViewTitleList;
	private DrawerLayout mDrawerLayout;
	private ExpandableListView mDrawerList;
	private boolean deleted = false, update = false;
	private ExpandableMenuListAdapter mMenuAdapter;
	
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
				switch(groupPosition){
				case 0:	//List
					switch(childPosition){
					case 0:	//History
						Intent newListIntent = new Intent(ListActivity.this, ShowHistoryActivity.class);
						newListIntent.putExtra("id", ListActivity.this.id);
						startActivity(newListIntent);
						break;
					case 1:	//Change Name/Leave List
						if(!isAdmin(list, LoginActivity.NAME)){
							showAlertDialog(R.string.leavelisttitle, R.string.leavelistquestion, new AfterDialogInterface() {
								
								@Override
								public void applyChanges() {
									refreshList(new AfterRefresh() {

										@Override
										public void applyChanges() {									
											for(int i = 0; i < list.getUser().size(); i++){
												if(list.getUser().get(i).getName().equals(LoginActivity.NAME)){
													list.getUser().remove(i);
													i--;
												}
											}
											for(int i = 0; i < list.getInvitedUser().size(); i++){
												if(list.getInvitedUser().get(i).getName().equals(LoginActivity.NAME)){
													list.getInvitedUser().remove(i);
													i--;
												}
											}
											String infoText = getString(R.string.infolistleft).replace("username", LoginActivity.NAME);
											infoText = infoText.replace("username", LoginActivity.NAME);
											uploadList(list, true, infoText);
											finish();
										}
										
									}, ListActivity.this.id);
								}
							});
							break;
						}
						AlertDialog.Builder alert = new AlertDialog.Builder(ListActivity.this);
			        	alert.setMessage(getString(R.string.pleaseenternewname));
			 
			            final EditText input = new EditText(ListActivity.this);
			            input.setText(list.getName());
			            alert.setView(input);
			 
			        	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			        		public void onClick(DialogInterface dialog, int whichButton) {
				        		if(input.getEditableText().toString().trim().length() != 0 && !input.getEditableText().toString().trim().equals(list.getName())){
				        			
				        			refreshList(new AfterRefresh() {
										
										@Override
										public void applyChanges() {
											final String oldname = list.getName();
											list.setName(input.getEditableText().toString().trim());
											String infoText = getString(R.string.infolistnamechanged).replace("username", LoginActivity.NAME) + "::" 
													+ new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US).format(new Date());
											infoText = infoText.replace("oldname", oldname);
											infoText = infoText.replace("newname", list.getName());
											try {
												new LoadDataTask(new String[]{"id", "data", "name", "history"},new String[]{list.getID()+"", objectToString(list), list.getName(), infoText}, "updatelist.php").execute();
											} catch (IOException e) {
												e.printStackTrace();
											}										
										}
									}, list.getID());			        			
									
								}else{
									Toast.makeText(getApplicationContext(), "You didn't insert a new name!", Toast.LENGTH_LONG).show();
								}		
			        		}
			        	});
			        	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			        	  public void onClick(DialogInterface dialog, int whichButton) {
			        		  dialog.cancel();
			        	  }
			        	});
			        	
			        	AlertDialog alertDialog = alert.create();
			        	alertDialog.show();			
						break;
					default:	//Delete List
						showAlertDialog(R.string.deletelisttitle, R.string.deletelistquestion, new AfterDialogInterface(){

							@Override
							public void applyChanges() {
								deleted = true;
								new LoadDataTask(new String[]{"id"},new String[]{list.getID()+""}, "deletelistbyid.php").execute();	
							}
							
						});
						break;
					}
					break;
				case 1:	//Items
					switch(childPosition){
					case 0:	//Import Favorite Items
						Intent newListIntent = new Intent(ListActivity.this, ImportFavoriteItemsActivity.class);
						newListIntent.putExtra("id", ListActivity.this.id);
						startActivityForResult(newListIntent, 0);
						break;
					case 1:	//Mark Items bought
						showAlertDialog(R.string.markallitemsboughttitle, R.string.markallitemsboughtquestion, new AfterDialogInterface() {
							
							@Override
							public void applyChanges() {
								for(GoListObject item : list.getItems()){
									((Item) item).setState(Item.STATE_BOUGHT);
								}
								String infoText = getString(R.string.infoallitemsbought).replace("username", LoginActivity.NAME);
								uploadList(list, false, infoText);
							}
						});
						break;
					case 2:	//Mark Items not bought
						showAlertDialog(R.string.markallitemsnotboughttitle, R.string.markallitemsnotboughtquestion, new AfterDialogInterface() {
							
							@Override
							public void applyChanges() {
								for(GoListObject item : list.getItems()){
									((Item) item).setState(Item.STATE_NORMAL);
								}
								String infoText = getString(R.string.infoallitemsnotbought).replace("username", LoginActivity.NAME);
								uploadList(list, false, infoText);
							}
						});						
						break;
					case 3:	//Delete bought Items
						showAlertDialog(R.string.deleteboughtitemstitle, R.string.deleteallitemsboughtquestion, new AfterDialogInterface() {
							
							@Override
							public void applyChanges() {
								for(int i = 0; i < list.getItems().size(); i++){
									if(((Item) list.getItems().get(i)).getState() == Item.STATE_BOUGHT){
										list.getItems().remove(i);
										i--;
									}
								}
								String infoText = getString(R.string.infoboughtitemsdeleted).replace("username", LoginActivity.NAME);
								uploadList(list, false, infoText);
							}
						});
						break;
					default:	//Delete all Items
						showAlertDialog(R.string.deleteallitemstitle, R.string.deleteallitemsquestion, new AfterDialogInterface() {
							
							@Override
							public void applyChanges() {
								refreshList(new AfterRefresh() {
									
									@Override
									public void applyChanges() {
										list.getItems().clear();
										String infoText = getString(R.string.infoitemsdeleted).replace("username", LoginActivity.NAME);
										uploadList(list, false, infoText);										
									}
								}, list.getID());								
							}
						});
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
						Intent intent = new Intent(ListActivity.this, ShowUserActivity.class);
						intent.putExtra("id", ListActivity.this.id);
						startActivityForResult(intent, 0);
						break;
					}
					break;
				}
				update = true;
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				return false;
			}
		});
	}
	
	private interface AfterDialogInterface{
		void applyChanges();
	}
	
	private void showAlertDialog(int titleId, int messageId, final AfterDialogInterface afterDialogInterface){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListActivity.this);
		 
		alertDialogBuilder.setTitle(titleId);

		alertDialogBuilder
			.setMessage(messageId)
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,int id) {
					afterDialogInterface.applyChanges();
				}
			  })
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,int id) {								
					dialog.cancel();
				}
			});

		alertDialogBuilder.create().show();		
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
		if(logoView != null){
			logoView.startDrawing();
		}
	}
		
	@Override
	protected void postExcecute(JSONObject json) {
		if(logoView != null){
			logoView.stopDrawing();
		}
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
		
		if(afterRefresh == null){
			//Liste wurde geupdated
			try {
				if(json.getString("message").contains("succes")){
					if(deleted){
						Toast.makeText(getApplicationContext(), "List deleted!", Toast.LENGTH_LONG).show();
						finish();
						return;
					}
				}else{
					Toast.makeText(getApplicationContext(), "Failed to update List: " + json.getString("message"), Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				Toast.makeText(getApplicationContext(), "Failed to update List: " + e.getMessage(), Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}else{
			afterRefresh.applyChanges();
			afterRefresh = null;
		}
		
		if(update){
			update = false;
			refreshList(null, this.id);
		}
		
		deleted = false;		
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
		if(data != null && data.getExtras().containsKey("finish")){
			finish();
		}else{
			//Nach Show oder Invite User liste aktualisieren
			refreshList(null, id);
		}
	}
}
