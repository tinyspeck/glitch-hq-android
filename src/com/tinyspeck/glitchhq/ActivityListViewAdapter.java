package com.tinyspeck.glitchhq;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.tinyspeck.android.GlitchRequest;
import com.tinyspeck.glitchhq.BaseFragment.glitchActivity;

public class ActivityListViewAdapter extends BaseAdapter 
{
	 private Vector<glitchActivity> m_actList;
 	 private LayoutInflater m_inflater;
 	 private Activity m_act;
 	 private BaseFragment m_bf;
     private MyApplication m_application;    
 	 
   	 public class ViewHolder {
	     ImageView icon;
   		 TextView  name;
   		 TextView  to_who;
   		 ImageView replyIcon;
	     TextView  time;
	     TextView  description;
	     View divider;
	     View whole;
	     Button addBack,notNow;
	     View request_buttons;
   	 };
	
     public ActivityListViewAdapter( BaseFragment bf, Vector<glitchActivity> actList  ) 
     {
    	 m_actList = actList;
    	 m_act = bf.getActivity();
    	 m_bf = bf;
    	 m_inflater = (LayoutInflater)m_act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	     m_application = (MyApplication)m_act.getApplicationContext();
     }
  
     public int getCount()
     {
    	 if( m_actList == null )
    		 return 0;
    	 
    	 return m_actList.size();
     }
  
     public Object getItem(int position)
     {
       return position;
     }
  
     public long getItemId(int position)
     {
       return position;
     }

     public View getView(int position, View convertView, ViewGroup parent)
     {
		ViewHolder holder=null; 
		
		if( convertView != null)
			holder = (ViewHolder)convertView.getTag();

		if( holder == null )
		{
    		convertView = m_inflater.inflate( R.layout.list_item, null);
    		holder = new ViewHolder();

    		holder.name = (TextView)convertView.findViewById(R.id.activity_who);
            holder.icon = (ImageView)convertView.findViewById(R.id.icon_skill);
            holder.to_who = (TextView)convertView.findViewById(R.id.activity_reply_to_who);
            holder.replyIcon = (ImageView)convertView.findViewById(R.id.reply_icon);
            holder.description = (TextView)convertView.findViewById(R.id.activity_what);
            holder.time = (TextView)convertView.findViewById(R.id.activity_when);
            holder.divider = (View)convertView.findViewById(R.id.list_diveider);
            holder.name.setTypeface( m_application.m_vagFont );  
            holder.to_who.setTypeface( m_application.m_vagFont );  
            
            holder.whole = (View)convertView.findViewById(R.id.actfeed_item);
//          holder.description.setTypeface( m_application.m_vagFont );
            holder.time.setTypeface( m_application.m_vagLightFont );
            holder.addBack = (Button)convertView.findViewById(R.id.btnAddBack);
            holder.notNow = (Button)convertView.findViewById(R.id.btnNotNow);
            holder.request_buttons = convertView.findViewById(R.id.request_buttons);
            convertView.setTag(holder);
		}		
		holder = (ViewHolder)convertView.getTag();

		if( position < getCount() )
		{
			glitchActivity act = m_actList.get(position);
			holder.name.setText( act.who );
			if( act.avatar != null )
	    		DrawableURL.CropShow( holder.icon, act.avatar );				
			else
				holder.icon.setImageResource( act.icon );
			
			holder.description.setText( act.what );
			
			holder.time.setText( act.when );
			if( position == getCount() - 1 )
				holder.divider.setVisibility(View.GONE);
			else
				holder.divider.setVisibility(View.VISIBLE);
			
			if( holder.request_buttons != null )
				holder.request_buttons.setVisibility( View.GONE );
			
			if( act.type.equalsIgnoreCase( "status_reply" ) )
			{
				String toname="";
				
				if( act.in_reply_to != null )
				{
					toname = act.in_reply_to.who;
					if (((HomeScreen)m_act).getPlayerID().equalsIgnoreCase(act.in_reply_to.playerID))
					{
						toname = "you";
						holder.to_who.setTextColor( 0xff707070 );
					}else
						holder.to_who.setTextColor( 0xff000000 );
				}
				
				holder.replyIcon.setVisibility( View.VISIBLE );
				holder.to_who.setVisibility( View.VISIBLE );
				holder.to_who.setText( toname );
			}else
			{
				holder.to_who.setVisibility( View.GONE );
				holder.replyIcon.setVisibility( View.GONE );
			}
			if( act.type.equalsIgnoreCase( "request_group_invite" ) )
			{
				holder.request_buttons.setVisibility( View.VISIBLE );
				
				holder.notNow.setText(R.string.btn_decline);
				holder.addBack.setText(R.string.btn_join);

				holder.addBack.setTag(act.id);
				holder.notNow.setTag(act.id);
				
				holder.addBack.setOnClickListener( new OnClickListener() {
					public void onClick(View v) {
						FlurryAgent.logEvent("Activity - Group Request - Join button pressed");
						sendRequest( (String)v.getTag(), "activity.joinGroup" );
					}
				});
				holder.notNow.setOnClickListener( new OnClickListener() {
					public void onClick(View v) {
						FlurryAgent.logEvent("Activity - Group Request - Decline button pressed");
						sendRequest( (String)v.getTag(), "activity.declineGroup" );
					}
				});
			}else
			if( act.type.equalsIgnoreCase( "request_friend_add" ) )
			{
				holder.addBack.setTag(act.id);
				holder.notNow.setTag(act.id);
				
				holder.request_buttons.setVisibility( View.VISIBLE );
				
				holder.addBack.setText(R.string.btn_add_back);
				holder.notNow.setText(R.string.btn_not_now);

				holder.addBack.setOnClickListener( new OnClickListener() {
					public void onClick(View v) {
						FlurryAgent.logEvent("Activity - Friend Request - Add Back button pressed");
						sendRequest( (String)v.getTag(), "activity.addBuddy" );
					}
				});
				holder.notNow.setOnClickListener( new OnClickListener() {
					public void onClick(View v) {
						FlurryAgent.logEvent("Activity - Friend Request - Not Now button pressed");
						sendRequest( (String)v.getTag(), "activity.declineBuddy" );
					}
				});
			}
		}
		holder.whole.setTag(position);
		holder.whole.setOnClickListener( new OnClickListener()
	    {
			public void onClick(View arg0) {
				glitchActivity currentActivity = m_actList.get( (Integer)arg0.getTag() );
				if(  currentActivity.type.equalsIgnoreCase( "request_friend_add" ) )
				{
					ProfileFragment f = new ProfileFragment( currentActivity.playerID, true );
					((HomeScreen)m_act).setCurrentFragment(f, true );
				}else{
					ActivityDetailFragment fm = new ActivityDetailFragment(currentActivity.playerID,currentActivity.id);
					((HomeScreen)m_act).setCurrentFragment(fm, true );
				}
			}
	    });  
       	return convertView;
	 }
     
     private void sendRequest( String requestID, String command )
     {
        Map<String,String> params = new  HashMap<String,String>();
        params.put("id", requestID ); 
    	
		GlitchRequest request = m_application.glitch.getRequest( command, params ); 
		request.execute( m_bf );

    	((HomeScreen)m_act).showSpinner(true);
     }
}
