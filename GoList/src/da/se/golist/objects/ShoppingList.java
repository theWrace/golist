package da.se.golist.objects;

import java.util.ArrayList;

public class ShoppingList {
	
	private ArrayList<Article> articles = new ArrayList<Article>();
	
	public void addArticle(Article article){
		articles.add(article);
	}
	
	public void removeArticle(Article article){
		articles.remove(article);
	}

}
