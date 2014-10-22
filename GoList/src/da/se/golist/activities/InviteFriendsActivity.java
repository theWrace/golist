package da.se.golist.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import da.se.golist.R;
import da.se.golist.adapters.UserListAdapter;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.User;

public class InviteFriendsActivity extends DataLoader{
	
	private ArrayList<GoListObject> user = new ArrayList<GoListObject>();
	private UserListAdapter listAdapter;
	private boolean isLoading = false;
	private ScaleAnimation blinkanimation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.invitefriendslayout);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBarInviteFriends);
		
		blinkanimation= new ScaleAnimation(progressBar.getScaleX(), progressBar.getScaleX()/2, progressBar.getScaleY(), progressBar.getScaleY()/2, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); // Change alpha from fully visible to invisible
		blinkanimation.setDuration(150); // duration - half a second
		blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
		blinkanimation.setRepeatCount(1); // Repeat animation infinitely
		blinkanimation.setRepeatMode(Animation.REVERSE);
		
		
		progressBar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				update();
			}
		});
		
		
		//set up animation
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
		
		update();
	}
	
	private void update(){
		if(!isLoading){
			progressBar.startAnimation(blinkanimation);
			new LoadDataTask(new String[]{},new String[]{}, "loadusers.php").execute();			
		}
	}
	
	@Override
	protected void preExcecute() {
		isLoading = true;
		progressBar.setIndeterminate(true);
	}
	
	@Override
	protected void postExcecute(JSONObject json) {
		JSONArray userArray;
		try {
			userArray = json.getJSONArray("users");
		
			System.out.println(userArray.length()+"");
			
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
		
		progressBar.setIndeterminate(false);
		listAdapter.notifyDataSetChanged();
		isLoading = false;
	}

}
