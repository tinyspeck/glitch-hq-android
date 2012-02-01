package com.twotalltotems.glitch;

import java.lang.Thread.UncaughtExceptionHandler;

import com.tinyspeck.android.Glitch;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;

public class MyApplication extends Application 
{
  private boolean m_bInit = false;
  public Glitch glitch;
  public Typeface m_vagFont;
  public Typeface m_vagLightFont;
  private ImageDownloader m_downloader;

  public static final int DOWNLOAD_TYPE_NORMAL = 0;
  public static final int DOWNLOAD_TYPE_MIRROR = 1;
  public static final int DOWNLOAD_TYPEE_CROP = 2;
  
  public void Init( Activity act  )
  {
	if( m_bInit )
	  return;	
	
    m_bInit = true;

    GlobalExceptionHandler handler = new GlobalExceptionHandler(Thread.getDefaultUncaughtExceptionHandler());
    Thread.setDefaultUncaughtExceptionHandler(handler);
    
    m_downloader = new ImageDownloader(){
    	public void setImageViewBitmap( ImageView iv, Bitmap bm )
    	{
    		int nType = (Integer)iv.getTag();
    		if( nType == DOWNLOAD_TYPE_MIRROR )
				bm = BitmapUtil.GetMirror(bm);
    		else if ( nType == DOWNLOAD_TYPEE_CROP )
    		{
    			bm = BitmapUtil.GetMirror(bm);
    			bm = BitmapUtil.CreateOvalImage(bm);
    		}	
    		iv.setVisibility(View.VISIBLE);    		
    		iv.setImageBitmap(bm);
    	}
    };
    
    m_vagFont = Typeface.createFromAsset( getAssets(), "VAG.ttf" );  
    m_vagLightFont = Typeface.createFromAsset( getAssets(), "VAGLight.ttf" );  
	    
	glitch = new Glitch("197-764ef7f4f676f4b53819e52ea7cca4c65badf353", "twotallglitch://auth");
  }
  
  private SharedPreferences getPreferences()
  {
	  return getSharedPreferences("Glitch", Context.MODE_PRIVATE );
  }
    
  public void PreferencePutString(String key, String value)
  {
		SharedPreferences.Editor editor = getPreferences().edit();
	    editor.putString(key, value);
	    editor.commit();
  }  
  
  public String PreferenceGetString( String key, String defValue )
  {
		return getPreferences().getString(key, defValue);
  }
  
  public void Download( String url, ImageView imageView, int nType )
  {
	  if( imageView != null )
		  imageView.setTag(nType);

	  imageView.setVisibility(View.INVISIBLE);
	  m_downloader.download(url, imageView);
  }
  
  private class GlobalExceptionHandler implements UncaughtExceptionHandler
  {
	  private UncaughtExceptionHandler oldHandler;
	  
	  GlobalExceptionHandler(UncaughtExceptionHandler oldHandler) {
		 this.oldHandler = oldHandler;
	  }
	  
	  public void uncaughtException(Thread thread, Throwable throwable) {
		  oldHandler.uncaughtException(thread, throwable);
	  }
  }
};
