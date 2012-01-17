package com.twotalltotems.glitch;

import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DrawableURL extends AsyncTask<String,Void,Bitmap>
{
	protected Object m_param;
	protected Object m_param2;
	
	public DrawableURL( Object param )
	{
		m_param = param;
	}
	
	public DrawableURL( Object param1, Object param2 )
	{
		m_param = param1;
		m_param2 = param2;
	}
	
	@Override
    protected Bitmap doInBackground(String... sURL)
    {
		try {
		
			InputStream is = (InputStream)new URL(sURL[0]).getContent();
			return BitmapFactory.decodeStream(is);
		} catch ( Exception e) 
		{
			e.printStackTrace();
		}
		return null;
    }
	
	public static void Show( ImageView iv, String sURL, final boolean bMirror )
	{
		if( sURL == null )
		{
			iv.setImageDrawable(null);
			iv.setTag(null);
			return;
		}
		if( iv.getTag() != sURL )
		{
			iv.setTag(sURL);
			iv.setImageDrawable(null);
			
			new DrawableURL( iv ){
				protected void onPostExecute(Bitmap bm){
					if( bMirror )
					{
						if( bm!= null )
							bm = BitmapUtil.GetMirror(bm);
					}
					((ImageView)m_param).setImageBitmap(bm);
				};
			}.execute(  sURL  );
		}
	}

	public static void CropShow( ImageView iv, String sURL )
	{
		if( sURL == null )
		{
			iv.setImageDrawable(null);
			iv.setTag(null);
			return;
		}
		if( iv.getTag() != sURL )
		{
			iv.setTag(sURL);
			iv.setImageDrawable(null);
			
			new DrawableURL( iv ){
				protected void onPostExecute(Bitmap bm)
				{
					if( bm!= null )
					{
						bm = BitmapUtil.GetMirror(bm);
						bm = BitmapUtil.CreateOvalImage(bm);
					}
					((ImageView)m_param).setImageBitmap(bm);
				};
			}.execute(  sURL  );
		}
	}
	
};
