package da.se.golist.activities;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import da.se.golist.R;

public class GoListApplication extends Application {
	
	public GoListApplication(){
		super();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
	    analytics.setDryRun(false);
	    analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
	    analytics.enableAutoActivityReports(this);
	}

	synchronized Tracker getTracker() {
		return GoogleAnalytics.getInstance(this).newTracker(R.xml.global_tracker);
	}

}
