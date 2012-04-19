package com.tinyspeck.glitchhq;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

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

	private MyApplication m_application;
	private String m_selfPlayerID;
	private View m_spinner;
	private int m_curTab = TAB_PROFILE;
	private Page m_curPage = Page.Profile;
	private int skillOrUnlearn = TAB_SKILLS;

	private ProfileFragment m_profileFrm;
	private SkillFragment m_skillFrm;
	private UnlearnFragment m_unlearnFrm;
	private ActivityFragment m_activityFrm;
	private FriendsFragment m_friendsFrm;
	private View m_stack;

	private BaseFragment m_curFrm;

	private View m_profileView, m_activityView, m_skillsView, m_unlearnView,
			m_friendsView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		m_application = (MyApplication) getApplicationContext();
		m_application.init(this);

		setTitle(getResources().getString(R.string.str_main_title));
		initLayout();
	}
	

	private void initBottomPane() {
		m_btnProfile = (RadioButton) findViewById(R.id.btn_home);
		m_btnActivity = (RadioButton) findViewById(R.id.btn_activity);
		m_btnSkills = (RadioButton) findViewById(R.id.btn_skill);
		m_btnFriends = (RadioButton) findViewById(R.id.btn_friends);

		m_btnProfile.setTypeface(m_application.m_vagFont);
		m_btnActivity.setTypeface(m_application.m_vagFont);
		m_btnSkills.setTypeface(m_application.m_vagFont);
		m_btnFriends.setTypeface(m_application.m_vagFont);

		m_btnProfile.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// int icon = isChecked? R.drawable.id_card_icon_focus:
				// R.drawable.id_card_icon;
				buttonView.setTextColor(isChecked ? 0xffffffff : 0xffa0a0a0);
				// buttonView.setCompoundDrawablesWithIntrinsicBounds(0, icon,
				// 0, 0);
			}

		});
		m_btnProfile.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {

				if (m_curTab == TAB_PROFILE)
					clearFragmentStack();
				else {
					setCurrentFragment(m_profileFrm, false);
					m_curTab = TAB_PROFILE;
				}
			}
		});

		m_btnSkills.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// int icon = isChecked? R.drawable.skill_icon_focus:
				// R.drawable.skill_icon;
				buttonView.setTextColor(isChecked ? 0xffffffff : 0xffa0a0a0);
				// buttonView.setCompoundDrawablesWithIntrinsicBounds(0, icon,
				// 0, 0);
			}
		});

		m_btnSkills.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (m_curTab == TAB_SKILLS || m_curTab == TAB_UNLEARN)
					clearFragmentStack();
				else {
					if (skillOrUnlearn == TAB_SKILLS) {
						setCurrentFragment(m_skillFrm, false);
						m_curTab = TAB_SKILLS;
						skillOrUnlearn = TAB_SKILLS;
					} else if (skillOrUnlearn == TAB_UNLEARN) {
						setCurrentFragment(m_unlearnFrm, false);
						m_curTab = TAB_UNLEARN;
						skillOrUnlearn = TAB_UNLEARN;
					}
				}
			}
		});

		m_btnActivity.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// int icon = isChecked? R.drawable.activity_icon_focus:
				// R.drawable.activity_icon;
				buttonView.setTextColor(isChecked ? 0xffffffff : 0xffa0a0a0);
				// buttonView.setCompoundDrawablesWithIntrinsicBounds(0, icon,
				// 0, 0);
			}

		if (page == null) {
			return;
		}

		if (m_curPage == page) {
			clearFragmentStack();
		} else {
			m_curPage = page;
			
			switch (page) {
			case Profile:
				setCurrentFragment(m_profileFrm, false);
				break;
			case Activity:
				setCurrentFragment(m_activityFrm, false);
				break;
			case Skills:
				setCurrentFragment(m_skillFrm, false);
				break;
			case Friends:
				setCurrentFragment(m_friendsFrm, false);
				break;
			default:
				break;
			}
		}

		/*
		 * Unlearning Code
		 * 
		 * if (m_curTab == TAB_SKILLS || m_curTab == TAB_UNLEARN)
		 * clearFragmentStack(); else { if (skillOrUnlearn == TAB_SKILLS) {
		 * setCurrentFragment(m_skillFrm, false); m_curTab = TAB_SKILLS;
		 * skillOrUnlearn = TAB_SKILLS; } else if (skillOrUnlearn ==
		 * TAB_UNLEARN) { setCurrentFragment(m_unlearnFrm, false); m_curTab =
		 * TAB_UNLEARN; skillOrUnlearn = TAB_UNLEARN; } }
		 */
	}

	/*
	 * Sidebar methods
	 */

	static final private long m_sidebarAnimationDuration = 500; // half second
																// (500ms)
	static final private float m_sidebarDeltaX = 0.8f; // 80%

	public void showSidebar() {
		// Get display width
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();

		int dx = (int) (width * m_sidebarDeltaX);

		// Create the show animation
		Animation animation = new TranslateAnimation(0, dx, 0, 0);
		animation.setDuration(m_sidebarAnimationDuration);

		animation.setAnimationListener(new AnimationListener() {

			public void onAnimationEnd(Animation animation) {
				HomeScreen.this.m_showingSidebar = true;

				// Get display width
				Display display = getWindowManager().getDefaultDisplay();
				int width = display.getWidth();

				int dx = (int) (width * m_sidebarDeltaX);

				// Set the sidebar's layout parameters so it'll show up
				// FrameLayout.LayoutParams params = new
				// FrameLayout.LayoutParams(width, m_stack.getHeight(),
				// Gravity.LEFT);
				// m_sidebarView.setLayoutParams(params);

				// m_sidebarView.getParent().bringChildToFront(m_sidebarView);
			}
		});

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationStart(Animation animation) {
			}
		});

		animation.setFillAfter(true);

		// Run the animation
		m_stack.startAnimation(animation);
	}

	private float sidebarTouchOffset;
	private float previousTouchX;
	private Boolean sidebarPickedUp = false;
	private final static int shadowWidth = 27;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		if (isShowingSidebar()) {
			// Get main stack location
			Display display = getWindowManager().getDefaultDisplay();
			int width = display.getWidth();
			int dx = (int) (width * m_sidebarDeltaX);

			// Touch location X
			float x = ev.getX();

			if (x < dx) {
				m_sidebarView.dispatchTouchEvent(ev);
				m_stack.getParent().requestDisallowInterceptTouchEvent(true);
			} else {
				// Touch action
				int action = ev.getAction();

				switch (action) {
				// Initial touch
				case (MotionEvent.ACTION_DOWN):
					if (x >= dx) {
						sidebarTouchOffset = dx - x;
						sidebarPickedUp = true;
						m_stack.getParent().bringChildToFront(m_stack);

						return true;
					}
					break;
				// Touch moved
				case (MotionEvent.ACTION_MOVE): {
					if (sidebarPickedUp) {
						// Set the sidebar's layout parameters so it'll show up
						// m_stack.layout((int) (-shadowWidth + x - dx), 0,
						// width, m_stack.getHeight());

						return true;
					}
					break;
				}
					// Touch ended
				case (MotionEvent.ACTION_UP):
					if (sidebarPickedUp) {
						dismissSidebar();
						sidebarPickedUp = false;
						return true;
					}
					break;
				default:
					break;
				}

				previousTouchX = x;
			}
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

		int dx = (int) (width * m_sidebarDeltaX);

		// Create the dismiss animation
		Animation animation = new TranslateAnimation(dx, 0, 0, 0);
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

	public void setCurrentFragment(Fragment f, boolean bAddToStack) {
		m_curFrm = (BaseFragment) f;
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
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

		} else if (m_curPage == Page.Activity) {
			viewId = R.id.fragmentView_activity;
			m_profileView.setVisibility(View.GONE);
			m_activityView.setVisibility(View.VISIBLE);
			m_skillsView.setVisibility(View.GONE);
			m_unlearnView.setVisibility(View.GONE);
			m_friendsView.setVisibility(View.GONE);

		} else if (m_curPage == Page.Friends) {
			viewId = R.id.fragmentView_friends;
			m_friendsView.setVisibility(View.VISIBLE);
			m_profileView.setVisibility(View.GONE);
			m_activityView.setVisibility(View.GONE);
			m_skillsView.setVisibility(View.GONE);
			m_unlearnView.setVisibility(View.GONE);
		} else {
			m_profileView.setVisibility(View.GONE);
			m_activityView.setVisibility(View.GONE);
			m_friendsView.setVisibility(View.GONE);

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
		m_friendsFrm = new FriendsFragment();

		m_stack = findViewById(R.id.view_stack);

		m_profileView = findViewById(R.id.fragmentView_profile);
		m_activityView = findViewById(R.id.fragmentView_activity);
		m_skillsView = findViewById(R.id.fragmentView_skills);
		m_unlearnView = findViewById(R.id.fragmentView_unlearn);
		m_friendsView = findViewById(R.id.fragmentView_friends);

		setCurrentFragment(m_profileFrm, false);
	}

	public void onBackPressed() {

		super.onBackPressed();

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		if (m_curFrm != null && m_curFrm.doesSupportRefresh())
			menu.add(0, MENU_COMMAND_REFRESH, Menu.NONE + 0,
					R.string.str_menu_refresh);

		if (m_curFrm != null && m_curFrm.doesSupportMore())
			menu.add(1, MENU_COMMAND_MORE, Menu.NONE + 1,
					R.string.str_menu_more);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case MENU_COMMAND_REFRESH:
			if (m_curFrm != null) {
				m_curFrm.onRefresh();
				FlurryAgent.logEvent(m_curFrm.getClass().toString()
						+ " - Clicked to refresh");
			}
			break;
		case MENU_COMMAND_MORE:
			if (m_curFrm != null) {
				m_curFrm.onMore();
				FlurryAgent.logEvent(m_curFrm.getClass().toString()
						+ " - Clicked to load more");
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void requestFailed(GlitchRequest request) {
		// if( m_homeScrollView != null )
		// m_homeScrollView.onRefreshComplete();

		m_spinner.setVisibility(View.INVISIBLE);
		Util.Alert(this, R.string.error_connection_message,
				R.string.error_connection_title);
		FlurryAgent
				.logEvent("App Delegate - Tried to show connection error alert");
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
		//m_btnProfile.setText(playerName);
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
}
