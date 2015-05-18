package da.se.golist.activities;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import da.se.application.GoListApplication;
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
		
		setTypeface("geosanslight", buttonDeleteList, buttonCancel);
		setTypeface("deluxe", textViewDeleteAccount);
		
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
				setResult(ManageListActivity.CODE_CANCELED, null);
				finish();				
			}
		});
	}

	@Override
	protected void postExcecute(JSONObject json) {
		if(!getMessageFromJson(json).contains("succes")){
			Toast.makeText(getApplicationContext(), getString(R.string.errordeletelistfailed), Toast.LENGTH_SHORT).show();
			return;
		}
			
		Tracker t = ((GoListApplication)getApplication()).getTracker();
		t.send(new HitBuilders.EventBuilder()
		.setCategory("Liste")
		.setAction("gelöscht")
		.setLabel("Name nicht verfügbar").build());
		Toast.makeText(getApplicationContext(), getString(R.string.listdeleted), Toast.LENGTH_LONG).show();			
		
		Intent returnIntent = new Intent();
		returnIntent.putExtra("listdeleted", true);
		setResult(RESULT_OK,returnIntent);
		finish();
	}

	@Override
	protected void preExcecute() {}

}
