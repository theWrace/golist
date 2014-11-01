Feature: Benutzer einladen
Der Benutzer kann andere Benutzer zu seiner Liste einladen.
Dies ist entweder gleich beim Erstellen einer Liste oder erst später möglich.
	
As a logged in User 
I want to invite other Users to my lists
so that they are able to participate in my list
	
	Scenario: Benutzer erfolgreich einladen
		Given that I am logged in
		When I click on "Invite Users"
		And I type in an existing username
		And this username is not mine
		Then the user with this name is invited
		And I get informed about the success
	
	Scenario: Benutzer einladen abbrechen
		Given that I am logged in
		When I click on "Invite Users"
		And I click on the back button
		Then I see the user interface of the last activity
		
	Scenario: Nicht existierender Benutzer einladen
		Given that I am logged in
		When I click on "Invite Users"
		And I type in an not existing username
		Then I see that this username does not exist
		
	Scenario: Benutzer lädt sich selbst ein
		Given that I am logged in
		When I click on "Invite Users"
		And I type in my username
		Then I see that I can not invite myself