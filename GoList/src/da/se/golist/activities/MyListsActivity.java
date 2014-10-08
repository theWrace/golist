package da.se.golist.activities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
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
import da.se.golist.R;
import da.se.golist.adapters.MyListsAdapter;
import da.se.golist.objects.ShoppingList;
import da.se.golist.objects.Base64Coder;

public class MyListsActivity extends Activity{
	
	private ProgressBar progressBar;
	private ArrayList<ShoppingList> myLists = new ArrayList<ShoppingList>();
	
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
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar.setIndeterminate(true);
		
		final ScaleAnimation blinkanimation= new ScaleAnimation(progressBar.getScaleX(), progressBar.getScaleX()/2, progressBar.getScaleY(), progressBar.getScaleY()/2, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); // Change alpha from fully visible to invisible
		blinkanimation.setDuration(150); // duration - half a second
		blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
		blinkanimation.setRepeatCount(1); // Repeat animation infinitely
		blinkanimation.setRepeatMode(Animation.REVERSE);
		
		
		progressBar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				progressBar.startAnimation(blinkanimation);
				//TODO: aktualisieren
			}
		});
		
		
		//set up animation        
		ListView myListsView = (ListView) findViewById(R.id.listView1);
		myListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,	int position, long id) {
				//TODO: liste anzeigen
			}

		});
		myListsView.setAdapter(new MyListsAdapter(this, myLists));
		
	}
	
	 /** Read the object from Base64 string. */
	   private static Object fromString( String s ) throws IOException , ClassNotFoundException {
	        byte [] data = Base64Coder.decode( s );
	        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(  data ) );
	        Object o  = ois.readObject();
	        ois.close();
	        return o;
	   }

	    /** Write the object to a Base64 string. */
	    private static String toString( Serializable o ) throws IOException {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ObjectOutputStream oos = new ObjectOutputStream( baos );
	        oos.writeObject( o );
	        oos.close();
	        return new String( Base64Coder.encode( baos.toByteArray() ) );
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

}
