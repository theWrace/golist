package da.se.application;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

public class GoListApplication extends Application {
	
	private Tracker tracker = null;
	
	public GoListApplication(){
		super();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
	    analytics.setDryRun(false);
	    analytics.getLogger().setLogLevel(Logger.LogLevel.WARNING);
	    analytics.enableAutoActivityReports(this);
	}

	public synchronized Tracker getTracker() {
		if(tracker == null){
			tracker = GoogleAnalytics.getInstance(this).newTracker("UA-56547668-3");
			tracker.enableAdvertisingIdCollection(true);
			tracker.enableAutoActivityTracking(true);
			tracker.enableExceptionReporting(true);
		}
		return tracker;
	}

}
