package da.se.golist.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import da.se.golist.R;

public class CreateNewListActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.createnewlistlayout);
		
		Button addUserButton = (Button) findViewById(R.id.buttonAddUser);
		
		addUserButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent newListIntent = new Intent(CreateNewListActivity.this, InviteFriendsActivity.class);
				startActivityForResult(newListIntent, 0); //TODO: result abfragen				
			}
		});
	}

}
