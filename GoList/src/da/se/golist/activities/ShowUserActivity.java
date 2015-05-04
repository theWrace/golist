package da.se.golist.activities;

import java.io.IOException;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import da.se.golist.R;
import da.se.golist.adapters.UserListAdapter;
import da.se.golist.objects.ShoppingList;

public class ShowUserActivity extends BaseActivity{
	
	private FragmentPagerAdapter mDemoCollectionPagerAdapter;
	private ViewPager mViewPager;
	private ShoppingList list = null;
	private boolean isAdmin = false;
	private int id;
	private MemberFragment userFragment, invitedFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.showuserlayout);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}
	
	@Override
	protected void onStart() {
		id = getIntent().getExtras().getInt("id");
		refreshList(new AfterRefresh() {
			
			@Override
			public void applyChanges() {
				isAdmin = isAdmin(list, LoginActivity.NAME);
				mDemoCollectionPagerAdapter = new PagerAdapter(getSupportFragmentManager());
				      
		        mViewPager = (ViewPager) findViewById(R.id.pager);
		        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
			}
			
		}, id);
		
		super.onStart();
	}	
	
	public class PagerAdapter extends FragmentPagerAdapter {
		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			switch (i){
			case 0:
				return (userFragment = new MemberFragment(MemberFragment.MEMBER));
			default:
				return (invitedFragment = new MemberFragment(MemberFragment.INVITED));
			}
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int i) {
			switch (i){
			case 0:
				return "User";
			default:
				return "Invited";
			}
		}
	}
	
	//Fragment zum Anzeigen von Mitgliedern oder eingeladenen Nutzern in Liste
		public class MemberFragment extends Fragment {
			
			protected final static int MEMBER = 1, INVITED = 2;
			private int type = MEMBER;
			private UserListAdapter listAdapter;
			
			public MemberFragment(int type) {
				this.type = type;
			}
			
			public void updateAdapter(){
				if(type == MEMBER){
					listAdapter.updateListObjects(list.getUser());
				}else{
					listAdapter.updateListObjects(list.getInvitedUser());
				}
			}

			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
				View rootView = inflater.inflate(R.layout.showuserlistview, container, false);
				ListView myListsView = (ListView) rootView.findViewById(R.id.listViewMembers);
				
				if(type == MEMBER){
					listAdapter = new UserListAdapter(this.getActivity(), list.getUser());
				}else{
					listAdapter = new UserListAdapter(this.getActivity(), list.getInvitedUser());
				}
				myListsView.setAdapter(listAdapter);

				if(isAdmin){
					myListsView.setOnItemLongClickListener(new OnItemLongClickListener() {
						
							@Override
							public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
								if(!(type == MEMBER && list.getUser().get(position).getName().equals(LoginActivity.NAME))){
									String username;
									if(type == MEMBER){
										username = list.getUser().get(position).getName();
									}else{
										username = list.getInvitedUser().get(position).getName();
									}
									Intent intent = new Intent(ShowUserActivity.this, ManageListActivity.class);
									intent.putExtra("type", ManageListActivity.TYPE_REMOVE_USER);
									intent.putExtra("username", username);
									intent.putExtra("id", list.getID());
									startActivityForResult(intent, 0);
								}	
								return true;
							}
					});
				}
				
				return rootView;
			}
		}

	@Override
	protected void postExcecute(JSONObject json) {
		if(getListFromJson(json) != null){
			list = getListFromJson(json);
		}
		runAfterRefresh();
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		if(data != null && data.getExtras().containsKey("list")){
			try {
				Intent returnIntent = new Intent();
				returnIntent.putExtra("list", (ShoppingList)data.getExtras().get("list"));
				this.setResult(RESULT_OK, returnIntent);
				
				list = (ShoppingList) objectFromString(data.getStringExtra("list"));
				userFragment.updateAdapter();
				invitedFragment.updateAdapter();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void preExcecute() {}

}
