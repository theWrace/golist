package da.se.golist.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
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
import da.se.golist.objects.JSONParser;
import da.se.golist.objects.User;

public class InviteFriendsActivity extends Activity{
	
	private ProgressBar progressBar;
	private ArrayList<User> user = new ArrayList<User>();
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
				//TODO: user anzeigen
			}

		});
		listAdapter = new UserListAdapter(this, user);
		myListsView.setAdapter(listAdapter);
		
		update();
	}
	
	private void update(){
		if(!isLoading){
			progressBar.startAnimation(blinkanimation);
			new LoadUserTask().execute();
		}
	}
	
	private class LoadUserTask extends AsyncTask<String, String, String> {
		
		public LoadUserTask(){
			isLoading = true;
			progressBar.setIndeterminate(true);
		}

		@Override
		protected String doInBackground(String... args) {			
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				
				JSONParser jsonParser = new JSONParser();
				
				JSONObject json = jsonParser.makeHttpRequest(getString(R.string.url) + "loadusers.php", "POST", params);			
				
				JSONArray userArray = json.getJSONArray("users");
				System.out.println(userArray.length()+"");
				
				user.clear();
				for (int i = 0; i < userArray.length(); i++) {
					user.add(new User(userArray.getString(i)));
				}			
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			return null; 
		}

		@Override
		protected void onPostExecute(String file_url) {
			progressBar.setIndeterminate(false);
			listAdapter.updateMails(user);
			isLoading = false;
		}

	}

}
