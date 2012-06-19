package com.tinyspeck.glitchhq;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

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

public class EncyclopediaSkillsInCategoryFragment extends BaseFragment {

	private String m_category;
	private EncyclopediaSkillsInCategoryListViewAdapter m_adapter;
	private LinearListView m_listView;
	private View m_root;
	private Button m_btnBack;
	private Button m_btnSidebar;
	private Vector<skillAvailable> m_skillsList;
	
	public EncyclopediaSkillsInCategoryFragment(String category)
	{
		m_category = category;
	}
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View curView = ViewInit(inflater, R.layout.encyclopedia_skills_in_category_view, container);
		m_root = curView;
		init(curView);
		return curView;
	}
	
	private void init(View root)
	{
		boolean bUpdateData = m_skillsList == null;
		
		m_btnBack = (Button) m_root.findViewById(R.id.btnBack);
		m_btnBack.setText("Skills");
		m_btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				fm.popBackStack();
			}
		});
		m_btnBack.setVisibility(View.VISIBLE);
		
		m_btnSidebar = (Button)m_root.findViewById(R.id.btnSidebar);
		m_btnSidebar.setVisibility(View.GONE);
		
		if (bUpdateData) {
			m_skillsList = new Vector<skillAvailable>();
		}
		
		m_adapter = new EncyclopediaSkillsInCategoryListViewAdapter(this, m_skillsList);
		m_listView = (LinearListView) root.findViewById(R.id.encyclopedia_skills_in_category_list);
		m_listView.setAdapter(m_adapter);
		
		TextView tv = (TextView) m_root.findViewById(R.id.encyclopedia_skills_in_category_title);
		tv.setTypeface(m_application.m_vagFont);
		tv.setText(m_category);
		
		if (bUpdateData) {
			getSkillsInCategory();
		} else {
			showSkillsInCategoryPage();
		}
	}
	
	private void showSkillsInCategoryPage() 
	{
		boolean bHas = m_skillsList.size() > 0;
		
		m_root.findViewById(R.id.encyclopedia_skills_in_category_list_message).setVisibility(bHas ? View.GONE : View.VISIBLE);
		m_listView.setVisibility(bHas ? View.VISIBLE : View.GONE);
		
		if (bHas)
			m_adapter.notifyDataSetChanged();
	}
	
	private void getSkillsInCategory() 
	{
		if (m_application != null) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("category", m_category);
			
			GlitchRequest request = m_application.glitch.getRequest("skills.listAllInCategory", params);
			request.execute(this);
			
			m_requestCount = 1;
			((HomeScreen)getActivity()).showSpinner(true);
		}
	}
	
	@Override
	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "skills.listAllInCategory") {
			m_skillsList.clear();
			JSONObject jSkills = response.optJSONObject("skills");
			if (jSkills != null && jSkills.length() > 0) {
				Iterator<String> it = jSkills.keys();
				
				while (it.hasNext()) {
					String key = it.next();
					JSONObject jobj = jSkills.optJSONObject(key);
					skillAvailable skill = new skillAvailable();
					skill.id = jobj.optString("class_tsid");
					skill.item = jobj.optString("name");
					skill.icon = jobj.optString("icon_44");
					skill.can_learn = ( response.optInt("can_learn") == 1 )? true: false;
					skill.got = ( response.optInt("got") == 1 )? true: false;
					skill.paused = ( response.optInt("paused") == 1 )? true: false;
					m_skillsList.add(skill);
				}
				Collections.sort(m_skillsList, new SortByName());
			}
			if (m_skillsList.size() > 0) {
				((TextView)m_root.findViewById(R.id.encyclopedia_skills_in_category_list_message)).setText("");
			}
			showSkillsInCategoryPage();
		}
		onRequestComplete();
	}
	
	class SortByName implements Comparator<skillAvailable> {
		public int compare(skillAvailable s1, skillAvailable s2) {
			return s1.item.compareTo(s2.item);
		}
	}
	
	protected boolean doesSupportRefresh()
	{
		return true;
	}
	
	protected void OnRefresh()
	{
		getSkillsInCategory();
	}
	
	protected void scrollToTop()
	{
		ScrollView sv = (ScrollView) m_root.findViewById(R.id.EncyclopediaSkillsInCategoryScrollView);
		sv.smoothScrollBy(0, 0);
	}
}
