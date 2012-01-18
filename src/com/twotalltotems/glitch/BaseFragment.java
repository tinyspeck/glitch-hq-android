package com.twotalltotems.glitch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import org.json.JSONObject;

import com.tinyspeck.android.GlitchRequest;
import com.tinyspeck.android.GlitchRequestDelegate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class BaseFragment extends Fragment implements GlitchRequestDelegate
{
	public int m_requestCount = 0;
	public boolean m_bAppendMode;
    protected MyApplication m_application;    
	
  	public class glitchActivity{
 		 String who;
 		 String when;
 		 String what;
 		 String avatar;
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
  	 
 	public class skillLearning {
		 String item;
		 int remainTime;
		 int totalTime;
		 long curTime;
 	 };

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
	
	public View ViewInit( LayoutInflater inflater, int nLayout, ViewGroup container )
	{
	    m_application = (MyApplication)getActivity().getApplicationContext();
		View curView = inflater.inflate( nLayout, container, false  );
		setEmptyClickListener( curView );
		setupTitlebar( curView );
		return curView;
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

	protected glitchActivity GetActivityFromJObject( JSONObject jobj  )
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

	public glitchActivity GetActivityFromJObject( JSONObject jobj, String key, boolean bOwner )
	{
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
		}else if( sType.equalsIgnoreCase("friend_add") || sType.equalsIgnoreCase("request_friend_add")  )
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
			act.what = "has invited you to join " + jobj.optString("group_name");
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
		}else
			return null;
		
		return act;
	}
	
	public void addActivityList( Vector<glitchActivity> actList, JSONObject response, boolean bOwner )
	{
		JSONObject jItems = response.optJSONObject("items");
		if( jItems != null )
		{
    		Iterator<String> it = jItems.keys(); 

    		while( it.hasNext() )
    		{	
    			String key =  it.next();
    			JSONObject jobj = jItems.optJSONObject( key );
    			glitchActivity act = GetActivityFromJObject( jobj, key, bOwner );
    			if( act != null && !findActivityInList( actList,act.id ) ) 	
    				actList.add(act);
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
	
	public void addToLearningList( Vector<skillLearning> learningList, JSONObject response  )
	{
		JSONObject jItems = response.optJSONObject("learning");
		
		if( jItems != null )
		{
    		Iterator<String> it = jItems.keys(); 

    		while( it.hasNext() )
    		{	
    			JSONObject jobj = jItems.optJSONObject( it.next() );
    			skillLearning skill = new skillLearning();

    			int nSec = jobj.optInt("time_remaining");
    			int nTotal = jobj.optInt("total_time");

    			int timeH = nSec/3600;
    			int timeM = ( nSec - timeH * 3600 ) / 60;
    			int timeS = nSec - timeH * 3600 - timeM * 60;
    			
    			skill.totalTime = nTotal;
    			skill.remainTime = nSec;
	    		skill.curTime = System.currentTimeMillis()/1000;
    			
    			skill.item = jobj.optString("name");
    			learningList.add(skill);
    		}
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
}
