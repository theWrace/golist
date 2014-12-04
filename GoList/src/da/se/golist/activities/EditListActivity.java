package da.se.golist.activities;

import java.io.IOException;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.objects.ShoppingList;

public class EditListActivity extends DataLoader{
	
	private ShoppingList list;
	private EditText editTextName;
	private ProgressDialog dialog;
	private Button buttonSave, buttonDelete;
	private boolean delete = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.editlistlayout);
		
		list = (ShoppingList) getIntent().getExtras().get("list");
		
		editTextName = (EditText) findViewById(R.id.editTextName);
		buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonDelete = (Button) findViewById(R.id.buttonDelete);
		
		editTextName.setText(list.getName());
		
		buttonSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(editTextName.getText().toString().trim().length() != 0 && !editTextName.getText().toString().trim().equals(list.getName())){
					list.setName(editTextName.getText().toString().trim());
					
					try {
						new LoadDataTask(new String[]{"id", "data", "name"},new String[]{list.getID()+"", listToString(list), list.getName()}, "updatelist.php").execute();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					Toast.makeText(getApplicationContext(), "No new data detected!", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		buttonDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditListActivity.this);
				 
				alertDialogBuilder.setTitle(R.string.deletelisttitle);
	 
				alertDialogBuilder
					.setMessage(R.string.deletelistquestion)
					.setCancelable(false)
					.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							delete = true;
							new LoadDataTask(new String[]{"id"},new String[]{list.getID()+""}, "deletelistbyid.php").execute();			
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

	@Override
	protected void postExcecute(JSONObject json) {
		dialog.dismiss();
		if(delete){
			Intent returnIntent = new Intent();
			setResult(RESULT_OK,returnIntent);
		}else{
			Toast.makeText(getApplicationContext(), "List updated!", Toast.LENGTH_LONG).show();
		}
		this.finish();
	}

	@Override
	protected void preExcecute() {
		dialog = new ProgressDialog(this);
        dialog.setMessage("Updating List...");
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
	}

}
