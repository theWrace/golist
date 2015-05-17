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
import da.se.golist.objects.ShoppingList;
import da.se.interfaces.AfterRefresh;
import da.se.interfaces.ManageListFunction;

public class ManageListActivity extends BaseActivity{
	
	private Button buttonYes, buttonCancel;
	private ShoppingList list;
	public static final int CODE_LIST_DELETED = 1, CODE_CANCELED = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.manageitemslayout);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		
		buttonYes = (Button) findViewById(R.id.buttonYes);
		buttonCancel = (Button) findViewById(R.id.buttonCancel);
		TextView textViewQuestion = (TextView) findViewById(R.id.textViewQuestion);		
		final ManageListFunction manageListFunction = (ManageListFunction)getIntent().getExtras().get("managelistfunction");
		
		textViewQuestion.setText(getString(manageListFunction.getQuestionId()));
		
		setTypeface("geosanslight", buttonCancel, buttonYes);
		setTypeface("deluxe", textViewQuestion);
		
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(CODE_CANCELED, null);
				finish();				
			}
		});		
		
		buttonYes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				refreshList(new AfterRefresh() {

					@Override
					public void applyChanges() {
						manageListFunction.execute(ManageListActivity.this);
					}
				}, getIntent().getIntExtra("id", 0));
				
			}
		});
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
	
	public ShoppingList getList(){
		return list;
	}

	@Override
	protected void preExcecute() {
		updateViews(false, buttonCancel, buttonYes);
	}

}
