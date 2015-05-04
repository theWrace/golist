package da.se.golist.activities;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.adapters.FavoriteItemListAdapter;
import da.se.golist.objects.Item;

public class ShowFavoriteItemsActivity extends BaseActivity{
	
	private FavoriteItemListAdapter favoriteItemListAdapter;
	private TextView textViewEmpty;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.showfavoriteitemslayout);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		
		ListView listView = (ListView) findViewById(R.id.listViewFavoriteItems);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setVerticalScrollBarEnabled(false);
		listView.setAdapter(favoriteItemListAdapter = new FavoriteItemListAdapter(getApplicationContext(), getFavoriteItemsAsGoListObjects(), ShowFavoriteItemsActivity.this));	
		
		textViewEmpty = (TextView) findViewById(R.id.textViewEmpty);
		if(getFavoriteItemsAsGoListObjects().size() == 0){
			textViewEmpty.setVisibility(View.VISIBLE);
		}
	}
	
	public void deleteFavoriteItem(final Item item){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShowFavoriteItemsActivity.this);
		 
		alertDialogBuilder.setTitle(R.string.deleteitemtitle);
 
		alertDialogBuilder
			.setMessage(R.string.deletefavoriteitemquestion)
			.setCancelable(false)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,int id) {
					removeFavoriteItem(item);
					favoriteItemListAdapter.updateListObjects(getFavoriteItemsAsGoListObjects());
					Toast.makeText(ShowFavoriteItemsActivity.this, "Item removed!", Toast.LENGTH_SHORT).show();
					if(getFavoriteItemsAsGoListObjects().size() == 0){
						textViewEmpty.setVisibility(View.VISIBLE);
					}
				}
			  })
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,int id) {								
					dialog.cancel();
				}
			});

		alertDialogBuilder.create().show();
	}

	@Override
	protected void postExcecute(JSONObject json) {}

	@Override
	protected void preExcecute() {}

}
