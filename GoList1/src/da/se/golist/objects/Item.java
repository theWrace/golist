package da.se.golist.objects;


public class Item extends GoListObject{	

	private String amount;
	public final static int STATE_NORMAL = 1;
	public final static int STATE_BOUGHT = 2;
	public final static int STATE_DISABLED = 3;	
	private int state = 1;
		
	public Item(String name, String description, String amount, int category) {
		this.name = name;
		this.description = description;
		this.amount = amount;
		this.category = category;
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
	
}
