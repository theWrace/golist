package da.se.otherclasses;

import java.io.Serializable;

import da.se.golist.R;
import da.se.golist.activities.LoginActivity;
import da.se.golist.activities.ManageListActivity;
import da.se.golist.objects.ShoppingList;
import da.se.interfaces.ManageListFunction;

public class DeleteAllItems implements ManageListFunction, Serializable{
	
	private static final long serialVersionUID = -5992315112068161393L;

	@Override
	public int getQuestionId() {
		return R.string.deleteallitemsquestion;
	}

	@Override
	public void execute(ManageListActivity activity) {
		ShoppingList list = activity.getList();
		list.getItems().clear();
		list.setDescription("0 Items");
		String infoText = activity.getString(R.string.infoitemsdeleted);
		infoText = infoText.replace("username", LoginActivity.NAME);
		activity.uploadList(list, false, infoText);
	}

}
