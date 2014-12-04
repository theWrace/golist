package da.se.golist.activities;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.adapters.UserListAdapter;
import da.se.golist.objects.GoListObject;

public class ShowUsersActivity extends FragmentActivity {

	private UserCollectionPagerAdapter mDemoCollectionPagerAdapter;
	private ViewPager mViewPager;
	private ArrayList<GoListObject> inviteduser, member;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		member = (ArrayList<GoListObject>) getIntent().getExtras().get("members");
		inviteduser = (ArrayList<GoListObject>) getIntent().getExtras().get("invitedusers");
		
		setContentView(R.layout.activity_collection_demo);		
        mDemoCollectionPagerAdapter = new UserCollectionPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
	}

	public class UserCollectionPagerAdapter extends FragmentPagerAdapter {
		public UserCollectionPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			if(i == 0){
				return new MemberFragment(MemberFragment.MEMBER);
			}
			return new MemberFragment(MemberFragment.INVITED);
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int i) {
			if(i == 0){
				return "Members";
			}
			return "Invited Users";
		}
	}
	
	/**
	 * falls user geloescht neue userlisten zurueckgeben
	 */
	@Override
	public void onBackPressed() {
		if(((ArrayList<GoListObject>) getIntent().getExtras().get("members")).size() != member.size() ||
				((ArrayList<GoListObject>) getIntent().getExtras().get("invitedusers")).size() != inviteduser.size()){
			Intent returnIntent = new Intent();
			returnIntent.putExtra("members", member);
			returnIntent.putExtra("invitedusers", inviteduser);
			setResult(RESULT_OK,returnIntent);
		}
		super.onBackPressed();
	}

	public class MemberFragment extends Fragment {
		
		protected final static int MEMBER = 1, INVITED = 2;
		private int type = MEMBER;
		private UserListAdapter listAdapter;
		
		public MemberFragment(int type) {
			this.type = type;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_collection_object, container, false);
			ListView myListsView = (ListView) rootView.findViewById(R.id.listViewMembers);
			
			if(type == MEMBER){
				listAdapter = new UserListAdapter(this.getActivity(), member);
			}else{
				listAdapter = new UserListAdapter(this.getActivity(), inviteduser);
			}
			myListsView.setAdapter(listAdapter);
			//TODO: nicht selber loeschen, nicht jeder darf loeschen
			myListsView.setOnItemLongClickListener(new OnItemLongClickListener() {
				
					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
						if(!(type == MEMBER && member.get(position).getName().equals(LoginActivity.NAME))){
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShowUsersActivity.this);
							 
							alertDialogBuilder.setTitle(R.string.deleteusertitle);
				 
							alertDialogBuilder
								.setMessage(R.string.deleteuserquestion)
								.setCancelable(false)
								.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {
										if(type == MEMBER){										
											member.remove(position);
										}else{
											inviteduser.remove(position);
										}
										listAdapter.notifyDataSetChanged();
										Toast.makeText(getApplicationContext(), "User removed!", Toast.LENGTH_LONG).show();
									}
								  })
								.setNegativeButton("No",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {								
										dialog.cancel();
									}
							});
			 
							alertDialogBuilder.create().show();
						}	
						return true;
					}
			});
			return rootView;
		}
	}
}
