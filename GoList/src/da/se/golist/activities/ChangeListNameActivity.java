package da.se.golist.activities;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Intent;
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
import da.se.interfaces.AfterRefresh;

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
		
		setTypeface("geosanslight", buttonCancel, buttonSave, editTextNewName);
		setTypeface("deluxe", textViewChangeListName);
		
		buttonSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String newName = editTextNewName.getText().toString().trim();
				if(newName.length() != 0){
					refreshList(new AfterRefresh() {
						
						@Override
						public void applyChanges() {
							final String oldname = list.getName();
							list.setName(newName);
							String infoText = getString(R.string.infolistnamechanged).replace("username", LoginActivity.NAME) + "::" 
									+ new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US).format(new Date());
							infoText = infoText.replace("oldname", oldname);
							infoText = infoText.replace("newname", list.getName());
							try {
								updateViews(false, buttonSave, buttonCancel);
								Intent returnIntent = new Intent();
								returnIntent.putExtra("list", list);
								ChangeListNameActivity.this.setResult(RESULT_OK, returnIntent);								
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
		if(getListFromJson(json) != null){
			list = getListFromJson(json);
		}
		
		if(list == null){
			Toast.makeText(getApplicationContext(), "Failed to load list!", Toast.LENGTH_SHORT).show();
		}else{
			runAfterRefresh();
			Toast.makeText(getApplicationContext(), "List updated!", Toast.LENGTH_SHORT).show();
		}
		finish();
	}

	@Override
	protected void preExcecute() {}

}
