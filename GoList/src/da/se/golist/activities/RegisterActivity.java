package da.se.golist.activities;

import org.json.JSONException;
import org.json.JSONObject;

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

public class RegisterActivity extends BaseActivity{
	
	private EditText editTextName, editTextPassword, editTextRepeatPassword;
	private Button buttonRegister;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		
		editTextName = (EditText) findViewById(R.id.editTextRegisterName);
		editTextPassword = (EditText) findViewById(R.id.editTextRegisterPassword);
		editTextRepeatPassword = (EditText) findViewById(R.id.editTextRegisterRepeatPassword);
		buttonRegister = (Button) findViewById(R.id.buttonCreateAcc);
		logoView = (LogoView) findViewById(R.id.logoViewRegister);
		
		setTypeface("geosanslight", editTextName, editTextPassword, editTextRepeatPassword, buttonRegister);
		
		buttonRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(editTextPassword.getText().toString().equals(editTextRepeatPassword.getText().toString())){
					if(editTextPassword.getText().toString().length() > 3 && editTextName.getText().toString().length() > 3){
						new LoadDataTask(new String[]{"password", "name"},new String[]{editTextPassword.getText().toString(), editTextName.getText().toString()}, "register.php").execute();
					}else{
						Toast.makeText(getApplicationContext(), "Error: Name or Password too short!", Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(getApplicationContext(), "Error: Passwords do not match!", Toast.LENGTH_LONG).show();
				}
				
			}
		});		

		logoView.showLogoBackground();
	}
	
	@Override
	protected void preExcecute() {
		updateViews(false, editTextName, editTextPassword, editTextRepeatPassword, buttonRegister);
	}
	
	@Override
	protected void postExcecute(JSONObject json) {
		String message = "";
		try {
			message = json.getString("message");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(message.equals("Registration successful!")){
			Tracker t = ((GoListApplication)getApplication()).getTracker();
			t.send(new HitBuilders.EventBuilder()
		    .setCategory("Account")
		    .setAction("erstellt")
		    .setLabel("Name: " + editTextName.getText().toString()).build());
			
			Toast.makeText(RegisterActivity.this, "Account created!", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		updateViews(true, editTextName, editTextPassword, editTextRepeatPassword, buttonRegister);
		Toast.makeText(RegisterActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
	}

}
