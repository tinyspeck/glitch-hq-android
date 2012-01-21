
package com.twotalltotems.glitch;

import java.io.File;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

public class Util
{
  static public Dialog Alert(Activity context, String msg, String title )
  {
    return Alert(context,msg,title,false,null);
  }      

  static public Dialog Alert(Activity context, int msgID, int titleID )
  {
      String szMsg = context.getResources().getString( msgID );
      String szTitle = null;
      if( titleID > 0 )
         szTitle = context.getResources().getString( titleID );
        
      return Alert( context, szMsg, szTitle );
  }
  
  static public Dialog Alert(Activity context, String msg, String title, boolean bYesNo, DialogInterface.OnClickListener lsner )
  {
    AlertDialog.Builder blder = new AlertDialog.Builder(context);
    blder.setTitle(title);
    blder.setMessage( msg );
    blder.setPositiveButton( R.string.str_ok, lsner );
    if( bYesNo )
      blder.setNegativeButton( R.string.str_cancel, lsner );
    
    return blder.show();
  }      

  static public Dialog Alert(Activity context, int msgID, int titleID, boolean bYesNo, DialogInterface.OnClickListener lsner )
  {
    String szMsg = context.getResources().getString( msgID );
    String szTitle = null;
    if( titleID > 0 )
       szTitle = context.getResources().getString( titleID );

    return Alert( context, szMsg, szTitle, bYesNo, lsner );
  }
  
  static public int[] GetRandomTops( int setNumber, int topNumber, int nExclude )
  {
	  int[] myNumbs = new int[topNumber];
	  int cur;
	  int i,j;
	  
	  for( i=0; i < topNumber; i++ )
	  {
		  do 
		  {
			  cur = (int)( Math.random() * setNumber );
			  for( j=0; j<i; j++ )
				  if( myNumbs[j] == cur )
					  break;
		  } while( j < i || cur == nExclude );	  
		  myNumbs[i] = cur;
	  }
	  return myNumbs;
  }
  
  static public boolean MFileExists(String localFolder, String fileName)
  {
	  File myFile = new File( localFolder + fileName );
      return ( myFile.length() > 0 )? true: false;	
  }

  static public String LoadRawFile( Context ctx, int nResId )
  {
	  InputStream ins = ctx.getResources().openRawResource(nResId);
	  byte b[]=null;                       
	  
	  try{
	    int nSize = ins.available();
	    b=new byte[nSize];
	    ins.read(b); 
	    ins.close();
	  }catch(Exception e)
	  {
	    e.printStackTrace();
	  }
	  return new String(b);
  }
  
  public static String GetFileNameFromPath( String path )
  {
	  int nIndex1 = path.lastIndexOf( File.separator );
	  int nIndex2 = path.lastIndexOf( "." );
	  if( nIndex2 < 0 )
		  nIndex2 = path.length();
	
	  return path.substring(nIndex1+1,nIndex2);
  }
  
  static Dialog ShowWaiter( Context context, int msgID )
  {
	  ProgressDialog dialog = ProgressDialog.show(context, "",
              "Please wait for few seconds...", true);
	  
/*	  ProgressDialog waitCatalogDlg = new ProgressDialog(context);
      waitCatalogDlg.setTitle( context.getResources().getString( R.string.app_name ) );
      waitCatalogDlg.setMessage( context.getResources().getString( msgID ) );
      waitCatalogDlg.setIndeterminate( true );
      waitCatalogDlg.show();  
      return waitCatalogDlg;  */
	  
	  return dialog;
  }

  public static PopupWindow showPopup( Activity act, int nLayout, boolean bDropDown, View vAnchor, int offsetX, int offsetY  )
  {
	  final PopupWindow pw = new PopupWindow( act );
	  
	  LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  View v = inflater.inflate( nLayout, null, false );
	  
	  pw.setContentView( v );
	  pw.setFocusable(true);				    
	  pw.setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	  
	  pw.setOutsideTouchable(true);
	  pw.setTouchable(true);				    
	  pw.setBackgroundDrawable(new BitmapDrawable());
	  
	  if( bDropDown )
	  	  pw.showAsDropDown( vAnchor,  offsetX, offsetY );
	  else
		  pw.showAtLocation(vAnchor.getRootView(), Gravity.NO_GRAVITY, offsetX, offsetY );  
	  
	  return pw;
  }

  public static Dialog OpenDialog( Activity act, int titleID, int layoutID )
  {
	  Dialog d = new Dialog(act);
	  Window window = d.getWindow();
	  window.addFlags( WindowManager.LayoutParams.FLAG_DIM_BEHIND );
	  
  	  if( titleID > 0 )
  		  d.setTitle( act.getResources().getString( titleID ) );
  	  
	  d.setContentView( layoutID );
	  
	  return d;
  }
  
  	public static void delayedFlingOfScrollView(final ScrollView sv, final int velocityY, int delay)
  	{
  		final Handler handler = new Handler();
  		
  		handler.postDelayed(new Runnable() {
  			public void run() {
  				sv.fling(velocityY);
  			}
  		}, delay);
  	}
  
  	public static void startAlphaAnimation(View v, int duration, float from, float to)
	{
		 AlphaAnimation animation = new AlphaAnimation(from,to);
		 
		 animation.setDuration(duration);	 
		 animation.setRepeatMode( TranslateAnimation.REVERSE );
		 animation.setRepeatCount( TranslateAnimation.INFINITE );
		 
		 v.startAnimation(animation);
	}
	
	public static void startScaleAnimation( View v, int duration )
	{
		ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f);
		animation.setDuration( duration );
		
		v.startAnimation(animation);
	}
	
	public static void startTranslateAnimation( View v, int duration )
	{
		 TranslateAnimation animation = new TranslateAnimation(
				 Animation.RELATIVE_TO_PARENT, -1.1f, Animation.RELATIVE_TO_PARENT, 1.0f,
				 Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
		        );
		 
		 animation.setDuration( duration );	 
		 animation.setRepeatMode( TranslateAnimation.RESTART );
		 animation.setRepeatCount( TranslateAnimation.INFINITE );
		 
		 v.startAnimation(animation);
	}

	public static DisplayMetrics GetScreenSize( Activity act )
	{
		  DisplayMetrics metrics = new DisplayMetrics();
		  act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		  return metrics;
	}

	public static int GetScreenSizeAttribute( Activity act )
	{
		  DisplayMetrics metrics = new DisplayMetrics();
		  act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		  if( metrics.widthPixels >= 720 )
			  return 2;
		  if( metrics.widthPixels >= 480 )
			  return 1; 
		  return 0;
	}
	
	public static String TimeToString( int nSec )
	{
		if( nSec < 60 )
			return "just now";
		if( nSec >= 48 * 3600 )
			return "" + nSec / ( 24 * 3600 ) + " days"; 
		else if ( nSec >= 24 * 3600 )
			return "1 day"; 
		else if( nSec < 3600 )
			return "" + ( nSec / 60 ) + " mins";
		else
			return "" + (nSec / 3600) + " hrs";
	}
	
	public static String TimeToString( int nSec, boolean bSimple )
	{
		int timeH = nSec/3600;
		int timeM = ( nSec - timeH * 3600 ) / 60;
		int timeS = nSec - timeH * 3600 - timeM * 60;

		if( timeH >= 24 )
		{
			int days = timeH / 24; 
			timeH -= days * 24;

			return bSimple? ( "" + days + " d " + timeH + " h " + timeM + " m " + timeS + " s" ) : ( "" + days + " days " + timeH + " hr " + timeM + " min " + timeS + " sec" );
		}
		if( timeH > 0 )
			return bSimple? ("" + timeH + " h " + timeM + " m " + timeS + " s") : ("" + timeH + " hr " + timeM + " min " + timeS + " sec") ;
		else
			return bSimple? ("" + timeM + " m " + timeS + " s") : ("" + timeM + " min " + timeS + " sec") ;
	}
	
	public static void showProgress( Activity act, View v, TextView vText, int remainTime, int totalTime, long curTime ) 
	{
 	    int nTotalWidth = act.getWindowManager().getDefaultDisplay().getWidth() - 20; 

 	    ViewGroup.LayoutParams params = v.getLayoutParams();

 	    long nElapse = (long)( System.currentTimeMillis()/1000 ) - curTime;
 	    int remain = (int)( remainTime - nElapse );
 	    if( remain < 0 )
 	    	remain = 0;
 	    
 	    params.width = 20 + ( nTotalWidth - 20 ) * ( totalTime - remain ) / totalTime ;
 	    
 	    v.setLayoutParams(params);
 	    if( v.getTag() == null )
 	    {
 	    	Util.startAlphaAnimation( v, 1000, 1, (float)0.5 );
 	    	v.setTag(true);
 	    }
 	    if( vText!=null )
 	    {
 	    	if( remain == 0 )
 	    	{	
 	    		v.findViewById( R.id.learning_progress_end ).setVisibility(View.VISIBLE);
 	 	    	vText.setText( R.string.str_progress_done );
 	    	}else
 	    		vText.setText( Util.TimeToString(remain, true) );
 	    }
	}
};
