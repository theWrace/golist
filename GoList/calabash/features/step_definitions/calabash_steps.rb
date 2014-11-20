require 'calabash-android/calabash_steps'

Given /^I am on the Login Screen$/ do
    wait_for(180) {element_exists("button id:'buttonLogin'")}
end

Given /^I am on the Register Screen$/ do
   steps %{
    Given I am on the Login Screen
  	Then I press "Create New Account"
	}
	wait_for(180) {element_exists("button id:'buttonCreateAcc'")}
end

Given /^I am on the New List Screen$/ do
	wait_for(180) {element_exists("button id:'buttonSave'")}
end

Then /^I am on the Invite Friends Screen$/ do
	wait_for(180) {element_exists("edittext id:'editTextSearch'")}
end
 
 Given /^I am logged in$/ do
  steps %{
    Given I am on the Login Screen
  	Then I enter text "admin" into field with id "editTextName"
  	Then I enter text "password" into field with id "editTextPassword"
	Then I press the "Login" button
	Then I should see my Lists
	}
 end
 
 Then /^I should see my Lists$/ do
    wait_for(180) {element_exists("button id:'buttonNewList'")}    
 end 