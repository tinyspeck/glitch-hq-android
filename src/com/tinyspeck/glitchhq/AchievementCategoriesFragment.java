package com.tinyspeck.glitchhq;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tinyspeck.android.GlitchRequest;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AchievementCategoriesFragment extends BaseFragment {

	private AchievementCategoriesListViewAdapter m_adapter;
	private LinearListView m_listView;
	private View m_root;
	private Vector<glitchAchievementCategory> m_categoriesList;
	
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
			m_categoriesList = new Vector<glitchAchievementCategory>();
		}
		TextView title = (TextView)root.findViewById(R.id.categories_title);
		title.setTypeface(m_application.m_vagFont);
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
			JSONObject items = response.optJSONObject("categories_full");
			
			if (items.length() > 0) {
				m_categoriesList.clear();
				Iterator<String> itr = items.keys();
				
				while(itr.hasNext()) {
					String key = itr.next();
					JSONObject jobj = items.optJSONObject(key);
					glitchAchievementCategory c = new glitchAchievementCategory();
					c.name = key;
					c.total = jobj.optInt("total");
					c.completed = jobj.optInt("got");
					m_categoriesList.add(c);
				}
				
				Collections.sort(m_categoriesList, new SortByName());
			}
			
			if (m_categoriesList.size() == 0) {
				((TextView)m_root.findViewById(R.id.categories_list_message)).setText("");
			}
			showCategoriesPage();
		}
		onRequestComplete();
	}
	
	class SortByName implements Comparator<glitchAchievementCategory> {
		public int compare(glitchAchievementCategory c1, glitchAchievementCategory c2) {
			return c1.name.compareToIgnoreCase(c2.name);
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
		getCategories();
	}
}
