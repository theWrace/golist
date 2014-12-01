package da.se.golist.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import da.se.golist.R;
import da.se.golist.adapters.UserListAdapter;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.User;

public class InviteFriendsActivity extends DataLoader{ 
	
	private ArrayList<GoListObject> user = new ArrayList<GoListObject>();
	private UserListAdapter listAdapter;
	private boolean isLoading = false;
	private EditText searchText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.invitefriendslayout);
		
		searchText = (EditText) findViewById(R.id.editTextSearch);
		
		ListView myListsView = (ListView) findViewById(R.id.listView1);
		myListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,	int position, long id) {
				Intent returnIntent = new Intent();
				returnIntent.putExtra("user",user.get(position).getName());
				setResult(RESULT_OK,returnIntent);
				finish();
			}

		});
		listAdapter = new UserListAdapter(this, user);
		myListsView.setAdapter(listAdapter);
		
		searchText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
				// TODO Auto-generated method stub
				
			}
			
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
			System.out.println(searchString);
		}
	}
	
	@Override
	protected void preExcecute() {
		isLoading = true;
	}
	
	@Override
	protected void postExcecute(JSONObject json) {
		JSONArray userArray;
		try {
			userArray = json.getJSONArray("users");
			
			user.clear();
			for (int i = 0; i < userArray.length(); i++) {
				boolean contains = false;
				for(String name : InviteFriendsActivity.this.getIntent().getExtras().getStringArray("names")){
					if(name.equalsIgnoreCase(userArray.getString(i))){
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
