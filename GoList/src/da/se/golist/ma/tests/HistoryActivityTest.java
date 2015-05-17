package da.se.golist.ma.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import com.robotium.solo.Solo;

import da.se.golist.R;
import da.se.golist.activities.ListActivity;
import da.se.golist.activities.LoginActivity;
import da.se.golist.activities.MyListsActivity;
import da.se.golist.activities.ShowHistoryActivity;


public class HistoryActivityTest extends ActivityInstrumentationTestCase2<MyListsActivity> {
  	private Solo solo;
  	
  	public HistoryActivityTest() {
		super(MyListsActivity.class);
  	}

  	public void setUp() throws Exception {
        super.setUp();
		solo = new Solo(getInstrumentation());
		LoginActivity.NAME = "user";
		getActivity();
  	}
  
   	@Override
   	public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
  	}
  
	public void testRun() {
		//Testen ob richtige Activity gestartet wurde
		solo.assertCurrentActivity("da.se.golist.activities.MyListsActivity is not found!", MyListsActivity.class);		
		//Erste Liste öffnen
		solo.clickInList(1, 0);
		//Testen ob richtige Activity gestartet wird
		assertTrue("da.se.golist.activities.ListActivity is not found!", solo.waitForActivity(ListActivity.class));
		// Menü öffnen
		solo.clickOnView(solo.getView(R.id.imageButtonMenu));
		// Untermenü List öffnen
		solo.clickOnText("List");
		// Auf History klicken
		solo.clickOnText("History");
		//Testen ob richtige Activity gestartet wird
		assertTrue("da.se.golist.activities.ShowHistoryActivity is not found!", solo.waitForActivity(ShowHistoryActivity.class));
		//Testen ob Verlauf in Liste angezeigt wird
		ListView listViewHistory = (ListView) solo.getView(R.id.listViewHistory);
		assertNotNull(listViewHistory);
	}
}
