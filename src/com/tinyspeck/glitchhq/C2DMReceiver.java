package com.tinyspeck.glitchhq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.c2dm.C2DMBaseReceiver;
import com.google.android.c2dm.C2DMessaging;
import com.tinyspeck.android.Glitch;
import com.tinyspeck.android.GlitchRequest;
import com.tinyspeck.android.GlitchRequestDelegate;

public class C2DMReceiver extends C2DMBaseReceiver implements GlitchRequestDelegate 
{
	private static final String TAG = "C2DMReceiver";
	private Context context;
	
	public C2DMReceiver() 
	{
		super(Glitch.C2DM_SENDER);
	}
	
	@Override
	public void onError(Context context, String errorId) 
	{
		Toast.makeText(context,  "Messaging registration error: " + errorId, 
				Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onRegistered(Context context, String registrationId) throws IOException
	{
		super.onRegistered(context, registrationId);		
		Log.i(TAG, "Received registration id. Let's tell the Glitch servers.");
		// see if we already had a registrationId, if yes, then remove it
		String oldRegistrationId = C2DMessaging.getRegistrationId(this);
		if (registrationId != null && registrationId.length() > 0) {
			removeRegistration(context, oldRegistrationId);
		}		
		addRegistrationId(context, registrationId);
	}
	
	public void addRegistrationId(Context context, String registrationId) {
		// save the pointer to the context so we can use it in the callback
		this.context = context;
		
		Map<String,String> params = new  HashMap<String,String>();
		params.put("type", "android");		
		params.put("id", registrationId);		
		params.put("registration_id", registrationId);
		
		GlitchRequest request = ((MyApplication)context.getApplicationContext()).glitch.getRequest("users.devices.add", params );
        request.execute(this);
	}
	
	public void removeRegistration(Context context, String registrationId) {
		this.context = context;
		
		Map<String,String> params = new  HashMap<String,String>();
		params.put("type", "android");		
		params.put("id", registrationId);
		params.put("registration_id", registrationId);
		
		GlitchRequest request = ((MyApplication)context.getApplicationContext()).glitch.getRequest("users.devices.remove", params );
        request.execute(this);
	}
	
	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "users.devices.add") {
			if (response.optInt("ok") != 1) {				
				Log.i(TAG, "Response to users.devices.add was not okay.\n" + response.toString());				
			} else {
				C2DMessaging.setLastDeviceRegistration(context);
			}
		} else if (method == "users.devices.remove") {
			if (response.optInt("ok") != 1) {
				Log.i(TAG, "response to users.devices.update was not okay.\n" + response.toString());
			}
		}
	}
	
	public void requestFinished(GlitchRequest request) {
		if (request != null && request.method != null ) {
        	JSONObject response = request.response;
        	if (response != null) {
        		Log.i("response", " method: " + request.method + " response: " + request.response );
        		onRequestBack( request.method, response );
        	}
        }
	}

	public void requestFailed(final GlitchRequest request) {
		Log.i(TAG, "We couldn't add this mobile device to Glitch. Will try again later");
		FlurryAgent.logEvent("C2DMReceiver: Request for " + request.method + " failed.\n\n" + request.params.toString() + "\n\n" + request.response.toString());
		
		final C2DMReceiver receiver = this;
		final Handler handler = new Handler();
  		
  		handler.postDelayed(new Runnable() {
  			public void run() {
  				request.execute(receiver);
  			}
  		}, 30000);
	}
	
	@Override
	public void onUnregistered(Context context)
	{
		super.onUnregistered(context);
		Log.i(TAG, "Successfully unregistered device");
	}
		
	@Override
	protected void onMessage(Context context, Intent intent) 
	{
		Bundle data = intent.getExtras();
		Set<String> keys = data.keySet();
		Log.i(TAG, "Received message:");
		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
			String k = it.next();
			Log.i(TAG, "(" + k + ", " + data.getString(k) + ")");
		}
		
		if (keys.contains("collapse_key") && data.getString("collapse_key").equals("notifications") && keys.contains("message")) {
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationMananger = (NotificationManager) getSystemService(ns);
			int icon = R.drawable.notification_icon;
			CharSequence ticketText = "Glitch Notification";
			long when = System.currentTimeMillis();
			
			Notification notification = new Notification(icon, ticketText, when);
			
			CharSequence contentTitle = "Glitch";
			CharSequence contentText = data.getCharSequence("message");
			Intent notificationIntent = new Intent(context, HomeScreen.class);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);			
			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
			notification.defaults = Notification.DEFAULT_ALL;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			mNotificationMananger.notify(1, notification);
		}
	}
	
	public static void register(Context context)
	{
		Log.i("C2DMReceiver", "registering");
		C2DMessaging.register(context, Glitch.C2DM_SENDER);
	}
	
	public static void unregister(Context context)
	{
		Log.i("C2DMReceiver", "unregistering");
		String registrationId = C2DMessaging.getRegistrationId(context);
		if (registrationId != null && registrationId.length() > 0) {			
			(new C2DMReceiver()).removeRegistration(context, registrationId);			
		}
		C2DMessaging.unregister(context);
	}

}
