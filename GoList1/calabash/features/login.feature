Feature: Login
   
  Scenario: I can login
  	Given I am on the Login Screen
  	Then I enter text "admin" into field with id "editTextName"
  	Then I enter text "password" into field with id "editTextPassword"
	Then I press the "Login" button
	Then I should see my Lists
	
Scenario: I can't login with a wrong username and password combination
  	Given I am on the Login Screen
  	Then I enter text "admin" into field with id "editTextName"
  	Then I enter text "wrongpassword" into field with id "editTextPassword"
	Then I press the "Login" button
	Then I wait up to 30 seconds for "Error: Wrong name or password!" to appear
	
  Scenario: I can logout
	Given I am logged in
	Then I press the menu key
	Then I press "Logout"
	Then I am on the Login Screen