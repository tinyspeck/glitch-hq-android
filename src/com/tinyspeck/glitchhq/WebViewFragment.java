package com.tinyspeck.glitchhq;

import com.flurry.android.FlurryAgent;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

public class WebViewFragment extends BaseFragment {

	private String m_backBtnText;
	private String m_url;
	private View m_root;
	private Button m_btnBack;
	private WebView m_webView;
	
	WebViewFragment(String url)
	{
		this(url, "Back");
	}
	
	WebViewFragment(String url, String backBtnText)
	{
		m_url = url;
		m_backBtnText = backBtnText;
	}
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View curView = ViewInit(inflater, R.layout.webview_view, container);
		m_root = curView;
		
		m_btnBack = (Button) m_root.findViewById(R.id.btnBack);
		m_btnBack.setText(m_backBtnText);
		m_btnBack.setVisibility(View.VISIBLE);
		m_btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FlurryAgent.logEvent("WebView - 'Back' button pressed");
				FragmentManager fm = getFragmentManager();
				fm.popBackStack();
			}
		});
		
		m_webView = (WebView) m_root.findViewById(R.id.webview);
		WebSettings webSettings = m_webView.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setJavaScriptEnabled(true);
		m_webView.loadUrl(m_url);
		
		return curView;
	}
}
