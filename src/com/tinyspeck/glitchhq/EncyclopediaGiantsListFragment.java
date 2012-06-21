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

public class EncyclopediaGiantsListFragment extends BaseFragment {

	private EncyclopediaGiantsListListViewAdapter m_adapter;
	private LinearListView m_listView;
	private View m_root;
	private Vector<glitchGiant> m_giantsList;
	private Button m_btnBack;
	private Button m_btnSidebar;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View curView = ViewInit(inflater, R.layout.encyclopedia_giants_list_view, container);
		m_root = curView;
		init(curView);
		return curView;
	}
	
	private void init(View root)
	{
		boolean bUpdateData = (m_giantsList == null);
		
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
			m_giantsList = new Vector<glitchGiant>();
		}
		TextView title = (TextView)root.findViewById(R.id.encyclopedia_giants_list_title);
		title.setTypeface(m_application.m_vagFont);
		m_adapter = new EncyclopediaGiantsListListViewAdapter(this, m_giantsList);
		m_listView = (LinearListView) root.findViewById(R.id.encyclopedia_giants_list_list);
		m_listView.setAdapter(m_adapter);
		
		if (bUpdateData) {
			getEncyclopediaGiantsList();
		} else {
			showEncyclopediaGiantsListPage();
		}
	}
	
	public void showEncyclopediaGiantsListPage()
	{
		boolean bHas = m_giantsList.size() > 0;
		m_root.findViewById(R.id.encyclopedia_giants_list_list_message).setVisibility(bHas ? View.GONE : View.VISIBLE);
		m_listView.setVisibility(bHas ? View.VISIBLE : View.GONE);
		
		if (bHas)
			m_adapter.notifyDataSetChanged();
	}
	
	public void getEncyclopediaGiantsList()
	{
		if (m_application != null) {
			GlitchRequest request = m_application.glitch.getRequest("giants.list");
			request.execute(this);
			
			m_requestCount = 1;
			((HomeScreen)getActivity()).showSpinner(true);
		}
	}
	
	@Override
	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "giants.list") {
			JSONObject giants = response.optJSONObject("giants");
			
			if (giants != null && giants.length() > 0) {
				m_giantsList.clear();
				Iterator<String> itr = giants.keys();
				
				while(itr.hasNext()) {
					String key = itr.next();
					String giant = giants.optString(key);
					glitchGiant g = new glitchGiant();
					g.id = key;
					g.name = giant;
					m_giantsList.add(g);
				}
				Collections.sort(m_giantsList, new SortByName());
			}
			if (m_giantsList.size() > 0) {
				((TextView)m_root.findViewById(R.id.encyclopedia_giants_list_list_message)).setText("");
			}
			showEncyclopediaGiantsListPage();
		}
		onRequestComplete();
	}
	
	class SortByName implements Comparator<glitchGiant> {
		public int compare(glitchGiant g1, glitchGiant g2) {
			return g1.name.compareToIgnoreCase(g2.name);
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
		getEncyclopediaGiantsList();
	}
	
	@Override
	protected void scrollToTop()
	{
		ScrollView sv = (ScrollView) m_root.findViewById(R.id.EncyclopediaGiantsListScrollView);
		sv.smoothScrollTo(0, 0);
	}
	
	
}
