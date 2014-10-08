package da.se.golist.activities;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.objects.JSONParser;


public class LoginActivity extends Activity{
	
	private int currentView = 0;
	private EditText nameText, passwordText, passwordText1;
	private Button buttonLogin, buttonRegister;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if(getIntent().getExtras() != null){
			SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
			prefs.edit().remove("name").commit();
			prefs.edit().remove("password").commit();
		}
		
		SharedPreferences prefs = LoginActivity.this.getPreferences(MODE_PRIVATE);
		if(prefs.contains("name")){
			new LoginTask(prefs.getString("name", ""), prefs.getString("password", "")).execute();
		}else{
			showLoginView();
		}
	}	
	
	private void showRegisterView(){
		setContentView(R.layout.register);
		currentView = 1;
		
		nameText = (EditText) findViewById(R.id.editText1);
		passwordText = (EditText) findViewById(R.id.editText2);
		passwordText1 = (EditText) findViewById(R.id.editText3);
		buttonRegister = (Button) findViewById(R.id.buttonCreateAcc);
		
		buttonRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(passwordText.getText().toString().equals(passwordText1.getText().toString())){
					if(passwordText.getText().toString().length() > 4 && nameText.getText().toString().length() > 4){
						new RegisterTask(nameText.getText().toString(), passwordText.getText().toString()).execute();
						nameText.setEnabled(false);		
						passwordText.setEnabled(false);
						passwordText1.setEnabled(false);		
						buttonRegister.setEnabled(false);
					}else{
						Toast.makeText(getApplicationContext(), "Error: Name or Password too short!", Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(getApplicationContext(), "Error: Passwords do not match!", Toast.LENGTH_LONG).show();
				}
				
			}
		});
	}
	
	private void showLoginView(){
		setContentView(R.layout.login);
		currentView = 0;
		
		buttonRegister = (Button) findViewById(R.id.buttonRegistrieren);
		buttonRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showRegisterView();
			}
		});
		
		nameText = (EditText) findViewById(R.id.editText1);
		passwordText = (EditText) findViewById(R.id.editText2);
		
		buttonLogin = (Button) findViewById(R.id.buttonLogin);
		buttonLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(nameText.getText().toString().length() > 4 && passwordText.getText().toString().length() > 4){
					new LoginTask(nameText.getText().toString(), passwordText.getText().toString()).execute();
				}else{
					Toast.makeText(LoginActivity.this, "Please enter a name and a password!", Toast.LENGTH_LONG).show();
				}
			}
		});
	}	
	
	private void startMyListsActivity(String name){
		Intent startMyListsActivityIntent = new Intent(LoginActivity.this, MyListsActivity.class);
		startMyListsActivityIntent.putExtra("name", name);
		startActivity(startMyListsActivityIntent);
		LoginActivity.this.finish();
	}
	
	@Override
	public void onBackPressed() {
		if(currentView == 1){
			showLoginView();
		}else{
			super.onBackPressed();
		}
	}
	
	private class LoginTask extends AsyncTask<String, String, String> {

		private String name, password, message = "";
		private ProgressBar progressBar;
		
		public LoginTask(String name, String password){
			this.name = name;
			this.password = password;
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
		protected String doInBackground(String... args) {			
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				
				params.add(new BasicNameValuePair("password", password));			
				params.add(new BasicNameValuePair("name", name));
				
				JSONParser jsonParser = new JSONParser();
				
				JSONObject json = jsonParser.makeHttpRequest(getString(R.string.url) + "login.php", "POST", params);
			
				message = json.getString("message");
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			return null; 
		}

		@Override
		protected void onPostExecute(String file_url) {
			if(progressBar != null){
				progressBar.setVisibility(ProgressBar.GONE);
			}
			if(message.equals("success")){
				SharedPreferences prefs = LoginActivity.this.getPreferences(MODE_PRIVATE);
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

	}
	
	private class RegisterTask extends AsyncTask<String, String, String> {

		private String name, password, message = "";
		private ProgressBar progressBar;
		
		public RegisterTask(String name, String password){			
			this.name = name;
			this.password = password;
			progressBar = (ProgressBar) findViewById(R.id.progressBarRegister);
			progressBar.setVisibility(ProgressBar.VISIBLE);
		}

		@Override
		protected String doInBackground(String... args) {			
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				
				params.add(new BasicNameValuePair("password", password));			
				params.add(new BasicNameValuePair("name", name));
				
				JSONParser jsonParser = new JSONParser();
				
				JSONObject json = jsonParser.makeHttpRequest(getString(R.string.url) + "register.php", "POST", params);
			
				message = json.getString("message");				
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			return null; 
		}

		@Override
		protected void onPostExecute(String file_url) {
			progressBar.setVisibility(ProgressBar.GONE);
			if(message.equals("Registration successful!")){
				showLoginView();
				Toast.makeText(LoginActivity.this, "Registration successful!", Toast.LENGTH_LONG).show();
			}else{
				buttonRegister.setEnabled(true);
				nameText.setEnabled(true);
				passwordText.setEnabled(true);
				passwordText1.setEnabled(true);
				Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
			}
		}

	}

}
