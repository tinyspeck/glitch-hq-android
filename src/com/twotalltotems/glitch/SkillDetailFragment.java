package com.twotalltotems.glitch;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.tinyspeck.android.GlitchRequest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SkillDetailFragment extends BaseFragment{

  	private skillAvailable m_currentSkill;
  	private String m_skillID;
  	private View m_root;
  	
  	SkillDetailFragment( String skillID )
  	{
  		m_skillID = skillID;
  	}

    public void onActivityCreated(Bundle savedInstanceState) {
    	
        super.onActivityCreated(savedInstanceState);
    }

    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) 
    {
    	View curView =  ViewInit( inflater, R.layout.skill_detail_view, container );
    	m_root = curView;
    	m_root.setVisibility(View.INVISIBLE);
    	getSkillDetail( m_skillID );
		return curView;
    }
    
	@Override
	public void onRequestBack( String method, JSONObject response )
	{
		if ( method == "skills.getInfo" )
    	{
	    	m_root.setVisibility(View.VISIBLE);
			
       		int lastRemainTime = 0;
       		int lastTotalTime = 0;
       		
       		if( m_currentSkill != null )
       		{
       			lastRemainTime = m_currentSkill.remainTime;
       			lastTotalTime = m_currentSkill.totalTime;
       		}

       		m_currentSkill = new skillAvailable();

       		m_currentSkill.totalTime = response.optInt("total_time");
       		if( m_currentSkill.totalTime == 0 )
       		{
           		m_currentSkill.learning = true;
       			m_currentSkill.remainTime = lastRemainTime;
       			m_currentSkill.totalTime = lastTotalTime;
       		}else
       			m_currentSkill.learning = false;

       		m_currentSkill.icon = response.optString("icon_100");
       		m_currentSkill.item = response.optString("name");
       		m_currentSkill.description = response.optString("description");
       		m_currentSkill.id = response.optString( "class_tsid" );
       		m_currentSkill.can_learn = ( response.optInt("can_learn") == 1 )? true: false;
       		m_currentSkill.got = ( response.optInt("got") == 1 )? true: false;
       		
       		m_currentSkill.postRequests = new Vector<skillAvailable>();
       		m_currentSkill.requirements = new Vector<skillAvailable>();
       		m_currentSkill.giants = new Vector<skillGiant>();
       		m_currentSkill.paused = ( response.optInt("paused") == 1 )? true: false;

       		m_currentSkill.curTime = System.currentTimeMillis()/1000;
       		
       		try{
           		JSONArray jReqs = response.optJSONArray("reqs");
           		if ( jReqs != null ) {
	       			for(int i=0; i < jReqs.length(); i++ )
	           		{
	        			skillAvailable skill = new skillAvailable();
	        			skill.item = jReqs.getJSONObject(i).optString("name");
	        			skill.id = jReqs.getJSONObject(i).optString("class_tsid");
	        			skill.level = jReqs.getJSONObject(i).optInt("level");
	        			skill.type = jReqs.getJSONObject(i).optString("type");
	           			m_currentSkill.requirements.add( skill );
	           		}
           		}
           		
       			jReqs = response.optJSONArray("post_reqs");           		
       			if ( jReqs != null ) {
	       			for(int i=0; jReqs!=null && i < jReqs.length(); i++ )
	           		{
	        			skillAvailable skill = new skillAvailable();
	        			skill.item = jReqs.getJSONObject(i).optString("name");
	        			skill.id = jReqs.getJSONObject(i).optString("class_tsid");
	        			m_currentSkill.postRequests.add( skill );
	           		}
       			}
       			
           		jReqs = response.optJSONArray("giants");
           		
           		if ( jReqs != null ) {
	           		for(int i=0; jReqs!=null && i < jReqs.length(); i++ )
	           		{
	           			skillGiant skill = new skillGiant();
	        			skill.id = jReqs.getJSONObject(i).optString("id");
	        			skill.isPrimary = ( jReqs.getJSONObject(i).optInt("is_primary") == 0 )? false: true;
	        			if( skill.isPrimary )
	        				m_currentSkill.giants.insertElementAt(skill, 0);
	        			else
	        				m_currentSkill.giants.add( skill );
	           		}
           		}
           		
           		setSkillDetailView();	
    			onRequestComplete();
       		}catch( Exception e )
       		{
       			e.printStackTrace();
       		}
    	}else if ( method == "skills.learn" )
    	{
            FragmentManager fm = getFragmentManager();
    		fm.popBackStack();
    		((HomeScreen)getActivity()).updateSkills();
    	}
	}	
	
	public void getSkillDetail( String skillId )
	{
        Map<String,String> params = new  HashMap<String,String>();
        params.put("skill_class", skillId );
		
        GlitchRequest request1 = m_application.glitch.getRequest("skills.getInfo", params );
        request1.execute(this);
        
        m_requestCount = 1;	
        ((HomeScreen)getActivity()).showSpinner(true);
	}

	private void learnSkill()
	{
        Map<String,String> params = new  HashMap<String,String>();
        params.put("skill_class", m_currentSkill.id );
		
        GlitchRequest request1 = m_application.glitch.getRequest("skills.learn", params );
        request1.execute(this);
	}

	private void UpdateSkillDetailProgress()
	{
		View v = m_root.findViewById( R.id.learning_progress );	
		TextView tv = (TextView) m_root.findViewById( R.id.learning_process_text );

		Util.showProgress( getActivity(), v, tv, m_currentSkill.remainTime, m_currentSkill.totalTime, m_currentSkill.curTime );   
	}
	
	void setSkillDetailView()
	{
		ImageView icon = (ImageView) m_root.findViewById(R.id.skill_detail_icon);

		//DrawableURL.Show( icon, m_currentSkill.icon, false );
		m_application.Download( m_currentSkill.icon, icon, MyApplication.DOWNLOAD_TYPE_NORMAL );

		TextView tv = (TextView) m_root.findViewById(R.id.skill_detail_name);
		tv.setText( m_currentSkill.item );
		
		tv = (TextView) m_root.findViewById(R.id.caption_status); 
		tv.setTypeface(m_application.m_vagFont);

		tv = (TextView) m_root.findViewById(R.id.caption_giant); 
		tv.setTypeface(m_application.m_vagFont);
		
		tv = (TextView) m_root.findViewById(R.id.caption_needed); 
		tv.setTypeface(m_application.m_vagFont);

		tv = (TextView) m_root.findViewById(R.id.caption_reqs); 
		tv.setTypeface(m_application.m_vagFont);
		
		tv = (TextView) m_root.findViewById(R.id.skill_detail_time);
		if( m_currentSkill.learning )
			tv.setText( R.string.str_you_are_learning );	
		else
			tv.setText( Util.TimeToString(m_currentSkill.totalTime, false ) );	

		tv = (TextView) m_root.findViewById(R.id.skill_detail_description);
		tv.setText( m_currentSkill.description );	

		Button btnLearn = (Button)m_root.findViewById(R.id.btn_learn_this_skill );
		btnLearn.setTypeface( m_application.m_vagFont );
		btnLearn.setOnClickListener( new OnClickListener(){
				public void onClick(View arg0) {
					FlurryAgent.logEvent("Skill Detail - Tapped learn button");
					learnSkill();
				}
			 });

		View v = m_root.findViewById( R.id.learning_process_bar );		
		
		if( m_currentSkill.learning )
		{
			btnLearn.setVisibility(View.GONE);
			v.setVisibility(View.VISIBLE);
			UpdateSkillDetailProgress();
		}else if( m_currentSkill.got || !m_currentSkill.can_learn )
		{
			v.setVisibility(View.GONE);
			btnLearn.setVisibility(View.GONE);
		}else
		{
			v.setVisibility(View.GONE);
			btnLearn.setVisibility(View.VISIBLE);
		}
		
		View mLayout = m_root.findViewById(R.id.requirement_layout);
		LinearLayout lreq = (LinearLayout) m_root.findViewById(R.id.requirements);
		if( !addTextViewToLinearLayout( lreq, m_currentSkill.requirements, true ) )
			mLayout.setVisibility(View.GONE);
		else
			mLayout.setVisibility(View.VISIBLE);

		mLayout = m_root.findViewById(R.id.need_for_layout);
		LinearLayout ll = (LinearLayout) m_root.findViewById( R.id.post_request_panel );
		if( !addTextViewToLinearLayout( ll, m_currentSkill.postRequests, false ) )
			mLayout.setVisibility(View.GONE);
		else
			mLayout.setVisibility(View.VISIBLE);

		mLayout = m_root.findViewById(R.id.giant_layout);
		ll = (LinearLayout) m_root.findViewById( R.id.giant_panel );
		if( !addGiantToLinearLayout( ll, m_currentSkill.giants ) )
			mLayout.setVisibility(View.GONE);
		else
			mLayout.setVisibility(View.VISIBLE);
		
		tv = (TextView) m_root.findViewById( R.id.skill_status );
		if( m_currentSkill.learning )
			tv.setText( R.string.str_you_are_learning );
		else if( m_currentSkill.got )
			tv.setText( R.string.str_you_have_skill );
		else if( !m_currentSkill.can_learn )
			tv.setText( R.string.str_you_can_not_learn );
		else
			tv.setText( m_currentSkill.paused? R.string.str_skill_status_started : R.string.str_skill_status_can_learn );

		m_root.scrollTo(0,0);
	}
	
	private boolean addTextViewToLinearLayout( LinearLayout ll, Vector<skillAvailable> skills, boolean bRequirement )
	{
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ll.removeAllViewsInLayout();		
		for(int i=0; i < skills.size(); i++ )
		{
			View v = inflater.inflate(R.layout.skill_post_request_item, null );
			TextView tv_skill = (TextView )v.findViewById(R.id.tv_skillName);
			ll.addView(v);
			if( skills.get(i).type != null && skills.get(i).type.equalsIgnoreCase("level") )
			{
				tv_skill.setText(  skills.get(i).type + skills.get(i).level );
				tv_skill.setTag( skills.get(i).id  );
				tv_skill.setTextColor(0xff808080);
			}else if( bRequirement && ( skills.get(i).type == null || !skills.get(i).type.equalsIgnoreCase("skill") ) )
			{
				tv_skill.setText(  skills.get(i).item );
				tv_skill.setTag( skills.get(i).id  );
				tv_skill.setTextColor(0xff808080);
			}else
			{	
				tv_skill.setTypeface(m_application.m_vagFont);
				tv_skill.setText(  skills.get(i).item );
				tv_skill.setTag( skills.get(i).id  );
				tv_skill.setOnClickListener(new OnClickListener(){
					public void onClick(View v) {
						String sKey = (String)v.getTag();
						SkillDetailFragment f = new SkillDetailFragment( sKey );
						((HomeScreen)getActivity()).setCurrentFragment( f, true );
					}
				});
			}
		}
		return ( skills.size() > 0 );
	}
	
	private boolean addGiantToLinearLayout( LinearLayout ll, Vector<skillGiant> skills )
	{
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ll.removeAllViewsInLayout();
		for(int i=0; i < skills.size(); i++ )
		{
			View v = inflater.inflate(R.layout.skill_post_request_item, null );
			TextView tv_skill = (TextView) v.findViewById(R.id.tv_skillName);
			ll.addView(v);
			String s = skills.get(i).id;
			s = s.substring(0,1).toUpperCase() + s.substring(1); 
			
			tv_skill.setTextColor(0xff151515);
			tv_skill.setTypeface(m_application.m_vagLightFont);

			if( skills.get(i).isPrimary )
			{
				tv_skill.setText( Html.fromHtml( "<b>" + s + "</b>" +  " (Primary)"  ) );
			}else
				tv_skill.setText(s);
		}
		return skills.size() > 0;
	}
	
}
