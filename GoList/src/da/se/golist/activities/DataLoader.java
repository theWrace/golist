package da.se.golist.activities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import da.se.golist.R;
import da.se.golist.objects.Base64Coder;
import da.se.golist.objects.JSONParser;

public abstract class DataLoader extends Activity{
	
	protected ProgressBar progressBar;
	
	protected void postExcecute(JSONObject json){}
	
	protected void preExcecute(){}
	
	/** Write the list to a Base64 string. */
	protected String listToString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return new String( Base64Coder.encode( baos.toByteArray() ) );
    }
	
	/** Read the list from Base64 string. */
	protected Object listFromString( String s ) throws IOException , ClassNotFoundException {
		byte [] data = Base64Coder.decode( s );
	    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(  data ) );
	    Object o  = ois.readObject();
	    ois.close();
	    return o;
	}
	
	protected class LoadDataTask extends AsyncTask<String, String, String> {
		
		protected String phpFile;
		protected String[] inputValues, inputNames;
		protected JSONObject json;
		
		public LoadDataTask(String[] inputNames, String[] inputValues, String phpFile){
			this.inputValues = inputValues;
			this.inputNames = inputNames;
			this.phpFile = phpFile;
		}
		
		@Override
		protected void onPreExecute() {
			preExcecute();
		}

		@Override
		protected String doInBackground(String... args) {			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for(int i = 0; i < inputNames.length; i++){
				params.add(new BasicNameValuePair(inputNames[i], inputValues[i]));
			}			
			
			json = new JSONParser().makeHttpRequest(getString(R.string.url) + phpFile, "POST", params);

			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			postExcecute(json);
		}

	}

}
