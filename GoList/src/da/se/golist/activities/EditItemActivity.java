package da.se.golist.activities;

import java.io.IOException;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.objects.Item;
import da.se.golist.objects.ShoppingList;

public class EditItemActivity extends DataLoader{
	
	private ShoppingList list;
	private EditText editTextName, editTextNameAmount, editTextNameDescription;
	private Button saveArticleButton, deleteArticleButton;
	private ProgressDialog dialog;
	private Item item;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edititemlayout);
		
		list = (ShoppingList) getIntent().getExtras().get("list");
		
		editTextName = (EditText) findViewById(R.id.editTextName);
		editTextNameAmount = (EditText) findViewById(R.id.editTextAmount);
		editTextNameDescription = (EditText) findViewById(R.id.editTextDescription);
		deleteArticleButton = (Button) findViewById(R.id.buttonDeleteItem);
		
		//load Item
		item = (Item) list.getItems().get(this.getIntent().getIntExtra("itemid", 0));
		editTextName.setText(item.getName());
		editTextNameAmount.setText(item.getMenge());
		editTextNameDescription.setText(item.getDescription());
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);		
		
		saveArticleButton = (Button) findViewById(R.id.buttonSaveItem);
		saveArticleButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(editTextName.getText().length() != 0 && editTextNameAmount.getText().length() != 0 &&
						(!editTextName.getText().toString().equals(item.getName()) || !editTextNameDescription.getText().toString().equals(item.getDescription()) ||
						!editTextNameAmount.getText().toString().equals(item.getMenge()))){
					item.setDescription(editTextNameDescription.getText().toString());
					item.setMenge(editTextNameAmount.getText().toString());
					item.setName(editTextName.getText().toString());				
					
					uploadList();
				}else{
					Toast.makeText(getApplicationContext(), "No new data detected!", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		deleteArticleButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditItemActivity.this);
		 
					alertDialogBuilder.setTitle(R.string.deleteitemtitle);
		 
					alertDialogBuilder
						.setMessage(R.string.deleteitemquestion)
						.setCancelable(false)
						.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								list.removeItem(item);
								uploadList();
							}
						  })
						.setNegativeButton("No",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {								
								dialog.cancel();
							}
						});
		 
						alertDialogBuilder.create().show();
			}
		});
	}
	
	private void uploadList(){
		try {
			new LoadDataTask(new String[]{"id", "data"},new String[]{list.getID()+"", listToString(list)}, "updatelist.php").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void postExcecute(JSONObject json) {
		dialog.dismiss();
		this.finish();
	}

	@Override
	protected void preExcecute() {
		dialog = new ProgressDialog(this);
        dialog.setMessage("Updating Item...");
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();		
	}

}
