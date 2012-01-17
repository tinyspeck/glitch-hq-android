package com.tinyspeck.android;


import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class GlitchRequest {
	
	//// Strings and objects used for authentication and requests ////
	
	public static final String API_URL = Glitch.BASE_URL + "/simple/";
	
	public String url; // Full url for request, e.g. "http://api.glitch.com/simple/players.info"
	public String method; // Specific method without 'simple', e.g. "players.info"
	public Map<String,String> params; // Dictionary of parameters passed in the request
	public GlitchRequestDelegate delegate; // Handler that will be called when events occur before, during, and after the request
	public JSONObject response; // JSON response object
	
    private GlitchAsyncTask task; // Async task that interacts with API
    private Glitch glitch; // Glitch parent
	
    
    //// Constructors ////
    
	public GlitchRequest(String startMethod, Map<String,String> startParams, Glitch startGlitch) {
		method = startMethod;
		params = startParams;
		glitch = startGlitch;
	}
	
	public GlitchRequest(String startMethod, Glitch startGlitch) {
		this(startMethod, null, startGlitch);
	}
	
	//// Interacting with the API ////
	
	// Call this to execute your request
	// Pass in the delegate which will be called when events occur that are related to this object
    public void execute(GlitchRequestDelegate requestDelegate) {
    	delegate = requestDelegate;
    	
    	String fullUrl = API_URL + method;
    	
    	if (params == null)
    	{
    		params = new HashMap<String,String>();
    	}
    	
    	if (glitch.accessToken != null)
		{
			params.put("oauth_token", glitch.accessToken);
		}
		
		fullUrl = serializeURL(fullUrl, params);
    	
        task = new GlitchAsyncTask(delegate, this);
        task.execute(fullUrl);
    }

    // Cancel the pending request
    public boolean cancel(boolean mayInterruptIfRunning){
        if (task != null)
            return task.cancel(mayInterruptIfRunning);
        return false;
    }
    
    
    //// URL Helper Methods ////
    
    public static String serializeURL(String url, Map<String,String> params)
    {
    	url = url + "?";
    	
    	url = url + serializeParams(params);
    	
    	return url;
    }
    
    public static String serializeParams(Map<String,String> params)
    {
    	String parameters = "";
    	
    	if (params != null && !params.isEmpty())
    	{    		
    		for (String key : params.keySet())
    		{
    			parameters = parameters + "&" + key + "=" + params.get(key);
    		}
			
			// Remove extra ampersand from front
			parameters = parameters.substring(1);
    	}
    	
    	return parameters;
    }
}