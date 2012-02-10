package com.twotalltotems.glitch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

public class UnlearnFragment extends BaseFragment {

	private View m_root;
	
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
    
    private void init(View root) {
    	
    	TextView tv = (TextView)root.findViewById(R.id.unlearnable_skills);
   	    tv.setTypeface(m_application.m_vagFont);
    	
   	    setupSettings();
    }
    
    private void setupSettings() {
		
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
}
