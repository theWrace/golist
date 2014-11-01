Feature: Benutzer registrieren
Der Benutzer kann sich beim System registrieren, 
um sich anschließend anzumelden und weitere Funktionen nutzen zu können.
	
As an unknown User 
I want to register me to the system
so that I am able to use further functions of the application
	
	Scenario: Benutzer erfolgreich registrieren
		Given that I am on the login interface
		When I click on "Register"
		And I type in an not existing username
		And I type in an acceptable password
		And I repeat the password
		And I click on "Register"
		Then an Account with this name and password is created on the database
		And I see the login interface
	
	Scenario: Benutzername existiert bereits
		Given that I am on the login interface
		When I click on "Register"
		And I type in an existing username
		And I type in an acceptable password
		And I repeat the password
		And I click on "Register"
		Then I see that the username already exists
		
	Scenario: Passwort wird nicht richtig wiederholt
		Given that I am on the login interface
		When I click on "Register"
		And I type in an not existing username
		And I type in an acceptable first password
		And I type in a second password that is not the first password
		And I click on "Register"
		Then I see that my password confirmation failed
		
	Scenario: Passwort ist zu kurz
		Given that I am on the login interface
		When I click on "Register"
		And I type in an not existing username
		And I type in an not acceptable password
		And I repeat the password
		And I click on "Register"
		Then I see that my password confirmation failed because it has to include at least 5 characters