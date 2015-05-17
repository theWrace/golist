package da.se.otherclasses;

import java.io.Serializable;

import da.se.golist.R;
import da.se.golist.activities.LoginActivity;
import da.se.golist.activities.ManageListActivity;
import da.se.interfaces.ManageListFunction;

public class LeaveList implements ManageListFunction, Serializable{
	
	private static final long serialVersionUID = 7723642193709044765L;

	@Override
	public int getQuestionId() {
		return R.string.leavelistquestion;
	}

	@Override
	public void execute(ManageListActivity activity) {
		for(int i = 0; i < activity.getList().getUser().size(); i++){
			if(activity.getList().getUser().get(i).getName().equals(LoginActivity.NAME)){
				activity.getList().getUser().remove(i);
				i--;
			}
		}
		for(int i = 0; i < activity.getList().getInvitedUser().size(); i++){
			if(activity.getList().getInvitedUser().get(i).getName().equals(LoginActivity.NAME)){
				activity.getList().getInvitedUser().remove(i);
				i--;
			}
		}
		String infoText = activity.getString(R.string.infolistleft);
		infoText = infoText.replace("username", LoginActivity.NAME);
		activity.uploadList(activity.getList(), true, infoText);
	}
}
