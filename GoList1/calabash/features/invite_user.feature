Feature: Create or delete a list
The owner of a list can invite others to join a list or remove others from the list.
	
As a logged in User 
I want to invite users to join my list
so that they can become members of the list
	
	Scenario: I can create a new list and invite users to it
		Given I am logged in
		When I press the "Create New List" button
		Then I am on the New List Screen
		And I enter text "New List with User" into field with id "editTextName"
		And I press the "Invite a Friend" button
		Then I am on the Invite Friends Screen
		Then I enter text "dan" into field with id "editTextSearch"
		Then I scroll to cell with "Daniel" label and touch it
		Then I am on the New List Screen
		And I press the "Save" button
		Then I wait up to 30 seconds for "New List with User created!" to appear
		
	Scenario: I can add users to an existing list
		Given I am logged in
		Then I should see my Lists
		Then I scroll to cell with "Changed Name" label and touch it
		Then I press view with id "buttonMenu"
		Then I press "Add User"
		Then I am on the Invite Friends Screen
		Then I enter text "dan" into field with id "editTextSearch"
		Then I scroll to cell with "Daniel" label and touch it
		Then I wait up to 30 seconds for "User invited!" to appear
		
	Scenario: I can remove a user from an existing list
		Given I am logged in
		Then I should see my Lists
		Then I scroll to cell with "Changed Name" label and touch it
		Then I press view with id "buttonMenu"
		Then I press "Show Members"
		Then I am on the Show User Screen
		Then I scroll to cell with "Daniel" label and long touch it
		Then I press "Yes"
		Then I wait up to 30 seconds for "User removed!" to appear