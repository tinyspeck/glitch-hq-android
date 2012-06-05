package com.tinyspeck.glitchhq;

import java.util.Iterator;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tinyspeck.android.GlitchRequest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class QuestsFragment extends BaseFragment {
	
	private QuestsListViewAdapter m_questsAdapter;
	private LinearListView m_questsListView;
	private View m_root;
	
	private Vector<glitchQuest> m_questsList;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View curView = ViewInit(inflater, R.layout.quests_view, container);
		m_root = curView;
		init(curView);
		return curView;
	}
	
	private void init(View root)
	{
		boolean bUpdateData = (m_questsList == null);
		
		if (bUpdateData) {
			m_questsList = new Vector<glitchQuest>();			
		}
		m_questsAdapter = new QuestsListViewAdapter(this, m_questsList);
		m_questsListView = (LinearListView) root.findViewById(R.id.quests_list);
		m_questsListView.setAdapter(m_questsAdapter);
		
		if (bUpdateData) {
			getQuests();
		} else {
			showQuestsPage();
		}
	}
	
	public void getQuests() 
	{
		if (m_application != null) {
			GlitchRequest request = m_application.glitch.getRequest("quests.getAll");
			request.execute(this);
			
			m_requestCount = 1;
			((HomeScreen)getActivity()).showSpinner(true);
		}
	}
	
	public void showQuestsPage()
	{
		boolean bHas = m_questsList.size() > 0;
		m_root.findViewById(R.id.quests_list_message).setVisibility(bHas ? View.GONE : View.VISIBLE);
		m_questsListView.setVisibility(bHas ? View.VISIBLE : View.GONE);
		
		if (bHas)
			m_questsAdapter.notifyDataSetChanged();
		
	}
	
	@Override
	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "quests.getAll") {
			JSONArray items = response.optJSONArray("todo");
			if (items != null) {
				m_questsList.clear();
				for (int i = 0; i < items.length(); i++) {
					JSONObject item = items.optJSONObject(i);
					glitchQuest q = new glitchQuest();
					q.title = item.optString("title");
					q.desc = item.optString("desc");
					
					JSONObject reqs = item.optJSONObject("reqs");
					q.reqs = new Vector<glitchQuestRequirement>();
					if (reqs.length() > 0) {						
						Iterator<String> itr = reqs.keys();
						
						while (itr.hasNext()) {
							String key = itr.next();
							JSONObject req = reqs.optJSONObject(key);
							glitchQuestRequirement qr = new glitchQuestRequirement();
							qr.desc = req.optString("desc");
							qr.isCount = req.optBoolean("is_count");
							qr.completed = req.optBoolean("completed");
							qr.gotNum = req.optInt("got_num");
							qr.needNum = req.optInt("need_num");
							qr.icon = req.optString("icon", null);
							q.reqs.add(qr);
						}
					}
					
					JSONObject rewards = item.optJSONObject("rewards");
					q.rewards = new glitchQuestRewards();
					q.rewards.favor = new Vector<glitchQuestRewardFavor>();
					q.rewards.recipes = new Vector<glitchQuestRewardRecipe>();
					
					q.rewards.imagination = rewards.optInt("xp");
					q.rewards.currants = rewards.optInt("currants");
					q.rewards.energy = rewards.optInt("energy");
					q.rewards.mood = rewards.optInt("mood");
					
					JSONArray favor = rewards.optJSONArray("favor");
					if (favor != null) {
						for (int j = 0; j < favor.length(); j++) {
							JSONObject favorGiant = favor.optJSONObject(j);
							glitchQuestRewardFavor fg = new glitchQuestRewardFavor();
							fg.giant = favorGiant.optString("giant");
							fg.giant = fg.giant.substring(0, 1).toUpperCase() + fg.giant.substring(1);
							fg.points = favorGiant.optInt("points");
							q.rewards.favor.add(fg);
						}
					}
					
					JSONArray recipes = rewards.optJSONArray("recipes");
					if (recipes != null) {
						for (int j = 0; j < recipes.length(); j++) {
							JSONObject recipe = recipes.optJSONObject(j);
							glitchQuestRewardRecipe r = new glitchQuestRewardRecipe();
							r.label = recipe.optString("label");
							r.icon = recipe.optString("icon");
							q.rewards.recipes.add(r);
						}
					}
					m_questsList.add(q);
				}
			}
			
			if (m_questsList.size() == 0) {
				((TextView)m_root.findViewById(R.id.quests_list_message)).setText("");
			}
			showQuestsPage();
		}
		onRequestComplete();
	}
}
