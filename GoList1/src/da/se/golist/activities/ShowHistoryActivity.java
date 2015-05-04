package da.se.golist.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import da.se.golist.R;
import da.se.golist.adapters.HistoryItemListAdapter;

public class ShowHistoryActivity extends BaseActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.showhistorylayout);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}
	
	@Override
	protected void onStart() {
		new LoadDataTask(new String[]{"id"},new String[]{getIntent().getExtras().getInt("id")+""}, "loadhistorybyid.php").execute();
		super.onStart();
	}

	@Override
	protected void postExcecute(JSONObject json) {
		try {
			String message = json.getString("message");
			if(message.equals("history loaded")){
				ListView listView = (ListView) findViewById(R.id.listViewHistory);
				listView.setVerticalScrollBarEnabled(false);
				List<String> historyList = new ArrayList<String>();
				JSONArray data = json.getJSONArray("history");
				for(int i = 0; i < data.length(); i++){
					if(data.getString(i).contains("::")){
						historyList.add(data.getString(i));
					}					
				}
				listView.setAdapter(new HistoryItemListAdapter(getApplicationContext(), historyList));
			}
		} catch (JSONException e) {			
			e.printStackTrace();
		}		       
	}

	@Override
	protected void preExcecute() {}

}
