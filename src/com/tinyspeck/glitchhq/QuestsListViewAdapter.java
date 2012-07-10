package com.tinyspeck.glitchhq;

import java.util.Iterator;
import java.util.Vector;
import com.tinyspeck.glitchhq.BaseFragment.glitchQuest;
import com.tinyspeck.glitchhq.BaseFragment.glitchQuestRequirement;
import com.tinyspeck.glitchhq.BaseFragment.glitchQuestRewardFavor;
import com.tinyspeck.glitchhq.BaseFragment.glitchQuestRewards;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class QuestsListViewAdapter extends BaseAdapter {

	private Vector<glitchQuest> m_questsList;
	private LayoutInflater m_inflater;
	private Activity m_act;
	private BaseFragment m_bf;
	private MyApplication m_application;
	private View m_current;
	
	public class ViewHolder
	{
		TextView name;
		TextView desc;
		LinearLayout reqs;
		TextView rewards;
		View whole;
	}
	
	public QuestsListViewAdapter(BaseFragment bf, Vector<glitchQuest> questsList)
	{
		m_questsList = questsList;
		m_act = bf.getActivity();
		m_bf = bf;
		m_inflater = (LayoutInflater)m_act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_application = (MyApplication)m_act.getApplicationContext();
		m_current = null;
	}
	
	public int getCount() 
	{
		if (m_questsList == null)
			return 0;
		return m_questsList.size();
	}

	public Object getItem(int position) 
	{	
		return position;
	}

	public long getItemId(int position) 
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder = null;
		
		if (convertView != null) {
			holder = (ViewHolder)convertView.getTag();
		}
		if (holder == null) {
			convertView = m_inflater.inflate(R.layout.quest_list_item, null);
			holder = new ViewHolder();
			
			holder.name = (TextView)convertView.findViewById(R.id.quest_name);
			holder.name.setTypeface(m_application.m_vagFont);
			holder.desc = (TextView)convertView.findViewById(R.id.quest_desc);
			holder.reqs = (LinearLayout)convertView.findViewById(R.id.quest_requirements);
			holder.rewards = (TextView)convertView.findViewById(R.id.quest_rewards);
			holder.whole = (View)convertView.findViewById(R.id.quest_item);
			convertView.setTag(holder);
		}
		holder = (ViewHolder)convertView.getTag();
		
		if (position < getCount()) {
			glitchQuest quest = m_questsList.get(position);
			holder.name.setText(quest.title);
			holder.desc.setText(quest.desc);
			Iterator<glitchQuestRequirement> itr = quest.reqs.iterator();
			while (itr.hasNext()) {
				glitchQuestRequirement req = itr.next();
				holder.reqs.addView((View)createRequirementView(req));
			}
			holder.rewards.setText(buildRewardsText(quest.rewards));
		}
		holder.whole.setTag(position);
		holder.whole.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				// make the old current view smaller and expand this view
				if (v != m_current) {
					if (m_current != null) {
						contractView(m_current);
					}
					m_current = v;
					expandView(v);
				}
			}
		});		
		return convertView;
	}
	
	private String buildRewardsText(glitchQuestRewards rewards)
	{
		String out = "Rewards:";
		int prefixLength = out.length();
		
		Iterator<glitchQuestRewardFavor> itr = rewards.favor.iterator();
		while (itr.hasNext()) {
			glitchQuestRewardFavor f = itr.next();
			if (out.length() > prefixLength) { out += ","; }
			out += " " + String.valueOf(f.points) + " favor with " + f.giant;
		}
		if (rewards.imagination > 0) {
			if (out.length() > prefixLength) { out += ","; }
			out += " " + String.valueOf(rewards.imagination) + " imagination";
		}
		if (rewards.currants > 0) {
			if (out.length() > prefixLength) { out += ","; }
			out += " " + String.valueOf(rewards.currants) + " currants";			
		}
		if (rewards.energy > 0) {
			if (out.length() > prefixLength) { out += ","; }
			out += " " + String.valueOf(rewards.energy) + " energy"; 
		}
		if (rewards.mood > 0) {
			if (out.length() > prefixLength) { out += ","; }
			out += " " + String.valueOf(rewards.mood) + " mood";
		}		
		int recipesCount = rewards.recipes.size();
		if (recipesCount > 0) {
			if (out.length() > prefixLength) { out += ","; }
			out += " +" + String.valueOf(recipesCount) + " recipes";
		}
		return out;
	}
	
	private View createRequirementView(glitchQuestRequirement req)
	{
		int id = 1;
		RelativeLayout holder = new RelativeLayout(m_act);
		LinearLayout.LayoutParams holderParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		holderParams.setMargins(10, 0, 0, 0);
		holder.setLayoutParams(holderParams);
		
		ImageView icon = new ImageView(m_act);
		icon.setId(id++);
		RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(23, 22);		
		iconParams.setMargins(2, 0, 0, 5);
		iconParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		iconParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		icon.setLayoutParams(iconParams);		
		if (req.icon != null) {
			DrawableURL.Show(icon, req.icon, false);
		} else {
			icon.setImageBitmap(BitmapFactory.decodeResource(m_act.getResources(), R.drawable.quest_requirement));
		}
		holder.addView(icon, iconParams);
		
		TextView last = new TextView(m_act);
		last.setId(id++);
		if (req.isCount) {
			TextView got = new TextView(m_act);			
			got.setId(id++);
			RelativeLayout.LayoutParams gotParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			gotParams.addRule(RelativeLayout.RIGHT_OF, icon.getId());
			gotParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			gotParams.setMargins(4, 0, 0, 0);
			got.setLayoutParams(gotParams);
			got.setTextAppearance(m_act.getApplicationContext(), R.style.MoodTextStyle);
			got.setTypeface(null, Typeface.BOLD);
			got.setText(String.valueOf(req.gotNum));
			holder.addView(got, gotParams);
						
			RelativeLayout.LayoutParams lastParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lastParams.addRule(RelativeLayout.RIGHT_OF, got.getId());
			lastParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			lastParams.setMargins(0, 0, 0, 0);
			last.setLayoutParams(lastParams);
			last.setTextAppearance(m_act.getApplicationContext(), R.style.MoodTextStyle);
			last.setText("/" + String.valueOf(req.needNum));
			holder.addView(last, lastParams);
		} else {
			RelativeLayout.LayoutParams lastParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lastParams.addRule(RelativeLayout.RIGHT_OF, icon.getId());
			lastParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			lastParams.setMargins(4, 0, 0, 0);
			last.setLayoutParams(lastParams);			
			last.setTextAppearance(m_act.getApplicationContext(), R.style.MoodTextStyle);
			last.setText(req.desc);
			holder.addView(last, lastParams);
		}
		
		if (req.completed) {
			ImageView check = new ImageView(m_act);
			RelativeLayout.LayoutParams checkParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			checkParams.addRule(RelativeLayout.ALIGN_LEFT, last.getId());			
			checkParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			checkParams.setMargins(4, 0, 0, 0);
			check.setLayoutParams(checkParams);			
			check.setImageBitmap(BitmapFactory.decodeResource(m_act.getResources(), R.drawable.checkmark_small));
			holder.addView(check, checkParams);
		}
		return holder;
	}
	
	private void contractView(View v) 
	{
		TextView desc = (TextView)v.findViewById(R.id.quest_desc);
		desc.setSingleLine(true);
		desc.setEllipsize(TruncateAt.END);
		LinearLayout reqs = (LinearLayout)v.findViewById(R.id.quest_requirements);
		reqs.setVisibility(View.GONE);
		TextView rewards = (TextView)v.findViewById(R.id.quest_rewards);
		rewards.setVisibility(View.GONE);
	}
	
	private void expandView(View v)
	{
		TextView desc = (TextView)v.findViewById(R.id.quest_desc);
		desc.setSingleLine(false);
		LinearLayout reqs = (LinearLayout)v.findViewById(R.id.quest_requirements);
		reqs.setVisibility(View.VISIBLE);
		TextView rewards = (TextView)v.findViewById(R.id.quest_rewards);
		rewards.setVisibility(View.VISIBLE);
		Util.startAlphaAnimation(desc, 800, 0, 1, TranslateAnimation.ABSOLUTE);
		Util.startAlphaAnimation(reqs, 800, 0, 1, TranslateAnimation.ABSOLUTE);
		Util.startAlphaAnimation(rewards, 800, 0, 1, TranslateAnimation.ABSOLUTE);
	}

}
