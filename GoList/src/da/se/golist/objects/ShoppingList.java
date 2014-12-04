package da.se.golist.objects;

import java.util.ArrayList;

public class ShoppingList extends GoListObject{
	
	private ArrayList<GoListObject> items = new ArrayList<GoListObject>();
	private ArrayList<GoListObject> user = new ArrayList<GoListObject>();
	private ArrayList<GoListObject> invitedUser = new ArrayList<GoListObject>();
	private int id;
	
	public ShoppingList(String name){
		this.name = name;
		description = "description";
	}
	
	public int getID(){
		return id;
	}
	
	public void setID(int id){
		this.id = id;
	}	

	public void addItem(Item item){
		items.add(item);
	}
	
	public void removeItem(Item item){
		items.remove(item);
	}
	
	public ArrayList<GoListObject> getItems(){
		return items;
	}
	
	public ArrayList<GoListObject> getInvitedUser(){
		return invitedUser;
	}
	
	public ArrayList<GoListObject> getUser(){
		return user;
	}
	
	public void addUser(User user){
		this.user.add(user);
	}
	
	public void inviteUser(User user){
		invitedUser.add(user);
	}
	
	public void updateUser(ArrayList<GoListObject> user){
		this.user = user;
	}
	
	public void updateInvitedUser(ArrayList<GoListObject> invitedUser){
		this.invitedUser = invitedUser;
	}
	
	public void updateItems(ArrayList<GoListObject> items){
		this.items = items;
	}

}
