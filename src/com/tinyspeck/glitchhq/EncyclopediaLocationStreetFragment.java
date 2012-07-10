package com.tinyspeck.glitchhq;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.json.JSONObject;

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

import com.tinyspeck.android.GlitchRequest;

public class EncyclopediaLocationStreetFragment extends BaseFragment {

	private EncyclopediaLocationStreetsListViewAdapter m_adapter;
	private glitchLocationHub m_hub;
	private LinearListView m_listView;
	private View m_root;
	private Vector<glitchLocationStreet> m_streetsList;
	private Button m_btnBack;
	private Button m_btnSidebar;
	
	public EncyclopediaLocationStreetFragment(glitchLocationHub hub)
	{
		m_hub = hub;
	}
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
	{
		View curView = ViewInit(inflater, R.layout.encyclopedia_location_streets_view, container);
		m_root = curView;
		init(curView);
		return curView;	
	}
	
	private void init(View root)
	{
		boolean bUpdateData = (m_streetsList == null);
		
		m_btnBack = (Button) m_root.findViewById(R.id.btnBack);
		m_btnBack.setText(m_hub.name);
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
			m_streetsList = new Vector<glitchLocationStreet>();
		}
		TextView title = (TextView) root.findViewById(R.id.encyclopedia_location_streets_title);
		title.setTypeface(m_application.m_vagFont);
		m_adapter = new EncyclopediaLocationStreetsListViewAdapter(this, m_streetsList);
		m_listView = (LinearListView) root.findViewById(R.id.encyclopedia_location_streets_list);
		m_listView.setAdapter(m_adapter);
		
		if (bUpdateData) {
			getEncyclopediaLocationStreets();
		} else {
			showEncyclopediaLocationStreetsPage();
		}
	}
	
	private void showEncyclopediaLocationStreetsPage()
	{
		boolean bHas = m_streetsList.size() > 0;
		m_root.findViewById(R.id.encyclopedia_location_streets_list_message).setVisibility(bHas ? View.GONE : View.VISIBLE);
		m_listView.setVisibility(bHas ? View.VISIBLE : View.GONE);
		
		if (bHas)
			m_adapter.notifyDataSetChanged();
	}
	
	private void getEncyclopediaLocationStreets()
	{
		if (m_application != null) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("hub_id", String.valueOf(m_hub.hub_id));
			
			GlitchRequest request = m_application.glitch.getRequest("locations.getStreets", params);
			request.execute(this);
			
			m_requestCount = 1;
			((HomeScreen)getActivity()).showSpinner(true);
		}
	}
	
	@Override
	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "locations.getStreets") {
			m_streetsList.clear();
			JSONObject streets = response.optJSONObject("streets");
			if (streets != null && streets.length() > 0) {
				Iterator<String> it = streets.keys();
				
				while (it.hasNext()) {
					String key = it.next();
					JSONObject jobj = streets.optJSONObject(key);
					if (jobj != null) {
						glitchLocationStreet s = new glitchLocationStreet();
						s.tsid = key;
						s.name = jobj.optString("name");						
						m_streetsList.add(s);
					}
				}
				Collections.sort(m_streetsList, new SortByName());
			}
			if (m_streetsList.size() > 0) {
				((TextView)m_root.findViewById(R.id.encyclopedia_location_streets_list_message)).setText("");
			}
			showEncyclopediaLocationStreetsPage();
		}
		onRequestComplete();
	}
	
	class SortByName implements Comparator<glitchLocationStreet> {
		public int compare(glitchLocationStreet s1, glitchLocationStreet s2) {
			return s1.name.compareToIgnoreCase(s2.name);
		}
	}
	
	public glitchLocationHub getHub()
	{
		return m_hub;
	}
	
	@Override
	protected boolean doesSupportRefresh()
	{
		return true;		
	}
	
	@Override
	protected void onRefresh()
	{
		getEncyclopediaLocationStreets();
	}
	
	@Override
	protected void scrollToTop()
	{
		ScrollView sv = (ScrollView) m_root.findViewById(R.id.EncyclopediaLocationStreetsScrollView);
		sv.smoothScrollTo(0, 0);
	}
}
