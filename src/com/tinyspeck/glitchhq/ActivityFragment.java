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

public class ActivityFragment extends BaseFragment{

    private ActivityListViewAdapter m_adapter;
    private LinearListView  m_listView;
    
    private Button m_btnFilter;
    private Button m_btnEdit;
    
	private String m_actItemLast;
	private boolean m_actHasMore;
	
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
    	View curView =  ViewInit( inflater, R.layout.activity_view, container );
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
		initTopMenu();
	}

	private void initTopMenu()
	{
		 m_btnFilter = (Button)m_root.findViewById(R.id.btnFilter);
		 m_btnEdit = (Button)m_root.findViewById(R.id.btnEdit);
		 m_btnFilter.setVisibility(View.VISIBLE);
		 m_btnEdit.setVisibility(View.VISIBLE);
		 
		 m_btnEdit.setOnClickListener( new OnClickListener(){
				public void onClick(View arg0) {
					FlurryAgent.logEvent("Activity - 'Compose' button pressed");
					composeNotes();
				}
		 });
		 
		 m_btnFilter.setOnClickListener( new OnClickListener(){
				public void onClick(View arg0) {
				
					final PopupWindow pw = Util.showPopup( getActivity(), R.layout.filter_menu, true, m_btnFilter, 5, 5 ); 
					View v = pw.getContentView();
					Button btn = (Button)v.findViewById(R.id.btn_filter_all);
					btn.setOnClickListener( new OnClickListener(){
						public void onClick(View v) {
							m_currentActType = ACTIVITY_TYPE_ALL;
							pw.dismiss();
							getActivity(false);
						}
					});

					btn = (Button)v.findViewById(R.id.btn_filter_updates);
					btn.setOnClickListener( new OnClickListener(){
						public void onClick(View v) {
							m_currentActType = ACTIVITY_TYPE_UPDATES;
							pw.dismiss();
							getActivity(false);
						}
					});

					btn = (Button)v.findViewById(R.id.btn_filter_requests);
					btn.setOnClickListener( new OnClickListener(){
						public void onClick(View v) {
							m_currentActType = ACTIVITY_TYPE_REQUESTS;
							pw.dismiss();
							getActivity(false);
						}
					});
				}
			 });
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
	
	@Override
	public void onRequestBack( String method, JSONObject response )
	{
		if( method == "activity.feed" )
		{
   	    	if( !m_bAppendMode )
   	    		m_actList.clear();
   	    	
		    m_actItemLast = response.optString("last");
    		m_actHasMore = (response.optInt("has_more")==1)? true: false;
    		
    		addActivityList( m_actList, response, true );
    		updateActivityFeeds();
    		if ( m_actList.size() == 0 ) {
    			((TextView)m_root.findViewById( R.id.list_message )).setText( R.string.activity_no_items );
    		}
			onRequestComplete();
		} else if ( method == "activity.setStatus" ) 
		{
			if (response.optInt("ok") != 1) {
				Util.shortToast(getActivity(), "Failed");
			} else {
				Util.shortToast(getActivity(), "Posted");				
			}
		} else if (method == "activity.joinGroup" || method == "activity.declineGroup" || method == "activity.addBuddy" || method == "activity.declineBuddy")
		{
			getActivity(false);
			onRequestComplete();
		}
	}

	private void composeNotes()
	{
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View v = inflater.inflate(R.layout.compose_notes, null, false );
		
		final EditText ed = ( EditText ) v.findViewById( R.id.note_editor );
		
	    final PopupWindow pw = new PopupWindow( getActivity() );
	    pw.setContentView( v );
		pw.setWindowLayoutMode( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT );
	    pw.setFocusable(true);				    
	    
	    pw.setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE  | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE );
	    
	    pw.setOutsideTouchable(false);
	    pw.setTouchable(true);				    
	    pw.setBackgroundDrawable(new BitmapDrawable());
	    if( Util.GetScreenSizeAttribute( getActivity() ) == 0 )
	    	pw.showAsDropDown( m_btnFilter,  0,  -40 ); 
	    else
	    	pw.showAtLocation( m_root, 0, 0, 0 );
	    
	    Button btnSend = (Button) v.findViewById(R.id.btnSend );

	    btnSend.setOnClickListener( new OnClickListener(){
			public void onClick(View v) {				
				sendActivityStatus( null, ed.getText().toString() );
				FlurryAgent.logEvent("Activity Compose Status Message - 'Send' button pressed");
				pw.dismiss();
			}
		});

	    Button btnClose = (Button) v.findViewById(R.id.btnClose );
	    btnClose.setOnClickListener( new OnClickListener(){
			public void onClick(View v) {
				FlurryAgent.logEvent("Activity Compose Status Message - 'Close' button pressed");
				pw.dismiss();
			}
		});
	}
	
	private void sendActivityStatus( String statusID, String msg )
	{
		
        Map<String,String> params = new  HashMap<String,String>();
        params.put("player_tsid", ((HomeScreen)getActivity()).getPlayerID() );
        
        if( statusID != null )
        	params.put("status_id", statusID );
        
        params.put("txt", msg );

        GlitchRequest request0 = m_application.glitch.getRequest("activity.setStatus", params );
        request0.execute(this);

        m_requestCount = 1;
    	((HomeScreen)getActivity()).showSpinner(true);
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
		getActivity(false);
	}

	protected void onMore()
	{
		getActivity(true);
	}
	
	protected void scrollToTop() 
	{
		ScrollView sv = (ScrollView) m_root.findViewById(R.id.ActivityScrolLView);
		sv.smoothScrollTo(0, 0);
	}
	
}
