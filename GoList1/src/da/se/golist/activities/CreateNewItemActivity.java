package da.se.golist.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.objects.Item;
import da.se.golist.objects.LogoView;
import da.se.golist.objects.ShoppingList;

public class CreateNewItemActivity extends BaseActivity{
	
	private ShoppingList list;
	private EditText editTextName, editTextNameAmount, editTextNameDescription;
	private Button saveItemButton;
	private int category;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.createnewitemlayout);
		
		list = (ShoppingList) getIntent().getExtras().get("list");
		
		Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/deluxe.ttf");
		TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
		textViewTitle.setTypeface(tf);
		textViewTitle.setText("New Item");
		
		editTextName = (EditText) findViewById(R.id.editTextName);
		editTextNameAmount = (EditText) findViewById(R.id.editTextAmount);
		editTextNameDescription = (EditText) findViewById(R.id.editTextDescription);
		Typeface tf1 = Typeface.createFromAsset(this.getAssets(), "fonts/geosanslight.ttf");
		editTextName.setTypeface(tf1);
		editTextNameAmount.setTypeface(tf1);
		editTextNameDescription.setTypeface(tf1);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		logoView = (LogoView) findViewById(R.id.logoView);
		
		saveItemButton = (Button) findViewById(R.id.buttonSaveItem);
		saveItemButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(editTextName.getText().length() != 0){		
					String amount = editTextNameAmount.getText().toString().trim();
					if(amount.length() == 0){
						amount = "1";
					}
					createItem(editTextName.getText().toString(), category, amount, editTextNameDescription.getText().toString());
				}else{
					Toast.makeText(getApplicationContext(), "Please fill in a name!", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		final TypedArray imgs = getResources().obtainTypedArray(R.array.category_icons);
		final ImageView imageView = (ImageView) findViewById(R.id.imageView1);
		final ArrayList<Integer> imageList = new ArrayList<Integer>();
		
		for(int i = 0; i < 10; i++){
			imageList.add(imgs.getResourceId(i, 0));
		}
		imgs.recycle();
		
		category = imageList.size()-1;
		
		imageView.setImageResource(imageList.get(imageList.size()-1));
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 AlertDialog.Builder builder = new AlertDialog.Builder(CreateNewItemActivity.this);
			        builder.setAdapter(new ArrayAdapter<Integer>(CreateNewItemActivity.this, R.layout.dialog_image, imageList) {
			            @Override
			            public View getView(int position, View convertView, ViewGroup parent) {
			                View view;
			                if (convertView == null) {
			                    LayoutInflater inflater = CreateNewItemActivity.this.getLayoutInflater();
			                    view = inflater.inflate(R.layout.dialog_image, parent, false);
			                } else {
			                    view = convertView;
			                }
			                view.setBackgroundColor(Color.parseColor("#007abb"));
			                ImageView imageView = (ImageView) view.findViewById(R.id.image);
			                int resId = getItem(position);
			                imageView.setImageResource(resId);
			                return view;
			            }
			        }, new DialogInterface.OnClickListener() {
						@Override
			            public void onClick(DialogInterface dialog, int which) {
							CreateNewItemActivity.this.category = which;
			            	imageView.setImageResource(imageList.get(which));
			            	dialog.dismiss();
			            }

			        });
			        builder.create().show();
			}
		});
		
	}
	
	private void createItem(final String name, final int category, final String amount, final String description){
		editTextName.setEnabled(false);
		editTextNameAmount.setEnabled(false);
		editTextNameDescription.setEnabled(false);
		saveItemButton.setEnabled(false);
		
		refreshList(new AfterRefresh() {
			
			@Override
			public void applyChanges() {				
				list.addItem(new Item(list.getFreeId(), name, description, amount, category, LoginActivity.NAME, new Date()));
				
				String infoText = getString(R.string.infoitemcreated).replace("listname", list.getName());
				infoText = infoText.replace("username", LoginActivity.NAME);
				infoText = infoText.replace("itemname", name);
				uploadList(list, false, infoText);
				
				Intent returnIntent = new Intent();
				try {
					returnIntent.putExtra("list", objectToString(list));
					CreateNewItemActivity.this.setResult(RESULT_OK,returnIntent);
				} catch (IOException e) {
					e.printStackTrace();
				}				
				CreateNewItemActivity.this.finish();
			}
		}, list.getID());
	}
	
	@Override
	protected void preExcecute() {}
	
	@Override
	protected void postExcecute(JSONObject json) {
		try {
			String message = json.getString("message");
			if(message.equals("succes") && json.has("data")){
				JSONArray dataArray = json.getJSONArray("data");
				list = (ShoppingList) objectFromString(dataArray.getString(0));
			}
		} catch (JSONException e) {			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(afterRefresh != null){
			afterRefresh.applyChanges();
			afterRefresh = null;
			return;
		}
		
		try {
			String message = json.getString("message");
		
			if(message.equals("successful")){
				String name = "Item from nfc tag";
				if(editTextName != null){
					name = editTextName.getText().toString();
				}
				Tracker t = ((GoListApplication)getApplication()).getTracker();
				t.send(new HitBuilders.EventBuilder()
			    .setCategory("Item")
			    .setAction("erstellt")
			    .setLabel("Name: " + name).build());
				
				Toast.makeText(getApplicationContext(), name + " created!", Toast.LENGTH_LONG).show();
			}else{
				saveItemButton.setEnabled(true);
				editTextName.setEnabled(true);
				editTextNameAmount.setEnabled(true);
				editTextNameDescription.setEnabled(true);
				Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
