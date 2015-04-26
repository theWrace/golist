package da.se.golist.activities;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.objects.LogoView;


public class LoginActivity extends BaseActivity{
	
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
		
		//Gespeicherte Daten löschen falls Logout gedrückt wurde
		if(getIntent().getExtras() != null){
			prefs.edit().remove("name").commit();
			prefs.edit().remove("password").commit();
		}
		
		//Falls Daten gespeichert gleich einloggen
		if(prefs.contains("name")){
			name = prefs.getString("name", "");
			password = prefs.getString("password", "");
			new LoadDataTask(new String[]{"password", "name"},new String[]{password, name}, "login.php").execute();
		}else{
			showLoginView();
		}
		
	}	
	
	private void startMyListsActivity(String name){
		Intent startMyListsActivityIntent = new Intent(LoginActivity.this, MyListsActivity.class);
		startMyListsActivityIntent.putExtra("name", name);
		NAME = name;
		startActivity(startMyListsActivityIntent);
		LoginActivity.this.finish();
	}	
	
	@Override
	protected void preExcecute() {}
	
	@Override
	protected void postExcecute(JSONObject json) {
		String message = "";
		try {
			message = json.getString("message");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(message.equals("success")){
			prefs.edit().putString("name", name).commit();
			prefs.edit().putString("password", password).commit();
			startMyListsActivity(name);
		}else{
			showLoginView();
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
		
		nameText = (EditText) findViewById(R.id.editTextName);
		passwordText = (EditText) findViewById(R.id.editTextPassword);
		
		
		buttonLogin = (Button) findViewById(R.id.buttonLogin);
		
		Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/geosanslight.ttf");
		
		nameText.setTypeface(tf);
		passwordText.setTypeface(tf);
		buttonLogin.setTypeface(tf);
		buttonRegister.setTypeface(tf);
		
		buttonLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(nameText.getText().toString().length() != 0 && passwordText.getText().toString().length() != 0){
					name = nameText.getText().toString();
					password = passwordText.getText().toString();
					new LoadDataTask(new String[]{"password", "name"},new String[]{password, name}, "login.php").execute();
				}else{
					Toast.makeText(LoginActivity.this, "Please enter a name and a password!", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		logoView = (LogoView) findViewById(R.id.logoViewLogin);
		logoView.showLogoBackground();
	}

}
