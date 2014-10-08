package da.se.golist.objects;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class ShoppingList {
	
	private ArrayList<Article> articles = new ArrayList<Article>();
	private String name, description = "description";
	
	public ShoppingList(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addArticle(Article article){
		articles.add(article);
	}
	
	public void removeArticle(Article article){
		articles.remove(article);
	}

}
