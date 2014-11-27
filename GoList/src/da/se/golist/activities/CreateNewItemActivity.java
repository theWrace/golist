package da.se.golist.activities;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.Item;
import da.se.golist.objects.ShoppingList;

public class CreateNewItemActivity extends DataLoader{
	
	private ShoppingList list;
	private EditText editTextName, editTextNameAmount, editTextNameDescription;
	private Button saveArticleButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.createnewitemlayout);
		
		list = (ShoppingList) getIntent().getExtras().get("list");
		
		editTextName = (EditText) findViewById(R.id.editTextName);
		editTextNameAmount = (EditText) findViewById(R.id.editTextAmount);
		editTextNameDescription = (EditText) findViewById(R.id.editTextDescription);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		
		saveArticleButton = (Button) findViewById(R.id.buttonSaveArticle);
		saveArticleButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(editTextName.getText().length() != 0 && editTextNameAmount.getText().length() != 0){
					list.addArticle(new Item(editTextName.getText().toString(), editTextNameDescription.getText().toString(), editTextNameAmount.getText().toString()));
					
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
						new LoadDataTask(new String[]{"id", "data", "user", "inviteduser"},new String[]{list.getID()+"", listToString(list), userString, inviteduser}, "updatelist.php").execute();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					Toast.makeText(getApplicationContext(), "Please fill in a name and an amount!", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	@Override
	protected void preExcecute() {
		progressBar.setVisibility(ProgressBar.VISIBLE);
		saveArticleButton.setVisibility(Button.INVISIBLE);
	}
	
	@Override
	protected void postExcecute(JSONObject json) {
		try {
			String message = json.getString("message");
		
			if(message.equals("successful")){
				Toast.makeText(getApplicationContext(), list.getName() + " created! " + list.getID(), Toast.LENGTH_LONG).show();
				finish();
			}else{
				progressBar.setVisibility(ProgressBar.GONE);
				saveArticleButton.setVisibility(Button.VISIBLE);
				Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
