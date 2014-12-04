package da.se.golist.activities;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.adapters.ItemListAdapter;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.ShoppingList;
import da.se.golist.objects.User;

public class ListActivity extends DataLoader{
	
	private ShoppingList list = null;
	private int id;
	private ItemListAdapter listAdapter = null;
	private ListView articleListView;
	private TextView textViewList;
	private Button menuButton;
	private PopupMenu popup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listlayout);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBarList);
		progressBar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				update();
			}
		});
		articleListView = (ListView) findViewById(R.id.listViewArticles);
		menuButton = (Button) findViewById(R.id.buttonMenu);
		
		articleListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(ListActivity.this, EditItemActivity.class);
				intent.putExtra("list", list);
				intent.putExtra("itemid", position);
				startActivity(intent);		
			}
		});
		
		menuButton.setEnabled(false);
		
		menuButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popup.show();
				
			}
		});
	}
	
	@Override
	protected void onStart() {
		update();
		super.onStart();
	}
	
	private void update(){
		id = getIntent().getExtras().getInt("id");
		new LoadDataTask(new String[]{"id"},new String[]{id+""}, "loadlistbyid.php").execute();
	}
	
	@Override
	protected void preExcecute() {
		progressBar.setClickable(false);
		progressBar.setIndeterminate(true);
		menuButton.setEnabled(false);
	}
		
	@Override
	protected void postExcecute(JSONObject json) {
		String message = "Loading failed!";
		try {
			message = json.getString("message");
			if(message.equals("succes")){
				JSONArray dataArray = json.getJSONArray("data");
				list = (ShoppingList) listFromString(dataArray.getString(0));
			}
		} catch (JSONException e) {			
			e.printStackTrace();
			
			//no value für data -> liste gelöscht -> activity beenden
			this.finish();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		progressBar.setIndeterminate(false);		       
        
		if(message.equals("succes")){
			popup = new PopupMenu(ListActivity.this, menuButton); 
			
			if(listAdapter == null){
				listAdapter = new ItemListAdapter(ListActivity.this, list.getItems());
				articleListView.setAdapter(listAdapter);
				textViewList = (TextView) findViewById(R.id.textViewList);
			}else{
				listAdapter.updateListObjects(list.getItems());
			}
			
			textViewList.setText(list.getName());
			if(list.getUser().get(0).getName().equalsIgnoreCase(LoginActivity.NAME)){
				popup.getMenuInflater().inflate(R.menu.list_popup_admin, popup.getMenu());  
			}else{
				popup.getMenuInflater().inflate(R.menu.list_popup_normal, popup.getMenu()); 
			}			
		
			popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					switch(item.getItemId()){
					case R.id.additem:
						Intent intent = new Intent(ListActivity.this, CreateNewItemActivity.class);
						intent.putExtra("list", list);
						startActivity(intent);
						break;
					case R.id.adduser:
						Intent newListIntent = new Intent(ListActivity.this, InviteFriendsActivity.class);
						String[] names = new String[list.getInvitedUser().size() + list.getUser().size()];
						int index = 0;
						for(GoListObject user : list.getInvitedUser()){
							names[index] = user.getName();
							index++;
						}
						for(GoListObject user : list.getUser()){
							names[index] = user.getName();
							index++;
						}
						newListIntent.putExtra("names", names);
						startActivityForResult(newListIntent, 1);
						break;
					case R.id.edit:
						Intent intent2 = new Intent(ListActivity.this, EditListActivity.class);
						intent2.putExtra("list", list);
						startActivityForResult(intent2, 2);
						break;
					case R.id.showmembers:
						Intent intent3 = new Intent(ListActivity.this, ShowUsersActivity.class);
						intent3.putExtra("invitedusers", list.getInvitedUser());
						intent3.putExtra("members", list.getUser());
						startActivityForResult(intent3, 3);
						break;
					}
					return true;
				}
			});
			menuButton.setEnabled(true);
		}
		
		progressBar.setClickable(true);	
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	    switch(requestCode) {
	    case 1:
	        if(resultCode == RESULT_OK){
	        	//ausgewählten User von InviteFriendsActivity zurueckbekommen
	        	list.addUser(new User(data.getStringExtra("user")));
	        	
	        	uploadList();
	        }
	        if (resultCode == RESULT_CANCELED) {
	            //TODO: einladen abgebrochen
	        }
	        break;
	    case 2:
	    	//Liste gelöscht
	    	if(resultCode == RESULT_OK){
	    		Toast.makeText(getApplicationContext(), "List deleted!", Toast.LENGTH_LONG).show();
	    		this.finish();
	    	}
	    	break;
	    case 3:
	    	//User gelöscht
	    	if(resultCode == RESULT_OK){
	    		if(data.getExtras().containsKey("members")){
	    			list.updateInvitedUser((ArrayList<GoListObject>) data.getExtras().get("invitedusers"));
	    			list.updateUser((ArrayList<GoListObject>) data.getExtras().get("members"));
	    			uploadList();
	    		}
	    	}
	    	break;
	    }
	}
	
	private void uploadList(){
		//liste mit neuem user hochladen
    	String inviteduser = "", userString = "";
		for(GoListObject user : list.getUser()){
			userString = userString + user.getName() + ";";
		}
		for(GoListObject user : list.getInvitedUser()){
			inviteduser = inviteduser + user.getName() + ";";
		}
		userString = userString.substring(0, userString.length()-1);
		if(inviteduser.length() != 0){
			inviteduser = inviteduser.substring(0, inviteduser.length()-1);
		}
		try {
			new LoadDataTask(new String[]{"id", "data", "user", "inviteduser"},new String[]{list.getID()+"", listToString(list), userString, inviteduser}, "updatelist.php").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
