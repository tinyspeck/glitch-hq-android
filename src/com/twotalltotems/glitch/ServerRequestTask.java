package com.twotalltotems.glitch;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

public class ServerRequestTask extends AsyncTask<Void,Void,String>
{
	private static final String TAG = "ServerRequestTask";
	private String m_sURL;
	private boolean m_bPost;
	private Vector<BasicNameValuePair> m_params;

	public ServerRequestTask(String sURL, boolean bPost, Vector<BasicNameValuePair> queryParams)
	{
		m_sURL = sURL;
		m_bPost = bPost;
		m_params = queryParams;
	}
	
	@Override
	protected String doInBackground(Void ... sUri )
    {
		String sResult = null;
		HttpResponse response = null;
		
    	try {
    		
    		DefaultHttpClient client = new DefaultHttpClient();
    		HttpPost request;
    		
   			request = new HttpPost(m_sURL); 
    		
			request.setEntity( new UrlEncodedFormEntity(m_params) );
    		response = client.execute(request);
    		
    		StatusLine status = response.getStatusLine();
			sResult = readURL( response.getEntity().getContent() );

    		if ( status.getStatusCode() != 200 ) 
    		{
    			Log.d( TAG, "HTTP error, invalid server status code: " + status );  
    			Log.d( TAG, "HTTP error, server result: " + sResult );  
    			sResult = "";
    		}
    		client.getConnectionManager().shutdown(); 
    	} catch ( Exception e) {
    		e.printStackTrace();
    	}
    	return sResult;
    }

    private String readURL(InputStream is)
    {
    	try{
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        int r;
	        while ((r = is.read()) != -1) {
	            baos.write(r);
	        }
	        return new String(baos.toByteArray());
    	}catch( Exception e)
    	{
    		e.printStackTrace();
    	}
    	return "";
    }
	
};
