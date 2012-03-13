package com.tinyspeck.glitchhq;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.tinyspeck.android.GlitchRequest;
import com.tinyspeck.glitchhq.BaseFragment.glitchActivity;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

public class SidebarFragment extends BaseFragment{

    private ActivityListViewAdapter m_adapter;
    private LinearListView  m_listView;
    
	private String m_actItemLast;
	
    static final public int ACTIVITY_TYPE_ALL = 0;
    static final public int ACTIVITY_TYPE_REQUESTS = 1;
    static final public int ACTIVITY_TYPE_UPDATES = 2;
	
	private int m_currentActType = ACTIVITY_TYPE_ALL;	
    private View m_root;
	
 	private Vector<glitchActivity> m_actList;
	
    public void onActivityCreated(Bundle savedInstanceState) {
    	
        super.onActivityCreated(savedInstanceState);
    }

    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) 
    {
    	View curView =  ViewInit( inflater, R.layout.sidebar_view, container );
    	m_root = curView;
    	init( curView  );
		return curView;
    }
    
	private void init( View root )
	{
		boolean bUpdateData = m_actList == null;
		
		if( bUpdateData )
			m_actList = new Vector<glitchActivity>();
		
	    m_adapter = new ActivityListViewAdapter( this, m_actList );
        m_listView = (LinearListView)root.findViewById( R.id.ActivityListView );
		m_listView.setAdapter( m_adapter );
		
		if( bUpdateData )
			getActivity(false);
		else
		{
			showHeader();
			updateActivityFeeds();
		}
	}

	private void getActivity( boolean bMore )
	{
		String selfPlayerID = ((HomeScreen)getActivity()).getPlayerID();
		
        Map<String,String> params = new  HashMap<String,String>();
        params.put("player_tsid", selfPlayerID );

        showHeader();
        
        if( m_currentActType == ACTIVITY_TYPE_REQUESTS )
            params.put("cat", "requests" );
        else if ( m_currentActType == ACTIVITY_TYPE_UPDATES )
            params.put("cat", "notes" );
        
        if( bMore && m_actItemLast != null )
        {
        	params.put("last", m_actItemLast );
        	m_bAppendMode = true;
        }else
        	m_bAppendMode = false;

        GlitchRequest request0 = m_application.glitch.getRequest("activity.feed", params );
        request0.execute(this);
        
        m_requestCount = 1;
    	((HomeScreen)getActivity()).showSpinner(true);
	}

	private void updateActivityFeeds()
	{
		boolean bHas = m_actList.size() > 0 ;
		m_root.findViewById( R.id.list_message ).setVisibility( bHas?  View.GONE : View.VISIBLE );
		m_listView.setVisibility( bHas? View.VISIBLE: View.GONE );
		
		if (bHas)
			m_adapter.notifyDataSetChanged();
		
		if (m_bAppendMode) {		
			ScrollView sv = (ScrollView) m_root.findViewById(R.id.ActivityScrolLView);			
			Util.delayedFlingOfScrollView(sv, 500, 500);
		}
	}
	
	private void showHeader()
	{
        TextView header = (TextView) m_root.findViewById( R.id.activity_header );
        header.setTypeface( m_application.m_vagFont );  
        
        if( m_currentActType == ACTIVITY_TYPE_REQUESTS )
        {
            header.setVisibility(View.VISIBLE);
            header.setText( "Requests" );
        }
        else if ( m_currentActType == ACTIVITY_TYPE_UPDATES )
        {
            header.setVisibility(View.VISIBLE);
            header.setText( "Updates" );
        }else
        	header.setVisibility(View.GONE);
	}
	
	protected boolean doesSupportRefresh()
	{
		return false;
	}

	protected boolean doesSupportMore()
	{
		return false;
	}
	
	protected void scrollToTop() 
	{
		ScrollView sv = (ScrollView) m_root.findViewById(R.id.ActivityScrolLView);
		sv.smoothScrollTo(0, 0);
	}
}
