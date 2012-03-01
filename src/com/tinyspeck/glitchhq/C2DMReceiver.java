package com.tinyspeck.glitchhq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
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
		Log.i(TAG, "Received registration id: " + registrationId + ". Let's tell the Glitch servers.");
		
		addRegistrationId(context, registrationId);
	}
	
	public void addRegistrationId(Context context, String registrationId) {
		// save the pointer to the context so we can use it in the callback
		this.context = context;
		
		Map<String,String> params = new  HashMap<String,String>();
		params.put("type", "android");
		
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		params.put("id", tm.getDeviceId());
		
		params.put("registration_id", registrationId);
		GlitchRequest request = ((MyApplication)context.getApplicationContext()).glitch.getRequest("users.devices.add", params );
        request.execute(this);
	}
	
	public void updateRegistrationId(Context context, String registrationId) {	
		// save the pointer to the context so we can use it in the callback
		this.context = context;
				
		Map<String,String> params = new  HashMap<String,String>();
		params.put("type", "android");
		
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		params.put("id", tm.getDeviceId());
		
		params.put("registration_id", registrationId);
		GlitchRequest request = ((MyApplication)context.getApplicationContext()).glitch.getRequest("users.devices.update", params );
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
		} else if (method == "users.devices.update") {
			if (response.optInt("ok") != 1) {
				Log.i(TAG, "response to suers.devices.update was not okay.\n" + response.toString());
			} else {
				C2DMessaging.setLastDeviceRegistration(context);
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
		Log.i(TAG, intent.getExtras().toString());
	}
	
	public static void register(Context context)
	{
		Log.i("C2DMReceiver", "registering");
		C2DMessaging.register(context, Glitch.C2DM_SENDER);
	}
	
	public static void unregister(Context context)
	{
		Log.i("C2DMReceiver", "unregistering");
		C2DMessaging.unregister(context);
	}

}
