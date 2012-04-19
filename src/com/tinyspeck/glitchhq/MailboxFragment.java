package com.tinyspeck.glitchhq;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.tinyspeck.android.GlitchRequest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class MailboxFragment extends BaseFragment {
	
	private MailboxListViewAdapter m_adapter;
	private LinearListView m_listView;
	
	private View m_root;
	private Button m_btnCompose;
	private TextView m_mailboxHeader;
	private TextView m_mailboxUnread;

	private Vector<glitchMail> m_mailList;
	private int unreadCount;
	private int messageCount;
	private int currentPage;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);	
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View curView = ViewInit(inflater, R.layout.mailbox_view, container);
		m_root = curView;
		init(curView);
		return curView;
	}
	
	private void init(View root)
	{
		boolean bUpdateData = (m_mailList == null);
		
		if (bUpdateData) {
			m_mailList = new Vector<glitchMail>();
		}
		
		m_adapter = new MailboxListViewAdapter(this, m_mailList);
		m_listView = (LinearListView) root.findViewById(R.id.mailbox_list);
		m_listView.setAdapter(m_adapter);
		
		if (bUpdateData) {
			getInbox(false);
		} else {
			showMailboxPage();
		}
		
		m_mailboxHeader = (TextView)m_root.findViewById(R.id.mailbox_header);
		m_mailboxHeader.setTypeface(m_application.m_vagFont);
		m_mailboxUnread = (TextView)m_root.findViewById(R.id.mailbox_unread);
		m_mailboxUnread.setTypeface(m_application.m_vagFont);
		
		m_btnCompose = (Button)m_root.findViewById(R.id.btnComposeMail);
		m_btnCompose.setVisibility(View.VISIBLE);
		m_btnCompose.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				FlurryAgent.logEvent("Mail - 'Compose' button pressed");
				composeMail();
			}
		});
	}
	
	private void showMailboxPage()
	{
		boolean bHas = m_mailList.size() > 0;
		m_root.findViewById(R.id.mailbox_list_message).setVisibility(bHas ? View.GONE : View.VISIBLE);
		m_listView.setVisibility(bHas ? View.VISIBLE : View.GONE);
		
		if (bHas)
			m_adapter.notifyDataSetChanged();
		
		if (m_bAppendMode) {
			ScrollView sv = (ScrollView) m_root.findViewById(R.id.MailboxScrollView);
			Util.delayedFlingOfScrollView(sv, 500, 500);
		}
		
		// Update message count?
		
		// Update unread count?
	}
	
	public void getInbox(boolean bMore)
	{
		if (m_application != null) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("per_page", "20");
			
			if (bMore) {
				params.put("page", String.valueOf(currentPage + 1));
				m_bAppendMode = true;
			} else {
				m_bAppendMode = false;
			}
			
			GlitchRequest request = m_application.glitch.getRequest("mail.getInbox", params);
			request.execute(this);
			
			m_requestCount = 1;
			((HomeScreen)getActivity()).showSpinner(true);
		}
	}
	
	@Override
	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "mail.getInbox") {
			
			if (!m_bAppendMode)
				m_mailList.clear();					
			
			addMailboxList(response);
			if (m_mailList.size() == 0) {
				((TextView)m_root.findViewById(R.id.mailbox_list_message)).setText("");
			}
			showMailboxPage();
		}
		onRequestComplete();
	}
	
	private void addMailboxList(JSONObject response)
	{
		JSONObject inbox = response.optJSONObject("inbox");
		if (inbox != null) {
			messageCount = inbox.optInt("message_count");
			unreadCount = inbox.optInt("unread_count");
			
			m_mailboxUnread.setText(String.valueOf(unreadCount));
			m_mailboxUnread.setVisibility(unreadCount > 0 ? View.VISIBLE : View.GONE);
			m_application.setMailUnreadCount(unreadCount);
			
			currentPage = inbox.optInt("page");
			JSONArray messages = inbox.optJSONArray("messages");
			if (messages != null) {
				for (int i = 0; i < messages.length(); i++) {
					JSONObject message = messages.optJSONObject(i);
					glitchMail mail = new glitchMail();
					
					mail.id = message.optInt("message_id");
					mail.currants = message.optInt("currants");
					mail.text = message.optString("text");
					mail.received = message.optLong("received");
					mail.replied = message.optInt("replied") == 1;
					mail.is_expedited = message.optBoolean("is_expedited");
					mail.is_read = message.optBoolean("is_read");
					
					
					JSONObject sender = message.optJSONObject("sender");
					if (sender != null) {
						mail.sender_tsid = sender.optString("tsid");
						mail.sender_label = sender.optString("label");
						mail.sender_avatar = sender.optString("singles_url");
					}
					
					JSONObject item = message.optJSONObject("item");
					if (item != null) {
						mail.item = new glitchMailItem();
						mail.item.tsid = item.optString("class_tsid");
						mail.item.name = item.optString("name");
						mail.item.count = item.optInt("count");
						mail.item.desc = item.optString("desc");
						mail.item.icon = item.optString("icon");
					}
					
					m_mailList.add(mail);
				}
			}
		}
	}
	
	public void markAsRead(int messageId) 
	{
		Iterator<glitchMail> itr = m_mailList.iterator();
		glitchMail mail;
		
		while(itr.hasNext()) {
			mail = itr.next();
			if (mail.id == messageId) {
				if (!mail.is_read) {
					mail.is_read = true;					
					m_application.decrMailUnreadCount();
					m_mailboxUnread.setText(String.valueOf(m_application.getMailUnreadCount()));
					if (m_application.getMailUnreadCount() <= 0)
						m_mailboxUnread.setVisibility(View.GONE);
					m_adapter.notifyDataSetChanged();
				}
				break;
			}
		}
	}
	
	public int getUnreadCount() {
		return unreadCount;
	}
	
	public void removeMessage(glitchMail message)
	{
		m_mailList.remove(message);
		m_adapter.notifyDataSetChanged();
	}
	
	private void composeMail()
	{
		MailChooseRecipientFragment f = new MailChooseRecipientFragment();
		((HomeScreen)getActivity()).setCurrentFragment(f, true);
	}
	
	protected boolean doesSupportRefresh()
	{
		return true;
	}
	
	protected boolean doesSupportMore()
	{
		return true;
	}
	
	protected void onRefresh()
	{
		getInbox(false);
	}
	
	protected void onMore()
	{
		getInbox(true);
	}
	
	protected void scrollToTop()
	{
		ScrollView sv = (ScrollView) m_root.findViewById(R.id.MailboxScrollView);
		sv.smoothScrollTo(0, 0);
	}
}
