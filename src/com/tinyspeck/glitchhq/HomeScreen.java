package com.tinyspeck.glitchhq;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

import com.flurry.android.FlurryAgent;
import com.tinyspeck.android.GlitchRequest;

public class HomeScreen extends FragmentActivity {

	static final int DIALOG_LOGIN_FAIL_ID = 0;
	static final int DIALOG_REQUEST_FAIL_ID = 1;

	static final int TAB_PROFILE = 0;
	static final int TAB_ACTIVITY = 1;
	static final int TAB_SKILLS = 2;
	static final int TAB_UNLEARN = 3;
	static final int TAB_FRIENDS = 4;

	static final private int MENU_COMMAND_REFRESH = Menu.FIRST + 0;
	static final private int MENU_COMMAND_MORE = Menu.FIRST + 1;

	private Boolean m_showingSidebar = false;

	private MyApplication m_application;
	private String m_selfPlayerID;
	private String m_selfPlayerName;
	private View m_spinner;
	private int m_curTab = TAB_ACTIVITY;
	private Page m_curPage = Page.Activity;
	private Page m_newPage;
	private int skillOrUnlearn = TAB_SKILLS;

	private ProfileFragment m_profileFrm;
	private SkillFragment m_skillFrm;
	private UnlearnFragment m_unlearnFrm;
	private ActivityFragment m_activityFrm;
	private QuestsFragment m_questsFrm;
	private FriendsFragment m_friendsFrm;
	private MailboxFragment m_mailboxFrm;
	private SettingsFragment m_settingsFrm;
	private AchievementCategoriesFragment m_achievementsFrm;
	private EncyclopediaCategoriesFragment m_encyclopediaFrm;
	private RecentSnapsFragment m_recentSnapsFrm;
	private View m_stack;

	private BaseFragment m_curFrm;

	private View m_profileView, m_activityView, m_skillsView, m_unlearnView,
			m_questsView, m_friendsView, m_mailboxView, m_settingsView, m_achievementsView,
			m_encyclopediaView, m_recentSnapsView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Util.getDevicePhysicalSize(this) < 6.0) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		m_application = (MyApplication) getApplicationContext();
		m_application.homeScreen = this;
		m_application.init(this);
		
		setTitle(getResources().getString(R.string.str_main_title));
		initLayout();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
				
		if (m_newPage != null)
		{
			if (m_curPage == m_newPage) {
				clearFragmentStack();
			} else {
				m_curPage = m_newPage;
				
				Handler handler = new Handler();
				Runnable runnable = new Runnable() {
				    public void run() {
				    	switch (m_curPage) {
						case Profile:
							setCurrentFragment(m_profileFrm, false);
							break;
						case Activity:
							setCurrentFragment(m_activityFrm, false);
							break;
						case Skills:
							setCurrentFragment(m_skillFrm, false);
							break;
						case Quests:
							setCurrentFragment(m_questsFrm, false);
							break;
						case Friends:
							setCurrentFragment(m_friendsFrm, false);
							break;
						case Encyclopedia:
							setCurrentFragment(m_encyclopediaFrm, false);
							break;
						case RecentSnaps:
							setCurrentFragment(m_recentSnapsFrm, false);
							break;
						case Achievements:
							setCurrentFragment(m_achievementsFrm, false);
							break;
						case Mailbox:
							setCurrentFragment(m_mailboxFrm, false);
							break;
						case Settings:
							setCurrentFragment(m_settingsFrm, false);
							break;
						default:
							break;
						}
				    }
				};
				
				handler.postDelayed(runnable, 1);
			}			
		}
		
		m_newPage = null;
	}

	public void setSelectedPage(Page page) {
		if (page != null) {
			m_newPage = page;
			switch (m_newPage) {
			case Profile:
				m_curFrm = m_profileFrm;
				break;
			case Activity:
				m_curFrm = m_activityFrm;
				break;
			case Skills:
				m_curFrm = m_skillFrm;
				break;
			case Quests:
				m_curFrm = m_questsFrm;
				break;
			case Friends:
				m_curFrm = m_friendsFrm;
				break;
			case Encyclopedia:
				m_curFrm = m_encyclopediaFrm;
				break;
			case Achievements:
				m_curFrm = m_achievementsFrm;
				break;
			case Mailbox:
				m_curFrm = m_mailboxFrm;
				break;
			case RecentSnaps:
				m_curFrm = m_recentSnapsFrm;
				break;
			case Settings:
				m_curFrm = m_settingsFrm;
				break;
			default:
				break;
			}
		}
	}
	
	/*
	 * Sidebar methods
	 */

	static final private long m_sidebarAnimationDuration = 500; // half second
																// (500ms)
	static final private float m_sidebarDeltaX = 0.8f; // 80%

	public void showSidebar() {
		
		Intent intent = new Intent();
		intent.setClass(HomeScreen.this, Sidebar.class);
		startActivityForResult(intent, 0); 
		
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		if (isShowingSidebar()) {
			// Get main stack location
			Display display = getWindowManager().getDefaultDisplay();
			int width = display.getWidth();

			// Touch location X
			float x = ev.getX();
			ev.setLocation(ev.getX(), ev.getY()-20.0f);
			
			//m_sidebarView.dispatchTouchEvent(ev);
			m_stack.getParent().requestDisallowInterceptTouchEvent(true);
		} else {
			// Handle event normally
			return super.dispatchTouchEvent(ev);
		}

		// Handle event normally
		return super.dispatchTouchEvent(ev);
	}

	public Boolean isShowingSidebar() {
		// If stack is away from the left side
		return m_showingSidebar;
	}

	public void dismissSidebar() {
		// Get display width
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();

		// Create the dismiss animation
		Animation animation = new TranslateAnimation(width, 0, 0, 0);
		animation.setDuration(m_sidebarAnimationDuration);

		animation.setAnimationListener(new AnimationListener() {

			public void onAnimationEnd(Animation animation) {
				// Get display width
				Display display = getWindowManager().getDefaultDisplay();

				// Move back to normal position and fill the whole display
				// width-wise
				// m_stack.layout(-27, 0, display.getWidth(),
				// m_stack.getHeight());

				HomeScreen.this.m_showingSidebar = false;
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationStart(Animation animation) {
			}
		});

		animation.setFillAfter(true);

		// Run the dismiss animation
		m_stack.startAnimation(animation);

		m_stack.getParent().bringChildToFront(m_stack);
	}

	/*
	 * End sidebar methods
	 */

	public void clearFragmentStack() {
		FragmentManager fm = getSupportFragmentManager();
		int nCount = fm.getBackStackEntryCount();
		for (int i = 0; i < nCount; i++)
			fm.popBackStack();
	}

	public void setCurrentFragmentSkills() {
		if (m_curTab == TAB_SKILLS) {
			clearFragmentStack();
		} else {
			setCurrentFragment(m_skillFrm, false);
			m_curTab = TAB_SKILLS;
			skillOrUnlearn = TAB_SKILLS;
		}
	}

	public void setCurrentFragmentUnlearn() {
		if (m_curTab == TAB_UNLEARN) {
			clearFragmentStack();
		} else {
			setCurrentFragment(m_unlearnFrm, false);
			m_curTab = TAB_UNLEARN;
			skillOrUnlearn = TAB_UNLEARN;
		}
	}
	
	public MailboxFragment getMailboxFragment()
	{
		return m_mailboxFrm;
	}

	public void setCurrentFragment(Fragment f, boolean bAddToStack) {
		m_curFrm = (BaseFragment) f;
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
				android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		int viewId = 0;
		
		if (f instanceof BaseFragment)
			((BaseFragment) f).logPageView();

		if (m_curPage == Page.Profile) {
			viewId = R.id.fragmentView_profile;
			m_profileView.setVisibility(View.VISIBLE);
			m_activityView.setVisibility(View.GONE);
			m_skillsView.setVisibility(View.GONE);
			m_unlearnView.setVisibility(View.GONE);
			m_friendsView.setVisibility(View.GONE);
			m_achievementsView.setVisibility(View.GONE);
			m_questsView.setVisibility(View.GONE);
			m_mailboxView.setVisibility(View.GONE);
			m_settingsView.setVisibility(View.GONE);		
			m_encyclopediaView.setVisibility(View.GONE);
			m_recentSnapsView.setVisibility(View.GONE);

		} else if (m_curPage == Page.Activity) {
			viewId = R.id.fragmentView_activity;
			m_profileView.setVisibility(View.GONE);
			m_activityView.setVisibility(View.VISIBLE);
			m_skillsView.setVisibility(View.GONE);
			m_unlearnView.setVisibility(View.GONE);
			m_questsView.setVisibility(View.GONE);
			m_achievementsView.setVisibility(View.GONE);
			m_friendsView.setVisibility(View.GONE);
			m_mailboxView.setVisibility(View.GONE);
			m_settingsView.setVisibility(View.GONE);
			m_encyclopediaView.setVisibility(View.GONE);
			m_recentSnapsView.setVisibility(View.GONE);

		} else if (m_curPage == Page.Friends) {
			viewId = R.id.fragmentView_friends;
			m_friendsView.setVisibility(View.VISIBLE);
			m_profileView.setVisibility(View.GONE);
			m_activityView.setVisibility(View.GONE);
			m_skillsView.setVisibility(View.GONE);
			m_achievementsView.setVisibility(View.GONE);
			m_unlearnView.setVisibility(View.GONE);
			m_questsView.setVisibility(View.GONE);
			m_mailboxView.setVisibility(View.GONE);
			m_settingsView.setVisibility(View.GONE);
			m_encyclopediaView.setVisibility(View.GONE);
			m_recentSnapsView.setVisibility(View.GONE);
			
		} else if (m_curPage == Page.Mailbox) {
			viewId = R.id.fragmentView_mailbox;
			m_mailboxView.setVisibility(View.VISIBLE);
			m_friendsView.setVisibility(View.GONE);
			m_profileView.setVisibility(View.GONE);
			m_activityView.setVisibility(View.GONE);
			m_achievementsView.setVisibility(View.GONE);
			m_skillsView.setVisibility(View.GONE);
			m_questsView.setVisibility(View.GONE);
			m_unlearnView.setVisibility(View.GONE);
			m_settingsView.setVisibility(View.GONE);
			m_encyclopediaView.setVisibility(View.GONE);
			m_recentSnapsView.setVisibility(View.GONE);
			
		} else if (m_curPage == Page.Achievements) {
			viewId = R.id.fragmentView_achievements;
			m_achievementsView.setVisibility(View.VISIBLE);
			m_mailboxView.setVisibility(View.GONE);
			m_friendsView.setVisibility(View.GONE);
			m_profileView.setVisibility(View.GONE);
			m_activityView.setVisibility(View.GONE);
			m_skillsView.setVisibility(View.GONE);
			m_questsView.setVisibility(View.GONE);
			m_unlearnView.setVisibility(View.GONE);			
			m_settingsView.setVisibility(View.GONE);
			m_encyclopediaView.setVisibility(View.GONE);
			m_recentSnapsView.setVisibility(View.GONE);
			
		} else if (m_curPage == Page.Quests) { 
			viewId = R.id.fragmentView_quests;
			m_questsView.setVisibility(View.VISIBLE);					
			m_friendsView.setVisibility(View.GONE);
			m_profileView.setVisibility(View.GONE);
			m_activityView.setVisibility(View.GONE);
			m_achievementsView.setVisibility(View.GONE);
			m_skillsView.setVisibility(View.GONE);
			m_unlearnView.setVisibility(View.GONE);
			m_mailboxView.setVisibility(View.GONE);
			m_settingsView.setVisibility(View.GONE);
			m_encyclopediaView.setVisibility(View.GONE);
			m_recentSnapsView.setVisibility(View.GONE);
			
		} else if (m_curPage == Page.RecentSnaps) { 
			viewId = R.id.fragmentView_recentSnaps;
			m_recentSnapsView.setVisibility(View.VISIBLE);
			m_questsView.setVisibility(View.GONE);					
			m_friendsView.setVisibility(View.GONE);
			m_profileView.setVisibility(View.GONE);
			m_activityView.setVisibility(View.GONE);
			m_achievementsView.setVisibility(View.GONE);
			m_skillsView.setVisibility(View.GONE);
			m_unlearnView.setVisibility(View.GONE);
			m_mailboxView.setVisibility(View.GONE);
			m_settingsView.setVisibility(View.GONE);
			m_encyclopediaView.setVisibility(View.GONE);
			
		} else if (m_curPage == Page.Encyclopedia) {
			viewId = R.id.fragmentView_encyclopedia;
			m_encyclopediaView.setVisibility(View.VISIBLE);
			m_settingsView.setVisibility(View.GONE);			
			m_friendsView.setVisibility(View.GONE);
			m_profileView.setVisibility(View.GONE);
			m_activityView.setVisibility(View.GONE);
			m_achievementsView.setVisibility(View.GONE);
			m_skillsView.setVisibility(View.GONE);
			m_questsView.setVisibility(View.GONE);
			m_unlearnView.setVisibility(View.GONE);
			m_mailboxView.setVisibility(View.GONE);
			m_recentSnapsView.setVisibility(View.GONE);
			
		} else if (m_curPage == Page.Settings) {
			viewId = R.id.fragmentView_settings;
			m_settingsView.setVisibility(View.VISIBLE);			
			m_friendsView.setVisibility(View.GONE);
			m_profileView.setVisibility(View.GONE);
			m_activityView.setVisibility(View.GONE);
			m_achievementsView.setVisibility(View.GONE);
			m_skillsView.setVisibility(View.GONE);
			m_questsView.setVisibility(View.GONE);
			m_unlearnView.setVisibility(View.GONE);
			m_mailboxView.setVisibility(View.GONE);
			m_encyclopediaView.setVisibility(View.GONE);
			m_recentSnapsView.setVisibility(View.GONE);
			
		} else {
			m_profileView.setVisibility(View.GONE);
			m_activityView.setVisibility(View.GONE);
			m_friendsView.setVisibility(View.GONE);
			m_achievementsView.setVisibility(View.GONE);
			m_mailboxView.setVisibility(View.GONE);
			m_questsView.setVisibility(View.GONE);
			m_settingsView.setVisibility(View.GONE);
			m_encyclopediaView.setVisibility(View.GONE);
			m_recentSnapsView.setVisibility(View.GONE);

			if (f instanceof SkillFragment) {
				viewId = R.id.fragmentView_skills;
				m_skillsView.setVisibility(View.VISIBLE);
				m_unlearnView.setVisibility(View.GONE);
			} else if (f instanceof UnlearnFragment) {
				viewId = R.id.fragmentView_unlearn;
				m_skillsView.setVisibility(View.GONE);
				m_unlearnView.setVisibility(View.VISIBLE);
			} else if (f instanceof SkillDetailFragment) {

				if (skillOrUnlearn == TAB_SKILLS) {
					viewId = R.id.fragmentView_skills;
					m_skillsView.setVisibility(View.VISIBLE);
					m_unlearnView.setVisibility(View.GONE);
				} else if (skillOrUnlearn == TAB_UNLEARN) {
					viewId = R.id.fragmentView_unlearn;
					m_skillsView.setVisibility(View.GONE);
					m_unlearnView.setVisibility(View.VISIBLE);
				}
			}
		}
		if (!f.isAdded()) {
			if (bAddToStack) {
				ft.add(viewId, f);
				ft.addToBackStack(null);
			} else
				ft.replace(viewId, f);

			ft.commit();
		}
	}

	/*
	 * private void setFragmentChangeListener() { FragmentManager fm =
	 * getSupportFragmentManager(); fm.addOnBackStackChangedListener( new
	 * OnBackStackChangedListener() { public void onBackStackChanged() { if(
	 * m_bUpdateSkills ) { m_bUpdateSkills = false; FragmentManager fm =
	 * getSupportFragmentManager(); SkillFragment f =
	 * (SkillFragment)fm.findFragmentById(R.id.fragmentView); f.getSkills(); } }
	 * }); }
	 */

	public void updateSkills() {
		m_skillFrm.getSkills();
		m_profileFrm.getProfileInfo(false);
	}

	public void updateUnlearnables() {
		m_unlearnFrm.getSkills();
		m_profileFrm.getProfileInfo(false);
	}

	public void setPlayerID(String pID) {
		m_selfPlayerID = pID;
	}

	public String getPlayerID() {
		return m_selfPlayerID;
	}

	private void initLayout() {
		setContentView(R.layout.home);

		m_spinner = findViewById(R.id.spinner);

		m_profileFrm = new ProfileFragment();
		m_skillFrm = new SkillFragment();
		m_unlearnFrm = new UnlearnFragment();
		m_activityFrm = new ActivityFragment();		
		m_questsFrm = new QuestsFragment();
		m_friendsFrm = new FriendsFragment();
		m_achievementsFrm = new AchievementCategoriesFragment();
		m_mailboxFrm = new MailboxFragment();
		m_settingsFrm = new SettingsFragment();
		m_encyclopediaFrm = new EncyclopediaCategoriesFragment();
		m_recentSnapsFrm = new RecentSnapsFragment();

		m_stack = findViewById(R.id.view_stack);

		m_profileView = findViewById(R.id.fragmentView_profile);
		m_activityView = findViewById(R.id.fragmentView_activity);
		m_skillsView = findViewById(R.id.fragmentView_skills);
		m_questsView = findViewById(R.id.fragmentView_quests);
		m_unlearnView = findViewById(R.id.fragmentView_unlearn);
		m_friendsView = findViewById(R.id.fragmentView_friends);
		m_achievementsView = findViewById(R.id.fragmentView_achievements);
		m_mailboxView = findViewById(R.id.fragmentView_mailbox);
		m_recentSnapsView = findViewById(R.id.fragmentView_recentSnaps);
		m_encyclopediaView = findViewById(R.id.fragmentView_encyclopedia);
		m_settingsView = findViewById(R.id.fragmentView_settings);		
		
		setCurrentFragment(m_activityFrm, false);
	}
	
	public void onBackPressed() {
		super.onBackPressed();		
	}
	
	public void requestFailed(GlitchRequest request) {
		// if( m_homeScrollView != null )
		// m_homeScrollView.onRefreshComplete();

		m_spinner.setVisibility(View.INVISIBLE);
		Util.Alert(this, R.string.error_connection_message,
				R.string.error_connection_title);
		FlurryAgent.logEvent("App Delegate - Tried to show connection error alert");
	}

	// // GlitchSession interface methods ////

	public void glitchLoggedOut() {
	}

	// // Dialog Creation ////

	protected Dialog onCreateDialog(int id) {
		Dialog dialog;

		switch (id) {
		case DIALOG_LOGIN_FAIL_ID:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Login failure")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			dialog = builder.create();

			break;

		case DIALOG_REQUEST_FAIL_ID:

			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
			builder1.setMessage("Request failure")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			dialog = builder1.create();

			break;
		default:
			dialog = null;
		}

		return dialog;
	}

	/*
	 * void initPushToRefresh() { m_homeScrollView = ( PullToRefreshScrollView)
	 * findViewById(R.id.homeScrollView); m_homeScrollView.init(this);
	 * 
	 * m_homeScrollView.setOnRefreshListener( new
	 * PullToRefreshScrollView.OnRefreshListener() {
	 * 
	 * @Override public void onRefresh(boolean bTop) { getMoreInfo(!bTop); } });
	 * }
	 */

	public void setPlayerName(String playerName) {
		m_selfPlayerName = playerName;
	}
	
	public String getPlayerName() {
		return m_selfPlayerName;
	}

	public void showSpinner(boolean bVisible) {
		m_spinner.setVisibility(bVisible ? View.VISIBLE : View.INVISIBLE);
	}

	public void Logout() {
		Log.i("HomeScreen", "Logout");

		// unregister your registrationId
		C2DMReceiver.unregister(this);

		m_application.PreferencePutString("username", "");
		m_application.PreferencePutString("password", "");

		Intent intent = new Intent();
		intent.setClass(HomeScreen.this, LoginScreen.class);
		startActivity(intent);

		FlurryAgent.logEvent("App Delegate - Logged out");

		finish();
	}

	@Override
	public void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, "WCCPI1W5AYGMARQV2QQL");
	}

	@Override
	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	public boolean isBaseFragmentVisible(BaseFragment f) {
		if (f instanceof ProfileFragment) {
			return (m_profileView.getVisibility() == View.VISIBLE);
		} else if (f instanceof SkillFragment) {
			return (m_skillsView.getVisibility() == View.VISIBLE);
		} else if (f instanceof UnlearnFragment) {
			return (m_unlearnView.getVisibility() == View.VISIBLE);
		} else if (f instanceof QuestsFragment) {
			return (m_questsView.getVisibility() == View.VISIBLE);
		} else if (f instanceof FriendsFragment) {
			return (m_friendsView.getVisibility() == View.VISIBLE);
		} else if (f instanceof EncyclopediaCategoriesFragment) {
			return (m_encyclopediaView.getVisibility() == View.VISIBLE);
		} else if (f instanceof AchievementCategoriesFragment) {
			return (m_achievementsView.getVisibility() == View.VISIBLE);
		} else if (f instanceof MailboxFragment) {
			return (m_mailboxView.getVisibility() == View.VISIBLE);
		} else if (f instanceof ActivityFragment) {
			return (m_activityView.getVisibility() == View.VISIBLE);
		} else if (f instanceof SettingsFragment) {
			return (m_settingsView.getVisibility() == View.VISIBLE);
		}
		return true;
	}

}
