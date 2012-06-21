package com.tinyspeck.glitchhq;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import org.json.JSONObject;

import com.tinyspeck.android.GlitchRequest;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class EncyclopediaLocationHubsFragment extends BaseFragment {

	private EncyclopediaLocationHubsListViewAdapter m_adapter;
	private LinearListView m_listView;
	private View m_root;
	private Vector<glitchLocationHub> m_hubsList;
	private Button m_btnBack;
	private Button m_btnSidebar;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
	{
		View curView = ViewInit(inflater, R.layout.encyclopedia_location_hubs_view, container);
		m_root = curView;
		init(curView);
		return curView;	
	}
	
	private void init(View root)
	{
		boolean bUpdateData = (m_hubsList == null);
		
		m_btnBack = (Button) m_root.findViewById(R.id.btnBack);
		m_btnBack.setText("Encyclopedia");
		m_btnBack.setSingleLine();
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
		
		if (bUpdateData) {
			m_hubsList = new Vector<glitchLocationHub>();
		}
		TextView title = (TextView) root.findViewById(R.id.encyclopedia_location_hubs_title);
		title.setTypeface(m_application.m_vagFont);
		m_adapter = new EncyclopediaLocationHubsListViewAdapter(this, m_hubsList);
		m_listView = (LinearListView) root.findViewById(R.id.encyclopedia_location_hubs_list);
		m_listView.setAdapter(m_adapter);
		
		if (bUpdateData) {
			getEncyclopediaLocationHubs();
		} else {
			showEncyclopediaLocationHubsPage();
		}
	}
	
	private void showEncyclopediaLocationHubsPage()
	{
		boolean bHas = m_hubsList.size() > 0;
		m_root.findViewById(R.id.encyclopedia_location_hubs_list_message).setVisibility(bHas ? View.GONE : View.VISIBLE);
		m_listView.setVisibility(bHas ? View.VISIBLE : View.GONE);
		
		if (bHas)
			m_adapter.notifyDataSetChanged();
	}
	
	private void getEncyclopediaLocationHubs()
	{
		if (m_application != null) {
			GlitchRequest request = m_application.glitch.getRequest("locations.getHubs");
			request.execute(this);
			
			m_requestCount = 1;
			((HomeScreen)getActivity()).showSpinner(true);
		}
	}
	
	@Override
	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "locations.getHubs") {
			JSONObject hubs = response.optJSONObject("hubs");
			if (hubs != null && hubs.length() > 0) {
				m_hubsList.clear();
				Iterator<String> itr = hubs.keys();
				
				while (itr.hasNext()) {
					String key = itr.next();
					JSONObject name = hubs.optJSONObject(key);
					if (name != null) {
						String nameStr = name.optString("name");
						glitchLocationHub h = new glitchLocationHub();
						h.hub_id = Integer.parseInt(key);
						h.name = nameStr;
						m_hubsList.add(h);
					}					
				}
				Collections.sort(m_hubsList, new SortByName());				
			}
			if (m_hubsList.size() > 0) {
				((TextView)m_root.findViewById(R.id.encyclopedia_location_hubs_list_message)).setText("");				
			}
			showEncyclopediaLocationHubsPage();
		}
		onRequestComplete();
	}
	
	class SortByName implements Comparator<glitchLocationHub> {
		public int compare(glitchLocationHub h1, glitchLocationHub h2) {
			return h1.name.compareToIgnoreCase(h2.name);
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
		getEncyclopediaLocationHubs();
	}
	
	@Override
	protected void scrollToTop()
	{
		ScrollView sv = (ScrollView) m_root.findViewById(R.id.EncyclopediaLocationHubsScrollView);
		sv.smoothScrollTo(0, 0);
	}
}
