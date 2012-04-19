package com.tinyspeck.glitchhq;

import com.flurry.android.FlurryAgent;
import com.google.android.c2dm.C2DMessaging;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingsFragment extends BaseFragment {
	
	public static final String PUSH_NOTIFICATIONS_OPTION = "push_notifications_option";
	
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
		termsOfService.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				WebViewFragment webview = new WebViewFragment("http://www.glitch.com/terms/", "Settings");
				((HomeScreen)getActivity()).setCurrentFragment(webview, true);
			}
		});
		
		TextView privacyPolicy = (TextView) m_root.findViewById(R.id.privacy_policy);
		privacyPolicy.setTypeface(m_application.m_vagFont);
		privacyPolicy.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				WebViewFragment webview = new WebViewFragment("http://www.glitch.com/privacy/", "Settings");
				((HomeScreen)getActivity()).setCurrentFragment(webview, true);
			}
		});
		
		TextView communityGuidelines = (TextView) m_root.findViewById(R.id.community_guidelines);
		communityGuidelines.setTypeface(m_application.m_vagFont);
		communityGuidelines.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				WebViewFragment webview = new WebViewFragment("http://www.glitch.com/guidelines/", "Settings");
				((HomeScreen)getActivity()).setCurrentFragment(webview, true);
			}
		});
		
		TextView settingsLogout = (TextView) m_root.findViewById(R.id.settings_logout);
		settingsLogout.setTypeface(m_application.m_vagFont);
		settingsLogout.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				((HomeScreen)getActivity()).Logout();
			}
		});
		
		m_btnNotification = (ToggleButton) m_root.findViewById(R.id.btnNotifications);
		m_btnNotification.setChecked(SettingsFragment.getPushNotificationsOption(getActivity()));
		m_btnNotification.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				FlurryAgent.logEvent("Settings - 'Notifications' " + arg1);
				SettingsFragment.setPushNotificationsOption(getActivity(), arg1);
				if (arg1) {
					turnOnPushNotifications();
				} else {
					turnOffPushNotifications();
				}
			}
			
		});
		
		TextView copyright = (TextView) m_root.findViewById(R.id.settings_copyright);
		copyright.setTypeface(m_application.m_vagLightFont);
		TextView version = (TextView) m_root.findViewById(R.id.settings_version);
		version.setTypeface(m_application.m_vagLightFont);
	}
	
	public static boolean getPushNotificationsOption(Context context) {		
		final SharedPreferences prefs = context.getSharedPreferences("com.tinyspeck.glitchhq", Context.MODE_PRIVATE);
		return prefs.getBoolean(PUSH_NOTIFICATIONS_OPTION, true);
	}
	
	public static void setPushNotificationsOption(Context context, boolean want) {
		final SharedPreferences prefs = context.getSharedPreferences("com.tinyspeck.glitchhq", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putBoolean(PUSH_NOTIFICATIONS_OPTION, want);
		editor.commit();
	}
	
	private void turnOnPushNotifications()
	{
		Context context = getActivity();
		String registrationId =  C2DMessaging.getRegistrationId(context);
		if (registrationId == null || registrationId.equals("")) {
			C2DMReceiver.register(context);
		} else {
			(new C2DMReceiver()).addRegistrationId(context, registrationId);
		}		
	}
	
	private void turnOffPushNotifications()
	{
		Context context = getActivity();
		String registrationId =  C2DMessaging.getRegistrationId(context);
		if (registrationId != null && !registrationId.equals("")) {
			(new C2DMReceiver()).removeRegistration(context, registrationId);
		}
		C2DMReceiver.unregister(context);
	}
}
