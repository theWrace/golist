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

Given /^I am on the Edit List Screen$/ do
	wait_for(180) {element_exists("button id:'buttonDelete'")}
end

Then /^I am on the Invite Friends Screen$/ do
	wait_for(180) {element_exists("edittext id:'editTextSearch'")}
end

Then /^I am on the Show User Screen$/ do
	wait_for(180) {element_exists("viewpager id:'pager'")}
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
 
Then /^I scroll to cell with "([^\"]*)" label and touch it$/ do |name|
    element="TextView text:'#{name}'"      
    if !element_exists(element)
    	sleep(3)    	
    end
    touch(element)    
end

Then /^I scroll to cell with "([^\"]*)" label and long touch it$/ do |name|
    element="TextView text:'#{name}'"      
    if !element_exists(element)
    	sleep(3)    	
    end
    long_press(element)    
end

 Then /^I clear the field with id "([^\"]*)"$/ do |name|
    clear_text("android.widget.EditText id:'#{name}'")
end