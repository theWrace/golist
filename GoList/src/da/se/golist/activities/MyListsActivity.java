package da.se.golist.activities;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.adapters.MyListsAdapter;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.ShoppingList;

public class MyListsActivity extends DataLoader{
	
	private ArrayList<GoListObject> myLists = new ArrayList<GoListObject>();
	private boolean isLoading = false;
	private MyListsAdapter listAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mylistslayout);
		
		Button buttonNewList = (Button) findViewById(R.id.buttonNewList);
		buttonNewList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent newListIntent = new Intent(MyListsActivity.this, CreateNewListActivity.class);
				startActivityForResult(newListIntent, 0); //TODO: result abfragen
			}
		});
		
		progressBar = (ProgressBar) findViewById(R.id.progressBarLists);
		progressBar.setIndeterminate(false);
		
		final ScaleAnimation blinkanimation= new ScaleAnimation(progressBar.getScaleX(), progressBar.getScaleX()/2, progressBar.getScaleY(), progressBar.getScaleY()/2, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); // Change alpha from fully visible to invisible
		blinkanimation.setDuration(150); // duration - half a second
		blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
		blinkanimation.setRepeatCount(1); // Repeat animation infinitely
		blinkanimation.setRepeatMode(Animation.REVERSE);
		
		
		progressBar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!isLoading){
					progressBar.startAnimation(blinkanimation);
					new LoadDataTask(new String[]{"name"},new String[]{LoginActivity.NAME}, "loadlists.php").execute();
				}
			}
		});
		
		
		//set up animation        
		ListView myListsView = (ListView) findViewById(R.id.listView1);
		myListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,	int position, long id) {
				Intent intent = new Intent(MyListsActivity.this, ListActivity.class);
				intent.putExtra("id", ((ShoppingList) myLists.get(position)).getID());
				startActivity(intent);
			}

		});
		listAdapter = new MyListsAdapter(this, myLists);
		myListsView.setAdapter(listAdapter);
		
	}
	
	@Override
	protected void onStart() {
		new LoadDataTask(new String[]{"name"},new String[]{LoginActivity.NAME}, "loadlists.php").execute();
		super.onStart();
	}
	 
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.logout:			
			Intent startLoginActivity = new Intent(MyListsActivity.this, LoginActivity.class);
			startLoginActivity.putExtra("logout", 1);
			startActivity(startLoginActivity);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}		
	}
	
	@Override
	protected void preExcecute() {
		isLoading = true;
		progressBar.setIndeterminate(true);
	}
	
	@Override
	protected void postExcecute(JSONObject json) {
		String message = "Loading failed!";
		ArrayList<ShoppingList> updatedList = new ArrayList<ShoppingList>();
		
		try {
			JSONArray dataArray = json.getJSONArray("data");
		
			for (int i = 0; i < dataArray.length(); i++) {
				updatedList.add((ShoppingList) listFromString(dataArray.getString(i)));
			}
			message = json.getString("message");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		myLists.clear();
		myLists.addAll(updatedList);
		listAdapter.notifyDataSetChanged();			
		progressBar.setIndeterminate(false);
		isLoading = false;
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}	

}
