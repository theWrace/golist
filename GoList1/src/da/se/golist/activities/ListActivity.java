package da.se.golist.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.graphics.Typeface;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
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
	private TextView textViewTitleList, textViewEmpty;
	private DrawerLayout mDrawerLayout;
	private ExpandableListView mDrawerList;
	private ExpandableMenuListAdapter mMenuAdapter;
	public final static int CODE_LIST_DELETED = 0, TYPE_DELETE_ALL_ITEMS = 1, 
			TYPE_DELETE_BOUGHT_ITEMS = 2, TYPE_MARK_ALL_BOUGHT = 3, TYPE_MARK_ALL_NOT_BOUGHT = 4, TYPE_LEAVE_LIST = 5;
	private NfcAdapter mNfcAdapter;
	public static final String MIME_TEXT_PLAIN = "text/plain";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listlayout);
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    
	    mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
	    		
		Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/deluxe.ttf");
		textViewEmpty = (TextView)findViewById(R.id.textViewEmpty);
		textViewEmpty.setTypeface(tf);
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
				startActivityForResult(intent, 0);
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
						listAdapter.notifyDataSetChanged();
						//refreshList(null, id);
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
	
	public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
 
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
 
        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};
 
        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }
         
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }
	
	@Override
    protected void onResume() {
        super.onResume();
        if(mNfcAdapter != null){
        	setupForegroundDispatch(this, mNfcAdapter);
        }
    }
     
    @Override
    protected void onPause() {
    	 if(mNfcAdapter != null){
    		 stopForegroundDispatch(this, mNfcAdapter); 
    	 }       
        super.onPause();
    }
    
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }
	
	@Override
	protected void onNewIntent(Intent intent) {
		System.out.println("newintent");
		 String action = intent.getAction();
		    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
		         
		        String type = intent.getType();
		        if (MIME_TEXT_PLAIN.equals(type)) {
		 
		            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		            new NdefReaderTask().execute(tag);
		             
		        } else {
		            Log.d("TAG", "Wrong mime type: " + type);
		        }
		    } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
		         
		        // In case we would still use the Tech Discovered Intent
		        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		        String[] techList = tag.getTechList();
		        String searchedTech = Ndef.class.getName();
		         
		        for (String tech : techList) {
		            if (searchedTech.equals(tech)) {
		                new NdefReaderTask().execute(tag);
		                break;
		            }
		        }
		    }
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
			
			//Button f�r neues Item
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
			
			if(list.getItems().size() == 0){
				textViewEmpty.setVisibility(View.VISIBLE);
			}else{
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
	 * Zeigt Einstellungen f�r Liste an
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
		if(data != null && data.getExtras().containsKey("list")){
			try {
				list = (ShoppingList) objectFromString(data.getStringExtra("list"));
				listAdapter.notifyDataSetChanged();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			return;
		}
		//Activity beenden falls liste gel�scht wurde
		if(requestCode == CODE_LIST_DELETED && data != null && data.getExtras().containsKey("listdeleted")){
			finish();
		}else{
			//Nach Show oder Invite User liste aktualisieren
			refreshList(null, id);
		}
	}
	
	private class NdefReaderTask extends AsyncTask<Tag, Void, String> {
		 
	    @Override
	    protected String doInBackground(Tag... params) {
	        Tag tag = params[0];
	         
	        Ndef ndef = Ndef.get(tag);
	        if (ndef == null) {
	            // NDEF is not supported by this Tag. 
	            return null;
	        }
	 
	        NdefMessage ndefMessage = ndef.getCachedNdefMessage();
	 
	        NdefRecord[] records = ndefMessage.getRecords();
	        for (NdefRecord ndefRecord : records) {
	            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
	                try {
	                    return readText(ndefRecord);
	                } catch (UnsupportedEncodingException e) {
	                    Log.e("TAG", "Unsupported Encoding", e);
	                }
	            }
	        }
	 
	        return null;
	    }
	     
	    private String readText(NdefRecord record) throws UnsupportedEncodingException {	 
	        byte[] payload = record.getPayload();
	        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
	        int languageCodeLength = payload[0] & 0063;
	        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
	    }
	     
	    @Override
	    protected void onPostExecute(String result) {
	        if (result == null || !result.contains(";;")) {	    
	        	return;
	        }
	        
	        String[] resultArray = result.split(";;");
	        if(resultArray.length != 4){
	        	Toast.makeText(getApplicationContext(), "Error: Incorrect Data!", Toast.LENGTH_SHORT).show();
	        	return;
	        }
	        
	        list.addItem(new Item(list.getFreeId(), resultArray[0], resultArray[2], resultArray[3], Integer.parseInt(resultArray[1]), LoginActivity.NAME, new Date()));
	        listAdapter.notifyDataSetChanged();
	        textViewEmpty.setVisibility(View.INVISIBLE);
	        String infoText = getString(R.string.infofromnfc).replace("username", LoginActivity.NAME);
	        infoText = infoText.replace("itemname", resultArray[0]);
	        //List wird nicht aktualisiert, dauert sonst lange
	        uploadList(list, false, infoText);
	    }
	}
}