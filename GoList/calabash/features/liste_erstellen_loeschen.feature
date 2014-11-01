Feature: Liste erstellen oder loeschen
Der Benutzer kann eigene Listen erstellen oder loeschen.
	
As a logged in User 
I want to delete one of my lists or create a new list
so that I can easier manage my shopping
	
	Scenario: Neue Liste erstellen
		Given that I am logged in
		When I click on "New List"
		And I type in a list name
		And I click on "Create new List"
		Then a new List is created
		And I see the list on my list of shopping lists
		
	Scenario: Neue Liste erstellen abbrechen
		Given that I am logged in
		When I click on "New List"
		And I press return
		Then I see my lists
	
	Scenario: Vorhandene Liste mit mehreren Teilnehmern löschen
		Given that I am logged in
		When I click long on a list
		And I click on "Delete List"
		And I confirm to delete the list
		Then I am no longer participating in that list
		And I do not see the list on my list of shopping lists
		
	Scenario: Vorhandene Liste ohne andere Teilnehmer löschen
		Given that I am logged in
		When I click long on a list
		And I click on "Delete List"
		And I confirm to delete the list
		Then the list is deleted from the database
		And I do not see the list on my list of shopping lists