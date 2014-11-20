Feature: Benutzer registrieren
Der Benutzer kann sich beim System registrieren, 
um sich anschließend anzumelden und weitere Funktionen nutzen zu koennen.
	
As an unknown User 
I want to register me to the system
so that I am able to use further functions of the application
	
	Scenario: I can create a new account
		Given I am on the Register Screen
		Then I enter text "notexistingusername" into field with id "editTextRegisterName"
  		And I enter text "password" into field with id "editTextRegisterPassword"
  		And I enter text "password" into field with id "editTextRegisterRepeatPassword"
		And I press the "Register" button
		Then I wait up to 30 seconds for "Registration successful!" to appear
		Then I am on the Login Screen
		
	Scenario: I can't create a new account without repeating the password correctly
		Given I am on the Register Screen
		Then I enter text "notexistingusername" into field with id "editTextRegisterName"
  		And I enter text "password" into field with id "editTextRegisterPassword"
  		And I enter text "differentpassword" into field with id "editTextRegisterRepeatPassword"
		And I press the "Register" button
		Then I wait up to 30 seconds for "Error: Passwords do not match!" to appear
		
	Scenario: I can't create a new account using an already existing name
		Given I am on the Register Screen
		Then I enter text "admin" into field with id "editTextRegisterName"
  		And I enter text "password" into field with id "editTextRegisterPassword"
  		And I enter text "password" into field with id "editTextRegisterRepeatPassword"
		And I press the "Register" button
		Then I wait up to 30 seconds for "Error: A user with this name already exists!" to appear
		
	Scenario: I can't create a new account using a name or password which is too short
		Given I am on the Register Screen
		Then I enter text "admin" into field with id "editTextRegisterName"
  		And I enter text "p" into field with id "editTextRegisterPassword"
  		And I enter text "p" into field with id "editTextRegisterRepeatPassword"
		And I press the "Register" button
		Then I wait up to 30 seconds for "Error: Name or Password too short!" to appear