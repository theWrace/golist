package da.se.golist.objects;

import java.util.Date;


public class Item extends GoListObject{	

	private String amount, author;
	public final static int STATE_NORMAL = 1, STATE_BOUGHT = 2, STATE_DISABLED = 3;	
	private int state = 1, id;
	private Date lastEdit;
		
	public Item(int id, String name, String description, String amount, int category, String author, Date lastEdit) {
		this.name = name;
		this.description = description;
		this.amount = amount;
		this.category = category;
		this.id = id;
		this.author = author;
		this.lastEdit = lastEdit;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	public void setState(int state){
		this.state = state;
	}
	
	public int getState(){
		return state;
	}
	
	public int getId(){
		return id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getLastEdit() {
		return lastEdit;
	}

	public void setLastEdit(Date lastEdit) {
		this.lastEdit = lastEdit;
	}
	
}
