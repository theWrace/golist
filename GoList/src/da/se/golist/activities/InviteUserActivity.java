package da.se.golist.activities;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.adapters.UserListAdapter;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.ShoppingList;
import da.se.golist.objects.User;

public class InviteUserActivity extends BaseActivity{
	
	private ArrayList<GoListObject> user = new ArrayList<GoListObject>();
	private ArrayList<GoListObject> userInList = new ArrayList<GoListObject>();
	private UserListAdapter listAdapter;
	private boolean isLoading = false, itemclicked = false;
	private ShoppingList list;
	private EditText searchText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.inviteuserlayout);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		
		Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/deluxe.ttf");
		TextView textViewTitleList = (TextView) findViewById(R.id.textViewTitle);
		textViewTitleList.setTypeface(tf);
		textViewTitleList.setText("Invite User");
		
		list = (ShoppingList) getIntent().getExtras().get("list");
		
		if(list != null){
			userInList.addAll(list.getInvitedUser());
			userInList.addAll(list.getUser());		
		}
		
		Typeface tf1 = Typeface.createFromAsset(this.getAssets(), "fonts/geosanslight.ttf");
		searchText = (EditText) findViewById(R.id.editTextSearch);
		searchText.setTypeface(tf1);
		
		ListView myListsView = (ListView) findViewById(R.id.listView1);
		myListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,	final int position, long id) {
				if(list == null){	//Liste wird gerade erstellt und existiert noch nicht
					Intent returnIntent = new Intent();
					returnIntent.putExtra("user",user.get(position).getName());
					setResult(RESULT_OK,returnIntent);
					finish();
				}else{
					refreshList(new AfterRefresh() {
						
						@Override
						public void applyChanges() {
							list.inviteUser(new User(user.get(position).getName()));
							itemclicked = true;
							String infoText = getString(R.string.infouserinvited).replace("username1", LoginActivity.NAME);
							infoText = infoText.replace("username2", user.get(position).getName());
							infoText = infoText.replace("listname", list.getName());
							uploadList(list, true, infoText);
							Toast.makeText(getApplicationContext(), user.get(position).getName() + " invited!", Toast.LENGTH_SHORT).show();
						}
					}, list.getID());
							
				}
			}

		});
		listAdapter = new UserListAdapter(this, user);
		myListsView.setAdapter(listAdapter);
		
		searchText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(searchText.getText().length() > 2){
					loadUsers(searchText.getText().toString());
				}				
			}
		});
	}
	
	private void loadUsers(String searchString){
		if(!isLoading){
			new LoadDataTask(new String[]{"searchstring"},new String[]{searchString}, "loadusers.php").execute();
		}
	}
	
	@Override
	protected void preExcecute() {
		isLoading = true;
	}
	
	@Override
	protected void postExcecute(JSONObject json) {
		if(itemclicked){
			try {
				if(json.getString("message").equals("successful")){
					Toast.makeText(getApplicationContext(), "List updated!", Toast.LENGTH_LONG).show();		
				}else{
					Toast.makeText(getApplicationContext(), "Failed to update List: " + json.getString("message"), Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				Toast.makeText(getApplicationContext(), "Failed to update List: " + e.getMessage(), Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			finish();
			return;
		}
		
		if(!json.has("users")){
			try {
				String message = json.getString("message");
				if(message.equals("succes") && json.has("data")){
					JSONArray dataArray = json.getJSONArray("data");
					list = (ShoppingList) objectFromString(dataArray.getString(0));
				}
			} catch (JSONException e) {			
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(afterRefresh != null){
				afterRefresh.applyChanges();
				afterRefresh = null;
			}
			return;
		}
		
		JSONArray userArray;
		try {
			userArray = json.getJSONArray("users");
			
			user.clear();
			for (int i = 0; i < userArray.length(); i++) {
				boolean contains = false;
				for(GoListObject user : userInList){
					if(user.getName().equalsIgnoreCase(userArray.getString(i))){
						contains = true;
						break;
					}
				}
				if(!contains){
					user.add(new User(userArray.getString(i)));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		listAdapter.notifyDataSetChanged();
		isLoading = false;
	}

}
