package com.tinyspeck.glitchhq;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tinyspeck.android.GlitchRequest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AchievementCategoriesFragment extends BaseFragment {

	private AchievementCategoriesListViewAdapter m_adapter;
	private LinearListView m_listView;
	private View m_root;
	private Vector<String> m_categoriesList;
	
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View curView = ViewInit(inflater, R.layout.categories_view, container);
		m_root = curView;
		init(curView);
		return curView;
	}
	
	private void init(View root)
	{
		boolean bUpdateData = (m_categoriesList == null);
		
		if (bUpdateData) {
			m_categoriesList = new Vector<String>();
		}
		m_adapter = new AchievementCategoriesListViewAdapter(this, m_categoriesList);
		m_listView = (LinearListView) root.findViewById(R.id.categories_list);
		m_listView.setAdapter(m_adapter);
		
		if (bUpdateData) {
			getCategories();
		} else {
			showCategoriesPage();
		}
	}
	
	public void showCategoriesPage()
	{
		boolean bHas = m_categoriesList.size() > 0;
		m_root.findViewById(R.id.categories_list_message).setVisibility(bHas ? View.GONE : View.VISIBLE);
		m_listView.setVisibility(bHas ? View.VISIBLE : View.GONE);
		
		if (bHas)
			m_adapter.notifyDataSetChanged();
				
	}
	
	public void getCategories()
	{
		if (m_application != null) {
			GlitchRequest request = m_application.glitch.getRequest("achievements.listAllCategories");
			request.execute(this);
			
			m_requestCount = 1;
			((HomeScreen)getActivity()).showSpinner(true);
		}
	}
	
	@Override
	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "achievements.listAllCategories") {
			JSONArray items = response.optJSONArray("categories");			
			if (items.length() > 0) {
				m_categoriesList.clear();				
				for (int i=0; i < items.length(); i++) {
					m_categoriesList.add(items.optString(i));
				}				
				Collections.sort(m_categoriesList);
			}
			if (m_categoriesList.size() == 0) {
				((TextView)m_root.findViewById(R.id.categories_list_message)).setText("");
			}
			showCategoriesPage();
		}
		onRequestComplete();
	}
	
	@Override
	protected boolean doesSupportRefresh()
	{
		return true;
	}
	
	@Override
	protected void onRefresh()
	{
		getCategories();
	}
}
