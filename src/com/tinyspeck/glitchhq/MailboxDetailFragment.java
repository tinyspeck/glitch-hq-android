package com.tinyspeck.glitchhq;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.tinyspeck.android.GlitchRequest;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MailboxDetailFragment extends BaseFragment {
	
	private glitchMail m_currentMessage;
	private int m_msgId;
	private View m_root;
	private Button m_btnBack;
	private Button m_btnReply;
	private Button m_btnDelete;
	
	MailboxDetailFragment(int msgId)
	{
		m_msgId = msgId;
	}
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View curView = ViewInit(inflater, R.layout.mail_detail_view, container);
		m_root = curView;
		m_root.setVisibility(View.INVISIBLE);
		
		m_btnBack = (Button) m_root.findViewById(R.id.btnBackToMailbox);
		m_btnBack.setVisibility(View.VISIBLE);
		m_btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				FlurryAgent.logEvent("Mail - back to 'Mailbox' button pressed");
				FragmentManager fm = getFragmentManager();
				fm.popBackStack();
			}
		});
		
		m_btnReply = (Button) m_root.findViewById(R.id.btnReply);
		m_btnReply.setVisibility(View.VISIBLE);
		m_btnReply.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				FlurryAgent.logEvent("Mail - 'Reply' button pressed");
				replyMail();
			}
		});
		getMessage();
		return curView;
	}
	
	public void getMessage()
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("message_id", Integer.toString(m_msgId));
		
		GlitchRequest request = m_application.glitch.getRequest("mail.getMessage", params);
		request.execute(this);
		
		m_requestCount = 1;
		((HomeScreen)getActivity()).showSpinner(true);
	}
	
	@Override
	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "mail.getMessage") {
			m_root.setVisibility(View.VISIBLE);
			if (response != null) {
				JSONObject message = response.optJSONObject("message");
				
				m_currentMessage = new glitchMail();
				m_currentMessage.id = m_msgId;
				m_currentMessage.sender_tsid = message.optString("sender_tsid");
				m_currentMessage.sender_label = message.optString("sender_label");
				m_currentMessage.sender_avatar = message.optString("sender_avatar");
				m_currentMessage.currants = message.optInt("currants");
				m_currentMessage.text = message.optString("text");
				m_currentMessage.received = message.optLong("delivery_time") * 1000; // convert to milliseconds
				m_currentMessage.is_read = message.optBoolean("is_read");
				m_currentMessage.is_expedited = message.optBoolean("is_expedited");
				
				JSONArray items = message.optJSONArray("itemstacks");
				// only one item can be attached now
				if (items != null && items.length() == 1) {					
					JSONObject item = items.optJSONObject(0);
					m_currentMessage.item = new glitchMailItem();
					m_currentMessage.item.tsid = item.optString("tsid");
					m_currentMessage.item.name = item.optString("label");
					m_currentMessage.item.class_id = item.optString("item_class");
					m_currentMessage.item.desc = item.optString("desc");
					m_currentMessage.item.icon = item.optString("icon_url");
					m_currentMessage.item.count = item.optInt("count");
				}
				
				setMailboxDetailView();
			}
		} else if (method == "mail.deleteMessage") {
			if (response.optInt("ok") == 1) {
				Util.shortToast(getActivity(), "Deleted");
			}
			((HomeScreen)getActivity()).getMailboxFragment().removeMessage(m_currentMessage);
			FragmentManager fm = getFragmentManager();
    		fm.popBackStack();
		}
		onRequestComplete();
	}
	
	protected void setMailboxDetailView()
	{
		ImageView icon = (ImageView) m_root.findViewById(R.id.message_detail_sender_icon);
		if (m_currentMessage.sender_avatar != null && !m_currentMessage.sender_avatar.equals(""))
			DrawableURL.CropShow(icon, m_currentMessage.sender_avatar + "_100.png");
		else
			BitmapUtil.CropShow(icon, BitmapFactory.decodeResource(m_root.getResources(), R.drawable.wireframe));
		
		TextView tvSenderName = (TextView) m_root.findViewById(R.id.message_detail_sender_name);
		tvSenderName.setTypeface(m_application.m_vagFont);
		if (m_currentMessage.sender_label != null && !m_currentMessage.sender_label.equals(""))
			tvSenderName.setText(m_currentMessage.sender_label);
		else
			tvSenderName.setText("Glitch");
		
		TextView tvReceived = (TextView) m_root.findViewById(R.id.message_detail_received);
		tvReceived.setTypeface(m_application.m_vagLightFont);
		tvReceived.setText(DateFormat.getDateTimeInstance().format(new Date(m_currentMessage.received)));
		
		TextView tvBody = (TextView) m_root.findViewById(R.id.message_detail_body);
		tvBody.setTypeface(m_application.m_vagLightFont);
		tvBody.setText(m_currentMessage.text);
		
		if (m_currentMessage.currants > 0) {
			LinearLayout messageCurrants = (LinearLayout) m_root.findViewById(R.id.message_currants);
			messageCurrants.setVisibility(View.VISIBLE);
			
			TextView tvCurrants = (TextView) m_root.findViewById(R.id.message_detail_currants);
			tvCurrants.setTypeface(m_application.m_vagLightFont);
			tvCurrants.setText(String.valueOf(m_currentMessage.currants));			
		} 
		
		if (m_currentMessage.item != null) {
			LinearLayout messageItem = (LinearLayout) m_root.findViewById(R.id.message_detail_item);
			messageItem.setVisibility(View.VISIBLE);
			
			ImageView messageItemIcon = (ImageView) m_root.findViewById(R.id.message_detail_item_icon);
			m_application.Download(m_currentMessage.item.icon, messageItemIcon, MyApplication.DOWNLOAD_TYPE_NORMAL);
			
			TextView messageItemTxt = (TextView) m_root.findViewById(R.id.message_detail_item_txt);
			if (m_currentMessage.item.count > 1) {
				messageItemTxt.setText(m_currentMessage.item.name + " (" + m_currentMessage.item.count + ")");
			} else {
				messageItemTxt.setText(m_currentMessage.item.name);
			}
		}
		
		if (m_currentMessage.currants <= 0 && m_currentMessage.item == null) {
			m_btnDelete = (Button) m_root.findViewById(R.id.btnDelete);
			m_btnDelete.setVisibility(View.VISIBLE);
			m_btnDelete.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					FlurryAgent.logEvent("Mail - 'Delete' button pressed");
					deleteMail();
				}
			});
		}
		
		m_root.scrollBy(0,0);
	}
	
	private void replyMail()
	{
		MailChooseRecipientFragment f1 = new MailChooseRecipientFragment();
		((HomeScreen)getActivity()).setCurrentFragment(f1, true);
		MailComposeFragment f2 = new MailComposeFragment(m_currentMessage.sender_label, m_currentMessage.sender_tsid, m_currentMessage.id);
		((HomeScreen)getActivity()).setCurrentFragment(f2, true);
	}
	
	private void deleteMail()
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("message_id", Integer.toString(m_msgId));
		
		GlitchRequest request = m_application.glitch.getRequest("mail.deleteMessage", params);	
		request.execute(this);
		
		m_requestCount = 1;
		((HomeScreen)getActivity()).showSpinner(true);
	}

	protected void scrollToTop()
	{
		ScrollView sv = (ScrollView)m_root.findViewById(R.id.MailboxDetailScrollView);
		sv.smoothScrollTo(0, 0);
	}
}
