package com.tinyspeck.glitchhq;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tinyspeck.android.GlitchRequest;

import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.content.ClipData.Item;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EncyclopediaItemDetailFragment extends BaseFragment {

	private glitchItem m_item;
	private View m_root;
	private Button m_btnBack;
	private Button m_btnSidebar;
	private BaseFragment m_parentBf;
	private EncyclopediaItemDetailFragment m_bf;
	private boolean bHas;
	
	public EncyclopediaItemDetailFragment(BaseFragment bf, String itemClass)
	{
		m_item = new glitchItem();
		m_item.class_id = itemClass;
		m_parentBf = bf;
		bHas = false;
	}
	
	public EncyclopediaItemDetailFragment(BaseFragment bf, glitchItem item)
	{
		m_item = item;
		m_parentBf = bf;
		bHas = true;
	}	
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		m_bf = this;
		View curView = ViewInit(inflater, R.layout.encyclopedia_item_detail_view, container);
		m_root = curView;
		m_btnBack = (Button) m_root.findViewById(R.id.btnBack);
		if (m_parentBf instanceof EncyclopediaItemsInCategoryFragment) {
			m_btnBack.setText("Items");
		} else {
			m_btnBack.setText("Back");
		}
		m_btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				fm.popBackStack();
			}
		});
		m_btnBack.setVisibility(View.VISIBLE);
		m_btnSidebar = (Button) m_root.findViewById(R.id.btnSidebar);
		m_btnSidebar.setVisibility(View.GONE);
		
		if (bHas)
			setEncyclopediaItemDetailView();
		else
			getEncyclopediaItem();
		
		return curView;
	}
	
	public void getEncyclopediaItem()
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("item_class", m_item.class_id);
		
		GlitchRequest request = m_application.glitch.getRequest("items.info", params);
		request.execute(this);
		
		m_requestCount = 1;
		((HomeScreen)getActivity()).showSpinner(true);
	}
	
	@Override
	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "items.info") {
			if (response != null) {
				m_item = new glitchItem();
				m_item.warnings = new Vector<String>();
				m_item.tips = new Vector<String>();
				
				m_item.class_id = response.optString("item_class");
				m_item.name = response.optString("name_single");
				m_item.desc = response.optString("info");
				m_item.baseCost = response.optInt("base_cost");
				m_item.maxStack = response.optInt("max_stack");
				m_item.durability = response.optInt("tool_wear");
				m_item.growTime = response.optInt("grow_time");
				m_item.requiredSkill = response.optString("required_skill");
				m_item.icon = response.optString("iconic_url");
				
				JSONArray jWarnings = response.optJSONArray("warnings");
				if (jWarnings != null) {
					for (int i=0; i < jWarnings.length(); i++) {
						String warning = jWarnings.optString(i);
						m_item.warnings.add(warning);
					}
				}
				
				JSONArray jTips = response.optJSONArray("tips");
				if (jTips != null) {
					for (int i = 0; i < jTips.length(); i++) {
						String tip = jTips.optString(i);
						m_item.tips.add(tip);
					}
				}
				setEncyclopediaItemDetailView();
			}
		}
		onRequestComplete();
	}
	
	protected void setEncyclopediaItemDetailView()
	{
		ImageView icon = (ImageView) m_root.findViewById(R.id.encyclopedia_item_detail_icon);
		DrawableURL.Show(icon, m_item.icon, false);
		TextView name = (TextView) m_root.findViewById(R.id.encyclopedia_item_detail_name);
		name.setTypeface(m_application.m_vagFont);
		name.setText(m_item.name);
		TextView desc = (TextView) m_root.findViewById(R.id.encyclopedia_item_detail_desc);
		desc.setText(Html.fromHtml(m_item.desc));
		
		if (m_item.warnings.size() > 0 || m_item.tips.size() > 0) {
			WebView warnings_tips = (WebView) m_root.findViewById(R.id.encyclopedia_item_detail_warnings_tips);
			
			String html = "<style>* {-webkit-touch-callout: none; } body " +
					"{margin: 0px; padding: 0px; font-family: 'Helvetica Neue'; font-size: 14px; } " +
					"a:link, a:visited, a { color: #005C73; text-decoration:none; } " +
					"ol,ul {display: block; margin-left: 0; padding-left: 0; text-indent: -1em; list-style:none; } " +
					".item-details{float:left;clear:left;margin-bottom:1em} " +
					".item-details li{background:url(http://c1.glitch.bz/img/sprite-help_32951.png) " +
					"no-repeat;padding:10px 15px 10px 35px} .item-details li:last-child{margin-bottom:0!important} " +
					".item-details li.item-warn{background-position:5px 0;border-top-left-radius:4px;" +
					"-moz-border-radius-topleft:4px;-webkit-border-top-left-radius:4px;border-top-right-radius:4px;" +
					"-moz-border-radius-topright:4px;-webkit-border-top-right-radius:4px;border-bottom-left-radius:4px;" +
					"-moz-border-radius-bottomleft:4px;-webkit-border-bottom-left-radius:4px;" +
					"border-bottom-right-radius:4px;-moz-border-radius-bottomright:4px;" +
					"-webkit-border-bottom-right-radius:4px;background-color:#900;color:#fff;" +
					"text-shadow:none;margin:0 0 1em 0;padding:15px 15px 15px 40px} " +
					".item-details li.item-warn a{color:#fff;text-decoration:underline;font-weight:bold} " +
					".item-details li.item-note{background-position:5px -100px;" +
					"background-color:#dfdabd;border-top-left-radius:4px;-moz-border-radius-topleft:4px;" +
					"-webkit-border-top-left-radius:4px;border-top-right-radius:4px;" +
					"-moz-border-radius-topright:4px;-webkit-border-top-right-radius:4px;" +
					"border-bottom-left-radius:4px;-moz-border-radius-bottomleft:4px;" +
					"-webkit-border-bottom-left-radius:4px;border-bottom-right-radius:4px;" +
					"-moz-border-radius-bottomright:4px;-webkit-border-bottom-right-radius:4px;" +
					"color:rgba(0,0,0,0.6);margin:0 0 1em 0;padding:15px 15px 15px 35px} " +
					".item-details li a{font-weight:bold}</style><body><ul class='item-details'>";
			
			if (m_item.warnings.size() > 0) {
				for (int i=0; i < m_item.warnings.size(); i++) {
					html += "<li class='item-warn'>&nbsp;&nbsp;&nbsp;" + m_item.warnings.get(i) + "</li>";
				}
			}
			
			if (m_item.tips.size() > 0) {
				for (int i=0; i < m_item.tips.size(); i++) {
					html += "<li class='item-note'>&nbsp;&nbsp;&nbsp;&nbsp;" + m_item.tips.get(i) + "</li>";
				}
			}
			
			html += "</ul></body>";
			
			warnings_tips.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					if (url.startsWith("event:item|")) {
						String itemClass = url.replace("event:item|", "");
						EncyclopediaItemDetailFragment f = new EncyclopediaItemDetailFragment(m_parentBf, itemClass);
						((HomeScreen)getActivity()).setCurrentFragment(f, true);
					} else if (url.startsWith("event:skill|")) {
						String skillID = url.replace("event:skill|", "");
						SkillDetailFragment f = new SkillDetailFragment(m_bf, skillID);
						((HomeScreen)getActivity()).setCurrentFragment(f, true);
					} else if (url.startsWith("event:achievement|")) {
						String achievementId = url.replace("event:achievement|", "");
						AchievementDetailFragment f = new AchievementDetailFragment(achievementId);
						((HomeScreen)getActivity()).setCurrentFragment(f, true);
					} else if (url.startsWith("event:location|")) {
						String[] parts = url.replace("event:location|", "").split("#");
						if (parts.length == 2) {
							String streetTsid = parts[1];
							glitchLocationStreet street = new glitchLocationStreet();
							street.tsid = streetTsid;
							EncyclopediaStreetDetailFragment f = new EncyclopediaStreetDetailFragment(null, street);
							((HomeScreen)getActivity()).setCurrentFragment(f, true);
						}						
					}
					return true;
				}
			});
			warnings_tips.loadData(html, "text/html", "utf-8");
			warnings_tips.setBackgroundColor(Color.parseColor("#f0f0f0"));
			warnings_tips.setVisibility(View.VISIBLE);
		}
		
		if (m_item.baseCost > 0) {
			TextView worth = (TextView) m_root.findViewById(R.id.encyclopedia_item_detail_worth_text);
			worth.setText(Html.fromHtml("Worth about <b>" + m_item.baseCost + " currants</b>"));
			LinearLayout worthLayout = (LinearLayout) m_root.findViewById(R.id.encyclopedia_item_detail_worth);
			worthLayout.setPadding(10, 2, 10, 2);
			worthLayout.setVisibility(View.VISIBLE);
		}
		
		if (m_item.maxStack > 0) {
			TextView stack = (TextView) m_root.findViewById(R.id.encyclopedia_item_detail_stack_text);
			stack.setText(Html.fromHtml("Fits <b>" + m_item.maxStack + "</b> in a backpack slot"));
			LinearLayout stackLayout = (LinearLayout) m_root.findViewById(R.id.encyclopedia_item_detail_stack);
			stackLayout.setPadding(10, 2, 10, 2);
			stackLayout.setVisibility(View.VISIBLE);
		}
		
		if (m_item.durability > 0) {
			TextView wear = (TextView) m_root.findViewById(R.id.encyclopedia_item_detail_wear_text);
			wear.setText(Html.fromHtml("Durable for about <b>" + m_item.durability + " units of wear</b>"));
			LinearLayout wearLayout = (LinearLayout) m_root.findViewById(R.id.encyclopedia_item_detail_wear);
			wearLayout.setPadding(10, 2, 10, 2);
			wearLayout.setVisibility(View.VISIBLE);
		}
	}
	
	public glitchItem getItem() {
		return m_item;
	}
	
	protected boolean doesSupportRefresh()
	{
		return true;
	}
	
	protected void onRefresh()
	{
		getEncyclopediaItem();
	}
}
