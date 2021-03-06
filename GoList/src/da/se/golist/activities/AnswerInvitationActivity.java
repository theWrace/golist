package da.se.golist.activities;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.ShoppingList;
import da.se.golist.objects.User;
import da.se.interfaces.AfterRefresh;

public class AnswerInvitationActivity extends BaseActivity{
	
	private Button buttonYes, buttonNo, buttonCancel;
	private ShoppingList list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.answerinvitationlayout);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		
		buttonYes = (Button) findViewById(R.id.buttonAnswerInvitationYes);
		buttonCancel = (Button) findViewById(R.id.buttonAnswerInvitationCancel);
		buttonNo = (Button) findViewById(R.id.buttonAnswerInvitationNo);
		
		setTypeface("geosanslight", buttonCancel, buttonYes, buttonNo);
		setTypeface("deluxe", (TextView) findViewById(R.id.textViewAnswerInvitation));
		
		buttonYes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				refreshList(new AfterRefresh() {
					
					@Override
					public void applyChanges() {
						int invitationIndex = getIndexOfMyInvitation();
						
						//Fehler: Einladung nicht vorhanden
						if(invitationIndex == -1){
							return;
						}

						//Einladung entfernen und Nutzer zu Liste hinzuf�gen
						list.getInvitedUser().remove(invitationIndex);
						list.addUser(new User(LoginActivity.NAME));
								
						//Liste in Datenbank speichern und Verlauf aktualisieren
						String infoText = getString(R.string.infoinvitationaccepted);
						infoText = infoText.replace("username", LoginActivity.NAME);
						uploadList(list, true, infoText);
						Toast.makeText(getApplicationContext(), getString(R.string.invitationaccepted), Toast.LENGTH_SHORT).show();
						
						finish();										
					}
					
				}, getIntent().getIntExtra("id", 0));
			}
		});		
		
		buttonNo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				refreshList(new AfterRefresh() {
					
					@Override
					public void applyChanges() {
						int invitationIndex = getIndexOfMyInvitation();
						
						//Fehler: Einladung nicht vorhanden
						if(invitationIndex == -1){							
							return;
						}
						
						//Einladung l�schen
						list.getInvitedUser().remove(invitationIndex);
						
						//Liste in Datenbank speichern und Verlauf aktualisieren
						String infoText = getString(R.string.infoinvitationnotaccepted);
						infoText = infoText.replace("username", LoginActivity.NAME);
						uploadList(list, true, infoText);
						Toast.makeText(getApplicationContext(), getString(R.string.invitationremoved), Toast.LENGTH_SHORT).show();
						
						finish();		
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
	
	/**
	 * Gibt den Index der Einladung an den eingeloggten Nutzer zur�ck
	 * @return
	 */
	private int getIndexOfMyInvitation(){
		for(GoListObject invitedUser : list.getInvitedUser()){
			if(invitedUser.getName().equals(LoginActivity.NAME)){
				return list.getInvitedUser().indexOf(invitedUser);
			}
		}
		
		Toast.makeText(getApplicationContext(), getString(R.string.errorinvitationnotexisting), Toast.LENGTH_SHORT).show();
		finish();
		return -1;
	}

	@Override
	protected void postExcecute(JSONObject json) {
		if(getListFromJson(json) != null){
			list = getListFromJson(json);
		}
		
		if(list == null){
			Toast.makeText(getApplicationContext(), getString(R.string.failedtoloadlist), Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		
		runAfterRefresh();
	}

	@Override
	protected void preExcecute() {
		updateViews(false, buttonCancel, buttonNo, buttonYes);
	}

}
