package com.tinyspeck.glitchhq;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tinyspeck.android.GlitchRequest;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.TextView;

public class RecentSnapsFragment extends BaseFragment 
{
	private RecentSnapsGridViewAdapter m_adapter;
	private GridView m_gridView;
	private View m_root;
	private int m_snapsCurrentPage;
	private BaseFragment m_bf;
	private Vector<glitchSnap> m_snapsList;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View curView = ViewInit(inflater, R.layout.recent_snaps_view, container);
		m_root = curView;
		m_bf = this;
		init(curView);
		return curView;
	}
	
	private void init(View root)
	{
		boolean bUpdateData = (m_snapsList == null);
		
		if (bUpdateData) {
			m_snapsList = new Vector<glitchSnap>();
		}
		TextView title = (TextView)root.findViewById(R.id.recent_snaps_title);
		title.setTypeface(m_application.m_vagFont);
		m_adapter = new RecentSnapsGridViewAdapter(this, m_snapsList);
		m_gridView = (GridView) root.findViewById(R.id.recent_snaps_grid);
		m_gridView.setAdapter(m_adapter);		
		
		if (bUpdateData) {
			getRecentSnaps(false);
		} else {
			showRecentSnapsPage();
		}
	}
	
	private void showRecentSnapsPage()
	{
		boolean bHas = m_snapsList.size() > 0;
		m_gridView.setVisibility(bHas ? View.VISIBLE : View.GONE);
		
		if (bHas)
			m_adapter.notifyDataSetChanged();
		
		if (m_bAppendMode) {
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					m_gridView.smoothScrollBy(100, 1);
				}
			}, 500);
		}
	}
	
	private void getRecentSnaps(boolean bMore)
	{
		Map<String, String> params = new HashMap<String, String>();
		
		if (bMore && m_snapsCurrentPage > 0) {
			params.put("page", String.valueOf(m_snapsCurrentPage));
			m_bAppendMode = true;
		} else {
			m_bAppendMode = false;
		}
		
		GlitchRequest request = m_application.glitch.getRequest("snaps.getRecent", params);
		request.execute(this);
		
		m_requestCount = 1;
		((HomeScreen)getActivity()).showSpinner(true);
	}
	
	@Override
	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "snaps.getRecent") {
			if (!m_bAppendMode)
				m_snapsList.clear();
			JSONObject pagination = response.optJSONObject("pagination");
			if (pagination != null) {
				m_snapsCurrentPage = pagination.optInt("page", 1); 
			}
			JSONArray photos = response.optJSONArray("photos");
			if (photos != null) {
				for (int i=0; i < photos.length(); i++) {
					JSONObject photo = photos.optJSONObject(i);
					if (photo != null) {
						glitchSnap snap = new glitchSnap();
						snap.id = Integer.parseInt(photo.optString("id"));
						JSONObject owner = photo.optJSONObject("owner");
						if (owner != null) {
							snap.who = owner.optString("name");
						}
						snap.playerID = photo.optString("player_tsid");
						snap.secret = photo.optString("secret");
						JSONObject urls = photo.optJSONObject("urls");
						snap.image = urls.optString("thumb");
						m_snapsList.add(snap);
					}
				}
			}
			showRecentSnapsPage();
		}
		onRequestComplete();
	}
	
	@Override
	protected boolean doesSupportRefresh()
	{
		return true;
	}
	
	@Override
	protected boolean doesSupportMore()
	{
		return true;
	}
	
	@Override
	protected void onRefresh()
	{
		getRecentSnaps(false);
	}
	
	@Override
	protected void onMore()
	{
		getRecentSnaps(true);
	}
}
