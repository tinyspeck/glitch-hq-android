package com.tinyspeck.glitchhq;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.tinyspeck.android.GlitchRequest;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AchievementDetailFragment extends BaseFragment {
	
	private glitchAchievement m_currentAchievement;
	private String m_achId;
	private String m_category;
	private View m_root;
	private Button m_btnBack;
	private Button m_btnSidebar;
	
	public AchievementDetailFragment()
	{
		super();
	}
	
	public AchievementDetailFragment(String achievementId)
	{
		// this constructor is for clicking from the Encyclopedia
		m_achId = achievementId;
		m_category = "Items";
	}
	
	public AchievementDetailFragment(String achievementId, String category)
	{
		m_achId = achievementId;		
		m_category = category;
	}
	
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View curView = ViewInit(inflater, R.layout.achievement_detail_view, container);
		m_root = curView;
		m_root.setVisibility(View.INVISIBLE);
		m_btnBack = (Button) m_root.findViewById(R.id.btnBack);
		m_btnBack.setText(m_category);
		m_btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				fm.popBackStack();
			}			
		});
		m_btnBack.setVisibility(View.VISIBLE);
		m_btnSidebar = (Button) m_root.findViewById(R.id.btnSidebar);
		m_btnSidebar.setVisibility(View.GONE);
		
		getAchievement();
		return curView;
	}
	
	public void getAchievement()
	{	
		Map<String, String> params = new HashMap<String, String>();
		params.put("achievement_class", m_achId);
		
		GlitchRequest request = m_application.glitch.getRequest("achievements.getInfo", params);
		request.execute(this);
		
		m_requestCount = 1;
		((HomeScreen)getActivity()).showSpinner(true);
	}
	
	@Override
	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "achievements.getInfo") {
			
			m_root.setVisibility(View.VISIBLE);
			
			if (response != null) {
				JSONObject item = response.optJSONObject("achievement");
				
				m_currentAchievement = new glitchAchievement();
				m_currentAchievement.id = item.optString("class");
				m_currentAchievement.name = item.optString("name");
				m_currentAchievement.desc = item.optString("desc");
				m_currentAchievement.icon = item.optString("image_180");
				m_currentAchievement.got = item.optInt("got") == 1 ? true : false;
				
				setAchievementDetailView(m_root);
			}
				
			onRequestComplete();			
		}
	}
	
	protected void setAchievementDetailView(View root)
	{
		ImageView icon = (ImageView) m_root.findViewById(R.id.achievement_detail_icon);
		if (m_currentAchievement.got) {
			DrawableURL.ShowGrayscaleAchievementBadge(icon, m_currentAchievement.icon, true);
		} else {
			DrawableURL.Show(icon, m_currentAchievement.icon, false);
		}		
		
		TextView tvName = (TextView) m_root.findViewById(R.id.achievement_detail_name);
		tvName.setTypeface(m_application.m_vagFont);
		tvName.setText(m_currentAchievement.name);
		
		TextView tvGot = (TextView) m_root.findViewById(R.id.achievement_got);
		tvGot.setTypeface(m_application.m_vagFont);
		if (m_currentAchievement.got) {
			tvGot.setText(R.string.str_got_achievement);
		} else {
			tvGot.setText(R.string.str_no_got_achievement);
		}
		
		TextView tvDesc = (TextView) m_root.findViewById(R.id.achievement_description);
		tvDesc.setTextColor(0xff151515);
		tvDesc.setTypeface(m_application.m_vagLightFont);
		tvDesc.setText(m_currentAchievement.desc);		
		m_root.scrollBy(0, 0);
	}
	
	protected boolean doesSupportRefresh()
	{
		return true;
	}
	
	protected void onRefresh()
	{
		getAchievement();
	}
}
