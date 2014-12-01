package da.se.golist.activities;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.adapters.UserListAdapter;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.ShoppingList;
import da.se.golist.objects.User;

public class CreateNewListActivity extends DataLoader{
	  
	private ArrayList<GoListObject> userOfList = new ArrayList<GoListObject>();
	private UserListAdapter listAdapter;
	private Button buttonSave;
	private ShoppingList list;
	private boolean firstTaskExcecution = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.createnewlistlayout);
		
		Button addUserButton = (Button) findViewById(R.id.buttonAddUser);
		
		addUserButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent newListIntent = new Intent(CreateNewListActivity.this, InviteFriendsActivity.class);
				String[] names = new String[userOfList.size()];
				int index = 0;
				for(GoListObject user : userOfList){
					names[index] = user.getName();
					index++;
				}
				newListIntent.putExtra("names", names);
				startActivityForResult(newListIntent, 1);			
			}
		});
		
		// set up animation
		ListView myListsView = (ListView) findViewById(R.id.listViewPeople);
		myListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
						//user anzeigen
					}

				});
		userOfList.add(new User(LoginActivity.NAME));
		listAdapter = new UserListAdapter(this, userOfList);
		myListsView.setAdapter(listAdapter);
		  
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		
		final EditText editTextName = (EditText) findViewById(R.id.editTextName);
		
		buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(editTextName.getText().toString().length() > 3){
					list = new ShoppingList(editTextName.getText().toString());
					list.addUser(new User(LoginActivity.NAME));
					for(int i = 1; i < userOfList.size(); i++){
						list.inviteUser((User)userOfList.get(i));
					}
					firstTaskExcecution = true;
					String inviteduser = "", userString = "";
					for(GoListObject user : list.getPeople()){
						userString = userString + user.getName() + ";";
					}
					for(GoListObject user : list.getInvitedPeople()){
						inviteduser = inviteduser + user.getName() + ";";
					}
					userString = userString.substring(0, userString.length()-1);
					if(inviteduser.length() != 0){
						inviteduser = inviteduser.substring(0, userString.length()-1);
					}
					try {
						new LoadDataTask(new String[]{"name", "data", "user", "inviteduser"},new String[]{list.getName()+"", listToString(list), userString, inviteduser}, "savelist.php").execute();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}else{
					Toast.makeText(getApplicationContext(), "Please enter a longer name!", Toast.LENGTH_LONG).show();
				}
				
			}
		});
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	    if (requestCode == 1) {
	        if(resultCode == RESULT_OK){
	        	userOfList.add(new User(data.getStringExtra("user")));
	        	listAdapter.notifyDataSetChanged();
	        }
	        if (resultCode == RESULT_CANCELED) {
	            //TODO
	        }
	    }
	}
	
	@Override
	protected void preExcecute() {
		progressBar.setVisibility(ProgressBar.VISIBLE);
		buttonSave.setVisibility(Button.INVISIBLE);
	}
	
	@Override
	protected void postExcecute(JSONObject json) {
		if(firstTaskExcecution){
			try {
				firstTaskExcecution = false;
				String inviteduser = "", userString = "";
				for(GoListObject user : list.getPeople()){
					userString = userString + user.getName() + ";";
				}
				for(GoListObject user : list.getInvitedPeople()){
					inviteduser = inviteduser + user.getName() + ";";
				}
				userString = userString.substring(0, userString.length()-1);
				if(inviteduser.length() != 0){
					inviteduser = inviteduser.substring(0, userString.length()-1);
				}
					String message = json.getString("message");
				
				list.setID(Integer.parseInt(message));
				new LoadDataTask(new String[]{"id", "data", "user", "inviteduser"},new String[]{list.getID()+"", listToString(list), userString, inviteduser}, "updatelist.php").execute();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}else{
			try {
				String message = json.getString("message");
			
				if(message.equals("successful")){
					Toast.makeText(getApplicationContext(), list.getName() + " created!", Toast.LENGTH_LONG).show();
					finish();
				}else{
					progressBar.setVisibility(ProgressBar.GONE);
					buttonSave.setVisibility(Button.VISIBLE);
					Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
