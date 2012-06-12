package com.tinyspeck.glitchhq;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tinyspeck.android.GlitchRequest;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class EncyclopediaItemsInCategoryFragment extends BaseFragment {
	
	private glitchItemCategory m_category;
	private EncyclopediaItemsInCategoryListViewAdapter m_adapter;
	private LinearListView m_listView;
	private View m_root;
	private Button m_btnBack;
	private Button m_btnSidebar;
	private Vector<glitchItem> m_itemList;
	
	public EncyclopediaItemsInCategoryFragment(glitchItemCategory category)
	{
		m_category = category;
	}
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View curView = ViewInit(inflater, R.layout.encyclopedia_items_in_category_view, container);
		m_root = curView;
		init(curView);
		return curView;
	}
	
	private void init(View root)
	{
		boolean bUpdateData = m_itemList == null;
		
		m_btnBack = (Button) m_root.findViewById(R.id.btnBack);
		m_btnBack.setText("Items");
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
			m_itemList = new Vector<glitchItem>();
		}
		
		m_adapter = new EncyclopediaItemsInCategoryListViewAdapter(getActivity(), m_itemList, m_category);
		m_listView = (LinearListView) root.findViewById(R.id.encyclopedia_items_in_category_list);
		m_listView.setAdapter(m_adapter);
		
		TextView tv = (TextView) m_root.findViewById(R.id.encyclopedia_items_in_category_title);
		tv.setTypeface(m_application.m_vagFont);
		tv.setText(m_category.name);
		
		if (bUpdateData) {
			getItemsInCategory();
		} else {
			showItemsInCategoryPage();
		}
	}
	
	private void showItemsInCategoryPage()
	{
		boolean bHas = m_itemList.size() > 0;
		
		m_root.findViewById(R.id.encyclopedia_items_in_category_list_message).setVisibility(bHas ? View.GONE : View.VISIBLE);
		m_listView.setVisibility(bHas ? View.VISIBLE : View.GONE);
		
		if (bHas)
			m_adapter.notifyDataSetChanged();
	}
	
	private void getItemsInCategory()
	{
		if (m_application != null) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("category", m_category.id);
			
			GlitchRequest request = m_application.glitch.getRequest("items.getItems", params);
			request.execute(this);
			
			m_requestCount = 1;
			((HomeScreen)getActivity()).showSpinner(true);
		}
	}
	
	@Override
	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "items.getItems") {
			m_itemList.clear();
			JSONObject jItems = response.optJSONObject("items");
			if (jItems != null && jItems.length() > 0) {
				Iterator<String> it = jItems.keys();
				
				while (it.hasNext()) {
					String key = it.next();
					JSONObject jobj = jItems.optJSONObject(key);
					glitchItem item = new glitchItem();
					item.warnings = new Vector<String>();
					item.tips = new Vector<String>();
					
					item.class_id = jobj.optString("class_id");
					item.name = jobj.optString("name_single");
					item.desc = jobj.optString("info");
					item.baseCost = jobj.optInt("base_cost");
					item.maxStack = jobj.optInt("max_stack");
					item.durability = jobj.optInt("tool_wear");
					item.growTime = jobj.optInt("grow_time");
					item.requiredSkill = jobj.optString("required_skill");
					item.icon = jobj.optString("iconic_url");
					
					JSONArray jWarnings = jItems.optJSONArray("warnings");
					if (jWarnings != null) {
						for (int i=0; i < jWarnings.length(); i++) {
							String warning = jWarnings.optString(i);
							item.warnings.add(warning);
						}
					}
					
					JSONArray jTips = jItems.optJSONArray("tips");
					if (jTips != null) {
						for (int i = 0; i < jTips.length(); i++) {
							String tip = jTips.optString(i);
							item.tips.add(tip);
						}
					}
					m_itemList.add(item);
				}
				Collections.sort(m_itemList, new SortByName());				
			}
			if (m_itemList.size() > 0) {
				((TextView)m_root.findViewById(R.id.encyclopedia_items_in_category_list_message)).setText("");
			}
			showItemsInCategoryPage();
		}
		onRequestComplete();
	}
	
	class SortByName implements Comparator<glitchItem> {
		public int compare(glitchItem i1, glitchItem i2) {
			return i1.name.compareTo(i2.name);
		}
	}
	
	protected boolean doesSupportRefresh()
	{
		return true;
	}
	
	protected void OnRefresh()
	{
		getItemsInCategory();
	}
	
	protected void scrollToTop()
	{
		ScrollView sv = (ScrollView) m_root.findViewById(R.id.EncyclopediaItemsInCategoryScrollView);
		sv.smoothScrollTo(0, 0);
	}
}
