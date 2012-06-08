package com.tinyspeck.glitchhq;

import java.util.Vector;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EncyclopediaCategoriesFragment extends BaseFragment {

	private EncyclopediaCategoriesListViewAdapter m_adapter;
	private LinearListView m_listView;
	private View m_root;
	private Vector<String> m_categoriesList;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);		
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View curView = ViewInit(inflater, R.layout.encyclopedia_categories_view, container);
		m_root = curView;
		init(curView);
		return curView;
	}
	
	private void init(View root)
	{
		m_categoriesList = new Vector<String>();
		m_categoriesList.add("Items");
		m_categoriesList.add("Skills");
		m_categoriesList.add("Achievements");
		m_categoriesList.add("Locations");
		m_categoriesList.add("Giants");
		
		m_adapter = new EncyclopediaCategoriesListViewAdapter(this, m_categoriesList);
		m_listView = (LinearListView) root.findViewById(R.id.encyclopedia_categories_list);
		m_listView.setAdapter(m_adapter);
		m_adapter.notifyDataSetChanged();
	}
}
