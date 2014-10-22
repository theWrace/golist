package da.se.golist.objects;

import java.util.ArrayList;

public class ShoppingList extends GoListObject{
	
	private ArrayList<GoListObject> articles = new ArrayList<GoListObject>();
	private ArrayList<GoListObject> people = new ArrayList<GoListObject>();
	private ArrayList<GoListObject> invitedPeople = new ArrayList<GoListObject>();
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

	public void addArticle(Article article){
		articles.add(article);
	}
	
	public void removeArticle(Article article){
		articles.remove(article);
	}
	
	public ArrayList<GoListObject> getArticles(){
		return articles;
	}
	
	public ArrayList<GoListObject> getInvitedPeople(){
		return invitedPeople;
	}
	
	public ArrayList<GoListObject> getPeople(){
		return people;
	}
	
	public void addUser(User user){
		people.add(user);
	}
	
	public void inviteUser(User user){
		invitedPeople.add(user);
	}

}
