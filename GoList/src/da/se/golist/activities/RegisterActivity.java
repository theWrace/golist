package da.se.golist.activities;

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

public class RegisterActivity extends DataLoader{
	
	private EditText nameText, passwordText, passwordText1;
	private Button buttonRegister;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBarRegister);
		nameText = (EditText) findViewById(R.id.editText1);
		passwordText = (EditText) findViewById(R.id.editText2);
		passwordText1 = (EditText) findViewById(R.id.editText3);
		buttonRegister = (Button) findViewById(R.id.buttonCreateAcc);
		
		buttonRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(passwordText.getText().toString().equals(passwordText1.getText().toString())){
					if(passwordText.getText().toString().length() > 3 && nameText.getText().toString().length() > 3){
						new LoadDataTask(new String[]{"password", "name"},new String[]{passwordText.getText().toString(), nameText.getText().toString()}, "register.php").execute();
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
	
	@Override
	protected void preExcecute() {
		progressBar.setVisibility(ProgressBar.VISIBLE);
	}
	
	@Override
	protected void postExcecute(JSONObject json) {
		String message = "";
		try {
			message = json.getString("message");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		progressBar.setVisibility(ProgressBar.GONE);
		if(message.equals("Registration successful!")){
			Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_LONG).show();
			finish();
		}else{
			buttonRegister.setEnabled(true);
			nameText.setEnabled(true);
			passwordText.setEnabled(true);
			passwordText1.setEnabled(true);
			Toast.makeText(RegisterActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
		}
	}

}
