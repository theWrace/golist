package da.se.golist.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class ListActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

}
