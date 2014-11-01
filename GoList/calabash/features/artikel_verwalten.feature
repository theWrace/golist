Feature: Artikel verwalten
Der Benutzer kann alle Artikel einer Liste, in der er Mitglied ist, verwalten. 
Dazu gehört das Erstellen eines neuen Artikels, das Löschen eines Artikels 
oder das Ändern der Daten eines bereits existierenden Artikels.
	
As a logged in User who is member of a list
I want to add new items to this list, delete items from this list or edit items of this list
	
	Scenario: Neuen Artikel erstellen
		Given that I am logged in
		When I click on "Add Item"
		And I type in a name and an amount
		And the name is valid
		And the amount is valid
		Then a new item with this data is created in my list
		And I can see this new item in my list
	
	Scenario: Artikel erstellen wird abgebrochen
		Given that I am logged in
		When I click on "Add Item"
		And I click on the back button
		Then I see the user interface of the last activity
		
	Scenario: Artikel erstellen schlägt fehl
		Given that I am logged in
		When I click on "Add Item"
		And I type in an invalid name or amount
		Then I get informed about an error
		
	Scenario: Artikel loeschen
		Given that I am logged in
		When I long click on an item
		And I click on delete
		And I click on ok
		Then this item is removed from my list
		And I can see all remaining items
		
	Scenario: Artikel loeschen wird abgebrochen
		Given that I am logged in
		When I long click on an item
		And I click on the back button or on cancel
		Then I see the user interface of the last activity