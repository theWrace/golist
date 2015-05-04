package da.se.golist.activities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.objects.Base64Coder;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.Item;
import da.se.golist.objects.JSONParser;
import da.se.golist.objects.LogoView;
import da.se.golist.objects.ShoppingList;

public abstract class BaseActivity extends FragmentActivity{
	
	protected LogoView logoView;
	protected AfterRefresh afterRefresh = null;
	
	protected abstract void postExcecute(JSONObject json);
	
	protected abstract void preExcecute();
	
	/** Write the list to a Base64 string. */
	protected String objectToString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return new String( Base64Coder.encode( baos.toByteArray() ) );
    }
	
	protected void setTypeface(String typeface, View... views){
		Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/" + typeface + ".ttf");
		for(View view : views){
			if(view instanceof TextView){
				((TextView)view).setTypeface(tf);
			}else if(view instanceof EditText){
				((EditText)view).setTypeface(tf);
			}else if(view instanceof Button){
				((Button)view).setTypeface(tf);
			}
		}
	}
	
	/** Read the list from Base64 string. */
	protected Object objectFromString( String s ) throws IOException , ClassNotFoundException {
		byte [] data = Base64Coder.decode( s );
	    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(  data ) );
	    Object o  = ois.readObject();
	    ois.close();
	    return o;
	}
	
	protected class LoadDataTask extends AsyncTask<String, String, String> {
		
		protected String phpFile;
		protected List<NameValuePair> params = new ArrayList<NameValuePair>();
		protected JSONObject json;
		private String uniqueName;
		
		public LoadDataTask(String[] inputNames, String[] inputValues, String phpFile){
			this.phpFile = phpFile;
			uniqueName = phpFile;
			for(int i = 0; i < inputNames.length; i++){
				params.add(new BasicNameValuePair(inputNames[i], inputValues[i]));
				uniqueName+=inputNames[i]+inputValues[i];
			}
		}
		
		@Override
		protected void onPreExecute() {
			if(logoView != null){
				logoView.startDrawing();
			}
			preExcecute();
		}

		@Override
		protected String doInBackground(String... args) {			
			json = new JSONParser().makeHttpRequest(getString(R.string.url) + phpFile, "POST", params);			
			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
			if(json != null){
				if(phpFile.contains("load") || phpFile.contains("login")){		//speichern im offline modus verhindern
					sharedPref.edit().putString(uniqueName, json.toString()).commit();
				}
			}else{
				Toast.makeText(getApplicationContext(), "Error: Can't connect to Server!", Toast.LENGTH_SHORT).show();
				if(sharedPref.contains(uniqueName)){
					try {
						json = new JSONObject(sharedPref.getString(uniqueName, ""));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}else{
					finish();	//Keine Daten -> darf nicht weiter ausgeführt werden sonst nullpointer
					return;
				}
			}
			postExcecute(json);		//Achtung: JSON null wenn keine Verbindung möglich
			if(logoView != null){
				logoView.stopDrawing();
			}
		}
	}

	protected boolean isAdmin(ShoppingList list, String name){
		for(String admin : list.getAdmins()){
			if(admin.equalsIgnoreCase(name)){
				return true;
			}
		}
		return false;
	}
	
	protected ShoppingList getListFromJson(JSONObject json) {
		try {
			String message = json.getString("message");
			if (message.equals("succes") && json.has("data")) {
				JSONArray dataArray = json.getJSONArray("data");
				return  (ShoppingList) objectFromString(dataArray.getString(0));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected boolean runAfterRefresh(){
		if(afterRefresh != null){
			afterRefresh.applyChanges();
			afterRefresh = null;
			return true;			
		}
		return false;
	}
	
	protected void updateViews(boolean enabled, View... views){
		for(View view : views){
			view.setEnabled(enabled);
		}
	}
	
	protected void uploadList(ShoppingList list, boolean userChanged, String infoText){
		//Liste zu vorheriger Activity zurückgeben
		//So muss nicht nochmal neu geladen werden
		Intent returnIntent = new Intent();
		returnIntent.putExtra("list", list);
		this.setResult(RESULT_OK,returnIntent);
		
		if(infoText.length() != 0){
			infoText += "::" + new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US).format(new Date());
		}
		
		if(userChanged){
			String inviteduser = "", userString = "";
			for(GoListObject user : list.getUser()){
				userString = userString + user.getName() + ";";
			}
			for(GoListObject user : list.getInvitedUser()){
				inviteduser = inviteduser + user.getName() + ";";
			}
			userString = userString.substring(0, userString.length()-1);
			if(inviteduser.length() != 0){
				inviteduser = inviteduser.substring(0, inviteduser.length()-1);
			}		
			try {
				new LoadDataTask(new String[]{"id", "data", "user", "inviteduser", "history"},
						new String[]{list.getID()+"", objectToString(list), userString, inviteduser, infoText}, "updatelist.php").execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		
		try {
			new LoadDataTask(new String[]{"id", "data", "history"},
					new String[]{list.getID()+"", objectToString(list), infoText}, "updatelist.php").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void refreshList(AfterRefresh afterRefresh, int id){
		this.afterRefresh = afterRefresh;
		new LoadDataTask(new String[]{"id"},new String[]{id+""}, "loadlistbyid.php").execute();
	}
	
	protected interface AfterRefresh{
		void applyChanges();
	}
	
	/**
	 * Liest Favorite Items aus Shared Preferences
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected ArrayList<Item> getFavoriteItems(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if(preferences.contains("favoriteItems")){
			try {
				return (ArrayList<Item>) objectFromString(preferences.getString("favoriteItems", ""));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ArrayList<Item>();
	}
	
	@SuppressWarnings("unchecked")
	protected ArrayList<GoListObject> getFavoriteItemsAsGoListObjects(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if(preferences.contains("favoriteItems")){
			try {
				return (ArrayList<GoListObject>) objectFromString(preferences.getString("favoriteItems", ""));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ArrayList<GoListObject>();
	}
	
	/**
	 * Überorüft ob Item in Favorite Items enthalten
	 * @param item
	 * @return
	 */
	protected boolean isFavoriteItem(Item item){
		for(Item favorite : getFavoriteItems()){
			if(favorite.getName().equals(item.getName()) && favorite.getCategory() == item.getCategory()
					&& favorite.getDescription().equals(item.getDescription())){
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Fügt neues Favorite Item hinzu
	 */
	protected void addFavoriteItem(Item item){
		ArrayList<Item> favoriteItems = getFavoriteItems();
		if(!isFavoriteItem(item)){
			favoriteItems.add(item);
			saveFavoriteItems(favoriteItems);
		}
	}
	
	/**
	 * entfernt Favorite Item
	 * @param item
	 */
	protected void removeFavoriteItem(Item item){
		ArrayList<Item> favoriteItems = new ArrayList<Item>();
		for(Item favorite : getFavoriteItems()){
			if(favorite.getName().equals(item.getName()) && favorite.getCategory() == item.getCategory()
					&& favorite.getDescription().equals(item.getDescription())){
				continue;
			}
			favoriteItems.add(favorite);
		}
		if(favoriteItems.size() == getFavoriteItems().size()){
			return;
		}
		saveFavoriteItems(favoriteItems);
	}
	
	/**
	 * Speichert Liste von Favorite Items
	 * @param favoriteItems
	 */
	private void saveFavoriteItems(ArrayList<Item> favoriteItems){
		try {
			PreferenceManager.getDefaultSharedPreferences(this).edit().putString("favoriteItems", objectToString(favoriteItems)).commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
