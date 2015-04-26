package da.se.golist.activities;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import da.se.golist.R;
import da.se.golist.adapters.UserListAdapter;
import da.se.golist.objects.GoListObject;
import da.se.golist.objects.Item;
import da.se.golist.objects.ShoppingList;

public class ListSettingsActivity extends BaseActivity {

	private FragmentPagerAdapter mDemoCollectionPagerAdapter;
	private ViewPager mViewPager;
	private ShoppingList list = null;
	private boolean deleted = false;
	private int id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_collection_demo);
	}

	public class NormalPagerAdapter extends FragmentPagerAdapter {
		public NormalPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			switch (i){
			case 0:
				return new MemberFragment(MemberFragment.MEMBER);
			case 1:
				return new MemberFragment(MemberFragment.INVITED);
			}
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int i) {
			switch (i){
			case 0:
				return "Members";
			case 1:
				return "Invited Users";
			}
			return null;
		}
	}
	
	public class AdminPagerAdapter extends FragmentPagerAdapter {
		public AdminPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			switch (i){
			case 0:
				return new SettingsFragment();
			case 1:
				return new MemberFragment(MemberFragment.MEMBER);
			case 2:
				return new MemberFragment(MemberFragment.INVITED);
			}
			return null;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int i) {
			switch (i){
			case 0:
				return "Settings";
			case 1:
				return "Members";
			case 2:
				return "Invited Users";
			}
			return null;
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

			if(isAdmin(list, LoginActivity.NAME)){
				myListsView.setOnItemLongClickListener(new OnItemLongClickListener() {
					
						@Override
						public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
							if(!(type == MEMBER && list.getUser().get(position).getName().equalsIgnoreCase(LoginActivity.NAME))){
								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListSettingsActivity.this);
								 
								alertDialogBuilder.setTitle(R.string.deleteusertitle);
					 
								alertDialogBuilder
									.setMessage(R.string.deleteuserquestion)
									.setCancelable(false)
									.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,int id) {
											if(type == MEMBER){										
												list.getUser().remove(position);
											}else{
												list.getInvitedUser().remove(position);
											}
											listAdapter.notifyDataSetChanged();
											uploadList(list, true);
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
	
	//Fragment für das Anzeigen der Einstellungen
	public class SettingsFragment extends Fragment {
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.editlistlayout, container, false);
			Button buttonDelete = (Button) rootView.findViewById(R.id.buttonDelete);
			Button buttonInviteUser = (Button) rootView.findViewById(R.id.buttonInviteAUser);			
			Button buttonChangeName = (Button) rootView.findViewById(R.id.buttonChangeName);
			Button buttonImportItems = (Button) rootView.findViewById(R.id.buttonImportItems);
			Button buttonDeleteBoughtItems = (Button) rootView.findViewById(R.id.buttonDeleteBoughtItems);
			Button buttonDeleteAllItems = (Button) rootView.findViewById(R.id.buttonDeleteItems);
			Button buttonMarkAllBought= (Button) rootView.findViewById(R.id.buttonMarkItemsBought);
			Button buttonMarkAllNotBought= (Button) rootView.findViewById(R.id.buttonMarkItemsUnbought);
			
			Typeface tf1 = Typeface.createFromAsset(ListSettingsActivity.this.getAssets(), "fonts/geosanslight.ttf");
			buttonDelete.setTypeface(tf1);
			buttonInviteUser.setTypeface(tf1);
			buttonChangeName.setTypeface(tf1);
			buttonImportItems.setTypeface(tf1);
			buttonDeleteAllItems.setTypeface(tf1);
			buttonMarkAllBought.setTypeface(tf1);
			buttonMarkAllNotBought.setTypeface(tf1);
			buttonDeleteBoughtItems.setTypeface(tf1);
			
			buttonDeleteBoughtItems.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListSettingsActivity.this);
					 
					alertDialogBuilder.setTitle(R.string.deleteboughtitemstitle);
		 
					alertDialogBuilder
						.setMessage(R.string.deleteallitemsboughtquestion)
						.setCancelable(false)
						.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int id) {
								refreshList(new AfterRefresh() {
									
									@Override
									public void applyChanges() {
										for(int i = 0; i < list.getItems().size(); i++){
											if(((Item) list.getItems().get(i)).getState() == Item.STATE_BOUGHT){
												list.getItems().remove(i);
												i--;
											}
										}
										uploadList(list, false);										
									}
								}, list.getID());
								
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
			});
			
			buttonDeleteAllItems.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListSettingsActivity.this);
					 
					alertDialogBuilder.setTitle(R.string.deleteallitemstitle);
		 
					alertDialogBuilder
						.setMessage(R.string.deleteallitemsquestion)
						.setCancelable(false)
						.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int id) {
								refreshList(new AfterRefresh() {
									
									@Override
									public void applyChanges() {
										list.getItems().clear();
										uploadList(list, false);										
									}
								}, list.getID());								
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
			});
			
			buttonMarkAllBought.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListSettingsActivity.this);
					 
					alertDialogBuilder.setTitle(R.string.markallitemsboughttitle);
		 
					alertDialogBuilder
						.setMessage(R.string.markallitemsboughtquestion)
						.setCancelable(false)
						.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int id) {
								refreshList(new AfterRefresh() {
									
									@Override
									public void applyChanges() {
										for(GoListObject item : list.getItems()){
											((Item) item).setState(Item.STATE_BOUGHT);
										}
										uploadList(list, false);										
									}
								}, list.getID());
								
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
			});
			
			buttonMarkAllNotBought.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListSettingsActivity.this);
					 
					alertDialogBuilder.setTitle(R.string.markallitemsnotboughttitle);
		 
					alertDialogBuilder
						.setMessage(R.string.markallitemsnotboughtquestion)
						.setCancelable(false)
						.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int id) {
								refreshList(new AfterRefresh() {
									
									@Override
									public void applyChanges() {
										for(GoListObject item : list.getItems()){
											((Item) item).setState(Item.STATE_NORMAL);
										}
										uploadList(list, false);										
									}
									
								}, list.getID());								
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
			});
			
			buttonInviteUser.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent newListIntent = new Intent(ListSettingsActivity.this, InviteUserActivity.class);
					newListIntent.putExtra("list", list);
					startActivity(newListIntent);					
				}
			});			
			
			buttonChangeName.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder alert = new AlertDialog.Builder(ListSettingsActivity.this);
		        	alert.setMessage("Please enter a new name");
		 
		            final EditText input = new EditText(ListSettingsActivity.this);
		            input.setText(list.getName());
		            alert.setView(input);
		 
		        	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		        		public void onClick(DialogInterface dialog, int whichButton) {
			        		if(input.getEditableText().toString().trim().length() != 0 && !input.getEditableText().toString().trim().equals(list.getName())){
			        			
			        			refreshList(new AfterRefresh() {
									
									@Override
									public void applyChanges() {
										list.setName(input.getEditableText().toString().trim());
										
										try {
											new LoadDataTask(new String[]{"id", "data", "name"},new String[]{list.getID()+"", objectToString(list), list.getName()}, "updatelist.php").execute();
										} catch (IOException e) {
											e.printStackTrace();
										}										
									}
								}, list.getID());			        			
								
							}else{
								Toast.makeText(getApplicationContext(), "You didn't insert a new name!", Toast.LENGTH_LONG).show();
							}		
		        		}
		        	});
		        	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		        	  public void onClick(DialogInterface dialog, int whichButton) {
		        		  dialog.cancel();
		        	  }
		        	});
		        	
		        	AlertDialog alertDialog = alert.create();
		        	alertDialog.show();					
				}
			});			
			
			buttonDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListSettingsActivity.this);
					 
					alertDialogBuilder.setTitle(R.string.deletelisttitle);
		 
					alertDialogBuilder
						.setMessage(R.string.deletelistquestion)
						.setCancelable(false)
						.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int id) {
								deleted = true;
								new LoadDataTask(new String[]{"id"},new String[]{list.getID()+""}, "deletelistbyid.php").execute();			
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
			});
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
		
		if(list == null){
			Toast.makeText(getApplicationContext(), "Failed to load List!", Toast.LENGTH_LONG).show();
			finish();
			return;
		}				
		
		if(afterRefresh == null){
			//Liste wurde geupdated
			try {
				if(json.getString("message").contains("succes")){
					if(deleted){
						Intent returnIntent = new Intent();
						returnIntent.putExtra("finish","");
						setResult(RESULT_OK,returnIntent);
						Toast.makeText(getApplicationContext(), "List deleted!", Toast.LENGTH_LONG).show();
						finish();
						return;
					}
					Toast.makeText(getApplicationContext(), "List updated!", Toast.LENGTH_LONG).show();		
				}else{
					Toast.makeText(getApplicationContext(), "Failed to update List: " + json.getString("message"), Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				Toast.makeText(getApplicationContext(), "Failed to update List: " + e.getMessage(), Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			return;
		}
		
		afterRefresh.applyChanges();
		afterRefresh = null;
		
		deleted = false;
	}

	@Override
	protected void preExcecute() {}
	
	@Override
	protected void onStart() {
		id = getIntent().getExtras().getInt("id");
		refreshList(new AfterRefresh() {
			
			@Override
			public void applyChanges() {
				if(isAdmin(list, LoginActivity.NAME)){
					mDemoCollectionPagerAdapter = new AdminPagerAdapter(getSupportFragmentManager());
				}else{
					mDemoCollectionPagerAdapter = new NormalPagerAdapter(getSupportFragmentManager());
				}        
		        mViewPager = (ViewPager) findViewById(R.id.pager);
		        mViewPager.setAdapter(mDemoCollectionPagerAdapter);		        
			}
			
		}, id);
		
		super.onStart();
	}
	
}
