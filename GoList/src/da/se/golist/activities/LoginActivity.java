package da.se.golist.activities;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import da.se.golist.R;


public class LoginActivity extends DataLoader{
	
	private EditText nameText, passwordText;
	private String name, password;
	private Button buttonLogin, buttonRegister;
	public static String NAME;
	private SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		prefs = this.getPreferences(MODE_PRIVATE);
		
		if(getIntent().getExtras() != null){
			prefs.edit().remove("name").commit();
			prefs.edit().remove("password").commit();
		}
		
//		if(prefs.contains("name")){
//			name = prefs.getString("name", "");
//			password = prefs.getString("password", "");
//			new LoadDataTask(new String[]{"password", "name"},new String[]{password, name}, "login.php").execute();
//		}else{
			showLoginView();
//		}
	}	
	
	private void startMyListsActivity(String name){
		Intent startMyListsActivityIntent = new Intent(LoginActivity.this, MyListsActivity.class);
		startMyListsActivityIntent.putExtra("name", name);
		NAME = name;
		startActivity(startMyListsActivityIntent);
		LoginActivity.this.finish();
	}	
	
	@Override
	protected void preExcecute() {
		progressBar = (ProgressBar) findViewById(R.id.progressBarLogin);
		if(progressBar != null){
			progressBar.setVisibility(ProgressBar.VISIBLE);
			buttonLogin.setEnabled(false);
			buttonRegister.setEnabled(false);
			nameText.setEnabled(false);
			passwordText.setEnabled(false);
		}
	}
	
	@Override
	protected void postExcecute(JSONObject json) {
		String message = "";
		try {
			message = json.getString("message");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(progressBar != null){
			progressBar.setVisibility(ProgressBar.GONE);
		}
		if(message.equals("success")){
			prefs.edit().putString("name", name).commit();
			prefs.edit().putString("password", password).commit();
			startMyListsActivity(name);
		}else{
			if(buttonLogin != null){
				buttonLogin.setEnabled(true);
				buttonRegister.setEnabled(true);
				nameText.setEnabled(true);
				passwordText.setEnabled(true);
			}else{
				showLoginView();
			}
			Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
		}
	}
	
	private void showLoginView() {
		setContentView(R.layout.login);
		
		buttonRegister = (Button) findViewById(R.id.buttonRegistrieren);
		buttonRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
		});
		
		nameText = (EditText) findViewById(R.id.editTextRegisterName);
		passwordText = (EditText) findViewById(R.id.editTextRegisterPassword);
		
		buttonLogin = (Button) findViewById(R.id.buttonLogin);
		buttonLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(nameText.getText().toString().length() > 4 && passwordText.getText().toString().length() > 4){
					name = nameText.getText().toString();
					password = passwordText.getText().toString();
					new LoadDataTask(new String[]{"password", "name"},new String[]{password, name}, "login.php").execute();
				}else{
					Toast.makeText(LoginActivity.this, "Please enter a name and a password!", Toast.LENGTH_LONG).show();
				}
			}
		});
	}

}
