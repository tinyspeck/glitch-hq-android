package com.tinyspeck.glitchhq;

import com.flurry.android.FlurryAgent;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.widget.Button;

public class WebViewFragment extends BaseFragment {

	private String m_backBtnText;
	private String m_url;
	private View m_root;
	private Button m_btnBack;
	private WebView m_webView;
	private boolean m_isImage;
	private static final String HTML_FORMAT = "<html>" + 
			"<body style=\"text-align: center; background-color: black;\">" +
			"<div style=\"height:100%%; width: 100%%; display: table;\">" +
			"<div style=\"display: table-cell; vertical-align: middle\">" +
			"<img src=\"%s\" />" +
			"</div></div></body></html>";
	
	public WebViewFragment()
	{
		super();
	}
	
	public WebViewFragment(String url)
	{
		this(url, "Back", false);
	}
	
	public WebViewFragment(String url, String backBtnText)
	{
		this(url, backBtnText, false);
	}
	
	public WebViewFragment(String url, String backBtnText, boolean isImage)
	{
		m_url = url;
		m_backBtnText = backBtnText;
		m_isImage = isImage;		
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
		webSettings.setSupportZoom(true);
		
		if (m_isImage) {
			webSettings.setLoadWithOverviewMode(true);
			webSettings.setUseWideViewPort(true);
			m_webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
			m_webView.setScrollbarFadingEnabled(true);
			final String html = String.format(HTML_FORMAT, m_url);
			m_webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
		} else {
			webSettings.setJavaScriptEnabled(true);
			m_webView.loadUrl(m_url);
		}
		getActivity().registerForContextMenu(m_webView);
		
		return curView;
	}
}
