package da.se.golist.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import da.se.application.GoListApplication;
import da.se.golist.R;

public class DeleteAccountActivity extends BaseActivity{
	
	private EditText editTextPassword;
	private Button buttonDeleteAccount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.deleteaccountlayout);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		buttonDeleteAccount = (Button) findViewById(R.id.buttonDeleteAcc);
		TextView textViewDeleteAccount = (TextView) findViewById(R.id.textViewDeleteAcc);
		
		setTypeface("geosanslight", editTextPassword, buttonDeleteAccount);
		setTypeface("deluxe", textViewDeleteAccount);
		
		buttonDeleteAccount.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(editTextPassword.getText().toString().length() > 3){
					new LoadDataTask(new String[]{"name", "password"},new String[]{LoginActivity.NAME, editTextPassword.getText().toString()}, "deleteuser.php").execute();
				}else{
					Toast.makeText(getApplicationContext(), "Error: Please enter your Password!", Toast.LENGTH_LONG).show();
				}				
			}
		});		
	}

	@Override
	protected void postExcecute(JSONObject json) {
		String message = "Error";
		try {
			message = json.getString("message");
			if(message.equals("succes")){
				message = "Account deleted";
				Intent returnIntent = new Intent();
				returnIntent.putExtra("accdeleted", true);
				setResult(RESULT_OK,returnIntent);
				
				Tracker t = ((GoListApplication)getApplication()).getTracker();
				t.send(new HitBuilders.EventBuilder()
			    .setCategory("Account")
			    .setAction("gelöscht")
			    .setLabel("Name: " + LoginActivity.NAME).build());
				
				finish();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
		editTextPassword.setText("");
		buttonDeleteAccount.setEnabled(true);
	}

	@Override
	protected void preExcecute() {
		buttonDeleteAccount.setEnabled(false);
	}
}
