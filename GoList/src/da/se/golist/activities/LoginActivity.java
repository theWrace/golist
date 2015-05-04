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
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import da.se.golist.R;
import da.se.golist.objects.LogoView;


public class LoginActivity extends BaseActivity{
	
	private EditText editTextName, editTextPassword;
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
		Tracker t = ((GoListApplication)getApplication()).getTracker();
		t.send(new HitBuilders.EventBuilder()
	    .setCategory("Login")
	    .setAction("eingeloggt")
	    .setLabel("Name: " + name).build());
		
		Intent startMyListsActivityIntent = new Intent(LoginActivity.this, MyListsActivity.class);
		startMyListsActivityIntent.putExtra("name", name);
		NAME = name;
		startActivity(startMyListsActivityIntent);
		LoginActivity.this.finish();
	}
	
	@Override
	protected void onStart() {
		Tracker t = ((GoListApplication)getApplication()).getTracker();
		t.enableAdvertisingIdCollection(true);
		t.enableAutoActivityTracking(true);
		t.enableExceptionReporting(true);
		super.onStart();
	}
	
	@Override
	protected void preExcecute() {
		if(buttonLogin != null){
			updateViews(false, buttonLogin, buttonRegister, editTextName, editTextPassword);
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
		if(message.equals("success")){
			prefs.edit().putString("name", name).commit();
			prefs.edit().putString("password", password).commit();
			startMyListsActivity(name);
		}else{
			showLoginView();
			Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
			updateViews(true, buttonLogin, buttonRegister, editTextName, editTextPassword);
		}
	}
	
	private void showLoginView() {
		setContentView(R.layout.login);
		
		editTextName = (EditText) findViewById(R.id.editTextName);
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);		
		buttonLogin = (Button) findViewById(R.id.buttonLogin);
		logoView = (LogoView) findViewById(R.id.logoViewLogin);
		buttonRegister = (Button) findViewById(R.id.buttonRegistrieren);
		
		setTypeface("geosanslight", editTextName, editTextPassword, buttonLogin, buttonRegister);
		
		buttonRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
		});
		
		buttonLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(editTextName.getText().toString().length() != 0 && editTextPassword.getText().toString().length() != 0){
					name = editTextName.getText().toString();
					password = editTextPassword.getText().toString();
					new LoadDataTask(new String[]{"password", "name"},new String[]{password, name}, "login.php").execute();
				}else{
					Toast.makeText(LoginActivity.this, "Please enter a name and a password!", Toast.LENGTH_LONG).show();
				}
			}
		});		
		
		logoView.showLogoBackground();
	}

}
