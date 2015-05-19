package da.se.golist.ma.tests;

import android.app.Activity;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.robotium.solo.Solo;

import da.se.golist.R;
import da.se.golist.activities.LoginActivity;
import da.se.golist.activities.MyListsActivity;
import da.se.golist.activities.RegisterActivity;

public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity>{
	
	private Solo solo;
	private LoginActivity loginActivity;
	private EditText editTextName, editTextPassword;

	public LoginActivityTest(){
		super(LoginActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		setActivityInitialTouchMode(false);
		loginActivity = getActivity();
		
		editTextName = (EditText) loginActivity.findViewById(R.id.editTextName);
		editTextPassword = (EditText) loginActivity.findViewById(R.id.editTextPassword);
		
		loginActivity.resetSharedPreferences();		
	}
	
	public void testPreconditions(){
		assertNotNull("LoginActivity is null!", loginActivity);
		assertNotNull("editTextName is null!", editTextName);
		assertNotNull("editTextPassword is null!", editTextPassword);
	}
	
	public void testOpenRegisterActivity(){
		solo.clickOnButton(solo.getString(R.string.newacc));
		solo.assertCurrentActivity("Register Activity not started!", RegisterActivity.class);
		solo.goBackToActivity("LoginActivity");
		solo.assertCurrentActivity("Failed to go back to LoginActivity!", LoginActivity.class);
	}
	
	public void testLogin(){
		clearFields();
		solo.enterText(editTextName, "name");
		solo.enterText(editTextPassword, "password");
		solo.clickOnButton(solo.getString(R.string.login));
		assertTrue(solo.waitForActivity(MyListsActivity.class, 2000));
		clearLoginData();
		solo.goBackToActivity("LoginActivity");
		clearFields();
	}
	
	public void testWrongPassword(){
		clearFields();
		solo.enterText(editTextName, "name");
		solo.enterText(editTextPassword, "wrongpassword");
		solo.clickOnButton(solo.getString(R.string.login));
		assertTrue(solo.waitForText(solo.getString(R.string.errorloginfailed)));
		clearFields();
	}
	
	public void testFieldEmpty(){
		clearFields();
		solo.clickOnButton(solo.getString(R.string.login));
		assertTrue(solo.waitForText(solo.getString(R.string.enternamepassword)));
	}
	
	private void clearLoginData(){
		SharedPreferences prefs = loginActivity.getPreferences(Activity.MODE_PRIVATE);
		prefs.edit().remove("name").commit();
		prefs.edit().remove("password").commit();
	}
	
	private void clearFields(){
		solo.clearEditText(editTextName);
		solo.clearEditText(editTextPassword);
	}

}
