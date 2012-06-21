package com.tinyspeck.glitchhq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.tinyspeck.android.GlitchRequest;
import com.tinyspeck.android.GlitchRequestDelegate;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class BaseFragment extends Fragment implements GlitchRequestDelegate
{
	public int m_requestCount = 0;
	public boolean m_bAppendMode;
    protected MyApplication m_application;
    static final private int MENU_COMMAND_REFRESH = Menu.FIRST + 0;
	static final private int MENU_COMMAND_MORE = Menu.FIRST + 1;
	
	public class glitchLocationStreet {
		String tsid;
		String name;
		int visits;
		int lastVisit;
		boolean activeProject;
		Vector<String> features;
		String image;
		int imageHeight;
		int imageWidth;
	}
	
	public class glitchLocationHub {
		String name;
		int hub_id;
		Vector<glitchLocationStreet> streets;
	}
	
	public class glitchGiantSkill {
		String name;
		int skill;
	}
	
	public class glitchGiant {
		String name;
		String id;
		String desc;
		String gender;
		String followers;
		String giantOf;
		String personality;
		Vector<glitchGiantSkill> skills;
		String image;
		String icon;
	}
	
	public class glitchItem {
		String class_id;
		String name;
		String desc;
		int baseCost;
		int maxStack;
		int growTime;
		int durability;		
		String icon;
		String requiredSkill;
		Vector<String> warnings;
		Vector<String> tips;
	}
	
	public class glitchItemCategory {
		String id;
		String name;
	}
	
  	public class glitchActivity {
 		 String who;
 		 String when;
 		 String what;
 		 String avatar;
 		 String secret; // for snaps
 		 String photo_id; // for snaps
 		 int icon;
 		 int time;
 		 int likes;
 		 String playerID;
 		 String type;
 		 String id;
 		 ArrayList<glitchActivity> replies;
 		 glitchActivity in_reply_to; 		  		 
  	 };

	class SortByTime implements Comparator<glitchActivity>{   
		public int compare(glitchActivity g1, glitchActivity g2) {
	       return (g2.time < g1.time)? -1: ( ( g2.time == g1.time )? 0: 1 ); 
		}   
	}   	

   public class skillAvailable {
   	 	String id;
   	 	String item;
   	 	String icon;
   	 	String description;
   	 	String type;
   	 	Vector<skillAvailable> requirements;
   	 	Vector<skillAvailable> postRequests;
   	 	Vector<skillGiant> giants;
   	 	String neededFor;
   	 	String glant;
   	 	boolean paused;
   	 	boolean can_learn;
   	 	boolean learning;
   	 	boolean can_unlearn;
   	 	boolean unlearning;
   	 	boolean got;
   	 	int level;
   	 	String classId;
   	 	int remainTime;
   	 	int totalTime;
   	 	long curTime;
	 };
	 
	public class skillGiant {
		String id;
		boolean isPrimary;
	};
	
	public class glitchFriend {
		String id;
		String player_name;
		String avatar;
		boolean is_reverse;
		String user_name;
	};
	
	public class glitchAchievement {
		String id;
		String name;
		String desc;
		String icon;
		boolean got;
	}
	
	public class glitchAchievementCategory {
		String name;
		int completed;
		int total;
	}
	
	public class glitchMail {
		int id;
		int currants;
		String sender_label;
		String sender_tsid;
		String sender_avatar;
		String text;
		long received;
		boolean replied;
		boolean is_read;
		boolean is_expedited;
		glitchMailItem item;
		
		@Override
		public boolean equals(Object other)
		{
			if (other == null) return false;
			if (other == this) return true;
			if (!(other instanceof glitchMail)) return false;
			glitchMail otherMail = (glitchMail)other;
			if (this.id == otherMail.id) return true;
			else return false;
		}
	}
	
	public class glitchMailItem {
		String tsid;
		String name;
		String class_id;
		String desc;
		int count;
		String icon;
	}
	
	public class glitchQuestRequirement {
		String desc;
		boolean isCount;
		boolean completed;
		int gotNum;
		int needNum;
		String icon;
	}
	
	public class glitchQuestRewardRecipe {
		String label;
		String icon;
	}
	
	public class glitchQuestRewardFavor {
		String giant;
		int points;		
	}
	
	public class glitchQuestRewards {
		int imagination;
		int currants;
		int energy;
		int mood;
		Vector<glitchQuestRewardFavor> favor;
		Vector<glitchQuestRewardRecipe> recipes;
	}
	
	public class glitchQuest {
		String id;
		String title;
		String desc;
		Vector<glitchQuestRequirement> reqs;
		glitchQuestRewards rewards;
		boolean complete;
		boolean started;
		long offeredTime;
		boolean failed;
		boolean finished;
		boolean startable;
		boolean accepted;
	}
	
	class SortByName implements Comparator<glitchFriend>{   
		public int compare(glitchFriend f1, glitchFriend f2) {
			String name1 = f1.player_name.toLowerCase();
			String name2 = f2.player_name.toLowerCase();
			return name1.compareTo(name2);  
		}   
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	public View ViewInit( LayoutInflater inflater, int nLayout, ViewGroup container )
	{
	    m_application = (MyApplication)getActivity().getApplicationContext();
		View curView = inflater.inflate( nLayout, container, false  );
		setEmptyClickListener( curView );
		setupTitlebar( curView );
		setupSidebarButton( curView );
		return curView;
	}
	
	
	private void setupSidebarButton( View root )
	{
		final Button btnSidebar = (Button)root.findViewById(R.id.btnSidebar);
		
		if (btnSidebar != null) {
			btnSidebar.setVisibility(View.VISIBLE);		
			btnSidebar.setOnClickListener( new OnClickListener() {
	
				public void onClick(View arg0) {
					HomeScreen activity = (HomeScreen) getActivity();
					activity.showSidebar();
				}
				
			});
		}
	}
	
	
	private void setupTitlebar( View root )
	{
		ImageView v = (ImageView)root.findViewById( R.id.img_title_bar );		
		if( v != null )
		{
			int [] nRes = { R.drawable.navbar_l, R.drawable.navbar_l, R.drawable.navbar_xl };
			int nType = Util.GetScreenSizeAttribute( getActivity() );
			v.setImageResource( nRes[nType] );
			LinearLayout titleBarLayout = (LinearLayout)root.findViewById( R.id.title_bar_layout);
			titleBarLayout.setOnClickListener( new OnClickListener(){
				public void onClick(View arg0) {
					scrollToTop();
				}
			 });
		}
	}
	
    public void onActivityCreated(Bundle savedInstanceState) {
    	
        super.onActivityCreated(savedInstanceState);
    }

    public void setEmptyClickListener( View v )
    {
		v.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
			}
		});
    }
    
	protected void onRequestComplete()
	{
		if( m_requestCount > 0 )
		{
			m_requestCount--;
			if( m_requestCount == 0 )
			{
				if( getActivity() != null )
					((HomeScreen)getActivity()).showSpinner(false);
			}
		}
	} 

	protected glitchActivity GetActivityFromJObject( JSONObject jobj )
	{
		if( jobj == null )
			return null;
		
   		glitchActivity act = new glitchActivity();

   		act.who = jobj.optString("who_name");
   		act.what = jobj.optString("txt");
   		act.playerID = jobj.optString("who_tsid");
   		act.id = jobj.optString("id");
   		int time = jobj.optInt("when");
   		long seconds = System.currentTimeMillis()/1000;
   		int nSec = (int)seconds - time; 
   		act.when = Util.TimeToString(nSec);
   		
   		return act;
	}

	public glitchActivity GetActivityFromJObject( JSONObject jobj, String key, String ownerName, String ownerTsid )
	{	
		boolean bOwner = ownerTsid.equalsIgnoreCase(((HomeScreen)getActivity()).getPlayerID());
		glitchActivity act = new glitchActivity();
		
		String sType = jobj.optString("type");	
		
		act.id = key;
		act.time = jobj.optInt("when");
		long seconds = System.currentTimeMillis()/1000;
		int nSec = (int)seconds - act.time; 
		act.when = Util.TimeToString(nSec);
		act.type = sType;
			
		if( sType.equalsIgnoreCase("skill_learned") )
		{
			act.playerID = jobj.optString("who_tsid");
			act.who = jobj.optString("who_name");
			act.what = "finished learning the " + jobj.optString("skill_name") + " skill";
			act.icon = R.drawable.skill_learned; 
		}else if( sType.equalsIgnoreCase("achievement") )
		{	
			act.playerID = jobj.optString("who_tsid");
			act.who = jobj.optString("who_name");
			act.icon = R.drawable.achievement; 
			act.what = "earned the " + jobj.optString("achievement_name") + " badge";
		}else if (sType.equalsIgnoreCase("request_friend_add")) {
			
			act.playerID = jobj.optString("who_tsid");
			act.who = jobj.optString("who_name");
			act.what = "added you as a friend. You can add them back, or not";
			act.icon = R.drawable.friend_request;
			
;		}else if( sType.equalsIgnoreCase("friend_add") )
		{
			act.playerID = jobj.optString("adder_tsid");
			act.who = jobj.optString("adder_name");
			if( sType.equalsIgnoreCase("friend_add") || jobj.optString("state").equalsIgnoreCase("mutual") )
			{
				act.what = bOwner? "added you as a friend! (now it's mutual!)" : "added " + jobj.optString("addee_name") + " as a friend! (now it's mutual!)" ;
				act.icon = R.drawable.update;
			}else
			{
				act.what = "added " + jobj.optString("addee_name")  + " as friend. You can add them back, or not";
				act.icon = R.drawable.friend_request; 
			}
		}else if( sType.equalsIgnoreCase( "group_join" ) )
		{
			act.who = jobj.optString("who_name");
			act.playerID = jobj.optString("who_tsid");
			act.what = "joined " + jobj.optString("group_name");
			act.icon = R.drawable.status;
		}
		else if( sType.equalsIgnoreCase( "auction_buy" ) )
		{
			act.who = jobj.optString("buyer_name");
			act.playerID = jobj.optString("buyer_tsid");
			act.what = "purchased your auction of " + jobj.optInt("quantity") + "x " + jobj.optString("item_name") + ", for " + jobj.optInt("cost") + " currants";
			act.icon = R.drawable.auction_buy;
		}
		else if( sType.equalsIgnoreCase( "status_reply" ) )
		{
			act.who = jobj.optString("who_name");
			act.playerID = jobj.optString("who_tsid");
			act.what = jobj.optString("txt");
			JSONObject jURL = jobj.optJSONObject("who_urls");
			if( jURL != null )
				act.avatar = jURL.optString("singles") + "_100.png";

			act.in_reply_to =  GetActivityFromJObject( jobj.optJSONObject("in_reply_to") );
			
			act.icon = R.drawable.reply;
		}else if( sType.equalsIgnoreCase( "level_up" ) )
		{
			act.who = jobj.optString("who_name");
			act.playerID = jobj.optString("who_tsid");
			act.what = "reached level " + jobj.optString("level");
			act.icon = R.drawable.level_up;
		}else if( sType.equalsIgnoreCase("status") )
		{
			act.who = jobj.optString("who_name");
			act.playerID = jobj.optString("who_tsid");
			act.what = jobj.optString("txt");
			act.icon = R.drawable.update;
			JSONObject jURL = jobj.optJSONObject("who_urls");
			if( jURL != null )
				act.avatar = jURL.optString("singles") + "_100.png";
		}else if( sType.equalsIgnoreCase("request_group_invite") ) 
		{
			act.who = jobj.optString("who_name");
			act.playerID = jobj.optString("who_tsid");
			act.what = "has invited you to join " + jobj.optString("group_name");
			act.icon = R.drawable.friend_request;
		} else if (sType.equalsIgnoreCase("photo")) {
			act.who = jobj.optString("who_name");
			act.playerID = jobj.optString("who_tsid");
			JSONObject jURL = jobj.optJSONObject("who_urls");
			if (jURL != null)
				act.avatar = jURL.optString("singles") + "_100.png";
			else
				act.icon = R.drawable.status;
			act.what = jobj.optString("txt");
			act.secret = jobj.optString("secret");
			act.photo_id = jobj.optString("photo_id");
		} else if (sType.equalsIgnoreCase("photo-comment")) 
		{
			act.who = jobj.optString("who_name");
			act.playerID = jobj.optString("who_name");
			JSONObject jURL = jobj.optJSONObject("who_urls");
			if (jURL != null)
				act.avatar = jURL.optString("singles") + "_100.png";
			else
				act.icon = R.drawable.status;
			act.what = jobj.optString("txt");
			glitchActivity in_reply_to = new glitchActivity();
			in_reply_to.who = jobj.optString("owner_name");
			in_reply_to.playerID = jobj.optString("owner_tsid");
			act.in_reply_to = in_reply_to;
			act.secret = jobj.optString("secret");
			act.photo_id = jobj.optString("photo_id");
		} else if (sType.equalsIgnoreCase("photo-comment-received")) 
		{			
			act.who = jobj.optString("who_name");
			act.playerID = jobj.optString("who_name");			
			JSONObject jURL = jobj.optJSONObject("who_urls");
			if (jURL != null)
				act.avatar = jURL.optString("singles") + "_100.png";
			else
				act.icon = R.drawable.status;
			act.what = jobj.optString("txt");				
			glitchActivity in_reply_to = new glitchActivity();
			in_reply_to.who = ownerName;
			in_reply_to.playerID = ownerTsid;	
			act.in_reply_to = in_reply_to;
			act.secret = jobj.optString("secret");
			act.photo_id = jobj.optString("photo_id");
		}else
			return null;
		
		return act;
	}
	
	public void addActivityList( Vector<glitchActivity> actList, JSONObject response, String ownerName, String ownerTsid)
	{
		JSONObject jItems = response.optJSONObject("items");
		if( jItems != null && jItems.length() > 0)
		{
    		Iterator<String> it = jItems.keys(); 

    		while( it.hasNext() )
    		{	
    			String key =  it.next();
    			JSONObject jobj = jItems.optJSONObject( key );
    			if (jobj != null) {
    				glitchActivity act = GetActivityFromJObject( jobj, key, ownerName, ownerTsid );
    				if( act != null && !findActivityInList( actList,act.id ) ) 	
    					actList.add(act);
    			}
    		}
    		Collections.sort( actList, new SortByTime() );
//    		m_adapter.notifyDataSetChanged();
//    		m_homeScrollView.setBottomHasMore(m_actHasMore);
		}
	}

	private boolean findActivityInList( Vector<glitchActivity> actList, String sId )
	{
		for( int i=0; i < actList.size(); i++ )
		{
			if( actList.get(i).id.equalsIgnoreCase(sId) )
				return true;
		}
		return false;
	}
	
	public void addToLearningList( Vector<skillAvailable> learningList, JSONObject response  )
	{
		JSONObject jItems = response.optJSONObject("learning");
		
		if( jItems!= null )
		{
    		Iterator<String> it = jItems.keys(); 

    		while( it.hasNext() )
    		{	
    			String sKey = it.next();
    			JSONObject jobj = jItems.optJSONObject(sKey);
    			skillAvailable skill = new skillAvailable();

    			int nSec = jobj.optInt("time_remaining");
    			int nTotal = jobj.optInt("total_time");

    			int timeH = nSec/3600;
    			int timeM = ( nSec - timeH * 3600 ) / 60;
    			int timeS = nSec - timeH * 3600 - timeM * 60;
    			
    			skill.totalTime = nTotal;
    			skill.remainTime = nSec;
	    		skill.curTime = System.currentTimeMillis()/1000;
    			
	    		skill.id = sKey;
	    		skill.icon = jobj.optString("icon_100");
    			skill.item = jobj.optString("name");    			
    			skill.description = jobj.optString("description");
           		skill.curTime = System.currentTimeMillis()/1000;
           		
    			learningList.add(skill);
    		}
		}
		
	}
	
	public void addToUnlearningList( Vector<skillAvailable> unlearningList, JSONObject response  )
	{
		JSONObject jItems = response.optJSONObject("unlearning");
		
		if( jItems != null )
		{
    		Iterator<String> it = jItems.keys(); 

    		while( it.hasNext() )
    		{	
    			String sKey = it.next();
    			JSONObject jobj = jItems.optJSONObject(sKey);
    			skillAvailable skill = new skillAvailable();

    			int nSec = jobj.optInt("time_remaining");
    			int nTotal = jobj.optInt("unlearn_time");

    			int timeH = nSec/3600;
    			int timeM = ( nSec - timeH * 3600 ) / 60;
    			int timeS = nSec - timeH * 3600 - timeM * 60;
    			
    			skill.totalTime = nTotal;
    			skill.remainTime = nSec;
	    		skill.curTime = System.currentTimeMillis()/1000;
    			
	    		skill.id = sKey;
	    		skill.icon = jobj.optString("icon_100");
    			skill.item = jobj.optString("name");    			
    			skill.description = jobj.optString("description");
           		skill.curTime = System.currentTimeMillis()/1000;
           		
           		unlearningList.add(skill);
    		}
		}
	}
	
	public void addFriendsList(Vector<glitchFriend> friendsList, JSONObject response) {
		
		JSONObject jItems = response.optJSONObject("friends");
		
		if (jItems != null) {
			Iterator<String> it = jItems.keys();
			
			while (it.hasNext()) {
				
				String tsid = it.next();
				JSONObject obj = jItems.optJSONObject(tsid);
				glitchFriend friend = new glitchFriend();
				
				friend.id = tsid;
				friend.player_name = obj.optString("player_name");
				friend.user_name = obj.optString("user_name");
				friend.is_reverse = obj.optBoolean("is_reverse");
				JSONObject avatar = obj.optJSONObject("avatar");
				if (avatar != null) {
					friend.avatar = avatar.optString("100");
				}
				
				friendsList.add(friend);
			}
			Collections.sort( friendsList, new SortByName() );
		}
	}
	
	public void requestFinished(GlitchRequest request) 
	{
		if( getActivity() == null )
			return;
		
        if (request != null && request.method != null )
        {
        	JSONObject response = request.response;
        	if( response != null )
        	{
        		Log.i("response", " method: " + request.method + " response: " + request.response );
        		onRequestBack( request.method, response );
        	}
        }
	}
	
	public void requestFailed(GlitchRequest request) {
		((HomeScreen)getActivity()).requestFailed(request);
	}
	
	protected void onRequestBack( String method, JSONObject response )
	{
	}
	
	protected boolean doesSupportRefresh()
	{
		return false;
	}

	protected boolean doesSupportMore()
	{
		return false;
	}
	
	protected void onRefresh()
	{
	}

	protected void onMore()
	{
	}
	
	protected void scrollToTop()
	{		
	}
	
	public void logPageView()
	{
		FlurryAgent.onPageView();
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		if (doesSupportRefresh())
			menu.add(0, MENU_COMMAND_REFRESH, Menu.NONE + 0, R.string.str_menu_refresh);

		if (doesSupportMore())
			menu.add(1, MENU_COMMAND_MORE, Menu.NONE + 1, R.string.str_menu_more);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case MENU_COMMAND_REFRESH:
			onRefresh();
			FlurryAgent.logEvent(getClass().toString() + " - Clicked to refresh");
			break;
		case MENU_COMMAND_MORE:
			onMore();
			FlurryAgent.logEvent(getClass().toString() + " - Clicked to load more");
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void launchLoginIntent()
	{
		// this is called from the empty constructor of fragments. this means it is coming back
		// from sleep so we should just clear the stack and start fresh
//		Intent intent = new Intent();
//		intent.setClass(getActivity(), LoginScreen.class);
//		startActivity(intent);
	}
}
