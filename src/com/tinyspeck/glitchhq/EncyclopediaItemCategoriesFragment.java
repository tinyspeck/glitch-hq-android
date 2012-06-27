package com.tinyspeck.glitchhq;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tinyspeck.android.GlitchRequest;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class EncyclopediaItemCategoriesFragment extends BaseFragment {

	private EncyclopediaItemCategoriesListViewAdapter m_adapter;
	private LinearListView m_listView;
	private View m_root;
	private Vector<glitchItemCategory> m_categoriesList;
	private Button m_btnBack;
	private Button m_btnSidebar;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View curView = ViewInit(inflater, R.layout.encyclopedia_item_categories_view, container);
		m_root = curView;
		init(curView);
		return curView;
	}
	
	private void init(View root)
	{
		boolean bUpdateData = (m_categoriesList == null);
		
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
			m_categoriesList = new Vector<glitchItemCategory>();
		}
		TextView title = (TextView)root.findViewById(R.id.encyclopedia_item_categories_title);
		title.setTypeface(m_application.m_vagFont);
		m_adapter = new EncyclopediaItemCategoriesListViewAdapter(this, m_categoriesList);
		m_listView = (LinearListView) root.findViewById(R.id.encyclopedia_item_categories_list);
		m_listView.setAdapter(m_adapter);		
		
		if (bUpdateData) {
			getEncyclopediaItemCategories();
		} else {
			showEncyclopediaItemCategoriesPage();
		}
	}
	
	public void showEncyclopediaItemCategoriesPage()
	{
		boolean bHas = m_categoriesList.size() > 0;
		m_root.findViewById(R.id.encyclopedia_item_categories_list_message).setVisibility(bHas ? View.GONE : View.VISIBLE);
		m_listView.setVisibility(bHas ? View.VISIBLE : View.GONE);
		
		if (bHas)
			m_adapter.notifyDataSetChanged();
	}
	
	public void getEncyclopediaItemCategories()
	{
		if (m_application != null) {
			GlitchRequest request = m_application.glitch.getRequest("items.getCategories");
			request.execute(this);
			
			m_requestCount = 1;
			((HomeScreen)getActivity()).showSpinner(true);
		}
	}
	
	@Override
	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "items.getCategories") {
			JSONArray items = response.optJSONArray("categories");
			if (items != null) {
				for (int i=0; i < items.length(); i++) {
					JSONObject jobj = items.optJSONObject(i);
					glitchItemCategory c = new glitchItemCategory();
					c.id = jobj.optString("id");
					c.name = jobj.optString("name");
					if (c.name.equalsIgnoreCase("Furniture")) {
						continue;
					} else {
						m_categoriesList.add(c);
					}
				}
				Collections.sort(m_categoriesList, new SortByName());
			}
			
			if (m_categoriesList.size() > 0) {
				((TextView)m_root.findViewById(R.id.encyclopedia_item_categories_list_message)).setText("");
			}
			showEncyclopediaItemCategoriesPage();
		}
		onRequestComplete();
	}
	
	class SortByName implements Comparator<glitchItemCategory> {
		public int compare(glitchItemCategory c1, glitchItemCategory c2) {
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
		getEncyclopediaItemCategories();
	}
	
	@Override
	protected void scrollToTop()
	{
		ScrollView sv = (ScrollView) m_root.findViewById(R.id.EncyclopediaItemCategoriesScrollView);
		sv.smoothScrollTo(0, 0);
	}
}
