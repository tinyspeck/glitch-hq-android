package com.tinyspeck.glitchhq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

import com.tinyspeck.android.GlitchRequest;

import android.content.Context;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ActivityDetailFragment extends BaseFragment{

  	private glitchActivity m_currentActivity;
  	private String m_actId, m_playerId;
  	private View m_root;
  	private boolean m_bNotes = false;
  	private boolean m_bRefreshToBottom = false;
  	
  	ActivityDetailFragment( String playerId, String actId )
  	{
  		m_actId = actId;
  		m_playerId = playerId;
  	}
  	
    public void onActivityCreated(Bundle savedInstanceState) {
    	
        super.onActivityCreated(savedInstanceState);
    }

    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) 
    {
    	View curView =  ViewInit( inflater, R.layout.activity_detail_view, container );
    	m_root = curView;
    	
    	getActivityStatus();
		return curView;
    }

    private void setLikeButton( boolean bLiked )
    {
    	if( !m_bNotes || ((HomeScreen)getActivity()).getPlayerID().equalsIgnoreCase( m_playerId ) )
    		return;
    	
    	Button btn = (Button)m_root.findViewById( R.id.btnLike );
		btn.setVisibility(View.VISIBLE);
		if( bLiked )
		{
			btn.setText( "Liked" );
			btn.setEnabled( false );
		}	
		btn.setOnClickListener( new OnClickListener(){
				public void onClick(View arg0) {
					setStatusLike( m_playerId, m_actId );
				}
		 });
    }
    
	@Override
	public void onRequestBack( String method, JSONObject response )
	{
		if ( method == "activity.setStatusReply" )
		{
			m_bRefreshToBottom = true;
			getActivityStatus();
		}else if ( method == "activity.getStatus" )
		{	
			receiveActivityStatus( response );
			
			if( m_bRefreshToBottom )
			{
				m_bRefreshToBottom = false;
				ScrollView sv = (ScrollView)m_root.findViewById(R.id.activity_detail_scrollview );
				sv.fullScroll(ScrollView.FOCUS_DOWN);
			}
			onRequestComplete();
		}else if ( method == "activity.setStatusLike" )
			getActivityStatus();
	}
	
	private void receiveActivityStatus( JSONObject response )
	{
		boolean bOwner = ((HomeScreen)getActivity()).getPlayerID() == m_playerId;
		m_currentActivity = GetActivityFromJObject( response.optJSONObject("item"), m_actId, bOwner );
		m_currentActivity.in_reply_to =  GetActivityFromJObject( response.optJSONObject("item").optJSONObject("in_reply_to") );
		m_currentActivity.likes = response.optJSONObject("item").optInt("likes");
		m_bNotes = m_currentActivity.type.equalsIgnoreCase("status_reply") || m_currentActivity.type.equalsIgnoreCase("status");
		
		boolean bLiked = false;
		
		String myPlayerID = ((HomeScreen)getActivity()).getPlayerID();
		JSONArray jLikes = response.optJSONArray("likes");
		if( jLikes != null )
			for( int i=0; i < jLikes.length(); i++ )
			{
	   			JSONObject jobj = jLikes.optJSONObject(i);
	   			if( myPlayerID.equalsIgnoreCase( jobj.optString("who_tsid") ) )
	   				bLiked = true;
			}
		
		JSONArray jItems = response.optJSONArray("replies");
		m_currentActivity.replies = new ArrayList<glitchActivity>();
		
		if( jItems != null )
			for( int i=0; i < jItems.length(); i++ )
			{
	   			JSONObject jobj = jItems.optJSONObject(i);
	   			
	   			glitchActivity act =  GetActivityFromJObject( jobj );
	   			m_currentActivity.replies.add(act);
			}
		setActivityDetailView( m_root );	
		setLikeButton( bLiked );
	}
	
	public void getActivityStatus()
	{
		String statusID = m_actId;
		
        Map<String,String> params = new  HashMap<String,String>();
        params.put("player_tsid", m_playerId );
        params.put("status_id", statusID );

        GlitchRequest request0 = m_application.glitch.getRequest("activity.getStatus", params );
        request0.execute(this);
        
        m_requestCount = 1;
        ((HomeScreen)getActivity()).showSpinner(true);
	}

	void setActivityDetailView( View root  )
	{
		View sectionAct = root.findViewById( R.id.section_activity );

		root.findViewById(R.id.notes_detail_view).setVisibility(m_bNotes? View.VISIBLE: View.GONE);
		sectionAct.setVisibility( m_bNotes? View.GONE: View.VISIBLE );
		 
		View sectionV =  root.findViewById(R.id.section_in_reply_to);

		setUpdatesSection( sectionV, m_currentActivity.in_reply_to );

		sectionV =  root.findViewById(R.id.section_myself);
		setUpdatesSection( m_bNotes? sectionV: sectionAct, m_currentActivity );

		sectionV =  root.findViewById(R.id.section_replies);
		if( m_currentActivity.replies == null || m_currentActivity.replies.size() == 0 )
			sectionV.setVisibility( View.GONE);
		else
		{
			sectionV.setVisibility( View.VISIBLE );
			addRepliesToLayout( root, m_currentActivity.replies );
		}
		if( m_bNotes )
		{
			root.findViewById(R.id.reply_pane ).setVisibility(View.VISIBLE);
			EditText ed = (EditText ) root.findViewById(R.id.replyEditor );
			ed.setHint( "Reply to " + m_currentActivity.who + "'s update...");
			replyNotes( ed );
		}
	}

	private void replyNotes( EditText editor )
	{
		editor.setImeActionLabel("Done", EditorInfo.IME_ACTION_DONE );
		editor.setImeOptions(EditorInfo.IME_ACTION_DONE);
		editor.setOnEditorActionListener(new OnEditorActionListener() 
		{
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
			{
				if ( actionId == EditorInfo.IME_NULL  || actionId == EditorInfo.IME_ACTION_DONE  )
				{ 
					setStatusReply( m_currentActivity.playerID, m_currentActivity.id, v.getText().toString() );
					v.setText("");
					
					InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.hideSoftInputFromWindow( v.getWindowToken(), 0);
					 
					return true;
				}
				return false;
			}
		});
	}

	void setUpdatesSection( View sectionV, glitchActivity act )
	{
		if( act == null )
		{
			sectionV.setVisibility(View.GONE);
			return;
		}
		
		sectionV.setVisibility(View.VISIBLE);

		ImageView icon = (ImageView) sectionV.findViewById(R.id.activity_detail_icon);
		if( icon != null )
			icon.setImageResource( act.icon );

		TextView tv = (TextView) sectionV.findViewById(R.id.itemName);
		tv.setTypeface(m_application.m_vagFont);
		tv.setText( act.who );
		tv.setTag( act.playerID );
		
		if( act == m_currentActivity && !act.playerID.equalsIgnoreCase( ( (HomeScreen)getActivity()).getPlayerID() )  )
		{
			OnClickListener lsn = new OnClickListener(){
				public void onClick(View v) {
					String pID = (String)v.getTag();
					ProfileFragment f = new ProfileFragment( pID, true );
					((HomeScreen)getActivity()).setCurrentFragment(f, true );
				}
			};

			View arrowView = sectionV.findViewById( R.id.activity_go_arrow );
			arrowView.setVisibility( View.VISIBLE );
			arrowView.setTag( act.playerID );
			arrowView.setOnClickListener(lsn);
			
			tv.setOnClickListener(lsn);
			
			if( m_bNotes && act.likes > 0 )
			{
				TextView tvLike = (TextView) sectionV.findViewById( R.id.itemLikedByPerson );
				tvLike.setText( act.likes + " person liked" );
			}
		}
		
		tv = (TextView) sectionV.findViewById(R.id.itemDetail); 
	//	tv.setTypeface(m_application.m_vagLightFont);		
		tv.setText( act.what );
		Linkify.addLinks(tv, Linkify.WEB_URLS);
		tv.setTag( act );
		
		if( act != m_currentActivity )
		{
			tv.setOnClickListener( new OnClickListener(){
				public void onClick(View v) {
					glitchActivity gact = (glitchActivity)v.getTag();
					ActivityDetailFragment fm = new ActivityDetailFragment( gact.playerID, gact.id);
					((HomeScreen)getActivity()).setCurrentFragment(fm, true );
				}
			});
		}
		
		tv = (TextView) sectionV.findViewById(R.id.itemTime); 
		tv.setTypeface(m_application.m_vagLightFont);
		if( !act.when.equalsIgnoreCase("just now") )
			tv.setText( act.when + " ago" );
		else
			tv.setText( act.when );
	}
	
	private void addRepliesToLayout( View root, ArrayList<glitchActivity> replies )
	{
		LinearLayout ll = (LinearLayout)root.findViewById(R.id.section_replies_content);
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ll.removeAllViewsInLayout();
		for(int i=0; i < replies.size(); i++ )
		{
			View v = inflater.inflate(R.layout.activity_reply_item, null );
			setUpdatesSection( v, replies.get(i) );
			ll.addView(v);
		}
	}
	
	private void setStatusReply( String replyToPlayer, String statusID, String msg )
	{
        Map<String,String> params = new  HashMap<String,String>();
        params.put("player_tsid", ((HomeScreen)getActivity()).getPlayerID() );
        params.put("reply_to_player_tsid", replyToPlayer );
       	params.put("reply_to_id", statusID );
        
        params.put("txt", msg );

        GlitchRequest request0 = m_application.glitch.getRequest("activity.setStatusReply", params );
        request0.execute(this);

        m_requestCount = 1;
    	((HomeScreen)getActivity()).showSpinner(true);
	}

	private void setStatusLike( String player, String statusID )
	{
        Map<String,String> params = new  HashMap<String,String>();
        params.put("like_player_tsid", player );
        params.put("like_id", statusID );
        
        GlitchRequest request0 = m_application.glitch.getRequest("activity.setStatusLike", params );
        request0.execute(this);

        m_requestCount = 1;
    	((HomeScreen)getActivity()).showSpinner(true);
	}
	
	protected boolean doesSupportRefresh()
	{
		return true;
	}

	protected void onRefresh()
	{
	   	getActivityStatus();
	}
}
