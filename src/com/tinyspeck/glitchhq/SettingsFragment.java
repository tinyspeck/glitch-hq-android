package com.tinyspeck.glitchhq;

import com.flurry.android.FlurryAgent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingsFragment extends BaseFragment {
	
	private View m_root;
	private ToggleButton m_btnNotification;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View curView = ViewInit(inflater, R.layout.settings_view, container);
		m_root = curView;
		init(curView);
		return curView;
	}
	
	private void init(View root)
	{
		TextView settingsHeader = (TextView) m_root.findViewById(R.id.settings_header);
		settingsHeader.setTypeface(m_application.m_vagFont);
		
		TextView pushNotifications = (TextView) m_root.findViewById(R.id.push_notifications);
		pushNotifications.setTypeface(m_application.m_vagFont);
		
		TextView termsOfService = (TextView) m_root.findViewById(R.id.terms_of_service);
		termsOfService.setTypeface(m_application.m_vagFont);
		
		TextView privacyPolicy = (TextView) m_root.findViewById(R.id.privacy_policy);
		privacyPolicy.setTypeface(m_application.m_vagFont);
		
		TextView communityGuidelines = (TextView) m_root.findViewById(R.id.community_guidelines);
		communityGuidelines.setTypeface(m_application.m_vagFont);
		
		TextView settingsLogout = (TextView) m_root.findViewById(R.id.settings_logout);
		settingsLogout.setTypeface(m_application.m_vagFont);
		
		m_btnNotification = (ToggleButton) m_root.findViewById(R.id.btnNotifications);
		m_btnNotification.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				FlurryAgent.logEvent("Settings - 'Notifications' " + arg1);
				if (arg1) {
					turnOnPushNotifications();
				} else {
					turnOffPushNotifications();
				}
			}
			
		});
		showSettingsPage();
	}
	
	private void turnOnPushNotifications()
	{
		
	}
	
	private void turnOffPushNotifications()
	{
		
	}
	
	private void showSettingsPage()
	{
		
	}
}
