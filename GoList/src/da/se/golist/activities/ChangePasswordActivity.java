package da.se.golist.activities;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import da.se.golist.R;

public class ChangePasswordActivity extends BaseActivity{
	
	private EditText editTextPassword, editTextRepeatNewPassword, editTextNewPassword;
	private Button buttonChangePassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.changepasswordlayout);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		editTextRepeatNewPassword = (EditText) findViewById(R.id.editTextRepeatNewPassword);
		editTextNewPassword = (EditText) findViewById(R.id.editTextNewPassword);
		buttonChangePassword = (Button) findViewById(R.id.buttonCreateAcc);
		
		setTypeface("geosanslight", editTextPassword, editTextRepeatNewPassword, editTextNewPassword, buttonChangePassword);
		
		buttonChangePassword.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(editTextNewPassword.getText().toString().equals(editTextRepeatNewPassword.getText().toString())){
					if(editTextPassword.getText().toString().length() > 3 && editTextNewPassword.getText().toString().length() > 3){
						buttonChangePassword.setEnabled(false);
						new LoadDataTask(new String[]{"password", "name", "newpassword"},new String[]{editTextPassword.getText().toString(), LoginActivity.NAME, editTextNewPassword.getText().toString()}, "changepassword.php").execute();
					}else{
						Toast.makeText(getApplicationContext(), getString(R.string.errorpasswordtooshort), Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(getApplicationContext(), getString(R.string.errorpasswordsnomatch), Toast.LENGTH_LONG).show();
				}				
			}
		});		
	}

	@Override
	protected void postExcecute(JSONObject json) {
		String message = getString(R.string.error);
		
		if(getMessageFromJson(json).equals("succes")){
			getPreferences(MODE_PRIVATE).edit().putString("password", editTextNewPassword.getText().toString()).commit();
			message = getString(R.string.passwordchanged);
			finish();
		}
		
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
		editTextPassword.setText("");
		editTextRepeatNewPassword.setText("");
		editTextNewPassword.setText("");
		buttonChangePassword.setEnabled(true);
	}

	@Override
	protected void preExcecute() {}

}
