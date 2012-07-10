package com.tinyspeck.glitchhq;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.tinyspeck.android.GlitchRequest;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

public class EncyclopediaStreetDetailFragment extends BaseFragment {

	private glitchLocationStreet m_street;
	private glitchLocationHub m_hub;
	private View m_root;
	private Button m_btnBack, m_btnSidebar;
	private TeleportInfo m_teleportInfo;
	
	public class TeleportInfo
	{
		boolean canTeleport;
		int mapTokensUsed;
		int mapTokensMax;
		int tokensRemaining;
	}
	
	public EncyclopediaStreetDetailFragment(glitchLocationHub hub, glitchLocationStreet street)
	{
		m_hub = hub;
		m_street = street;
	}
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View curView = ViewInit(inflater, R.layout.encyclopedia_street_detail_view, container);
		m_root = curView;
		m_root.setVisibility(View.INVISIBLE);
		m_btnBack = (Button) m_root.findViewById(R.id.btnBack);
		if (m_hub != null) {
			m_btnBack.setText(m_hub.name);
		} else {
			m_btnBack.setText("Back");
		}
		m_btnBack.setEllipsize(TruncateAt.END);
		m_btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				fm.popBackStack();
			}
		});
		m_btnBack.setVisibility(View.VISIBLE);
		m_btnSidebar = (Button) m_root.findViewById(R.id.btnSidebar);
		m_btnSidebar.setVisibility(View.GONE);
		
		getEncyclopediaStreet();
		
		return curView;
	}
	
	private void setAsDestination() 
	{
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("street_tsid", m_street.tsid);
		
		GlitchRequest request = m_application.glitch.getRequest("locations.setAsDestination", params);
		request.execute(this);
		
	}
	
	private void teleport() 
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("street_tsid", m_street.tsid);
		
		GlitchRequest request = m_application.glitch.getRequest("locations.teleport", params);
		request.execute(this);
	}
	
	private void showEncyclopediaStreetPage()
	{
		TextView title = (TextView)m_root.findViewById(R.id.street_detail_title);
		title.setTypeface(m_application.m_vagFont);
		title.setText(m_street.name + " in " + m_hub.name);
		
		ImageView image = (ImageView)m_root.findViewById(R.id.street_detail_image);
		DrawableURL.Show(image, m_street.image, false);
		
		Button setDestinationButton = (Button)m_root.findViewById(R.id.street_detail_set_as_destination_btn);
		setDestinationButton.setTypeface(m_application.m_vagFont);
		setDestinationButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FlurryAgent.logEvent("Street Detail - Tapped set as destination button");
				setAsDestination();
			}
		});
		
		TextView visits = (TextView)m_root.findViewById(R.id.street_detail_visits);
		if (m_street.visits > 0) {
			String visitedText = "You've been here <b>" + m_street.visits + "</b> time";
			if (m_street.visits > 1) {
				visitedText += "s";
			}
			visitedText += ".";
			visits.setText(Html.fromHtml(visitedText));
		} else {
			visits.setText("You've never been here before!");
		}
		
		LinearLayout features = (LinearLayout) m_root.findViewById(R.id.street_detail_features);
		features.removeAllViews();
		if (m_street.features != null) {
			for (int i=0; i < m_street.features.size(); i++) {
				String feature = m_street.features.get(i);
				TextView featureTv = new TextView(getActivity());
				LayoutParams featureParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				featureTv.setLayoutParams(featureParams);
				featureTv.setPadding(10, 2, 10, 2);
				featureTv.setTextSize(14);
				featureTv.setTextColor(Color.parseColor("#505050"));
				featureTv.setText(Html.fromHtml(feature));
				features.addView(featureTv);
			}
			features.setVisibility(View.VISIBLE);
		}
	}
	
	private void showTeleportButton()
	{
		Button teleportBtn = (Button) m_root.findViewById(R.id.street_detail_teleport_btn);
		teleportBtn.setTypeface(m_application.m_vagFont);
		teleportBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FlurryAgent.logEvent("Street Detail - Tapped teleport button");
				String msg = "Are you sure you want to spend a Teleportation Token to teleport to " +
						m_street.name + "?\n\nYou have used " + m_teleportInfo.mapTokensUsed + " of your " +
						m_teleportInfo.mapTokensMax + " allowed token teleports today, and have a total of " +
						m_teleportInfo.tokensRemaining + " tokens.";
				Util.Alert(getActivity(), msg, "Teleport?", true, "Teleport", "Cancel",
					new DialogInterface.OnClickListener() {							
						public void onClick(DialogInterface dialog, int which) {
							if (which == DialogInterface.BUTTON_POSITIVE) {
								teleport();
							} else {
								dialog.dismiss();
							}
						}
					}
				);
			}
		});
		teleportBtn.setVisibility(View.VISIBLE);
	}
	
	private void getEncyclopediaStreet()
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("street_tsid", m_street.tsid);
		
		GlitchRequest request1 = m_application.glitch.getRequest("locations.streetInfo", params);
		request1.execute(this);
		
		GlitchRequest request2 = m_application.glitch.getRequest("locations.canTeleport", params);
		request2.execute(this);
		
		m_requestCount = 2;
		((HomeScreen)getActivity()).showSpinner(true);
	}
	
	@Override
	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "locations.canTeleport") {
			JSONObject teleport = response.optJSONObject("teleport");
			if (teleport != null) {
				m_teleportInfo = new TeleportInfo();
				m_teleportInfo.canTeleport = teleport.optBoolean("can_teleport");
				m_teleportInfo.mapTokensUsed = teleport.optInt("map_tokens_used");
				m_teleportInfo.mapTokensMax = teleport.optInt("map_tokens_max");
				m_teleportInfo.tokensRemaining = teleport.optInt("tokens_remaining");
				
				if (m_teleportInfo.canTeleport) {
					showTeleportButton();
				} else {
					Button teleportBtn = (Button) m_root.findViewById(R.id.street_detail_teleport_btn);
					teleportBtn.setVisibility(View.GONE);
				}
			}
			onRequestComplete();
		} else if (method == "locations.streetInfo") {
			
			m_root.setVisibility(View.VISIBLE);
			
			m_hub = new glitchLocationHub();
			JSONObject hub = response.optJSONObject("hub");
			m_hub.hub_id = hub.optInt("id");
			m_hub.name = hub.optString("name");
			
			m_street.activeProject = response.optBoolean("active_project");
			JSONArray features = response.optJSONArray("features");
			if (features != null) {
				m_street.features = new Vector<String>();
				for (int i=0; i < features.length(); i++) {
					String feature = features.optString(i);
					m_street.features.add(feature);
				}
			}
			JSONObject visits = response.optJSONObject("visits");
			if (visits != null) {
				m_street.visits = visits.optInt("total");
				m_street.lastVisit = visits.optInt("last");
			}
			JSONObject image = response.optJSONObject("image");
			if (image != null) {
				m_street.image = image.optString("url");
				m_street.imageHeight = image.optInt("h");
				m_street.imageWidth = image.optInt("w");
			}
			showEncyclopediaStreetPage();
			onRequestComplete();
		} else if (method == "locations.setAsDestination") {
			int result = response.optInt("ok");
			if (result == 0) {
				String error = response.optString("error");
				Util.shortToast(getActivity(), error);
			} else {
				String msg = response.optString("msg");
				Util.shortToast(getActivity(), msg);
			}
		} else if (method == "locations.teleport") {
			int result = response.optInt("ok");
			if (result == 0) {
				String error = response.optString("error");
				Util.shortToast(getActivity(), error);												
			} else {
				String msg = response.optString("msg");
				Util.shortToast(getActivity(), msg);
			}
			// want to refresh information about teleporting
			Map<String, String> params = new HashMap<String, String>();
			params.put("street_tsid", m_street.tsid);
			
			GlitchRequest request = m_application.glitch.getRequest("locations.streetInfo", params);
			request.execute(this);
		}
		
	}
	
	@Override
	protected boolean doesSupportRefresh()
	{
		return true;
	}
	
	@Override
	protected void onRefresh()
	{
		getEncyclopediaStreet();
	}
	
	@Override
	protected void scrollToTop()
	{
		ScrollView sv = (ScrollView) m_root.findViewById(R.id.EncyclopediaStreetDetailScrollView);
		sv.smoothScrollTo(0, 0);
	}
	
}
