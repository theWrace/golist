package da.se.golist.activities;

import org.json.JSONException;
import org.json.JSONObject;

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
		
		Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/geosanslight.ttf");
		editTextName.setTypeface(tf);
		editTextPassword.setTypeface(tf);
		editTextRepeatPassword.setTypeface(tf);
		buttonRegister.setTypeface(tf);
		
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

		logoView = (LogoView) findViewById(R.id.logoViewRegister);
		logoView.showLogoBackground();
	}
	
	@Override
	protected void preExcecute() {
		editTextName.setEnabled(false);		
		editTextPassword.setEnabled(false);
		editTextRepeatPassword.setEnabled(false);		
		buttonRegister.setEnabled(false);
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
			Toast.makeText(RegisterActivity.this, "Account created!", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		buttonRegister.setEnabled(true);
		editTextName.setEnabled(true);
		editTextPassword.setEnabled(true);
		editTextRepeatPassword.setEnabled(true);
		Toast.makeText(RegisterActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
	}

}
