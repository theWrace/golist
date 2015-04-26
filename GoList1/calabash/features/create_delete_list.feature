Feature: Create or delete a list
A user can create or delete his own lists.
	
As a logged in User 
I want to delete one of my lists or create a new list
so that I can easier manage my shopping
	
	Scenario: I can create a new list
		Given I am logged in
		When I press the "Create New List" button
		Then I am on the New List Screen
		And I enter text "New List" into field with id "editTextName"
		And I press the "Save" button
		Then I wait up to 30 seconds for "New List created!" to appear
	
	Scenario: I can cancel the creating of a list
		Given I am logged in
		When I press the "Create New List" button
		Then I am on the New List Screen
		Then I go back
		Then I go back
		Then I should see my Lists
		
	Scenario: I can change the name of a list
		Given I am logged in
		Then I should see my Lists
		Then I scroll to cell with "New List" label and touch it
		Then I press view with id "buttonMenu"
		Then I press "Edit List"
		Then I am on the Edit List Screen
		Then I clear the field with id "editTextName"
		And I enter text "Changed Name" into field with id "editTextName"
		Then I press view with id "buttonSave"
		Then I wait up to 30 seconds for "List updated!" to appear
		
	Scenario: I can delete a list
		Given I am logged in
		Then I should see my Lists
		Then I scroll to cell with "Changed Name" label and touch it
		Then I press view with id "buttonMenu"
		Then I press "Edit List"
		Then I am on the Edit List Screen
		Then I press view with id "buttonDelete"
		Then I press "Yes"
		Then I wait up to 30 seconds for "List deleted!" to appear