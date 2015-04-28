package da.se.golist.activities;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.objects.ShoppingList;

public class ChangeListNameActivity extends BaseActivity{
	
	private EditText editTextNewName;
	private ShoppingList list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.changelistnamelayout);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		
		
		final Button buttonSave = (Button) findViewById(R.id.buttonSave);
		final Button buttonCancel = (Button) findViewById(R.id.buttonCancel);
		TextView textViewChangeListName = (TextView) findViewById(R.id.textViewChangeListName);
		editTextNewName = (EditText) findViewById(R.id.editTextNewName);
		
		Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/geosanslight.ttf");
		Typeface tf1 = Typeface.createFromAsset(this.getAssets(), "fonts/deluxe.ttf");
		textViewChangeListName.setTypeface(tf1);
		buttonCancel.setTypeface(tf);
		buttonSave.setTypeface(tf);
		editTextNewName.setTypeface(tf);
		
		buttonSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(editTextNewName.getText().toString().trim().length() != 0){
					refreshList(new AfterRefresh() {
						
						@Override
						public void applyChanges() {
							final String oldname = list.getName();
							list.setName(editTextNewName.getText().toString().trim());
							String infoText = getString(R.string.infolistnamechanged).replace("username", LoginActivity.NAME) + "::" 
									+ new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US).format(new Date());
							infoText = infoText.replace("oldname", oldname);
							infoText = infoText.replace("newname", list.getName());
							try {
								buttonSave.setEnabled(false);
								buttonCancel.setEnabled(false);
								new LoadDataTask(new String[]{"id", "data", "name", "history"},new String[]{list.getID()+"", objectToString(list), list.getName(), infoText}, "updatelist.php").execute();
							} catch (IOException e) {
								e.printStackTrace();
							}										
						}
					}, getIntent().getIntExtra("id", 0));
				}else{
					Toast.makeText(getApplicationContext(), "Please insert a new name!", Toast.LENGTH_LONG).show();
				}
				
			}
		});
		
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
	}

	@Override
	protected void postExcecute(JSONObject json) {
		String message = "Loading failed!";
		try {
			message = json.getString("message");			
			if(message.equals("succes")){
				JSONArray dataArray = json.getJSONArray("data");
				list = (ShoppingList) objectFromString(dataArray.getString(0));
				if(afterRefresh != null){
					afterRefresh.applyChanges();
					afterRefresh = null;
				}
				return;
			}
		} catch (JSONException e) {			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(list == null){
			Toast.makeText(getApplicationContext(), "Failed to load list!", Toast.LENGTH_SHORT).show();			
		}else if(message.equals("succes")){
			Toast.makeText(getApplicationContext(), "List updated!", Toast.LENGTH_SHORT).show();
		}
		finish();		
	}

	@Override
	protected void preExcecute() {}

}
