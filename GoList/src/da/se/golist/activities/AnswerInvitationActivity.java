package da.se.golist.activities;

import java.io.IOException;

import org.json.JSONArray;
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
import da.se.golist.objects.ShoppingList;
import da.se.golist.objects.User;

public class AnswerInvitationActivity extends BaseActivity{
	
	private Button buttonYes, buttonNo, buttonCancel;
	private ShoppingList list = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.answerinvitationlayout);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		
		buttonYes = (Button) findViewById(R.id.buttonAnswerInvitationYes);
		buttonCancel = (Button) findViewById(R.id.buttonAnswerInvitationCancel);
		buttonNo = (Button) findViewById(R.id.buttonAnswerInvitationNo);
		TextView textViewAnswerInvitation = (TextView) findViewById(R.id.textViewAnswerInvitation);
		
		Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/geosanslight.ttf");
		Typeface tf1 = Typeface.createFromAsset(this.getAssets(), "fonts/deluxe.ttf");
		buttonCancel.setTypeface(tf);
		buttonYes.setTypeface(tf);
		buttonNo.setTypeface(tf);
		textViewAnswerInvitation.setTypeface(tf1);
		
		buttonYes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				disableButtons();
				refreshList(new AfterRefresh() {
					
					@Override
					public void applyChanges() {
						for(int i = 0; i < list.getInvitedUser().size(); i++){
							if(list.getInvitedUser().get(i).getName().equals(LoginActivity.NAME)){
								list.getInvitedUser().remove(i);
								list.addUser(new User(LoginActivity.NAME));
								break;
							}
						}
						String infoText = getString(R.string.infoinvitationaccepted);
						infoText = infoText.replace("username", LoginActivity.NAME);
						uploadList(list, true, infoText);
						Toast.makeText(getApplicationContext(), "Invitation accepted!", Toast.LENGTH_SHORT).show();
						exitToMyLists();
					}
				}, getIntent().getIntExtra("id", 0));
			}
		});		
		
		buttonNo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				disableButtons();
				refreshList(new AfterRefresh() {
					
					@Override
					public void applyChanges() {
						for(int i = 0; i < list.getInvitedUser().size(); i++){
							if(list.getInvitedUser().get(i).getName().equals(LoginActivity.NAME)){
								list.getInvitedUser().remove(i);
								break;
							}
						}
						String infoText = getString(R.string.infoinvitationnotaccepted);
						infoText = infoText.replace("username", LoginActivity.NAME);
						uploadList(list, true, infoText);
						Toast.makeText(getApplicationContext(), "Invitation removed!", Toast.LENGTH_SHORT).show();
						exitToMyLists();
					}
				}, getIntent().getIntExtra("id", 0));
			}
		});		
		
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
	}
	
	private void disableButtons(){
		buttonCancel.setEnabled(false);
		buttonNo.setEnabled(false);
		buttonYes.setEnabled(false);
	}
	
	private void exitToMyLists(){
		Intent returnIntent = new Intent();
		returnIntent.putExtra("listupdated", true);
		setResult(RESULT_OK,returnIntent);
		finish();
	}

	@Override
	protected void postExcecute(JSONObject json) {
		String message = "Loading failed!";
		try {
			message = json.getString("message");			
			if(message.equals("succes")){
				JSONArray dataArray = json.getJSONArray("data");
				list = (ShoppingList) objectFromString(dataArray.getString(0));
			}
		} catch (JSONException e) {			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(list == null){
			Toast.makeText(getApplicationContext(), "Failed to load list!", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		if(afterRefresh != null){
			afterRefresh.applyChanges();
			afterRefresh = null;
		}
	}

	@Override
	protected void preExcecute() {}

}
