package da.se.golist;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;


public class Login extends Activity{
	
	private int currentView = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		showLoginView();
	}
	
	private void showLoginView(){
		setContentView(R.layout.login);
		currentView = 0;
		
		Button buttonRegister = (Button) findViewById(R.id.buttonRegistrieren);
		buttonRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setContentView(R.layout.register);
				currentView = 1;				
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		if(currentView == 1){
			showLoginView();
		}else{
			super.onBackPressed();
		}
	}

}
