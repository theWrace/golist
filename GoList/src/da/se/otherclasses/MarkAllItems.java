package da.se.otherclasses;

import java.io.Serializable;

import da.se.golist.R;
import da.se.golist.activities.LoginActivity;
import da.se.golist.activities.ManageListActivity;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.Item;
import da.se.interfaces.ManageListFunction;

public class MarkAllItems implements ManageListFunction, Serializable{
	
	private static final long serialVersionUID = 7586355031806241123L;
	private boolean bought;
	
	public MarkAllItems(boolean bought) {
		this.bought = bought;
	}

	@Override
	public int getQuestionId() {
		if(bought){
			return R.string.markallitemsboughtquestion;
		}
		return R.string.markallitemsnotboughtquestion;
	}

	@Override
	public void execute(ManageListActivity activity) {
		int state;
		String infoText;
		if(bought){
			state = Item.STATE_BOUGHT;
			infoText = activity.getString(R.string.infoallitemsbought).replace("username", LoginActivity.NAME);
		}else{
			state = Item.STATE_NORMAL;
			infoText = activity.getString(R.string.infoallitemsnotbought).replace("username", LoginActivity.NAME);
		}
		for(GoListObject item : activity.getList().getItems()){
			((Item) item).setState(state);
		}						 
		activity.uploadList(activity.getList(), false, infoText);
	}
}
