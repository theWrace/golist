package da.se.golist.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
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

public class EditItemActivity extends BaseActivity{
	
	private ShoppingList list;
	private EditText editTextName, editTextAmount, editTextDescription;
	private Button saveItemButton, deleteItemButton, markAsBoughtButton;
	private Item item;
	private int state, category;
	private TextView textViewTitle, textViewLastEdit;
	private NfcAdapter adapter;
	private PendingIntent pendingIntent;
	private ImageView imageViewFavorite;
	private IntentFilter writeTagFilters[];
	private Tag mytag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edititemlayout);
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		list = (ShoppingList) getIntent().getExtras().get("list");
		item = list.getItemById(this.getIntent().getIntExtra("itemid", 0));						
		textViewTitle = (TextView) findViewById(R.id.textViewTitle);
		textViewLastEdit = (TextView) findViewById(R.id.textViewLastEdit);
		editTextName = (EditText) findViewById(R.id.editTextName);
		editTextAmount = (EditText) findViewById(R.id.editTextAmount);
		editTextDescription = (EditText) findViewById(R.id.editTextDescription);
		deleteItemButton = (Button) findViewById(R.id.buttonDeleteItem);
		saveItemButton = (Button) findViewById(R.id.buttonSaveItem);
		markAsBoughtButton = (Button) findViewById(R.id.buttonMarkBought);
		logoView = (LogoView) findViewById(R.id.logoView);
		
		setTypeface("geosanslight", deleteItemButton, saveItemButton, markAsBoughtButton, editTextName, editTextAmount, editTextDescription);
		setTypeface("deluxe", textViewLastEdit, textViewTitle);
		
		textViewTitle.setText(item.getName());
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US);
		textViewLastEdit.setText("Last edited by " + item.getAuthor() + ", " + sdf.format(item.getLastEdit()));
		editTextName.setText(item.getName());
		editTextAmount.setText(item.getAmount());
		editTextDescription.setText(item.getDescription());
		state = item.getState();
		category = item.getCategory();
		
		if(item.getState() == Item.STATE_BOUGHT){
			markAsBoughtButton.setText(R.string.marknotbought);
		}
		
		markAsBoughtButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(state == Item.STATE_BOUGHT){
					state = Item.STATE_NORMAL;
					Toast.makeText(getApplicationContext(), R.string.markednotbought, Toast.LENGTH_SHORT).show();
					markAsBoughtButton.setText(R.string.markbought);
				}else{
					state = Item.STATE_BOUGHT;
					Toast.makeText(getApplicationContext(), R.string.markedbought, Toast.LENGTH_SHORT).show();
					markAsBoughtButton.setText(R.string.marknotbought);
				}
			}
		});
		
		saveItemButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				refreshList(new AfterRefresh() {
					
					@Override
					public void applyChanges() {
						item.setDescription(editTextDescription.getText().toString());
						item.setAmount(editTextAmount.getText().toString());
						item.setName(editTextName.getText().toString());
						item.setState(state);
						item.setCategory(category);
						item.setAuthor(LoginActivity.NAME);
						item.setLastEdit(new Date());
											
						String infoText = getString(R.string.infoitemedited).replace("username", LoginActivity.NAME);
						infoText = infoText.replace("itemname", item.getName());
						uploadList(list, false, infoText);
						
						finish();
					}
					
				}, list.getID());
			}
		});
		
		deleteItemButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EditItemActivity.this, ManageListActivity.class);
				intent.putExtra("itemid", EditItemActivity.this.getIntent().getIntExtra("itemid", 0));
				intent.putExtra("id", list.getID());
				intent.putExtra("type", ManageListActivity.TYPE_DELETE_ITEM);
				startActivityForResult(intent, 0);
			}
		});
		
		final TypedArray imgs = getResources().obtainTypedArray(R.array.category_icons);
		final ImageView imageView = (ImageView) findViewById(R.id.imageView1);
		imageViewFavorite = (ImageView) findViewById(R.id.ImageViewFavorite);
		final ArrayList<Integer> imageList = new ArrayList<Integer>();
		
		for(int i = 0; i < 10; i++){
			imageList.add(imgs.getResourceId(i, 0));
		}
		imgs.recycle();
		
		imageView.setImageResource(imageList.get(item.getCategory()));
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 AlertDialog.Builder builder = new AlertDialog.Builder(EditItemActivity.this);
			        builder.setAdapter(new ArrayAdapter<Integer>(EditItemActivity.this, R.layout.dialog_image, imageList) {
			            @Override
			            public View getView(int position, View convertView, ViewGroup parent) {
			                View view;
			                if (convertView == null) {
			                    LayoutInflater inflater = EditItemActivity.this.getLayoutInflater();
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
							category = which;
			            	imageView.setImageResource(imageList.get(which));
			            	dialog.dismiss();
			            }

			        });
			        builder.create().show();
			}
		});
		
		if(isFavoriteItem(item)){
			imageViewFavorite.setImageResource(R.drawable.favorite_enabled);
		}
		
		imageViewFavorite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Item item = new Item(-1, editTextName.getText().toString(), editTextDescription.getText().toString(), "1", category,
						LoginActivity.NAME, new Date());
				if(isFavoriteItem(item)){
					removeFavoriteItem(item);
					imageViewFavorite.setImageResource(R.drawable.favorite_disabled);
					return;
				}
				addFavoriteItem(item);
				imageViewFavorite.setImageResource(R.drawable.favorite_enabled);				
			}
		});
		
		adapter = NfcAdapter.getDefaultAdapter(this);
		if(adapter != null){
		    pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		    IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		    tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
		    writeTagFilters = new IntentFilter[] { tagDetected };
		}
	}

	@Override
	protected void postExcecute(JSONObject json) {
		if(getListFromJson(json) != null){
			list = getListFromJson(json);
		}
		if(list != null){
			item = list.getItemById(this.getIntent().getIntExtra("itemid", 0));
		}
		
		runAfterRefresh();
		updateViews(true, saveItemButton, markAsBoughtButton, deleteItemButton, imageViewFavorite,
				editTextName, editTextAmount, editTextDescription);
	}

	@Override
	protected void preExcecute() {
		updateViews(false, saveItemButton, markAsBoughtButton, deleteItemButton, imageViewFavorite,
				editTextName, editTextAmount, editTextDescription);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
			mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			try {
				if(editTextName.getText().toString().length() != 0){
					String amount = "1";
					if(editTextAmount.getText().toString().length() != 0){
						amount = editTextAmount.getText().toString();
					}
					write(editTextName.getText().toString() + ";;" + category + ";;" + editTextDescription.getText().toString() + ";;" + amount, mytag);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (FormatException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		if(data != null && data.hasExtra("list")){
			Intent returnIntent = new Intent();
			returnIntent.putExtra("list", data.getStringExtra("list"));
			this.setResult(RESULT_OK,returnIntent);
			finish();
		}
	}
	
	private NdefRecord createRecord(String text) throws UnsupportedEncodingException {

	    String lang = "en";
	    byte[] textBytes = text.getBytes();
	    byte[] langBytes = lang.getBytes("US-ASCII");
	    int langLength = langBytes.length;
	    int textLength = textBytes.length;

	    byte[] payload = new byte[1 + langLength + textLength];
	    payload[0] = (byte) langLength;

	    System.arraycopy(langBytes, 0, payload, 1, langLength);
	    System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

	    NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
	    return recordNFC;
	}
	
	private void write(String text, Tag tag) throws IOException, FormatException {

	    NdefRecord[] records = { createRecord(text) };
	    NdefMessage message = new NdefMessage(records); 
	    Ndef ndef = Ndef.get(tag);
	    ndef.connect();
	    ndef.writeNdefMessage(message);
	    ndef.close();
	    Toast.makeText(getApplicationContext(), "Saved Item on Tag!", Toast.LENGTH_SHORT).show();
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        if(adapter != null){
        	adapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
        }
    }
     
    @Override
    protected void onPause() {
    	if(adapter != null){
    		adapter.disableForegroundDispatch(this);      
    	}
        super.onPause();
    }

}
