package com.tinyspeck.android;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.twotalltotems.glitch.ServerRequestTask;

public class Glitch {
	
	//// Strings used for authentication and requests ////
	
	public static final String BASE_URL = "http://api.glitch.com"; // Base service URL

    public String accessToken = null; // Access token for the currently logged in user
    
    private String redirectUri = null; // Redirect URI for OAuth flow 
    private String clientId = null; // Client Id for OAuth flow
    
    
    // Constructor for Glitch object
    // API Key is required for generating an auth token
    // Redirect URI is required for OAuth flow
    public Glitch(String apiKey, String uri)
    {
    	if (apiKey == null || uri == null)
    	{
    		throw new IllegalArgumentException(
                    "Please specify your API key and Redirect URI when initializing a Glitch object");
    	}
    	
    	this.clientId = apiKey;
    	this.redirectUri = uri;
    }
    
    
    //// Interacting with the API ////
    
    public GlitchRequest getRequest(String method)
    {
		return getRequest(method, null);
    }
    
    public GlitchRequest getRequest(String method, Map<String,String> params)
    {
		return new GlitchRequest(method, params, this);
    }

    //// Authorization ////
    
    // Start browser with authorize URL so the user can authorize the app with OAuth
    public void authorize(String scope, Activity activity) {
    	Uri authorizeUri = getAuthorizeUri(scope);
    	
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, authorizeUri);
        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(browserIntent);  
    }

    public void login( String emailAddress, String password, final GlitchSessionDelegate delegate )
    {
    	String sURL = "http://api.glitch.com/oauth2/token";

    	Vector<BasicNameValuePair> params = new Vector<BasicNameValuePair>(6);
		params.add(new BasicNameValuePair("username", emailAddress));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("grant_type", "password"));
		params.add(new BasicNameValuePair("scope", "write"));
		params.add(new BasicNameValuePair("client_id", "197-764ef7f4f676f4b53819e52ea7cca4c65badf353"));
		params.add(new BasicNameValuePair("client_secret", "584d266525e5d8d7cf17faf9391f0145f12b12c6"));
		
		new ServerRequestTask(sURL, true, params  )
		{
		    protected void onPostExecute(String result)
		    {
		    	if( result == null )
		    	{
	            	delegate.glitchConnectionError();
		    		return;
		    	}	
				Log.i("glitch", result );

				if( result.length() == 0 )
				{
	            	delegate.glitchLoginFail();
					return;
				}
	            try {
	            	JSONTokener tokener = new JSONTokener(result);
					JSONObject jObject = new JSONObject(tokener);
					accessToken = (String)jObject.get("access_token");
					
	            	delegate.glitchLoginSuccess();
	            	
				} catch (JSONException e) {
					e.printStackTrace();
				}
		    }
		}.execute();
    }
    
    public void handleRedirect(Uri uri, GlitchSessionDelegate delegate)
    {
    	if (uri != null) {
        	// Get access token from URI fragment
            String fragment = uri.getFragment();
            
            String token = Glitch.extractTokenFromFragment(fragment);
            
            if (token != null)
            {
            	this.accessToken = token;
            	delegate.glitchLoginSuccess();
            }
        }
    }
    
    public boolean isAuthenticated()
    {
    	return this.accessToken != null;
    }
    
    
    //// Authorization URI Creation ////
    
    public Uri getAuthorizeUri(String scope) {
    	return this.getAuthorizeUri(scope, null);
    }
    
    public Uri getAuthorizeUri(String scope, String state) {
    	scope = scope == null ? "identity" : scope;
    	
    	String url = this.getAuthUrl(scope, state);
    	
    	return Uri.parse(url);
    }
    
    public String getAuthUrl(String scope, String state){
    	
    	String authUrl = BASE_URL + "/oauth2/authorize?response_type=token&client_id=" + this.clientId + "&scope=" + scope + "&redirect_uri=" + this.redirectUri;
    	
    	if (state != null)
    	{
    		authUrl = authUrl + "&state=" + state;
    	}
    	
        return authUrl;
    }
    
    
    //// Static Helper Methods ////
    
    private static String extractTokenFromFragment(String fragment) {
    	String[] vars = fragment.split("&");
        for (int i = 0; i < vars.length; i++) {
            String[] param = vars[i].split("=");

            if (param.length == 2 && param[0].equals("access_token")) {
                return param[1];
            }
        }
        
        return null;
    }
}