package da.se.golist.objects;

import java.io.Serializable;

public abstract class GoListObject implements Serializable, Comparable<GoListObject>{
	
	protected String name, description;
	protected int category;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}
	
	public int compareTo(GoListObject another) {		
		return getCategory() - another.getCategory();
	}
}
