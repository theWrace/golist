package da.se.golist.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import da.se.golist.R;

public class DeleteListActivity extends BaseActivity{
	
	private Button buttonDeleteList, buttonCancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.deletelistlayout);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		
		buttonDeleteList = (Button) findViewById(R.id.buttonDeleteListYes);
		buttonCancel = (Button) findViewById(R.id.buttonDeleteListNo);
		TextView textViewDeleteAccount = (TextView) findViewById(R.id.textViewDeleteList);
		
		Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/geosanslight.ttf");
		Typeface tf1 = Typeface.createFromAsset(this.getAssets(), "fonts/deluxe.ttf");
		buttonDeleteList.setTypeface(tf);
		buttonCancel.setTypeface(tf);
		textViewDeleteAccount.setTypeface(tf1);
		
		buttonDeleteList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buttonDeleteList.setEnabled(false);
				new LoadDataTask(new String[]{"id"},new String[]{getIntent().getIntExtra("id", 0)+""}, "deletelistbyid.php").execute();
			}
		});		
		
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
	}

	@Override
	protected void postExcecute(JSONObject json) {
		try {
			if(json.getString("message").contains("succes")){
				Toast.makeText(getApplicationContext(), "List deleted!", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(getApplicationContext(), "Failed to delete List: " + json.getString("message"), Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Intent returnIntent = new Intent();
		returnIntent.putExtra("listdeleted", true);
		setResult(RESULT_OK,returnIntent);
		finish();
	}

	@Override
	protected void preExcecute() {}

}
