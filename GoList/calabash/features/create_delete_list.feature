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
		
	@active
	Scenario: I can create a new list and invite users to it
		Given I am logged in
		When I press the "Create New List" button
		Then I am on the New List Screen
		And I enter text "New List" into field with id "editTextName"
		And I press the "Invite a Friend" button
		Then I am on the Invite Friends Screen
		Then I enter text "dan" into field with id "editTextSearch"
		Then I wait up to 5 seconds for list item number 0 to appear
		Then I press list item number 0
		Then I am on the New List Screen
		And I press the "Save" button
		Then I wait up to 30 seconds for "New List created!" to appear		
	
	Scenario: I can cancel the creating of a list
		Given I am logged in
		When I press the "Create New List" button
		Then I am on the New List Screen
		Then I go back
		Then I go back
		Then I should see my Lists