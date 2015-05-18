package da.se.golist.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.json.JSONObject;

import da.se.golist.R;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.widget.Toast;

public abstract class WriteNFCActivity extends BaseActivity {	

	private NfcAdapter adapter;
	private PendingIntent pendingIntent;
	private IntentFilter writeTagFilters[];
	private Tag mytag;

	@Override
	protected void postExcecute(JSONObject json) {}

	@Override
	protected void preExcecute() {}
	
	protected void initAdapter(){
		adapter = NfcAdapter.getDefaultAdapter(this);
		if(adapter != null){
		    pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		    IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		    tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
		    writeTagFilters = new IntentFilter[] { tagDetected };
		}
	}
	
	protected void write(String text, Intent intent) {
		if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
			mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			try {				
				NdefRecord[] records = { createRecord(text) };
				NdefMessage message = new NdefMessage(records); 
				Ndef ndef = Ndef.get(mytag);
				ndef.connect();
				ndef.writeNdefMessage(message);
				ndef.close();
				Toast.makeText(getApplicationContext(), getString(R.string.saveditemontag), Toast.LENGTH_SHORT).show();				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (FormatException e) {
				e.printStackTrace();
			}
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
