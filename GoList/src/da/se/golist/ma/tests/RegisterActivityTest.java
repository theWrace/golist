package da.se.golist.ma.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

import com.robotium.solo.Solo;

import da.se.golist.R;
import da.se.golist.activities.RegisterActivity;

public class RegisterActivityTest extends ActivityInstrumentationTestCase2<RegisterActivity>{
	
	private RegisterActivity registerActivity;
	private EditText editTextName, editTextPassword, editTextRepeatPassword;
	private Button createAccButton;
	private Solo solo;
	
	public RegisterActivityTest(){
		super(RegisterActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		setActivityInitialTouchMode(false);		
		registerActivity = getActivity();
		editTextName = (EditText) registerActivity.findViewById(R.id.editTextRegisterName);
		editTextPassword = (EditText) registerActivity.findViewById(R.id.editTextRegisterPassword);
		editTextRepeatPassword = (EditText) registerActivity.findViewById(R.id.editTextRegisterRepeatPassword);
		createAccButton = (Button) registerActivity.findViewById(R.id.buttonCreateAcc);
	}
	
	public void testRegister() throws Exception{
		solo.enterText(editTextName, "name");
		solo.enterText(editTextPassword, "password");
		solo.enterText(editTextRepeatPassword, "passw");
		solo.clickOnButton("Create Account");
		assertTrue(solo.waitForText("Error: Passwords do not match!"));
		
		clearFields();
		
		solo.enterText(editTextName, "na");
		solo.enterText(editTextPassword, "password");
		solo.enterText(editTextRepeatPassword, "password");
		solo.clickOnButton("Create Account");
		assertTrue(solo.waitForText("Error: Name or Password too short!"));
		
		clearFields();
		
		solo.enterText(editTextName, "name");
		solo.enterText(editTextPassword, "password");
		solo.enterText(editTextRepeatPassword, "password");
		solo.clickOnButton("Create Account");
		assertTrue(solo.waitForText("Error: A user with this name already exists!"));
	}
	
	private void clearFields(){
		solo.clearEditText(editTextName);
		solo.clearEditText(editTextPassword);
		solo.clearEditText(editTextRepeatPassword);		
	}
	
	public void testPreconditions(){
		assertNotNull("registerActivity is null", registerActivity);
		assertNotNull("editTextName is null", editTextName);
	    assertNotNull("editTextPassword is null", editTextPassword);
	    assertNotNull("editTextRepeatPassword is null", editTextRepeatPassword);
	    assertNotNull("createAccButton is null", createAccButton);
	}

}
