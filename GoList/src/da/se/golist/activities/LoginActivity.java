package da.se.golist.activities;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
	private EditText nameText, passwordText;
	private Button buttonLogin, buttonRegister;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		showLoginView();
	}
	
	private void showLoginView(){
		setContentView(R.layout.login);
		currentView = 0;
		
		buttonRegister = (Button) findViewById(R.id.buttonRegistrieren);
		buttonRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setContentView(R.layout.register);
				currentView = 1;
			}
		});
		
		nameText = (EditText) findViewById(R.id.editText1);
		passwordText = (EditText) findViewById(R.id.editText2);
		
		buttonLogin = (Button) findViewById(R.id.buttonLogin);
		buttonLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(nameText.getText().toString().length() > 4 && passwordText.getText().toString().length() > 4){
					login(nameText.getText().toString(), passwordText.getText().toString());
				}else{
					Toast.makeText(LoginActivity.this, "Please enter a name and a password!", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	private void login(String name, String password){
		new LoginTask(name, password).execute();
		buttonLogin.setEnabled(false);
		buttonRegister.setEnabled(false);
		nameText.setEnabled(false);
		passwordText.setEnabled(false);
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
	
	class LoginTask extends AsyncTask<String, String, String> {

		private String name, password, message = "";
		private ProgressBar progressBar;
		
		public LoginTask(String name, String password){
			this.name = name;
			this.password = password;
			progressBar = (ProgressBar) findViewById(R.id.progressBarLogin);
			progressBar.setVisibility(ProgressBar.VISIBLE);
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
				if(message.equals("success")){
					name = json.getString("name");
					password = json.getString("password");
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
			progressBar.setVisibility(ProgressBar.GONE);
			if(message.equals("success")){
				startMyListsActivity(name);
			}else{
				buttonLogin.setEnabled(true);
				buttonRegister.setEnabled(true);
				nameText.setEnabled(true);
				passwordText.setEnabled(true);
				Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
			}
		}

	}

}
