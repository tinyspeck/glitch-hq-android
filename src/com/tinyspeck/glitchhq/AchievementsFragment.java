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
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class AchievementsFragment extends BaseFragment {

	private glitchAchievementCategory m_category;
	private AchievementsListViewAdapter m_incompleteAdapter;
	private AchievementsListViewAdapter m_completedAdapter;
	private LinearListView m_incompleteListView;
	private LinearListView m_completedListView;
	private View m_root;
	private Button m_btnBack;
	private Button m_btnSidebar;
	private TextView m_incompleteHeader;
	private TextView m_completedHeader;
	
	private Vector<glitchAchievement> m_incompleteList;
	private Vector<glitchAchievement> m_completedList;
	
	public AchievementsFragment(glitchAchievementCategory category)
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
		boolean bUpdateData = (m_incompleteList == null || m_completedList == null);
	
		m_btnBack = (Button) m_root.findViewById(R.id.btnBack);
		m_btnBack.setText("Achieves");
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
			m_incompleteList = new Vector<glitchAchievement>();
			m_completedList = new Vector<glitchAchievement>();
		}
		
		m_incompleteAdapter = new AchievementsListViewAdapter(getActivity(), m_incompleteList, m_category.name);
		m_completedAdapter = new AchievementsListViewAdapter(getActivity(), m_completedList, m_category.name);
		m_incompleteListView = (LinearListView) root.findViewById(R.id.incomplete_achievements_list);
		m_incompleteListView.setAdapter(m_incompleteAdapter);
		m_completedListView = (LinearListView) root.findViewById(R.id.completed_achievements_list);
		m_completedListView.setAdapter(m_completedAdapter);		
		
		TextView tv = (TextView)m_root.findViewById(R.id.achievements_title);
		tv.setTypeface(m_application.m_vagFont);
		tv.setText(m_category.name + " (" + m_category.completed + "/" + m_category.total + ")");
		m_incompleteHeader = (TextView)m_root.findViewById(R.id.incomplete_header);
		m_incompleteHeader.setTypeface(m_application.m_vagFont);
		m_completedHeader = (TextView)m_root.findViewById(R.id.completed_header);
		m_completedHeader.setTypeface(m_application.m_vagFont);
		
		
		if (bUpdateData) {
			getAchievements();
		} else {
			showAchievementsPage();
		}		
	}
	
	private void showAchievementsPage()
	{
		boolean bHasIncomplete = m_incompleteList.size() > 0;
		boolean bHasCompleted = m_completedList.size() > 0;
		m_root.findViewById(R.id.achievements_list_message).setVisibility(bHasIncomplete || bHasCompleted ? View.GONE : View.VISIBLE);
		
		m_incompleteListView.setVisibility(bHasIncomplete ? View.VISIBLE : View.GONE);
		m_incompleteHeader.setVisibility(bHasIncomplete ? View.VISIBLE : View.GONE);
		m_completedListView.setVisibility(bHasCompleted ? View.VISIBLE : View.GONE);
		m_completedHeader.setVisibility(bHasCompleted ? View.VISIBLE : View.GONE);
		
		if (bHasIncomplete)
			m_incompleteAdapter.notifyDataSetChanged();
		if (bHasCompleted)
			m_completedAdapter.notifyDataSetChanged();
	}
	
	public void getAchievements()
	{
		if (m_application != null) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("category", m_category.name);
			params.put("per_page", String.valueOf(m_category.total));
			
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
			
			m_incompleteList.clear();
			m_completedList.clear();
						
			addAchievementsList(response);
					
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
				
				if (ach.got) {
					m_completedList.add(ach);
				} else {
					m_incompleteList.add(ach);
				}
			}
			SortById comp = new SortById();
			Collections.sort(m_completedList, comp);
			Collections.sort(m_incompleteList, comp);
		}			
	}	
	
	protected boolean doesSupportRefresh()
	{
		return true;
	}	
	
	protected void OnRefresh()
	{
		getAchievements();
	}
	
	protected void scrollToTop()
	{
		ScrollView sv = (ScrollView) m_root.findViewById(R.id.AchievementsScrollView);
		sv.smoothScrollTo(0, 0);
	}
}
