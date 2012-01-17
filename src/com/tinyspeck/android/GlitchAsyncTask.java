package com.tinyspeck.android;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


public class GlitchAsyncTask extends AsyncTask<String, Void, Object> {

	GlitchRequest request;
	GlitchRequestDelegate delegate;

    public GlitchAsyncTask(GlitchRequestDelegate startDelegate, GlitchRequest startRequest) {
    	delegate = startDelegate;
    	request = startRequest;
    }

    @Override
    protected Object doInBackground(String... strings) {

        URL url = null;
        try {
            url = new URL(strings[0]);
            String result = readURL(url);

            Log.i("vetal", result);

            JSONTokener tokener = new JSONTokener(result);
            JSONObject jObject = new JSONObject(tokener);

            return jObject;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String readURL(URL url) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = url.openStream();
        int r;
        while ((r = is.read()) != -1) {
            baos.write(r);
        }
        return new String(baos.toByteArray());
    }

    protected void onPostExecute(Object result) {
        if (delegate != null && result != null && result.getClass() == JSONObject.class)
        {
        	request.response = (JSONObject)result;
        	delegate.requestFinished(request);
        }
        else
        {
        	delegate.requestFailed(request);
        }
    }
}
