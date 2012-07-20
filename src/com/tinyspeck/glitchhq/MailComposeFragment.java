package com.tinyspeck.glitchhq;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.tinyspeck.android.GlitchRequest;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MailComposeFragment extends BaseFragment {
	
	private String m_recipientLabel;
	private String m_recipientTsid;
	private TextView m_recipientTextView;
	private EditText m_composer;
	private Button m_btnBackToRecipientChooser;
	private Button m_btnSendMail;
	private Button m_btnSidebar;
	private View m_root;
	private int m_inReplyTo;
		
	public MailComposeFragment(String recipientLabel, String recipientTsid)
	{
		this(recipientLabel, recipientTsid, 0);
	}
	
	public MailComposeFragment(String recipientLabel, String recipientTsid, int inReplyTo)
	{
		m_recipientLabel = recipientLabel;
		m_recipientTsid = recipientTsid;
		m_inReplyTo = inReplyTo;
	}
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View curView = ViewInit(inflater, R.layout.compose_mail_view, container);
		m_root = curView;
		init(curView);
		return curView;
	}
	
	private void init(View root)
	{
		m_btnBackToRecipientChooser = (Button) root.findViewById(R.id.btnBackToRecipientChooser);
		m_btnBackToRecipientChooser.setVisibility(View.VISIBLE);
		m_btnBackToRecipientChooser.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				FlurryAgent.logEvent("MailCompose - 'To' button pressed");
				// assume always have MailChooseRecipientFragment underneath
				FragmentManager fm = getFragmentManager();
				fm.popBackStack();
			}
		});
		
		m_btnSendMail = (Button) root.findViewById(R.id.btnSendMail);
		m_btnSendMail.setVisibility(View.VISIBLE);
		m_btnSendMail.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				FlurryAgent.logEvent("MailCompose - 'Send' button pressed");
				if (m_composer.getText().toString().length() == 0) {
					Util.Alert(getActivity(), "Your message is empty, do you want to send anyway?", 
							"Send empty message?", true, "Send", "Cancel", new DialogInterface.OnClickListener() {								
								public void onClick(DialogInterface dialog, int which) {
									if (which == DialogInterface.BUTTON_POSITIVE) {
										sendMail();
									} else {
										dialog.dismiss();
									}
								}
							});
				} else {
					sendMail();
				}
			}
		});
		
		m_btnSidebar = (Button) root.findViewById(R.id.btnSidebar);
		m_btnSidebar.setVisibility(View.GONE);
		
		m_recipientTextView = (TextView) root.findViewById(R.id.recipient_text_view);
		m_recipientTextView.setTypeface(m_application.m_vagFont);
		m_recipientTextView.setText("To: " + m_recipientLabel);
		
		m_composer = (EditText) root.findViewById(R.id.mail_composer);
		
	}
	
	public void setNewRecipient(String recipientLabel, String recipientTsid)
	{
		m_recipientLabel = recipientLabel;
		m_recipientTsid = recipientTsid;
		m_recipientTextView.setText("To: " + m_recipientLabel);
	}
	
	private void sendMail()
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("recipient", m_recipientTsid);
		params.put("message", m_composer.getText().toString());
		if (m_inReplyTo > 0) {
			params.put("in_reply_to", String.valueOf(m_inReplyTo));
		}
				
		GlitchRequest request = m_application.glitch.getRequest("mail.sendMessage", params);
		request.execute(this);
	        
		m_requestCount = 1;
		((HomeScreen)getActivity()).showSpinner(true);
	}
	
	@Override
	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "mail.sendMessage") {
			if (response.optInt("ok") != 1) {
				Util.shortToast(getActivity(), "Failed");
			} else {
				Util.shortToast(getActivity(), "Sent");
			}
			FragmentManager fm = getFragmentManager();
			fm.popBackStack(); // go back to recipient selector
			fm.popBackStack(); // go back to whatever was before
		}
		onRequestComplete();
	}
}
