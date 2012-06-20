package com.tinyspeck.glitchhq;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.json.JSONObject;

import com.tinyspeck.android.GlitchRequest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class EncyclopediaGiantDetailFragment extends BaseFragment {

	private glitchGiant m_giant;
	private View m_root;
	private BaseFragment m_bf;
	private Button m_btnBack, m_btnSidebar;
	
	public EncyclopediaGiantDetailFragment(BaseFragment bf, glitchGiant giant)
	{
		m_giant = giant;
		m_bf = bf;
	}
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View curView = ViewInit(inflater, R.layout.encyclopedia_giant_detail_view, container);
		m_root = curView;
		m_root.setVisibility(View.INVISIBLE);
		m_btnBack = (Button) m_root.findViewById(R.id.btnBack);
		m_btnBack.setText("Giants");
		m_btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				fm.popBackStack();
			}
		});
		m_btnBack.setVisibility(View.VISIBLE);
		m_btnSidebar = (Button) m_root.findViewById(R.id.btnSidebar);
		m_btnSidebar.setVisibility(View.GONE);
		
		getEncyclopediaGiant();
		
		return curView;
	}
	
	private void showEncyclopediaGiantPage()
	{
		Activity m_act = getActivity();
		String packageName = m_act.getPackageName();
		
		RelativeLayout topSection = (RelativeLayout) m_root.findViewById(R.id.encyclopedia_giant_detail_top_section);
		int backgroundResID = m_act.getResources().getIdentifier("bg_"+m_giant.id+"_repeat", "drawable", packageName);		
		topSection.setBackgroundResource(backgroundResID);	
		
		ImageView icon = (ImageView) m_root.findViewById(R.id.encyclopedia_giant_detail_icon);
		int symbolResID = m_act.getResources().getIdentifier(m_giant.id+"_symbol", "drawable", packageName);
		icon.setImageBitmap(BitmapFactory.decodeResource(m_act.getResources(), symbolResID));		
		
		TextView name = (TextView) m_root.findViewById(R.id.encyclopedia_giant_detail_name_sex);
		name.setTypeface(m_application.m_vagFont);
		name.setText(m_giant.name + " " + m_giant.gender);
		
		TextView personality = (TextView) m_root.findViewById(R.id.encyclopedia_giant_detail_personality);
		personality.setText(m_giant.personality);
		
		ImageView image = (ImageView) m_root.findViewById(R.id.encyclopedia_giant_detail_image);
		int imageResID = m_act.getResources().getIdentifier(m_giant.id, "drawable", packageName);
		image.setImageBitmap(BitmapFactory.decodeResource(m_act.getResources(), imageResID));
		
		
		TextView desc = (TextView) m_root.findViewById(R.id.encyclopedia_giant_detail_desc);
		desc.setText(m_giant.desc);
		
		TextView followers = (TextView) m_root.findViewById(R.id.encyclopedia_giant_detail_adherents);
		followers.setText("Adherents are known as \"" + m_giant.followers + "\".");
		
		TextView skills = (TextView) m_root.findViewById(R.id.encyclopedia_giant_detail_skills);
		String skillsStr = "";
				
		Iterator<glitchGiantSkill> itr = m_giant.skills.iterator();
		while (itr.hasNext()) {
			glitchGiantSkill skill = itr.next();
			skillsStr += skill.name;
			if (itr.hasNext()) {
				skillsStr += ", ";
			}
		}
		skills.setText(skillsStr);
		
		m_root.setVisibility(View.VISIBLE);
	}
	
	private void getEncyclopediaGiant()
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("giant", m_giant.id);
		
		GlitchRequest request = m_application.glitch.getRequest("giants.info", params);
		request.execute(this);
		
		m_requestCount = 1;
		((HomeScreen)getActivity()).showSpinner(true);
	}
	
	@Override
	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "giants.info") {
			m_giant.skills = new Vector<glitchGiantSkill>();
			
			m_giant.name = response.optString("name");
			m_giant.desc = response.optString("desc");
			m_giant.gender = response.optString("gender");
			m_giant.followers = response.optString("followers");
			m_giant.giantOf = response.optString("giant_of");
			m_giant.personality = response.optString("personality");
			m_giant.image = response.optString("image");
			m_giant.icon = response.optString("icon");
			
			JSONObject skills = response.optJSONObject("skills");
			if (skills != null && skills.length() > 0) {
				Iterator<String> it = skills.keys();
				
				while (it.hasNext()) {
					String skillName = it.next();
					int skillVal = skills.optInt(skillName);
					glitchGiantSkill s = new glitchGiantSkill();
					s.name = skillName;
					s.skill = skillVal;
					m_giant.skills.add(s);
				}
			}
			showEncyclopediaGiantPage();
		}
		onRequestComplete();
	}
	
	@Override
	protected boolean doesSupportRefresh()
	{
		return true;
	}
	
	@Override
	protected void onRefresh()
	{
		getEncyclopediaGiant();
	}
	
	@Override
	protected void scrollToTop()
	{
		ScrollView sv = (ScrollView) m_root.findViewById(R.id.EncyclopediaGiantDetailScrollView);
		sv.smoothScrollTo(0, 0);
	}
	
}
