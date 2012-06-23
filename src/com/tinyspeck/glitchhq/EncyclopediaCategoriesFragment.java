package com.tinyspeck.glitchhq;

import java.util.Vector;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EncyclopediaCategoriesFragment extends BaseFragment {

	private EncyclopediaCategoriesListViewAdapter m_adapter;
	private LinearListView m_listView;
	private View m_root;
	private Vector<String> m_categoriesList;
	private EditText m_searchBox;
	private EncyclopediaSearchListViewAdapter m_searchAdapter;
	private LinearListView m_searchResults;
	private Vector<searchResult> m_searchResultsList;
	
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
		
		m_searchResultsList = new Vector<searchResult>();
		
		TextView title = (TextView)root.findViewById(R.id.encyclopedia_categories_title);
		title.setTypeface(m_application.m_vagFont);
		
		m_adapter = new EncyclopediaCategoriesListViewAdapter(this, m_categoriesList);
		m_listView = (LinearListView) root.findViewById(R.id.encyclopedia_categories_list);
		m_listView.setAdapter(m_adapter);
		m_adapter.notifyDataSetChanged();
				
		m_searchBox = (EditText) m_root.findViewById(R.id.encyclopedia_search_box);
		m_searchBox.addTextChangedListener(new EncyclopediaSearchTextWatcher(m_application, this, m_root, m_searchResultsList));
		ImageView searchClear = (ImageView)root.findViewById(R.id.encyclopedia_search_clear);
		searchClear.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				m_searchBox.setText("");				
			}			
		});
				
		m_searchAdapter = new EncyclopediaSearchListViewAdapter(this, m_searchBox, m_searchResultsList);
		m_searchResults = (LinearListView) m_root.findViewById(R.id.encyclopedia_search_results_list);
		m_searchResults.setAdapter(m_searchAdapter);				
	}
}
