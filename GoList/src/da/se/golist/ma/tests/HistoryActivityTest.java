package da.se.golist.ma.tests;

import da.se.golist.activities.LoginActivity;
import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;


public class HistoryActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {
  	private Solo solo;
  	
  	public HistoryActivityTest() {
		super(LoginActivity.class);
  	}

  	public void setUp() throws Exception {
        super.setUp();
		solo = new Solo(getInstrumentation());
		getActivity();
  	}
  
   	@Override
   	public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
  	}
  
	public void testRun() {
		// Wait for activity: 'da.se.golist.activities.LoginActivity'
		solo.waitForActivity(da.se.golist.activities.LoginActivity.class, 2000);
		// Wait for activity: 'da.se.golist.activities.MyListsActivity'
		assertTrue("da.se.golist.activities.MyListsActivity is not found!", solo.waitForActivity(da.se.golist.activities.MyListsActivity.class));
		// Set default small timeout to 40644 milliseconds
		Timeout.setSmallTimeout(40644);
		// Click on 0 Items test
		solo.clickInList(1, 0);
		// Wait for activity: 'da.se.golist.activities.ListActivity'
		assertTrue("da.se.golist.activities.ListActivity is not found!", solo.waitForActivity(da.se.golist.activities.ListActivity.class));
		// Click on ImageView
		solo.clickOnView(solo.getView(da.se.golist.R.id.imageButtonMenu));
		// Click on List
		solo.clickOnText(java.util.regex.Pattern.quote("List"));
		// Click on History
		solo.clickOnText(java.util.regex.Pattern.quote("History"));
		// Wait for activity: 'da.se.golist.activities.ShowHistoryActivity'
		assertTrue("da.se.golist.activities.ShowHistoryActivity is not found!", solo.waitForActivity(da.se.golist.activities.ShowHistoryActivity.class));
		// Scroll to 173064
		android.widget.ListView listView0 = (android.widget.ListView) solo.getView(android.widget.ListView.class, 0);
		solo.scrollListToLine(listView0, 3);
		// Press menu back key
		solo.goBack();
		// Press menu back key
		solo.goBack();
	}
}
