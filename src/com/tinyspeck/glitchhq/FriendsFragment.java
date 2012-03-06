package com.tinyspeck.glitchhq;

import java.util.Vector;

import org.json.JSONObject;

import com.tinyspeck.android.GlitchRequest;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class FriendsFragment extends BaseFragment {

	private ImageView m_filterClearImg;
	private FriendsListViewAdapter m_adapter;
	private LinearListView m_listView;
	private EditText m_filterText;
	private View m_root;
	
	private Vector<glitchFriend> m_friendsList;
	
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{	
		View curView = ViewInit(inflater, R.layout.friends_view, container);
		m_root = curView;
		init(curView);
		return curView;
	}
	
	private void init(View root)
	{
		boolean bUpdateData = m_friendsList == null;
		
		if (bUpdateData)
			m_friendsList = new Vector<glitchFriend>();
		
		m_filterText = (EditText)root.findViewById(R.id.friends_search_box);
		m_filterText.addTextChangedListener(filterTextWatcher);
		m_filterClearImg = (ImageView)root.findViewById(R.id.friends_filter_clear);
		m_filterClearImg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				m_filterText.setText("");				
			}			
		});
		
		m_adapter = new FriendsListViewAdapter(this, m_friendsList);
		m_listView = (LinearListView)root.findViewById(R.id.FriendsListView);
		m_listView.setAdapter(m_adapter);
		
		if (bUpdateData) {
			getFriends();
		} else {
			updateFriends();
		}
	}
	
	private void getFriends()
	{
		if (m_application != null) {
			GlitchRequest request1 = m_application.glitch.getRequest("friends.list");
	        request1.execute(this);
	        
	        m_requestCount = 1;
	        ((HomeScreen)getActivity()).showSpinner(true);
		}
	}
	
	private void updateFriends()
	{
		boolean bHas = m_friendsList.size() > 0;
		
		m_root.findViewById( R.id.list_friends_message ).setVisibility( bHas? View.GONE: View.VISIBLE );
		m_listView.setVisibility( bHas? View.VISIBLE: View.GONE );
   		
   		if( bHas )
   			m_adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "friends.list") {
			m_friendsList.clear();
			addFriendsList(m_friendsList, response);
			updateFriends();
			if (m_friendsList.size() == 0) {
				((TextView)m_root.findViewById(R.id.list_friends_message)).setText(R.string.friends_no_items);
			}
			onRequestComplete();
		}
	}
	
	protected boolean doesSupportRefresh()
	{
		return true;
	}
	
	protected boolean doesSupportMore()
	{
		return false;
	}
	
	protected void onRefresh()
	{
		getFriends();
	}
	
	protected void scrollToTop()
	{
		ScrollView sv = (ScrollView)m_root.findViewById(R.id.FriendsScrolLView);
		sv.smoothScrollTo(0, 0);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		m_filterText.removeTextChangedListener(filterTextWatcher);
	}
	
	private TextWatcher filterTextWatcher = new TextWatcher() {

		public void afterTextChanged(Editable s) {}

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		public void onTextChanged(CharSequence s, int start, int before, int count) {
			m_adapter.getFilter().filter(s);
		}
		
	};

}
