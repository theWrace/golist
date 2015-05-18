package da.se.golist.activities;
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

import da.se.application.GoListApplication;
import da.se.golist.R;
import da.se.otherclasses.LogoView;


public class LoginActivity extends BaseActivity{
	
	private String password;
	public static String NAME;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
				
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);	
		
		//Beim Ersten Ausführen direkt zur Registration
		if(!prefs.contains("firstStart")){
			prefs.edit().putBoolean("firstStart", false).commit();
			showLoginView();
			register();
			return;
		}
		
		//Gespeicherte Daten löschen falls Logout gedrückt wurde
		if(getIntent().getExtras() != null && getIntent().getExtras().containsKey("logout")){
			prefs.edit().remove("name").commit();
			prefs.edit().remove("password").commit();
		}
		
		//Falls Daten gespeichert gleich einloggen
		if(prefs.contains("name")){
			LoginActivity.NAME = prefs.getString("name", "");
			password = prefs.getString("password", "");
			new LoadDataTask(new String[]{"password", "name"},new String[]{password, LoginActivity.NAME}, "login.php").execute();
			return;
		}
		
		showLoginView();
	}	
	
	private void startMyListsActivity(){
		Tracker t = ((GoListApplication)getApplication()).getTracker();
		t.send(new HitBuilders.EventBuilder()
	    .setCategory("Login")
	    .setAction("eingeloggt")
	    .setLabel("Name: " + LoginActivity.NAME).build());
		
		startActivity(new Intent(LoginActivity.this, MyListsActivity.class));
		finish();
	}
	
	@Override
	protected void preExcecute() {
		setContentView(R.layout.loginloadingscreen);
		logoView = (LogoView) findViewById(R.id.logoViewLoading);
		logoView.showLogoBackground();
		logoView.startPulseAnimation();
	}
	
	@Override
	protected void postExcecute(JSONObject json) {		
		if(getMessageFromJson(json).equals("success")){
			SharedPreferences prefs = getPreferences(MODE_PRIVATE);
			prefs.edit().putString("name", LoginActivity.NAME).commit();
			prefs.edit().putString("password", password).commit();
			startMyListsActivity();
		}else{
			showLoginView();
			Toast.makeText(LoginActivity.this, getString(R.string.errorloginfailed), Toast.LENGTH_LONG).show();			
		}
	}
	
	private void showLoginView() {
		setContentView(R.layout.login);
		
		final EditText editTextName = (EditText) findViewById(R.id.editTextName);
		final EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);		
		Button buttonLogin = (Button) findViewById(R.id.buttonLogin);
		logoView = (LogoView) findViewById(R.id.logoViewLogin);
		Button buttonRegister = (Button) findViewById(R.id.buttonRegistrieren);
		
		setTypeface("geosanslight", editTextName, editTextPassword, buttonLogin, buttonRegister);
		
		buttonRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				register();
			}
		});
		
		buttonLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(editTextName.getText().toString().length() != 0 && editTextPassword.getText().toString().length() != 0){
					LoginActivity.NAME = editTextName.getText().toString();
					password = editTextPassword.getText().toString();
					new LoadDataTask(new String[]{"password", "name"},
							new String[]{password, LoginActivity.NAME}, "login.php").execute();
				}else{
					Toast.makeText(LoginActivity.this, getString(R.string.enternamepassword), Toast.LENGTH_SHORT).show();
				}
			}
		});		
		
		logoView.showLogoBackground();		
		updateViews(true, buttonLogin, buttonRegister, editTextName, editTextPassword);
	}
	
	private void register(){
		Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
		startActivity(intent);
	}

}
