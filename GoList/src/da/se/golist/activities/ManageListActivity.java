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
import da.se.golist.R;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.Item;
import da.se.golist.objects.ShoppingList;

public class ManageListActivity extends BaseActivity{
	
	private Button buttonYes, buttonCancel;
	private ShoppingList list = null;
	private int type;
	private OnClickListener listenerYes;
	public final static int CODE_LIST_DELETED = 0, TYPE_DELETE_ALL_ITEMS = 1, 
			TYPE_DELETE_BOUGHT_ITEMS = 2, TYPE_MARK_ALL_BOUGHT = 3, TYPE_MARK_ALL_NOT_BOUGHT = 4,
			TYPE_LEAVE_LIST = 5, TYPE_REMOVE_USER = 6, TYPE_DELETE_ITEM = 7;
	
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
		
		setTypeface("geosanslight", buttonCancel, buttonYes);
		setTypeface("deluxe", textViewQuestion);
		
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
		
		switch(type){
		case TYPE_DELETE_ALL_ITEMS:
			textViewQuestion.setText(getString(R.string.deleteallitemsquestion));
			listenerYes = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
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
		case TYPE_DELETE_BOUGHT_ITEMS:
			textViewQuestion.setText(getString(R.string.deleteboughtitems));
			listenerYes = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
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
		case TYPE_MARK_ALL_BOUGHT:
			textViewQuestion.setText(getString(R.string.markallitemsboughtquestion));
			listenerYes = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
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
		case TYPE_MARK_ALL_NOT_BOUGHT:
			textViewQuestion.setText(getString(R.string.markallitemsnotboughtquestion));
			listenerYes = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
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
		case TYPE_LEAVE_LIST:
			textViewQuestion.setText(getString(R.string.leavelistquestion));
			listenerYes = new OnClickListener() {
				
				@Override
				public void onClick(View v) {					
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
		case TYPE_REMOVE_USER:
			textViewQuestion.setText(getString(R.string.deleteuserquestion));
			listenerYes = new OnClickListener() {
				
				@Override
				public void onClick(View v) {					
					refreshList(new AfterRefresh() {

						@Override
						public void applyChanges() {	
							String username = ManageListActivity.this.getIntent().getStringExtra("username");
							for(int i = 0; i < list.getUser().size(); i++){
								if(list.getUser().get(i).getName().equals(username)){
									list.getUser().remove(i);
									break;
								}
							}
							for(int i = 0; i < list.getInvitedUser().size(); i++){
								if(list.getInvitedUser().get(i).getName().equals(username)){
									list.getInvitedUser().remove(i);
									break;
								}
							}
							String infoText = getString(R.string.infouserremoved).replace("username1", LoginActivity.NAME);
							infoText = infoText.replace("username2", username);
							infoText = infoText.replace("listname", list.getName());
							uploadList(list, true, infoText);
						}
					
					}, getIntent().getIntExtra("id", 0));
				}				
			};
			break;
		case TYPE_DELETE_ITEM:
			textViewQuestion.setText(getString(R.string.deleteitemquestion));
			listenerYes = new OnClickListener() {
				
				@Override
				public void onClick(View v) {					
					refreshList(new AfterRefresh() {

						@Override
						public void applyChanges() {
							Item item = list.getItemById(ManageListActivity.this.getIntent().getIntExtra("itemid", 0));
							String infoText = getString(R.string.infoitemdeleted).replace("username", LoginActivity.NAME);
							infoText = infoText.replace("itemname", item.getName());
							list.removeItem(item);
							uploadList(list, false, infoText);
						}
					
					}, getIntent().getIntExtra("id", 0));
				}				
			};
			break;
		}
		
		buttonYes.setOnClickListener(listenerYes);
	}

	@Override
	protected void postExcecute(JSONObject json) {
		if(getListFromJson(json) != null){
			list = getListFromJson(json);
		}
		
		if(list == null){
			Toast.makeText(getApplicationContext(), "Failed to load list!", Toast.LENGTH_SHORT).show();			
		}else{
			runAfterRefresh();
			Toast.makeText(getApplicationContext(), "List updated!", Toast.LENGTH_SHORT).show();
		}
		
		finish();
	}

	@Override
	protected void preExcecute() {
		updateViews(false, buttonCancel, buttonYes);
	}

}
