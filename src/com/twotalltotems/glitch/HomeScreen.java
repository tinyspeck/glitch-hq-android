package com.twotalltotems.glitch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.flurry.android.FlurryAgent;
import com.tinyspeck.android.GlitchRequest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

public class HomeScreen extends FragmentActivity{
	
	static final int DIALOG_LOGIN_FAIL_ID = 0;
	static final int DIALOG_REQUEST_FAIL_ID = 1;

	static final int TAB_PROFILE = 0;
	static final int TAB_ACTIVITY = 1;
	static final int TAB_SKILLS = 2;
	
	static final private int MENU_COMMAND_REFRESH = Menu.FIRST+0;
    static final private int MENU_COMMAND_MORE = Menu.FIRST+1;
    
    private MyApplication m_application;    
    private String m_selfPlayerID;
    private View m_spinner;
	private int m_curTab = 	TAB_PROFILE;				
	
    private RadioButton m_btnProfile;
    private RadioButton m_btnActivity;
    private RadioButton m_btnSkills;
    
    private ProfileFragment m_profileFrm;
    private SkillFragment m_skillFrm;
    private ActivityFragment m_activityFrm;
    
    private View m_profileView, m_activityView, m_skillsView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	     m_application = (MyApplication)getApplicationContext();
	     m_application.Init( this );

	     setTitle( getResources().getString( R.string.str_main_title ) );
	     initLayout();

	}

	private void initBottomPane()
	{
		 m_btnProfile = (RadioButton) findViewById(R.id.btn_home);
		 m_btnActivity = (RadioButton) findViewById(R.id.btn_activity);
		 m_btnSkills = (RadioButton) findViewById(R.id.btn_skill);
		 
		 m_btnProfile.setTypeface( m_application.m_vagFont );  
		 m_btnActivity.setTypeface( m_application.m_vagFont );  
		 m_btnSkills.setTypeface( m_application.m_vagFont );  
		 
		 m_btnProfile.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
		//		int icon = isChecked? R.drawable.id_card_icon_focus: R.drawable.id_card_icon; 
				buttonView.setTextColor( isChecked? 0xffffffff : 0xffa0a0a0 );
		//		buttonView.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0);
			}
			 
		 });
		 m_btnProfile.setOnClickListener( new OnClickListener(){
				public void onClick(View arg0) {
					if(  m_curTab == TAB_PROFILE )
						clearFragmentStack();
					else
					{
						setCurrentFragment( m_profileFrm, false );						
						m_curTab = 	TAB_PROFILE;	
					}
				}
			 });

		 m_btnSkills.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
			//		int icon = isChecked? R.drawable.skill_icon_focus: R.drawable.skill_icon; 
					buttonView.setTextColor( isChecked? 0xffffffff : 0xffa0a0a0 );
			//		buttonView.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0);
				}
				 
			 });
		 
		 m_btnSkills.setOnClickListener( new OnClickListener(){
				public void onClick(View arg0) {
					if( m_curTab == TAB_SKILLS )
						clearFragmentStack();
					else
					{
						setCurrentFragment( m_skillFrm, false );
						m_curTab = 	TAB_SKILLS;	
					}
				}
			 });

		 m_btnActivity.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
			//		int icon = isChecked? R.drawable.activity_icon_focus: R.drawable.activity_icon; 
					buttonView.setTextColor( isChecked? 0xffffffff : 0xffa0a0a0 );
			//		buttonView.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0);
				}
				 
			 }); 
		 
		 m_btnActivity.setOnClickListener( new OnClickListener(){
				public void onClick(View arg0) {
					if( m_curTab == TAB_ACTIVITY )
						clearFragmentStack();
					else
					{
						setCurrentFragment( m_activityFrm, false );
						m_curTab = 	TAB_ACTIVITY;				
					}
				}
			 });
	}
	
	public void clearFragmentStack()
	{
        FragmentManager fm = getSupportFragmentManager();
		int nCount = fm.getBackStackEntryCount();
		for(int i=0; i < nCount; i++ )
			fm.popBackStack();
	}
	
    public void setCurrentFragment( Fragment f, boolean bAddToStack )
    {
        FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		int viewId = 0;
		int nTab = m_btnProfile.isChecked()? TAB_PROFILE: ( m_btnActivity.isChecked()? TAB_ACTIVITY: TAB_SKILLS );
		
		if( nTab == TAB_PROFILE )
		{
			viewId = R.id.fragmentView_profile;
			m_profileView.setVisibility(View.VISIBLE);
			m_activityView.setVisibility(View.GONE);
			m_skillsView.setVisibility(View.GONE);
			((ProfileFragment)f).logPageView();

		}else if( nTab == TAB_ACTIVITY )
		{
			viewId = R.id.fragmentView_activity;
			m_profileView.setVisibility(View.GONE);
			m_activityView.setVisibility(View.VISIBLE);
			m_skillsView.setVisibility(View.GONE);
			if (f instanceof ActivityFragment)
				((ActivityFragment)f).logPageView();
			else if (f instanceof ActivityDetailFragment)
				((ActivityDetailFragment)f).logPageView();
		}else
		{
			viewId = R.id.fragmentView_skills;
			m_profileView.setVisibility(View.GONE);
			m_skillsView.setVisibility(View.VISIBLE);
			m_activityView.setVisibility(View.GONE);
			if (f instanceof SkillFragment)
				((SkillFragment)f).logPageView();
			else if (f instanceof SkillDetailFragment)
				((SkillDetailFragment)f).logPageView();
		}
		if( !f.isAdded() )
		{
			if( bAddToStack )
			{
				ft.add( viewId, f );
				ft.addToBackStack(null);
			}else
				ft.replace( viewId, f );
			
			ft.commit(); 
		}
    }

/*    private void setFragmentChangeListener()
    {
        FragmentManager fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener( new OnBackStackChangedListener()
        {
			public void onBackStackChanged() {
				if( m_bUpdateSkills )
				{
					m_bUpdateSkills = false;
					FragmentManager fm = getSupportFragmentManager();
		    		SkillFragment f = (SkillFragment)fm.findFragmentById(R.id.fragmentView);
		    		f.getSkills();
				}
			}
        }); 
    } */
    
    public void updateSkills()
    {
    	m_skillFrm.getSkills();
    }
    
    public void setPlayerID( String pID )
    {
    	m_selfPlayerID = pID;
    }
    
    public String getPlayerID()
    {
    	return m_selfPlayerID;
    }
    
	private void initLayout()
	{
	    setContentView( R.layout.home );  
	    initBottomPane();

	    m_spinner = findViewById(R.id.spinner);
		m_btnProfile.setChecked(true);

		m_profileFrm = new ProfileFragment(null,false);
		m_skillFrm = new SkillFragment();
		m_activityFrm = new ActivityFragment();

		m_profileView = findViewById(R.id.fragmentView_profile);
		m_activityView = findViewById(R.id.fragmentView_activity);
		m_skillsView = findViewById(R.id.fragmentView_skills);
		
		setCurrentFragment( m_profileFrm, false );
	}

	public void onBackPressed() {
		
		super.onBackPressed();
		
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		 menu.clear();
		 
	     BaseFragment bf;
   		 bf = getCurrentFragment();
   		 
   		 if( bf != null && bf.doesSupportRefresh() )
   		     menu.add(0, MENU_COMMAND_REFRESH, Menu.NONE+0, R.string.str_menu_refresh);

   		 if( bf != null && bf.doesSupportMore() )
   		     menu.add(1, MENU_COMMAND_MORE, Menu.NONE+1, R.string.str_menu_more);
	     
	     return super.onCreateOptionsMenu(menu);
	}

   @Override
   public boolean onOptionsItemSelected( MenuItem item )
   {
	     BaseFragment bf;
	     
	     switch(item.getItemId())
	     {
	     	case MENU_COMMAND_REFRESH:
	     		bf = getCurrentFragment();
	     		if( bf!= null ) {
	     			bf.onRefresh();
	     			FlurryAgent.logEvent(bf.getClass().toString() + " - Clicked to refresh");
	     		}
	     		break;
	     	case MENU_COMMAND_MORE:
	     		bf = getCurrentFragment();
	     		if( bf!= null ) {
	     			bf.onMore();
	     			FlurryAgent.logEvent(bf.getClass().toString() + " - Clicked to load more");
	     		}
	     		break;
	     } 
	     return super.onOptionsItemSelected(item);
   }

   public BaseFragment getCurrentFragment()
   {
       FragmentManager fm = getSupportFragmentManager();

       int resId = m_btnProfile.isChecked()? R.id.fragmentView_profile: ( m_btnActivity.isChecked()? R.id.fragmentView_activity: R.id.fragmentView_skills );

       return (BaseFragment)fm.findFragmentById( resId );
   }
   
	public void requestFailed(GlitchRequest request) {
//		if( m_homeScrollView != null )
//			m_homeScrollView.onRefreshComplete();
		
		m_spinner.setVisibility(View.INVISIBLE);
    	Util.Alert(this, R.string.error_connection_message, R.string.error_connection_title );
    	FlurryAgent.logEvent("App Delegate - Tried to show connection error alert");
	}
	
	//// GlitchSession interface methods ////

	public void glitchLoggedOut() {
		// Called when user logs out (method stub, not yet implemented)
	}
	
	//// Dialog Creation ////
	
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog;
	    
	    switch(id) {
	    case DIALOG_LOGIN_FAIL_ID:
	    	
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage("Login failure")
	    	       .setCancelable(false)
	    	       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   dialog.cancel();
	    	           }
	    	       });
	    	dialog = builder.create();
	    	
	        break;
	        
	    case DIALOG_REQUEST_FAIL_ID:
	    	
	    	AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
	    	builder1.setMessage("Request failure")
	    	       .setCancelable(false)
	    	       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
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

/*	void initPushToRefresh()
	{
        m_homeScrollView = ( PullToRefreshScrollView) findViewById(R.id.homeScrollView);
		m_homeScrollView.init(this);
		
		m_homeScrollView.setOnRefreshListener( new PullToRefreshScrollView.OnRefreshListener() 
		{
	        @Override
	        public void onRefresh(boolean bTop) {
	        	getMoreInfo(!bTop);
	        }
	    });
	} */
	
	public void setPlayerName( String playerName )
	{
		m_btnProfile.setText( playerName );	        		
	}

	public void showSpinner( boolean bVisible )
	{
		m_spinner.setVisibility( bVisible ? View.VISIBLE: View.INVISIBLE);
	}
	
	public void Logout()
	{
		m_application.PreferencePutString("username", "");
		m_application.PreferencePutString("password", "");

		Intent intent = new Intent();
		intent.setClass(HomeScreen.this, LoginScreen.class);
		startActivity(intent);
		
		FlurryAgent.logEvent("App Delegate - Logged out");
		finish();
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "WCCPI1W5AYGMARQV2QQL");
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
}	
