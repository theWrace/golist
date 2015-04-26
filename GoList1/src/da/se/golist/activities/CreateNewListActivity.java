package da.se.golist.activities;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.adapters.UserListAdapter;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.LogoView;
import da.se.golist.objects.ShoppingList;
import da.se.golist.objects.User;

public class CreateNewListActivity extends BaseActivity{
	  
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
		
		Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/deluxe.ttf");
		TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
		textViewTitle.setTypeface(tf);
		textViewTitle.setText("New List");
		
		Button addUserButton = (Button) findViewById(R.id.buttonAddUser);
		
		addUserButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent newListIntent = new Intent(CreateNewListActivity.this, InviteUserActivity.class);
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
		
		ListView myListsView = (ListView) findViewById(R.id.listViewPeople);
		myListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
						//TODO: user anzeigen
					}

				});
		userOfList.add(new User(LoginActivity.NAME));
		listAdapter = new UserListAdapter(this, userOfList);
		myListsView.setAdapter(listAdapter);
		 
		logoView = (LogoView) findViewById(R.id.logoView);
		
		final EditText editTextName = (EditText) findViewById(R.id.editTextName);
		Typeface tf1 = Typeface.createFromAsset(this.getAssets(), "fonts/geosanslight.ttf");
		editTextName.setTypeface(tf1);
		
		buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(editTextName.getText().toString().length() > 3){
					list = new ShoppingList(editTextName.getText().toString(), LoginActivity.NAME, "");
					list.addUser(new User(LoginActivity.NAME));
					for(int i = 1; i < userOfList.size(); i++){
						list.addUser((User)userOfList.get(i));	//TODO: zu invite ändern wenn annehmen funktioniert
					}
					firstTaskExcecution = true;
					try {
						new LoadDataTask(new String[]{"name", "data", "user", "inviteduser"},new String[]{list.getName()+"", objectToString(list), "", ""}, "savelist.php").execute();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}else{
					Toast.makeText(getApplicationContext(), "Please enter a longer name!", Toast.LENGTH_LONG).show();
				}
				
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	    if (requestCode == 1) {
	        if(resultCode == RESULT_OK){
	        	//ausgewählten User von InviteFriendsActivity zurueckbekommen
	        	userOfList.add(new User(data.getStringExtra("user")));
	        	listAdapter.notifyDataSetChanged();
	        }
	        if (resultCode == RESULT_CANCELED) {
	            //TODO: liste erstellen abgebrochen
	        }
	    }
	}
	
	@Override
	protected void preExcecute() {
		buttonSave.setEnabled(false);
	}
	
	@Override
	protected void postExcecute(JSONObject json) {
		if(firstTaskExcecution){
			firstTaskExcecution = false;
			try {
				list.setID(Integer.parseInt(json.getString("message")));
				uploadList(list, true);
			} catch (NumberFormatException | JSONException e) {
				e.printStackTrace();
			}			
		}else{
			try {
				String message = json.getString("message");
			
				if(message.equals("successful")){
					Toast.makeText(getApplicationContext(), list.getName() + " created!", Toast.LENGTH_LONG).show();
					finish();
				}else{
					buttonSave.setEnabled(true);
					Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
