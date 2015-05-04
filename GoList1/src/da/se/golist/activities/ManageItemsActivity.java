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
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.Item;
import da.se.golist.objects.ShoppingList;

public class ManageItemsActivity extends BaseActivity{
	
	private Button buttonYes, buttonCancel;
	private ShoppingList list = null;
	private int type;
	private OnClickListener listenerYes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.manageitemslayout);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		
		type = getIntent().getIntExtra("type", 0);
		
		buttonYes = (Button) findViewById(R.id.buttonYes);
		buttonCancel = (Button) findViewById(R.id.buttonCancel);
		TextView textViewQuestion = (TextView) findViewById(R.id.textViewQuestion);		
		
		Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/geosanslight.ttf");
		Typeface tf1 = Typeface.createFromAsset(this.getAssets(), "fonts/deluxe.ttf");
		buttonCancel.setTypeface(tf);
		buttonYes.setTypeface(tf);
		textViewQuestion.setTypeface(tf1);
		
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
		
		switch(type){
		case ListActivity.TYPE_DELETE_ALL_ITEMS:
			textViewQuestion.setText(getString(R.string.deleteallitemsquestion));
			listenerYes = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					disableButtons();
					refreshList(new AfterRefresh() {
						
						@Override
						public void applyChanges() {
							list.getItems().clear();
							String infoText = getString(R.string.infoitemsdeleted).replace("username", LoginActivity.NAME);
							uploadList(list, false, infoText);
						}
					}, getIntent().getIntExtra("id", 0));								
				}
				
			};
			break;
		case ListActivity.TYPE_DELETE_BOUGHT_ITEMS:
			textViewQuestion.setText(getString(R.string.deleteboughtitems));
			listenerYes = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					disableButtons();
					refreshList(new AfterRefresh() {
						
						@Override
						public void applyChanges() {
							for(int i = 0; i < list.getItems().size(); i++){
								if(((Item) list.getItems().get(i)).getState() == Item.STATE_BOUGHT){
									list.getItems().remove(i);
									i--;
								}
							}
							String infoText = getString(R.string.infoboughtitemsdeleted).replace("username", LoginActivity.NAME);
							uploadList(list, false, infoText);
						}
					}, getIntent().getIntExtra("id", 0));								
				}
			};
			
			break;
		case ListActivity.TYPE_MARK_ALL_BOUGHT:
			textViewQuestion.setText(getString(R.string.markallitemsboughtquestion));
			listenerYes = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					disableButtons();
					refreshList(new AfterRefresh() {
						
						@Override
						public void applyChanges() {
							for(GoListObject item : list.getItems()){
								((Item) item).setState(Item.STATE_BOUGHT);
							}
							String infoText = getString(R.string.infoallitemsbought).replace("username", LoginActivity.NAME);
							uploadList(list, false, infoText);							
						}
					}, getIntent().getIntExtra("id", 0));
								
				}
			};
			break;
		case ListActivity.TYPE_MARK_ALL_NOT_BOUGHT:
			textViewQuestion.setText(getString(R.string.markallitemsnotboughtquestion));
			listenerYes = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					disableButtons();
					refreshList(new AfterRefresh() {
						
						@Override
						public void applyChanges() {
							for(GoListObject item : list.getItems()){						
								((Item) item).setState(Item.STATE_NORMAL);
							}
							String infoText = getString(R.string.infoallitemsnotbought).replace("username", LoginActivity.NAME);
							uploadList(list, false, infoText);					
						}
					}, getIntent().getIntExtra("id", 0));
				}
				
			};
			break;
		case ListActivity.TYPE_LEAVE_LIST:
			textViewQuestion.setText(getString(R.string.leavelistquestion));
			listenerYes = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					disableButtons();
					refreshList(new AfterRefresh() {

						@Override
						public void applyChanges() {									
							for(int i = 0; i < list.getUser().size(); i++){
								if(list.getUser().get(i).getName().equals(LoginActivity.NAME)){
									list.getUser().remove(i);
									i--;
								}
							}
							for(int i = 0; i < list.getInvitedUser().size(); i++){
								if(list.getInvitedUser().get(i).getName().equals(LoginActivity.NAME)){
									list.getInvitedUser().remove(i);
									i--;
								}
							}
							String infoText = getString(R.string.infolistleft).replace("username", LoginActivity.NAME);
							infoText = infoText.replace("username", LoginActivity.NAME);
							Intent returnIntent = new Intent();
							returnIntent.putExtra("listdeleted", true);
							setResult(RESULT_OK,returnIntent);
							uploadList(list, true, infoText);
						}
					
					}, getIntent().getIntExtra("id", 0));
				}				
			};
			break;
		}
		
		buttonYes.setOnClickListener(listenerYes);
	}
	
	private void disableButtons(){
		buttonYes.setEnabled(false);
		buttonCancel.setEnabled(false);
	}

	@Override
	protected void postExcecute(JSONObject json) {
		String message = "Loading failed!";
		try {
			message = json.getString("message");			
			if(message.equals("succes")){
				JSONArray dataArray = json.getJSONArray("data");
				list = (ShoppingList) objectFromString(dataArray.getString(0));
				if(afterRefresh != null){
					afterRefresh.applyChanges();
					afterRefresh = null;
				}
				return;
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
		}else if(message.equals("succes")){
			Toast.makeText(getApplicationContext(), "List updated!", Toast.LENGTH_SHORT).show();
		}
		finish();		
	}

	@Override
	protected void preExcecute() {}

}
