package da.se.golist.activities;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import da.se.application.GoListApplication;
import da.se.golist.R;
import da.se.golist.adapters.UserListAdapter;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.ShoppingList;
import da.se.golist.objects.User;
import da.se.otherclasses.LogoView;

public class CreateNewListActivity extends BaseActivity{
	  
	private ArrayList<GoListObject> userOfList = new ArrayList<GoListObject>();
	private UserListAdapter listAdapter;
	private Button buttonSave, buttonAddUser;
	private ShoppingList list;
	private boolean firstTaskExcecution = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.createnewlistlayout);
				
		TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
		TextView textViewUser = (TextView) findViewById(R.id.textViewUser);
		final EditText editTextName = (EditText) findViewById(R.id.editTextName);
		buttonSave = (Button) findViewById(R.id.buttonSave);
		logoView = (LogoView) findViewById(R.id.logoView);
		ListView myListsView = (ListView) findViewById(R.id.listViewPeople);
		buttonAddUser = (Button) findViewById(R.id.buttonAddUser);
		
		setTypeface("deluxe", textViewTitle, textViewUser);
		setTypeface("geosanslight", editTextName);
		textViewTitle.setText(getString(R.string.newlist));		
		
		buttonAddUser.setOnClickListener(new OnClickListener() {
			
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

		userOfList.add(new User(LoginActivity.NAME));
		listAdapter = new UserListAdapter(this, userOfList);
		myListsView.setAdapter(listAdapter);
		
		buttonSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String name = editTextName.getText().toString().trim();
				if(name.length() != 0){					
					list = new ShoppingList(name, LoginActivity.NAME, "");
					list.addUser(new User(LoginActivity.NAME));
					for(int i = 1; i < userOfList.size(); i++){
						list.inviteUser((User)userOfList.get(i));
					}
					firstTaskExcecution = true;
					
					String infoText = getString(R.string.infolistcreated).replace("listname", name);
					infoText += "::" + new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US).format(new Date());
					infoText = infoText.replace("username", LoginActivity.NAME);
					
					try {
						new LoadDataTask(new String[]{"name", "data", "user", "inviteduser", "history"},new String[]{list.getName()+"", objectToString(list), "", "", infoText}, "savelist.php").execute();
					} catch (IOException e) {
						e.printStackTrace();
					}					
				}else{
					Toast.makeText(getApplicationContext(), getString(R.string.insertname), Toast.LENGTH_LONG).show();
				}				
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == 1 && resultCode == RESULT_OK) {
	        //ausgewählten User von InviteFriendsActivity zurueckbekommen
	        userOfList.add(new User(data.getStringExtra("user")));
	        listAdapter.notifyDataSetChanged();
	    }
	}
	
	@Override
	protected void preExcecute() {
		updateViews(false, buttonSave, buttonAddUser);
	}
	
	@Override
	protected void postExcecute(JSONObject json) {
		if(firstTaskExcecution){
			firstTaskExcecution = false;
			try {
				list.setID(Integer.parseInt(getMessageFromJson(json)));
				String users = "";
				for(GoListObject user : list.getUser()){
					users += user + ", ";
				}
				String infoText = "";
				if(users.length() != 0){
					infoText = getString(R.string.infomultiuserinvited);
					infoText = infoText.replace("username", LoginActivity.NAME);
					infoText = infoText.replace("users", users);
					infoText = infoText.replace("listname", list.getName());
				}
				
				uploadList(list, true, infoText);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}else{		
			if(getMessageFromJson(json).equals("successful")){
				Tracker t = ((GoListApplication)getApplication()).getTracker();
				t.send(new HitBuilders.EventBuilder()
			    .setCategory("Liste")
			    .setAction("erstellt")
			    .setLabel("Name: " + list.getName()).build());
					
				Toast.makeText(getApplicationContext(), list.getName() + " " + getString(R.string.created), Toast.LENGTH_SHORT).show();
				finish();
			}else{
				Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
				updateViews(true, buttonSave, buttonAddUser);
			}
		}		
	}

}
