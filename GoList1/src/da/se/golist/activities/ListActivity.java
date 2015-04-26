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
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.adapters.ItemListAdapter;
import da.se.golist.objects.Item;
import da.se.golist.objects.LogoView;
import da.se.golist.objects.ShoppingList;

public class ListActivity extends BaseActivity{
	
	private ShoppingList list = null;
	private int id;
	private ItemListAdapter listAdapter = null;
	private ListView articleListView;
	private TextView textViewTitleList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listlayout);
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/deluxe.ttf");
		textViewTitleList = (TextView) findViewById(R.id.textViewTitle);
		textViewTitleList.setTypeface(tf);
		textViewTitleList.setText("List");
		
		logoView = (LogoView) findViewById(R.id.logoView);
		logoView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				refreshList(null, id);
			}
		});
		
		articleListView = (ListView) findViewById(R.id.listViewArticles);
		
		articleListView.setDividerHeight(6);
		
		articleListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(ListActivity.this, EditItemActivity.class);
				intent.putExtra("list", list);
				intent.putExtra("itemid", position);
				startActivity(intent);		
			}
		});
		
		articleListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
				articleListView.setEnabled(false);
				refreshList(new AfterRefresh() {
					
					@Override
					public void applyChanges() {
						Item item = (Item) list.getItems().get(position);
						if(item.getState() == Item.STATE_BOUGHT){
							item.setState(Item.STATE_NORMAL);
							uploadList(list, false);
							Toast.makeText(ListActivity.this, R.string.markednotbought, Toast.LENGTH_SHORT).show();
						}else if(item.getState() == Item.STATE_NORMAL){
							item.setState(Item.STATE_BOUGHT);
							uploadList(list, false);
							Toast.makeText(ListActivity.this, R.string.markedbought, Toast.LENGTH_SHORT).show();
						}
						articleListView.setEnabled(true);
						refreshList(null, id);
					}
				}, id);
				return true;
			}
		});
	}
	
	@Override
	protected void onStart() {
		id = getIntent().getExtras().getInt("id");
		refreshList(null, id);
		super.onStart();
	}
	
	
	@Override
	protected void preExcecute() {}
		
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
        
		//Daten von Liste erfolgreich geladen
		if(message.equals("succes")){
			
			//Button für neues Item
			((ImageButton) findViewById(R.id.buttonNewItem)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ListActivity.this, CreateNewItemActivity.class);
					intent.putExtra("list", list);
					startActivity(intent);
				}
			});
			
			if(listAdapter == null){
				listAdapter = new ItemListAdapter(ListActivity.this, list.getItems());
				articleListView.setAdapter(listAdapter);
			}else{
				listAdapter.updateListObjects(list.getItems());
			}
		    
		    textViewTitleList.setText(list.getName());
			textViewTitleList.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ListActivity.this, ListSettingsActivity.class);
					intent.putExtra("list", list);
					intent.putExtra("id", id);
					startActivityForResult(intent, 3);
				}
			});
			
			TextView textViewEmpty = (TextView)findViewById(R.id.textViewEmpty);
			if(list.getItems().size() == 0){
				Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/deluxe.ttf");
				textViewEmpty.setVisibility(View.VISIBLE);
				textViewEmpty.setTypeface(tf);
			}
			else{
				textViewEmpty.setVisibility(View.INVISIBLE);
			}
			
		}
		
		if(afterRefresh != null){
			afterRefresh.applyChanges();
			afterRefresh = null;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Activity beenden falls liste gelöscht wurde
		if(data != null && data.getExtras().containsKey("finish")){
			finish();
		}
	}
}
