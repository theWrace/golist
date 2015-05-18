package da.se.golist.activities;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
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

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import da.se.application.GoListApplication;
import da.se.golist.R;
import da.se.golist.objects.Item;
import da.se.golist.objects.ShoppingList;
import da.se.interfaces.AfterRefresh;
import da.se.otherclasses.LogoView;

public class CreateNewItemActivity extends BaseActivity{
	
	private ShoppingList list;
	private EditText editTextName, editTextAmount, editTextDescription;
	private Button saveItemButton;
	private ImageView imageView;
	private int category;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.createnewitemlayout);
		
		list = (ShoppingList) getIntent().getExtras().get("list");
		
		TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
		setTypeface("deluxe", textViewTitle);
		textViewTitle.setText(getString(R.string.newitem));
		
		editTextName = (EditText) findViewById(R.id.editTextName);
		editTextAmount = (EditText) findViewById(R.id.editTextAmount);
		editTextDescription = (EditText) findViewById(R.id.editTextDescription);
		setTypeface("geosanslight", editTextName, editTextAmount, editTextDescription);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		logoView = (LogoView) findViewById(R.id.logoView);
		
		saveItemButton = (Button) findViewById(R.id.buttonSaveItem);
		saveItemButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String name = editTextName.getText().toString().trim();
				if(name.length() != 0){
					String amount = editTextAmount.getText().toString().trim();
					if(amount.length() == 0){
						amount = "1";
					}
					createItem(name, category, amount, editTextDescription.getText().toString().trim());
				}else{
					Toast.makeText(getApplicationContext(), getString(R.string.insertname), Toast.LENGTH_LONG).show();
				}
			}
		});
		
		final TypedArray imgs = getResources().obtainTypedArray(R.array.category_icons);
		imageView = (ImageView) findViewById(R.id.imageView1);
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
		updateViews(false, editTextName, editTextAmount, editTextDescription, saveItemButton, imageView);
		
		refreshList(new AfterRefresh() {
			
			@Override
			public void applyChanges() {				
				list.addItem(new Item(list.getFreeId(), name, description, amount, category, LoginActivity.NAME, new Date()));
				list.setDescription(list.getItems().size() + getString(R.string._items));
				
				String infoText = getString(R.string.infoitemcreated).replace("listname", list.getName());
				infoText = infoText.replace("username", LoginActivity.NAME);
				infoText = infoText.replace("itemname", name);
				uploadList(list, false, infoText);
				CreateNewItemActivity.this.finish();
			}
		}, list.getID());
	}
	
	@Override
	protected void preExcecute() {}
	
	@Override
	protected void postExcecute(JSONObject json) {
		if(getListFromJson(json) != null){
			list = getListFromJson(json);
		}
		
		if(!runAfterRefresh()){
			return;
		}
		
		if(!getMessageFromJson(json).equals("succes")){
			updateViews(true, editTextName, editTextAmount, editTextDescription, saveItemButton, imageView);
			Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG).show();
			return;
		}
			
		final String name = editTextName.getText().toString().trim();
			
		Tracker t = ((GoListApplication)getApplication()).getTracker();
		t.send(new HitBuilders.EventBuilder()
		   .setCategory("Item")
		   .setAction("erstellt")
		   .setLabel("Name: " + name).build());
			
		Toast.makeText(getApplicationContext(), name + getString(R.string.created), Toast.LENGTH_LONG).show();
			
	}
}
