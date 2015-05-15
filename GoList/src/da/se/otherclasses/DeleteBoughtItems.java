package da.se.otherclasses;

import java.io.Serializable;

import da.se.golist.R;
import da.se.golist.activities.LoginActivity;
import da.se.golist.activities.ManageListActivity;
import da.se.golist.objects.Item;
import da.se.golist.objects.ShoppingList;
import da.se.interfaces.ManageListFunction;

public class DeleteBoughtItems implements ManageListFunction, Serializable{
	
	private static final long serialVersionUID = 7654937786276773471L;

	@Override
	public int getQuestionId() {
		return R.string.deleteboughtitems;
	}

	@Override
	public void execute(ManageListActivity activity) {
		ShoppingList list = activity.getList();
		for(int i = 0; i < list.getItems().size(); i++){
			if(((Item) list.getItems().get(i)).getState() == Item.STATE_BOUGHT){
				list.getItems().remove(i);
				i--;
			}
		}
		list.setDescription(list.getItems().size() + " Items");
		
		String infoText = activity.getString(R.string.infoboughtitemsdeleted);
		infoText = infoText.replace("username", LoginActivity.NAME);
		activity.uploadList(list, false, infoText);
	}
}
