package com.tinyspeck.glitchhq;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tinyspeck.android.GlitchRequest;
import com.tinyspeck.android.GlitchRequestDelegate;
import com.tinyspeck.glitchhq.BaseFragment.searchResult;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

public class EncyclopediaSearchTextWatcher implements TextWatcher, GlitchRequestDelegate 
{
	private MyApplication m_application;
	private BaseFragment m_bf;
	private View m_root;
	private LinearListView m_listView;
	private LinearListView m_searchResultsListView;
	private Vector<searchResult> m_searchResultsList;
	private String m_type;
	
	public EncyclopediaSearchTextWatcher(MyApplication application, BaseFragment bf, View root, Vector<searchResult> searchResultsList)
	{
		this(application, bf, root, searchResultsList, "all");
	}
	
	public EncyclopediaSearchTextWatcher(MyApplication application, BaseFragment bf, View root, Vector<searchResult> searchResultsList, String type) 
	{
		m_application = application;
		m_bf = bf;
		m_root = root;
		m_searchResultsList = searchResultsList;
		m_listView = (LinearListView) root.findViewById(R.id.encyclopedia_categories_list);
		m_searchResultsListView = (LinearListView) m_root.findViewById(R.id.encyclopedia_search_results_list);
		m_type = type;
	}
	
	public void requestFinished(GlitchRequest request) {
		if (((HomeScreen)m_bf.getActivity()) == null)
			return;
		if (request != null && request.method != null)
		{
			JSONObject response = request.response;
			if (response != null)
			{
				Log.i("response", " method: " + request.method + " response: " + request.response );
				if (response.optInt( "ok") == 1) {
					onRequestBack( request.method, response );
				} else {
					m_root.findViewById(R.id.encyclopedia_search_results_list_message).setVisibility(View.GONE);
				}
			}
		}
	}

	public void requestFailed(GlitchRequest request) {
		((HomeScreen)m_bf.getActivity()).requestFailed(request);
	}
	
	private void onRequestBack(String method, JSONObject response)
	{
		if (method == "encyclopedia.search") {			
			JSONObject jResults = response.optJSONObject("matches");
			if (jResults != null && jResults.length() > 0) {
				Iterator<String> types = jResults.keys();
				while (types.hasNext()) {
					String type = types.next();
					if (!type.equalsIgnoreCase("Furniture") && !type.equalsIgnoreCase("Upgrade")) {
						JSONArray typedResults = jResults.optJSONArray(type);
						for (int i=0; i < typedResults.length();i++) {
							JSONObject jResult = typedResults.optJSONObject(i);
							if (jResult != null) {
								searchResult result = m_bf.new searchResult();
								result.type = type;
								result.name = jResult.optString("name");
								result.icon = jResult.optString("icon");
								result.url = jResult.optString("url");
								result.id = jResult.optString("class_tsid");
								m_searchResultsList.add(result);
							}
						}
					}
				}
			}
			m_root.findViewById(R.id.encyclopedia_search_results_list_message).setVisibility(View.GONE);
			((EncyclopediaSearchListViewAdapter)m_searchResultsListView.adapter).notifyDataSetChanged();
		}
	}

	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		
	}

	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (s.length() > 0) {
			m_searchResultsList.clear();
			((EncyclopediaSearchListViewAdapter)m_searchResultsListView.adapter).notifyDataSetChanged();
			
			m_searchResultsListView.setVisibility(View.VISIBLE);
			m_listView.setVisibility(View.GONE);
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("term", s.toString());
			if (!m_type.equals("all")) {
				params.put("type", m_type);
			}
			GlitchRequest request = m_application.glitch.getRequest("encyclopedia.search", params);
			request.execute(this);
			
			m_root.findViewById(R.id.encyclopedia_search_results_list_message).setVisibility(View.VISIBLE);
		} else {
			m_searchResultsListView.setVisibility(View.GONE);
			m_listView.setVisibility(View.VISIBLE);
		}
	}

}
