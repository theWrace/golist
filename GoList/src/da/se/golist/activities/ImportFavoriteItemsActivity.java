package da.se.golist.activities;

import java.util.Date;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.adapters.SelectFavoriteItemListAdapter;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.Item;
import da.se.golist.objects.ShoppingList;

public class ImportFavoriteItemsActivity extends BaseActivity{
	
	private ShoppingList list = null;
	private Button buttonImport, buttonCancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.importfavoriteitemslayout);		
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		buttonImport = (Button) findViewById(R.id.buttonImport);
		buttonCancel = (Button) findViewById(R.id.buttonCancel);
	}
	
	@Override
	protected void onStart() {
		refreshList(null, getIntent().getExtras().getInt("id"));
		super.onStart();
	}

	@Override
	protected void postExcecute(JSONObject json) {
		if(list == null){
			list = getListFromJson(json);

			if(list == null){
				Toast.makeText(getApplicationContext(), "Error: Failed to load list!", Toast.LENGTH_SHORT).show();
				finish();
				return;
			}
			
			ListView listView = (ListView) findViewById(R.id.listViewFavoriteItems);
			listView.setDivider(null);
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			listView.setVerticalScrollBarEnabled(false);
			final SelectFavoriteItemListAdapter adapter;
			listView.setAdapter(adapter = new SelectFavoriteItemListAdapter(getApplicationContext(), getFavoriteItemsAsGoListObjects()));				
			
			buttonCancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();						
				}
			});
			
			if(getFavoriteItemsAsGoListObjects().size() == 0){
				TextView textViewEmpty = (TextView) findViewById(R.id.textViewEmpty);
				textViewEmpty.setVisibility(View.VISIBLE);
				buttonImport.setEnabled(false);
				return;
			}				
			
			buttonImport.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String itemString = "";
					for(GoListObject object : adapter.getSelectedItems()){
						Item item = (Item) object;
						list.addItem(new Item(list.getFreeId(), item.getName(), item.getDescription(), 
								item.getAmount(), item.getCategory(), LoginActivity.NAME ,new Date()));
						itemString += item.getName() + ", ";
					}
					String infoText = getString(R.string.infoitemsfromfavorites).replace("username", LoginActivity.NAME);
					infoText = infoText.replace("items", itemString.substring(0, itemString.length()-2));
					uploadList(list, false, infoText);
					finish();
				}
			});				
		}
		
		if(buttonCancel != null){
			updateViews(true, buttonCancel, buttonImport);
		}
	}

	@Override
	protected void preExcecute() {
		if(buttonCancel != null){
			updateViews(false, buttonCancel, buttonImport);
		}
	}

}
