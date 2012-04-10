package com.tinyspeck.glitchhq;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.tinyspeck.android.GlitchRequest;
import com.tinyspeck.glitchhq.BaseFragment.skillAvailable;


import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

public class ProfileFragment extends BaseFragment{

	private TextView nameTextView;
    private LinearListView  m_listView, m_learningListView, m_unlearningListView;
    private String m_playerTsid, m_avatarUrl, m_playerName;
    private ImageView m_avatar;
    private int m_currentActType;

	private int m_nCurrants;
	private int m_nEnergy;
	private int m_nEnergyMax;
	private int m_nLevel;
	private int m_nMood;
	private int m_nMoodMax;
    private String m_address;
	
    private ActivityListViewAdapter m_adapter;
    private LearningListViewAdapter m_learningAdapter;
    private UnlearningListViewAdapter m_unlearningAdapter;
    
    private TextView m_tv_energy1,m_tv_energy2;
    private ImageView m_imgMood;
    private TextView m_tv_level;
    private TextView m_tv_address;
    private TextView m_tv_currants;
    private TextView m_tv_logout;
    private View m_vProfile;
    
	private Timer m_RemainingTimer;
	
	private boolean m_bOtherProfile=false;
	
	private String m_actItemLast;
	private boolean m_actHasMore;
	private boolean m_bFriend;
	
 	private Vector<glitchActivity> m_actList;
  	private Vector<skillAvailable> m_learningList, m_unlearningList;
  	private View m_root;
	
  	ProfileFragment( String playerID, boolean bOthers )
  	{
  		m_bOtherProfile = bOthers;
  		m_playerTsid = playerID;
  		m_bFriend = false;
  	}
  	
    public void onActivityCreated(Bundle savedInstanceState) {
    	
        super.onActivityCreated(savedInstanceState);
    }

    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) 
    {
    	View curView = ViewInit( inflater, R.layout.profile_view, container );
    	m_root = curView;
    	init( curView );
		return curView;
    }
    
	private void init( View root )
	{
		boolean bUpdateData = m_actList == null;
		
	    View profileSelf = root.findViewById(R.id.profile_user_self);
	    View profileOther = root.findViewById(R.id.profile_others);
	    
	    ImageView vbg = (ImageView) root.findViewById( R.id.profilebg );
	    int nType = Util.GetScreenSizeAttribute( getActivity() );
	    int [] resBackgrounds = { R.drawable.profile_background, R.drawable.profile_background_l, R.drawable.profile_background_xl };
	    int [] resGroddleBg = { R.drawable.profile_other_background, R.drawable.profile_other_background, R.drawable.profile_other_background_xl };
	    
	    vbg.setImageResource( m_bOtherProfile? resGroddleBg[nType]: resBackgrounds[nType] );
	    if( nType == 0 )
	    {
	    	int nPaddingTop = 180;
	    	
	    	int nWidth = Util.GetScreenSize( getActivity() ).widthPixels;
	    	if( nWidth > 320 )
	    		nPaddingTop = 170;
	    	
		    if( m_bOtherProfile )
		    	nPaddingTop -= 36;
		    
	    	vbg.setPadding(0, nPaddingTop, 0, 0);
	    }else if( nType == 2 )
	    {
	    	int nPaddingTop = 120;
		    if( m_bOtherProfile )
		    	nPaddingTop = 0;
		    
	    	vbg.setPadding(0, nPaddingTop, 0, 0);
	    }
	    
	    ScrollView sv = (ScrollView)root.findViewById( R.id.scr_profile );
	    sv.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
	    });
	    
	    profileSelf.setVisibility( m_bOtherProfile? View.GONE: View.VISIBLE );
	    profileOther.setVisibility( m_bOtherProfile? View.VISIBLE: View.GONE );
	    ((ImageView)root.findViewById( R.id.rookfossil_image )).setVisibility( m_bOtherProfile ? View.GONE: View.VISIBLE);
	    ((ImageView)root.findViewById( R.id.treasure_image )).setVisibility( m_bOtherProfile ? View.VISIBLE: View.GONE);
	    
	    m_vProfile = m_bOtherProfile? profileOther: profileSelf;
	    
	    m_vProfile.findViewById( R.id.profile_currant ).setVisibility( m_bOtherProfile? View.GONE: View.VISIBLE );
	    
        nameTextView = (TextView) m_vProfile.findViewById(R.id.playername);
        nameTextView.setTypeface( m_application.m_vagFont );  

        m_listView = (LinearListView)root.findViewById( R.id.homeListView );
		m_learningListView = (LinearListView)root.findViewById( R.id.learning_list );
		m_unlearningListView = (LinearListView)root.findViewById(R.id.unlearning_list);

		m_avatar = (ImageView)m_vProfile.findViewById( R.id.avatar );
	    m_tv_energy1 = (TextView) m_vProfile.findViewById(R.id.tv_energy1);
	    m_tv_energy1.setTypeface( m_application.m_vagLightFont );  
	    m_tv_energy2 = (TextView) m_vProfile.findViewById(R.id.tv_energy2);
	    m_tv_energy2.setTypeface( m_application.m_vagLightFont );  
	    
	    m_tv_address = (TextView) m_vProfile.findViewById(R.id.player_address);
	    m_tv_address.setTypeface( m_application.m_vagLightFont );  

	    m_imgMood = (ImageView) m_vProfile.findViewById(R.id.img_mood);

	    m_tv_level = (TextView) m_vProfile.findViewById(R.id.tv_level);
	    m_tv_level.setTypeface( m_application.m_vagLightFont );  
	    
	    m_tv_currants = (TextView) m_vProfile.findViewById(R.id.tv_currants);
	    m_tv_currants.setTypeface( m_application.m_vagLightFont );  
	    
	    if( bUpdateData )
	    	m_actList = new Vector<glitchActivity>();
	    	
	    m_adapter = new ActivityListViewAdapter( this, m_actList );
		m_listView.setAdapter( m_adapter );

		if( bUpdateData ) {
			m_learningList = new Vector<skillAvailable>();
			m_unlearningList = new Vector<skillAvailable>();
		}
			
		m_learningAdapter = new LearningListViewAdapter( getActivity(), m_learningList );
		m_learningListView.setAdapter( m_learningAdapter );
		
		m_unlearningAdapter = new UnlearningListViewAdapter(getActivity(), m_unlearningList);
		m_unlearningListView.setAdapter(m_unlearningAdapter);		

		View clouds = root.findViewById(R.id.clouds);
		Util.startTranslateAnimation( clouds, 300000 );

		m_learningListView.setOnClickListener( new OnClickListener() 
		{			
			public void onClick(View arg0) {
				skillAvailable skill = m_learningList.get(0);
				SkillDetailFragment fm = new SkillDetailFragment(skill);				
				((HomeScreen)getActivity()).setCurrentFragment(fm, true);
			}
			
		});
		
		m_unlearningListView.setOnClickListener( new OnClickListener()
		{
			public void onClick(View arg0) {
				skillAvailable skill = m_unlearningList.get(0);
				SkillDetailFragment fm = new SkillDetailFragment(skill);
				((HomeScreen)getActivity()).setCurrentFragment(fm, true);
			}
		});
		
//     initPushToRefresh();
	   setupSettings();
		
	   if( m_bOtherProfile )
	   {
		   m_learningListView.setVisibility(View.GONE);
		   m_unlearningListView.setVisibility(View.GONE);
		   if( bUpdateData )
			   getProfileInfo(false);
	   }
	   else{
		   m_learningListView.setVisibility(View.VISIBLE);
		   m_unlearningListView.setVisibility(View.VISIBLE);
		   if( bUpdateData )
		   {
			   GlitchRequest request = m_application.glitch.getRequest("players.info");
			   request.execute( this );
		   }
	   }
	   if( !bUpdateData )
		   showProfilePage();
	}

	public void getProfileInfo( boolean bMore )
	{
		String selfPlayerID = ((HomeScreen)getActivity()).getPlayerID();
        Map<String,String> params = new  HashMap<String,String>();
        params.put("player_tsid", m_playerTsid ); 
        if( m_playerTsid != selfPlayerID )
        	params.put("viewer_tsid", selfPlayerID );
        	
 //       params.put("cat", "skill_learned" );
        if( bMore && m_actItemLast != null )
        {
        	params.put("last", m_actItemLast );
        	m_bAppendMode = true;
        }else
        	m_bAppendMode = false;

        if ( m_currentActType == ActivityFragment.ACTIVITY_TYPE_UPDATES )
            params.put("cat", "notes" );
        
        GlitchRequest request1 = m_application.glitch.getRequest("activity.playerFeed", params );
        request1.execute(this);

        GlitchRequest request2 = m_application.glitch.getRequest("skills.listLearning");
        request2.execute(this);

        GlitchRequest request3 = m_application.glitch.getRequest("players.fullInfo", params );
        request3.execute(this);
        
        GlitchRequest request4 = m_application.glitch.getRequest("skills.listUnlearning");
        request4.execute(this);

        m_requestCount = 4;
    	((HomeScreen)getActivity()).showSpinner(true);
    }
	
	@Override
	public void onStop()
	{
	    if( m_RemainingTimer != null )
	    {
		   m_RemainingTimer.cancel();
		   m_RemainingTimer = null;
	    }
	    super.onStop();
	}
	
	private void showProfilePage()
	{
		int type = m_bOtherProfile? MyApplication.DOWNLOAD_TYPE_NORMAL : MyApplication.DOWNLOAD_TYPE_MIRROR;

		m_application.Download( m_avatarUrl, m_avatar, type );
		nameTextView.setText( m_playerName );

		updateActivityFeed();
		updateProfileInfo();
		
		m_learningAdapter.notifyDataSetChanged();
		m_unlearningAdapter.notifyDataSetChanged();
        InitUpdateSkillRemainingTimer();		
	}

	private void updateActivityFeed()
	{
		View vLayout = m_root.findViewById( R.id.homeListViewLayout );		
		vLayout.setVisibility(View.VISIBLE);
		vLayout.setBackgroundResource( m_bOtherProfile? R.drawable.wallpaper2_other: R.drawable.wallpaper2 );

		m_root.findViewById( R.id.fl_logout ).setVisibility(  View.VISIBLE  );

		ImageView imgv = (ImageView)m_root.findViewById( R.id.iv_wallpaper );
		imgv.setImageResource( m_bOtherProfile? R.drawable.wallpaper_other: R.drawable.wallpaper );
		
		m_listView.setVisibility( m_actList.size() == 0 ? View.GONE : View.VISIBLE );
		
	    ScrollView sv = (ScrollView)m_root.findViewById( R.id.scr_profile );	    
	    sv.setOnTouchListener(null);	
		m_adapter.notifyDataSetChanged();
		if (m_bAppendMode) {
			Util.delayedFlingOfScrollView(sv, 500, 500);
	    }
	}
	
	private void updateProfileInfo()
	{
		m_tv_energy1.setText( String.valueOf( m_nEnergy) );
		m_tv_energy2.setText( String.valueOf( m_nEnergyMax) );
		
		m_tv_level.setText( Html.fromHtml("Made it to <b>Level " + m_nLevel + "</b>") );

		String sCurrants;

        if( m_nCurrants > 1000000 )
             sCurrants = String.format("%d,%03d,%03d",m_nCurrants/1000000, (m_nCurrants%1000000)/1000, m_nCurrants%1000 );
        else if( m_nCurrants > 1000 )
             sCurrants = String.format("%d,%03d", m_nCurrants/1000, (m_nCurrants%1000) );
        else
             sCurrants = String.valueOf(m_nCurrants);
        
		m_tv_currants.setText( Html.fromHtml("You have <b>" + sCurrants + "</b> currants" ));
		if( m_address != null && !m_address.equalsIgnoreCase("null") )
		{
			if (m_bOtherProfile) {
				m_tv_address.setText(m_playerName + " owns " + m_address);
			} else {
				m_tv_address.setText("You own " + m_address);
			}
			m_tv_address.setVisibility(View.VISIBLE);
		}else
		{
			View v = m_vProfile.findViewById( R.id.player_address_view );
			v.setVisibility(View.INVISIBLE);
		}

		View v = m_vProfile.findViewById( R.id.user_data );
		v.setVisibility(View.VISIBLE);

		if( !m_bOtherProfile )
		{
			v = m_vProfile.findViewById( R.id.player_mood );
			v.setVisibility(View.VISIBLE);

			v = m_vProfile.findViewById( R.id.player_energy );
			v.setVisibility(View.VISIBLE);

    		int nMoodRes[] = { R.drawable.mood1, R.drawable.mood2, R.drawable.mood3, R.drawable.mood4, R.drawable.mood5,
    						   R.drawable.mood6, R.drawable.mood7, R.drawable.mood8, R.drawable.mood9, R.drawable.mood10 };

    		int nEnergyRes[] = { R.drawable.energy1, R.drawable.energy2, R.drawable.energy3, R.drawable.energy4, R.drawable.energy5,
					   R.drawable.energy6, R.drawable.energy7, R.drawable.energy8, R.drawable.energy9, R.drawable.energy10 };
    		
    		m_imgMood.setImageResource( nMoodRes[ 10 * (m_nMoodMax-m_nMood-1)/m_nMoodMax] );
    		m_vProfile.findViewById(R.id.img_energy).setBackgroundResource( nEnergyRes[ 10 * (m_nEnergyMax-m_nEnergy-1) / m_nEnergyMax ] );
		}
	}
	
	@Override
	public void onRequestBack( String method, JSONObject response )
	{
    	if( method == "players.info" )
    	{	
    		m_playerTsid = response.optString("player_tsid");
    		((HomeScreen)getActivity()).setPlayerID( m_playerTsid );
    		m_playerName = response.optString("player_name");
    		m_avatarUrl = response.optString("avatar_url");

    		m_application.Download( m_avatarUrl, m_avatar, MyApplication.DOWNLOAD_TYPE_MIRROR );
    		nameTextView.setText( m_playerName );
    		
    		( (HomeScreen)getActivity() ).setPlayerName( m_playerName );	        		
    		getProfileInfo( false);
    		
    	}else if( method == "activity.playerFeed" )
    	{
   	    	if( !m_bAppendMode )
   	    		m_actList.clear();
   	    	
		    m_actItemLast = response.optString("last");
    		m_actHasMore = (response.optInt("has_more")==1)? true: false;

    		addActivityList( m_actList, response, !m_bOtherProfile );
    		updateActivityFeed();
    		onRequestComplete();
    		
    	}else if( method == "skills.listLearning" )
		{
    		m_learningList.clear();
			addToLearningList( m_learningList, response );
    		m_learningAdapter.notifyDataSetChanged();			
	        InitUpdateSkillRemainingTimer();
			onRequestComplete();
			if ( !m_bAppendMode )
				Util.startAlphaAnimation(m_learningListView, 1000, 0, 1, TranslateAnimation.ABSOLUTE);	
			
		}else if (method == "skills.listUnlearning")
		{
			m_unlearningList.clear();
			addToUnlearningList(m_unlearningList, response);
			m_unlearningAdapter.notifyDataSetChanged();
			InitUpdateSkillRemainingTimer();
			onRequestComplete();
			if (!m_bAppendMode)
				Util.startAlphaAnimation(m_unlearningListView, 1000, 0, 1, TranslateAnimation.ABSOLUTE);
			
		}else if ( method == "friends.remove" ||  method == "friends.add" )
		{
			onRequestComplete();
		}else if( method == "players.fullInfo" )
    	{
    		if( response.optInt("ok") != 0 )
    		{
    			if( m_bOtherProfile )
    			{
    				m_avatarUrl = response.optJSONObject("avatar").optString("172");
    				m_bFriend = response.optJSONObject("relationship").optBoolean("is_rev_contact");

    				m_application.Download( m_avatarUrl, m_avatar, MyApplication.DOWNLOAD_TYPE_NORMAL );
    				
    				m_playerName = response.optString("player_name");
	        		nameTextView.setText( m_playerName );
    			}

    			m_nCurrants = response.optJSONObject("stats").optInt("currants");
        		m_nEnergy = response.optJSONObject("stats").optInt("energy");
        		m_nEnergyMax = response.optJSONObject("stats").optInt("energy_max");
        		m_nLevel = response.optJSONObject("stats").optInt("level");
        		m_nMood = response.optJSONObject("stats").optInt("mood");
        		m_nMoodMax = response.optJSONObject("stats").optInt("mood_max");
        		
        		if( m_nMoodMax == 0 )
        			m_nMoodMax = 1;
        		if( m_nEnergyMax == 0 )
        			m_nEnergyMax = 1;

        		m_address = response.optJSONObject("pol").optString("name");
        		
        		updateProfileInfo();
//        		addPreShowListener(m_listView);
    		}
    		onRequestComplete();
    	}
	}

	private void InitUpdateSkillRemainingTimer()
	{
	   if( m_RemainingTimer != null )
		   m_RemainingTimer.cancel();
		   
	   m_RemainingTimer = new Timer();
	   m_RemainingTimer.scheduleAtFixedRate( new TimerTask(){
	       public void run()
		   {
		    	 getActivity().runOnUiThread(new Runnable(){
		    		 public void run(){
		        		  if( m_learningAdapter!= null && m_learningAdapter.getCount() > 0 )
		    				 m_learningAdapter.notifyDataSetChanged(); 
		        		  if (m_unlearningAdapter != null && m_unlearningAdapter.getCount() > 0)
		        			  m_unlearningAdapter.notifyDataSetChanged();
			         }});
   	       }  
		}, 1000, 1000 ); 
	}
	
	private void AddOrRemoveFriend( boolean bRemove, String player )
	{
        Map<String,String> params = new  HashMap<String,String>();
        params.put("tsid", player );
        
        String command = bRemove? "friends.remove": "friends.add";
        
        GlitchRequest request0 = m_application.glitch.getRequest( command, params );
        request0.execute(this);

        m_requestCount = 1;
    	((HomeScreen)getActivity()).showSpinner(true);
	}
	
	private void setupSettings()
	{
		final Button btnSettings = (Button)m_root.findViewById(R.id.btnSettings);
		btnSettings.setVisibility( View.VISIBLE );
		
		btnSettings.setOnClickListener( new OnClickListener(){
				public void onClick(View arg0) {
				
					int nLayout = m_bOtherProfile? R.layout.profile_settings: R.layout.profile_settings2;
					final PopupWindow pw = Util.showPopup( getActivity(), nLayout, true, btnSettings, 5, 5 ); 
					View v = pw.getContentView();
					Button btn = (Button)v.findViewById(R.id.btn_filter_all);
					btn.setOnClickListener( new OnClickListener(){
						public void onClick(View v) {
							m_currentActType = ActivityFragment.ACTIVITY_TYPE_ALL;
							pw.dismiss();
							getProfileInfo(false);
						}
						
					});

					btn = (Button)v.findViewById(R.id.btn_filter_updates);
					btn.setOnClickListener( new OnClickListener(){
						public void onClick(View v) {
							m_currentActType = ActivityFragment.ACTIVITY_TYPE_UPDATES;
							getProfileInfo(false);
							pw.dismiss();
						}
					});

					btn = (Button)v.findViewById(R.id.btn_add_remove_friend);
					if( btn!= null )
					{
						btn.setBackgroundResource( m_bFriend? R.drawable.friend_remove_button_background: R.drawable.friend_add_button_background );
						btn.setText( m_bFriend? "Remove from friends": "Add to friends" );
						btn.setOnClickListener( new OnClickListener(){
							public void onClick(View v) {
								AddOrRemoveFriend( m_bFriend, m_playerTsid );
								m_bFriend = !m_bFriend;
								pw.dismiss();
							}
						});
					}
				}
			 });
	}

	protected boolean doesSupportRefresh()
	{
		return true;
	}

	protected boolean doesSupportMore()
	{
		return true;
	}
	
	protected void onRefresh()
	{
		getProfileInfo(false);
	}

	protected void onMore()
	{		
		getProfileInfo(true);
	}
	
	protected void scrollToTop() {
		ScrollView sv = (ScrollView)m_root.findViewById( R.id.scr_profile );
		sv.smoothScrollTo(0, 0);
	}

}
