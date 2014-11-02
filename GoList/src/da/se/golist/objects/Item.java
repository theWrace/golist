package da.se.golist.objects;


public class Item extends GoListObject{	

	private String amount;
		
	public Item(String name, String description, String amount) {
		this.name = name;
		this.description = description;
		this.amount = amount;
	}

	public String getMenge() {
		return amount;
	}

	public void setMenge(String menge) {
		this.amount = menge;
	}

}
