package da.se.golist.activities;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.adapters.ArticleListAdapter;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.ShoppingList;

public class ListActivity extends DataLoader{
	
	private ShoppingList list = null;
	private int id;
	private ArticleListAdapter listAdapter = null;
	private ListView articleListView;
	private boolean isLoading = false;
	private TextView textViewList;
	private Button newArticleButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listlayout);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBarList);
		progressBar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				update();
			}
		});
		newArticleButton = (Button) findViewById(R.id.buttonNewArticle);
		newArticleButton.setEnabled(false);
		newArticleButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ListActivity.this, CreateNewArticleActivity.class);
				intent.putExtra("list", list);
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onStart() {
		update();
		super.onStart();
	}
	
	private void update(){
		if(!isLoading){
			id = getIntent().getExtras().getInt("id");
			new LoadDataTask(new String[]{"id"},new String[]{id+""}, "loadlistbyid.php").execute();
		}
	}
	
	@Override
	protected void preExcecute() {
		isLoading = true;
		progressBar.setIndeterminate(true);
		newArticleButton.setEnabled(false);
	}
		
	@Override
	protected void postExcecute(JSONObject json) {
		String message = "Loading failed!";
		try {
			message = json.getString("message");		
			JSONArray dataArray = json.getJSONArray("data");
			list = (ShoppingList) listFromString(dataArray.getString(0));
			System.out.println(list.getArticles().size());
			newArticleButton.setEnabled(true);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		progressBar.setIndeterminate(false);
		isLoading = false;
		Toast.makeText(getApplicationContext(), message + " " + id, Toast.LENGTH_LONG).show();
		if(message.equals("succes") && listAdapter == null){
			articleListView = (ListView) findViewById(R.id.listViewArticles);
			listAdapter = new ArticleListAdapter(ListActivity.this, list.getArticles());
			articleListView.setAdapter(listAdapter);
			
			textViewList = (TextView) findViewById(R.id.textViewList);
		}else if(message.equals("succes")){
			listAdapter.updateListObjects(list.getArticles());
			textViewList.setText(list.getName());
		}
	}	

}
