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
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.Item;
import da.se.golist.objects.ShoppingList;
import da.se.interfaces.AfterRefresh;
import da.se.otherclasses.Base64Coder;
import da.se.otherclasses.JSONParser;
import da.se.otherclasses.LogoView;

public abstract class BaseActivity extends FragmentActivity{
	
	protected LogoView logoView;
	protected AfterRefresh afterRefresh = null;
	
	protected abstract void postExcecute(JSONObject json);
	
	protected abstract void preExcecute();
	
	/**
	 * Macht ein serialisierbares Objekt zu einem String
	 * @param o
	 * @return
	 * @throws IOException
	 */
	protected String objectToString(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return new String(Base64Coder.encode(baos.toByteArray()));
    }
	
	/**
	 * Typeface für mehrere Views setzen
	 * @param typeface
	 * @param views
	 */
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
	
	/**
	 * Liest ein Object aus einem String
	 * @param s
	 * @return
	 */
	protected Object objectFromString(String s) {		
		try {
			byte [] data = Base64Coder.decode(s);
		    ObjectInputStream ois;
			ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o  = ois.readObject();
			ois.close();			   
		    return o;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected class LoadDataTask extends AsyncTask<String, String, JSONObject> {
		
		protected String phpFile;
		protected List<NameValuePair> params = new ArrayList<NameValuePair>();
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
				logoView.startRotationAnimation();
			}
			preExcecute();
		}

		@Override
		protected JSONObject doInBackground(String... args) {
			return new JSONParser().makeHttpRequest(getString(R.string.url) + phpFile, "POST", params);
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
			if(json != null){
				if(phpFile.contains("load") || phpFile.contains("login")){		//speichern im offline modus verhindern
					sharedPref.edit().putString(uniqueName, json.toString()).commit();
				}
			}else{
				Toast.makeText(getApplicationContext(), getString(R.string.connectionerror), Toast.LENGTH_SHORT).show();
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
				logoView.stopAnimation();
			}
		}
	}

	/**
	 * Prüft ob ein Nutzer Administrator einer Liste ist
	 * @param list
	 * @param name
	 * @return
	 */
	protected boolean isAdmin(ShoppingList list, String name){
		for(String admin : list.getAdmins()){
			if(admin.equals(name)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Lädt eine ShoppingList aus einem JSONObject
	 * @param json
	 * @return
	 */
	protected ShoppingList getListFromJson(JSONObject json) {
		if (getMessageFromJson(json).equals("succes") && json.has("data")) {
			ArrayList<String> dataArray = getStringArrayListFromJson(json, "data");
			return  (ShoppingList) objectFromString(dataArray.get(0));
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
	
	/**
	 * mehrere Views aktivieren oder deaktivieren
	 * @param enabled
	 * @param views
	 */
	protected void updateViews(boolean enabled, View... views){
		for(View view : views){
			view.setEnabled(enabled);
		}
	}
	
	/**
	 * Liste in Datenbank speichern
	 * @param list
	 * @param userChanged
	 * @param infoText
	 */
	public void uploadList(ShoppingList list, boolean userChanged, String infoText){
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
	
	public void refreshList(AfterRefresh afterRefresh, int id){
		this.afterRefresh = afterRefresh;
		new LoadDataTask(new String[]{"id"},new String[]{id+""}, "loadlistbyid.php").execute();
	}
	
	/**
	 * Liest Favoriten aus Shared Preferences
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected ArrayList<Item> getFavoriteItems(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if(preferences.contains("favoriteItems")){
			return (ArrayList<Item>) objectFromString(preferences.getString("favoriteItems", ""));
		}
		return new ArrayList<Item>();
	}
	
	/**
	 * Liest Favoriten als GoListObjects aus Shared Preferences
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected ArrayList<GoListObject> getFavoriteItemsAsGoListObjects(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if(preferences.contains("favoriteItems")){
			return (ArrayList<GoListObject>) objectFromString(preferences.getString("favoriteItems", ""));
		}
		return new ArrayList<GoListObject>();
	}
	
	/**
	 * Überprüft ob ein Item ein Favorit ist
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
	
	/** 
	 * Fügt neuen Favorit hinzu
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
	
	/**
	 * Liest den String "message" aus einem JSONObject
	 * @param json
	 * @return
	 */
	protected String getMessageFromJson(JSONObject json){
		try {
			return json.getString("message");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getString(R.string.error);
	}
	
	/**
	 * Liest eine ArrayList mit Strings aus einem JSONObject
	 * @param json
	 * @param arrayName
	 * @return
	 */
	protected ArrayList<String> getStringArrayListFromJson(JSONObject json, String arrayName){
		ArrayList<String> stringArrayList = new ArrayList<String>();
		
		try {
			JSONArray jsonArray = json.getJSONArray(arrayName);
			for(int i = 0; i < jsonArray.length(); i++){
				stringArrayList.add(jsonArray.getString(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return stringArrayList;
	}

}
