package com.tinyspeck.glitchhq;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import org.json.JSONObject;

import com.tinyspeck.android.GlitchRequest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EncyclopediaSkillCategoriesFragment extends BaseFragment {
	
	private EncyclopediaSkillCategoriesListViewAdapter m_adapter;
	private LinearListView m_listView;
	private View m_root;
	private Vector<String> m_categoriesList;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View curView = ViewInit(inflater, R.layout.encyclopedia_skill_categories_view, container);
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
		TextView title = (TextView)root.findViewById(R.id.encyclopedia_skill_categories_title);
		title.setTypeface(m_application.m_vagFont);
		m_adapter = new EncyclopediaSkillCategoriesListViewAdapter(this, m_categoriesList);
		m_listView = (LinearListView) root.findViewById(R.id.encyclopedia_skill_categories_list);
		m_listView.setAdapter(m_adapter);
		
		if (bUpdateData) {
			getEncyclopediaSkillCategories();
		} else {
			showEncyclopediaSkillCategoriesPage();
		}
	}
	
	public void showEncyclopediaSkillCategoriesPage()
	{
		boolean bHas = m_categoriesList.size() > 0;
		m_root.findViewById(R.id.encyclopedia_skill_categories_list_message).setVisibility(bHas ? View.GONE : View.VISIBLE);
		m_listView.setVisibility(bHas ? View.VISIBLE : View.GONE);
		
		if (bHas)
			m_adapter.notifyDataSetChanged();
	}
	
	public void getEncyclopediaSkillCategories()
	{
		if (m_application != null) {
			GlitchRequest request = m_application.glitch.getRequest("skills.listAllCategories");
			request.execute(this);
			
			m_requestCount = 1;
			((HomeScreen)getActivity()).showSpinner(true);
		}
	}
	
	@Override
	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "skills.listAllCategories") {
			JSONObject categories = response.optJSONObject("categories");
			
			if (categories.length() > 0) {
				m_categoriesList.clear();
				Iterator<String> itr = categories.keys();
				
				while(itr.hasNext()) {
					String key = itr.next();
					String category = categories.optString(key);
					m_categoriesList.add(category);
				}
				Collections.sort(m_categoriesList);
			}
			
			if (m_categoriesList.size() == 0) {
				((TextView)m_root.findViewById(R.id.encyclopedia_skill_categories_list_message)).setText("");
			}
			showEncyclopediaSkillCategoriesPage();
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
		getEncyclopediaSkillCategories();
	}
}
