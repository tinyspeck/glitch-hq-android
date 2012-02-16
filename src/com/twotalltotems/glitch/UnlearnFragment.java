package com.twotalltotems.glitch;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.json.JSONObject;

import com.tinyspeck.android.GlitchRequest;
import com.twotalltotems.glitch.BaseFragment.skillAvailable;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

public class UnlearnFragment extends BaseFragment {

	private UnlearnableListAdapter m_unlearnableAdapter;
	private LinearListView  m_unlearnableListView;
	
	private Timer m_RemainingTimer;
	private View m_root;	
	
	private TextView m_unlearningSkillName;
	private TextView m_unlearningSkillTime;
	private View m_unlearningSkillProgress;
	
	private Vector<skillAvailable> m_unlearningList;
	private Vector<skillAvailable> m_unlearnableList;
	private boolean m_hasUnlearning;
	
    public void onActivityCreated(Bundle savedInstanceState) {
    	
        super.onActivityCreated(savedInstanceState);
    }
    
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) 
    {
    	View curView = ViewInit( inflater, R.layout.unlearn_view, container );
    	m_root = curView;
    	init( curView );
		return curView;
    }
    
    private void init(View root) 
    {
    	boolean bUpdateData = m_unlearnableList == null;
		
   	    m_unlearnableListView = (LinearListView)root.findViewById( R.id.unlearn_list );
    	
   	    TextView tv = (TextView)root.findViewById(R.id.unlearnable_skills);
   	    tv.setTypeface(m_application.m_vagFont);
    	
   	    m_unlearnableAdapter = new UnlearnableListAdapter();
		m_unlearnableListView.setAdapter( m_unlearnableAdapter );

		m_unlearningSkillName = (TextView)root.findViewById(R.id.tv_unlearnName);
		m_unlearningSkillTime = (TextView)root.findViewById(R.id.tv_unlearnTime);
		m_unlearningSkillProgress = (View)root.findViewById(R.id.unlearning_progress);
		
		m_unlearningSkillName.setTypeface( m_application.m_vagLightFont );
		m_unlearningSkillTime.setTypeface( m_application.m_vagLightFont );

   	    
   		FrameLayout unlearningPanel = (FrameLayout) m_root.findViewById(R.id.unlearn_view_unlearning_panel);
		unlearningPanel.setOnClickListener( new OnClickListener() 
		{			
			public void onClick(View arg0) {
				skillAvailable skill = m_unlearningList.get(0);
				SkillDetailFragment fm = new SkillDetailFragment(skill);					
				((HomeScreen)getActivity()).setCurrentFragment(fm, true);
			}
			
		});
   	    
   	    setupSettings();
   	    
   	    if( bUpdateData )
		{
			m_unlearningList = new Vector<skillAvailable>();
			getSkills();
		} else {
			showSkillPage();
		}
    }
    
    private void showSkillPage()
	{
		setUnlearningSkill();
        InitUpdateUnlearnRemainningTimer();
        updateUnlearnableList();
	}

    public void getSkills()
    {
    	GlitchRequest request1 = m_application.glitch.getRequest("skills.listUnlearning");
        request1.execute(this);

		GlitchRequest request2 = m_application.glitch.getRequest("skills.listUnlearnable");
        request2.execute(this);
        
        GlitchRequest request3 = m_application.glitch.getRequest("skills.hasUnlearning");
        request3.execute(this);
        
        m_requestCount = 3;
		((HomeScreen)getActivity()).showSpinner(true);
    }
    
    private void updateUnlearnableList()
	{
   		boolean bHas = m_unlearnableList.size() > 0;
   		
   		m_root.findViewById( R.id.unlearn_list_message ).setVisibility( bHas? View.GONE: View.VISIBLE );
   		m_unlearnableListView.setVisibility( bHas? View.VISIBLE: View.GONE );
   		
   		if( bHas )
   			m_unlearnableAdapter.notifyDataSetChanged();
	}
    
    private class UnlearnableListAdapter extends BaseAdapter 
	{
	   	 public class ViewHolder {
	   		 TextView  item;
		     TextView  time;
		     ImageView icon;
		     ImageView status;
		     View whole;
	   	 };
		
	     public UnlearnableListAdapter() 
	     {
	     }
	  
	     public int getCount()
	     {
	    	 if( m_unlearnableList == null )
	    		 return 0;
	    	 
	    	 return m_unlearnableList.size();
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
    			LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			
        		convertView = inflater.inflate( R.layout.skill_list_item, null);
        		holder = new ViewHolder();
        		
        		holder.whole = convertView.findViewById(R.id.skill_item);
        		holder.item = (TextView)convertView.findViewById(R.id.skill_name);
                holder.time = (TextView)convertView.findViewById(R.id.skill_time);
                holder.icon = (ImageView)convertView.findViewById(R.id.skill_icon);
                holder.status = (ImageView)convertView.findViewById(R.id.skill_status);

                holder.item.setTypeface( m_application.m_vagFont );
                holder.time.setTypeface( m_application.m_vagLightFont );
                
                convertView.setTag(holder);
    		}
    		holder = (ViewHolder)convertView.getTag();

    		if( position < getCount() )
    		{
    			skillAvailable skill = m_unlearnableList.get(position);
    				
    			//DrawableURL.Show( holder.icon, skill.icon, false);
    			m_application.Download( skill.icon, holder.icon, MyApplication.DOWNLOAD_TYPE_NORMAL );
    			
    			holder.item.setText( skill.item );
    			
   				holder.time.setText( Util.TimeToString( skill.totalTime, false ) );
   				boolean bShowStop = ( skill.remainTime > 0);
				holder.status.setVisibility( bShowStop? View.VISIBLE: View.INVISIBLE );
    		}
    		holder.whole.setTag(position);
    		holder.whole.setOnClickListener( new OnClickListener()
    	    {
				public void onClick(View arg0) {
					
					int nItem = (Integer)arg0.getTag();

					skillAvailable skill = m_unlearnableList.get(nItem);
					SkillDetailFragment fm = new SkillDetailFragment(skill.id);
					((HomeScreen)getActivity()).setCurrentFragment(fm,true);
				}
    	    });
    		
           	return convertView;
    	 }
    }
    
    private void setupSettings() 
    {
		
		final Button btnSettings = (Button)m_root.findViewById(R.id.btnSettings);
		btnSettings.setVisibility( View.VISIBLE );
		
		btnSettings.setOnClickListener( new OnClickListener(){
			public void onClick(View arg0) {
				final PopupWindow pw = Util.showPopup( getActivity(), R.layout.skill_settings, true, btnSettings, 5, 5 );
				View v = pw.getContentView();
				Button btn = (Button)v.findViewById(R.id.btn_learning);
				btn.setOnClickListener( new OnClickListener(){
					public void onClick(View v) {
						pw.dismiss();
						((HomeScreen)getActivity()).setCurrentFragmentSkills();
					}
					
				});
		
				btn = (Button)v.findViewById(R.id.btn_unlearning);
				btn.setOnClickListener( new OnClickListener(){
					public void onClick(View v) {
						pw.dismiss();
						((HomeScreen)getActivity()).setCurrentFragmentUnlearn();
					}
				});
			}
		});
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
    
    @Override
	public void onRequestBack( String method, JSONObject response )
    {
    	if (method == "skills.listUnlearning") {
    		
    		if( m_unlearningList != null )
				m_unlearningList.clear();
			
    		addToUnlearningList(m_unlearningList, response);
			setUnlearningSkill();
			
	        InitUpdateUnlearnRemainningTimer();
			onRequestComplete();
    	
    	} else if (method == "skills.listUnlearnable") {
    		
    		m_unlearnableList = new Vector<skillAvailable>();

       		JSONObject jItems = response.optJSONObject("skills");
       		
       		if (jItems != null) {
       			
       			Iterator<String> it = jItems.keys();
       			
       			while( it.hasNext() ) {	
        			
       				String sKey = it.next();
        			JSONObject jobj = jItems.optJSONObject( sKey );
        			skillAvailable skill = new skillAvailable();

        			skill.remainTime = jobj.optInt("time_remaining");
        			skill.totalTime = jobj.optInt("unlearn_time");
        			skill.icon = jobj.optString("icon_44");
        			skill.item = jobj.optString("name");
        			skill.description = jobj.optString("description");
        			skill.id = sKey;
	        		skill.curTime = System.currentTimeMillis()/1000;
        			m_unlearnableList.add(skill);
        		}
       		}
       		updateUnlearnableList();
       		if ( m_unlearnableList.size() == 0 ) {
       			// no skills to unlearn. change text
       			((TextView)m_root.findViewById( R.id.unlearn_list_message )).setText("");
       		}
       		onRequestComplete();
    	} else if (method == "skills.hasUnlearning") {
    		m_hasUnlearning = response.optInt("has_unlearning") == 1 ? true : false;
    		if (m_hasUnlearning) {
    			setupSettings();
    		}
    		onRequestComplete();
    	}
    }

    
    private void setUnlearningSkill() 
    {
    	skillAvailable skill = null;
		FrameLayout unlearningPanel = (FrameLayout) m_root.findViewById(R.id.unlearn_view_unlearning_panel);
		int wasVisible = unlearningPanel.getVisibility();
		
		if( m_unlearningList.size() > 0 )
			skill = m_unlearningList.get(0);
		
		if( skill != null )
		{			
			unlearningPanel.setVisibility(View.VISIBLE);			
			Util.showUnlearnProgress( getActivity(), m_unlearningSkillProgress, m_unlearningSkillTime, skill.remainTime, skill.totalTime, skill.curTime ); 
			m_unlearningSkillName.setText( "Unlearning " + m_unlearningList.get(0).item  );
			if (wasVisible == View.GONE) {
				Util.startScaleAnimation(unlearningPanel, 600);
			}			
		} else {
			unlearningPanel.setVisibility(View.GONE);
		}
    }
    
    private void InitUpdateUnlearnRemainningTimer()
    {
 	   if( m_RemainingTimer != null )
		   m_RemainingTimer.cancel();
		   
	   m_RemainingTimer = new Timer();
	   m_RemainingTimer.scheduleAtFixedRate( new TimerTask(){
	       public void run()
		   {
		    	 getActivity().runOnUiThread(new Runnable(){
		    		 public void run(){
	        			 setUnlearningSkill();
			         }});
   	       }  
		}, 1000, 1000 ); 
    }
    
	protected boolean doesSupportRefresh()
	{
		return true;
	}

	protected void onRefresh()
	{
		getSkills();
	}
	
	protected void scrollToTop()
	{
		ScrollView sv = (ScrollView) m_root.findViewById(R.id.UnlearnScrollView);
		sv.smoothScrollTo(0, 0);
	}
}
