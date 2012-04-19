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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class AchievementsFragment extends BaseFragment {

	private String m_category;
	private AchievementsListViewAdapter m_adapter;
	private LinearListView m_listView;
	private View m_root;
	
	private int currentPage;
	
	private Vector<glitchAchievement> m_achievementsList;
	
	public AchievementsFragment(String category)
	{
		m_category = category; 
	}
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View curView = ViewInit(inflater, R.layout.achievements_view, container);
		m_root = curView;
		init(curView);
		return curView;
	}
	
	private void init(View root)
	{
		boolean bUpdateData = (m_achievementsList == null);
		
		if (bUpdateData) {
			m_achievementsList = new Vector<glitchAchievement>();			
		}
		
		m_adapter = new AchievementsListViewAdapter(getActivity(), m_achievementsList);
		m_listView = (LinearListView) root.findViewById(R.id.achievements_list);
		m_listView.setAdapter(m_adapter);
		TextView tv = (TextView)m_root.findViewById(R.id.achievements_title);
		tv.setTypeface(m_application.m_vagFont);
		tv.setText(m_category);
		
		if (bUpdateData) {
			getAchievements(false);
		} else {
			showAchievementsPage();
		}		
	}
	
	private void showAchievementsPage()
	{
		boolean bHas = m_achievementsList.size() > 0;		
		m_root.findViewById(R.id.achievements_list_message).setVisibility(bHas ? View.GONE : View.VISIBLE);
		m_listView.setVisibility(bHas ? View.VISIBLE : View.GONE);
		
		if (bHas)
			m_adapter.notifyDataSetChanged();
		
		if (m_bAppendMode) {
			ScrollView sv = (ScrollView) m_root.findViewById(R.id.AchievementsScrollView);
			Util.delayedFlingOfScrollView(sv, 500, 500);
		}
	}
	
	public void getAchievements(boolean bMore)
	{
		if (m_application != null) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("category", m_category);
			params.put("per_page", "20");
			
			if (bMore) {
				params.put("page", String.valueOf(currentPage + 1));
				m_bAppendMode = true;
			} else {
				m_bAppendMode = false;
			}
			
			GlitchRequest request = m_application.glitch.getRequest("achievements.listAllInCategory", params);
			request.execute(this);
			
			m_requestCount = 1;
			((HomeScreen)getActivity()).showSpinner(true);
		}
	}
	
	@Override
	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "achievements.listAllInCategory") {
			
			if (!m_bAppendMode)
				m_achievementsList.clear();
			
			currentPage = response.optInt("page");
						
			addAchievementsList(response);
			if (m_achievementsList.size() == 0) {
				((TextView)m_root.findViewById(R.id.achievements_list_message)).setText("");
			}
			showAchievementsPage();			
		}
		onRequestComplete();
	}
	
	class SortById implements Comparator<glitchAchievement> {
		public int compare(glitchAchievement a1, glitchAchievement a2) {
			return a1.id.compareTo(a2.id);
		}
	}
	
	private void addAchievementsList(JSONObject response)
	{
		JSONObject jItems = response.optJSONObject("items");
		if (jItems != null && jItems.length() > 0) {
			Iterator<String> it = jItems.keys();
			
			while (it.hasNext()) {
				String key = it.next();
				JSONObject jobj = jItems.optJSONObject(key);
				glitchAchievement ach = new glitchAchievement();
				ach.id = jobj.optString("class");
				ach.name = jobj.optString("name");
				ach.icon = jobj.optString("image_60");
				ach.got = jobj.optInt("got") == 1 ? true : false;
				
				if (ach != null && !findAchievementInList(ach.id)) {
					m_achievementsList.add(ach);
				}
			}
			Collections.sort(m_achievementsList, new SortById());
		}			
	}
	
	private boolean findAchievementInList(String id) {
		for (int i=0; i < m_achievementsList.size(); i++) {
			if (m_achievementsList.get(i).id.equalsIgnoreCase(id))
				return true;
		}
		return false;
	}
	
	protected boolean doesSupportRefresh()
	{
		return true;
	}
	
	protected boolean doesSupportMore()
	{
		return true;
	}
	
	protected void OnRefresh()
	{
		getAchievements(false);
	}
	
	protected void onMore()
	{
		getAchievements(true);
	}
	
	protected void scrollToTop()
	{
		ScrollView sv = (ScrollView) m_root.findViewById(R.id.AchievementsScrollView);
		sv.smoothScrollTo(0, 0);
	}
}
