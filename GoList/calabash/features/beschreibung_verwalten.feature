Feature: Beschreibung verwalten
Der Benutzer kann alle Artikel einer Liste, in der er Mitglied ist, verwalten.
Dazu gehört auch das Ändern der Beschreibung eines bereits existierenden Artikels.
	
As a logged in User who is member of a list
I want to edit the description of items of this list.
	
	Scenario: Beschreibung verwalten
		Given that I am logged in
		When I click on an item of a list I am a member of
		And I see the name, the amount and the description of this item
		And I edit the description
		And I click on "Save"
		Then the description sould be saved
		And I can see the new description when I click on the item
	
	Scenario: Beschreibung verwalten wird abgebrochen
		Given that I am logged in
		When I click on an item of a list I am a member of
		And I click on the back button
		Then I see the user interface of the last activity