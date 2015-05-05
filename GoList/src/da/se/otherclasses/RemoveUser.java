package da.se.otherclasses;

import java.io.Serializable;

import da.se.golist.R;
import da.se.golist.activities.LoginActivity;
import da.se.golist.activities.ManageListActivity;
import da.se.interfaces.ManageListFunction;

public class RemoveUser implements ManageListFunction, Serializable{
	
	private static final long serialVersionUID = 4221363088051885403L;

	@Override
	public int getQuestionId() {
		return R.string.deleteuserquestion;
	}

	@Override
	public void execute(ManageListActivity activity) {
		String username = activity.getIntent().getStringExtra("username");
		for(int i = 0; i < activity.getList().getUser().size(); i++){
			if(activity.getList().getUser().get(i).getName().equals(username)){
				activity.getList().getUser().remove(i);
				break;
			}
		}
		for(int i = 0; i < activity.getList().getInvitedUser().size(); i++){
			if(activity.getList().getInvitedUser().get(i).getName().equals(username)){
				activity.getList().getInvitedUser().remove(i);
				break;
			}
		}
		String infoText = activity.getString(R.string.infouserremoved);
		infoText = infoText.replace("username1", LoginActivity.NAME);
		infoText = infoText.replace("username2", username);
		infoText = infoText.replace("listname", activity.getList().getName());
		activity.uploadList(activity.getList(), true, infoText);
	}

}
