package da.se.golist.activities;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
				return new MemberFragment(MemberFragment.MEMBER);
			default:
				return new MemberFragment(MemberFragment.INVITED);
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
								if(!(type == MEMBER && list.getUser().get(position).getName().equalsIgnoreCase(LoginActivity.NAME))){
									AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShowUserActivity.this);
									 
									alertDialogBuilder.setTitle(R.string.deleteusertitle);
						 
									alertDialogBuilder
										.setMessage(R.string.deleteuserquestion)
										.setCancelable(false)
										.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog,int id) {
												String username;
												if(type == MEMBER){
													username = list.getUser().get(position).getName();
													list.getUser().remove(position);
												}else{
													username = list.getInvitedUser().get(position).getName();
													list.getInvitedUser().remove(position);
												}
												listAdapter.notifyDataSetChanged();
												String infoText = getString(R.string.infouserremoved).replace("username1", LoginActivity.NAME);
												infoText = infoText.replace("username2", username);
												infoText = infoText.replace("listname", list.getName());
												uploadList(list, true, infoText);
											}
										  })
										.setNegativeButton("No",new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog,int id) {								
												dialog.cancel();
											}
									});
					 
									alertDialogBuilder.create().show();
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
		try {
			String message = json.getString("message");
			if(message.equals("succes") && json.has("data")){
				JSONArray dataArray = json.getJSONArray("data");
				list = (ShoppingList) objectFromString(dataArray.getString(0));
			}
		} catch (JSONException e) {			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(afterRefresh != null){
			afterRefresh.applyChanges();
			afterRefresh = null;
		}
	}

	@Override
	protected void preExcecute() {}

}
