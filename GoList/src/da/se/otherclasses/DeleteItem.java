package da.se.otherclasses;

import java.io.Serializable;

import da.se.golist.R;
import da.se.golist.activities.LoginActivity;
import da.se.golist.activities.ManageListActivity;
import da.se.golist.objects.Item;
import da.se.golist.objects.ShoppingList;
import da.se.interfaces.ManageListFunction;

public class DeleteItem implements ManageListFunction, Serializable{

	private static final long serialVersionUID = 5598076532652278686L;

	@Override
	public int getQuestionId() {
		return R.string.deleteitemquestion;
	}

	@Override
	public void execute(ManageListActivity activity) {
		Item item = activity.getList().getItemById(activity.getIntent().getIntExtra("itemid", 0));
		ShoppingList list = activity.getList();
		String infoText = activity.getString(R.string.infoitemdeleted);
		infoText = infoText.replace("username", LoginActivity.NAME);		
		infoText = infoText.replace("itemname", item.getName());
		list.removeItem(item);
		list.setDescription(list.getItems().size() + " Items");
		activity.uploadList(list, false, infoText);		
	}

}
