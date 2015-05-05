package da.se.otherclasses;

import java.io.Serializable;

import da.se.golist.R;
import da.se.golist.activities.LoginActivity;
import da.se.golist.activities.ManageListActivity;
import da.se.golist.objects.Item;
import da.se.interfaces.ManageListFunction;

public class DeleteBoughtItems implements ManageListFunction, Serializable{
	
	private static final long serialVersionUID = 7654937786276773471L;

	@Override
	public int getQuestionId() {
		return R.string.deleteboughtitems;
	}

	@Override
	public void execute(ManageListActivity activity) {
		for(int i = 0; i < activity.getList().getItems().size(); i++){
			if(((Item) activity.getList().getItems().get(i)).getState() == Item.STATE_BOUGHT){
				activity.getList().getItems().remove(i);
				i--;
			}
		}
		String infoText = activity.getString(R.string.infoboughtitemsdeleted);
		infoText = infoText.replace("username", LoginActivity.NAME);
		activity.uploadList(activity.getList(), false, infoText);
	}
}
