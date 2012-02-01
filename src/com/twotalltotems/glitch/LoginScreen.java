package com.twotalltotems.glitch;

import com.flurry.android.FlurryAgent;
import com.tinyspeck.android.GlitchSessionDelegate;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginScreen extends Activity implements GlitchSessionDelegate
{
   private MyApplication m_application;    
   private Button m_btnLogin;
   private EditText m_email;
   private EditText m_password;
   private String m_sUsername, m_sPassword;
   private View m_background;
   private View m_loginSpinner;
   private TextView m_errorMsg;
   private TextView m_tv_forgot_password;
   private final int HomeScreenFLAG = 101;
   
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
     super.onCreate(savedInstanceState);

     Bundle bundle = getIntent().getExtras();    

     m_application = (MyApplication)getApplicationContext();
     m_application.Init( this );

     setContentView( R.layout.login);  
     setTitle( getResources().getString( R.string.str_main_title ) );

     m_loginSpinner = (View) findViewById(R.id.loginSpinner);

     ((TextView)findViewById( R.id.tv_login )).setTypeface( m_application.m_vagFont );  
     
	 m_btnLogin = (Button)findViewById( R.id.btnLogin );
	 m_btnLogin.setTypeface( m_application.m_vagFont );  

	 setupTitlebar();
	
	 m_email = (EditText)findViewById( R.id.email );
	 m_password = (EditText) findViewById(R.id.password);

	 m_email.setTypeface( m_application.m_vagLightFont );  
	 m_password.setTypeface( m_application.m_vagLightFont );  
	 
	 m_errorMsg = (TextView) findViewById(R.id.tv_login_error);
	 
	 m_tv_forgot_password = (TextView) findViewById(R.id.tv_forgot_password );

	 TextView tv = (TextView) findViewById(R.id.copyright );
	 tv.setText( Html.fromHtml("Glitch is built by <a href='http://tinyspeck.com'>Tiny Speck</a> <br>Copyright @ 2011 Tiny Speck<br>All rights reserved") );
	 tv.setMovementMethod(LinkMovementMethod.getInstance());
	 tv.setTypeface( m_application.m_vagLightFont );
	 
	 m_background = findViewById( R.id.LayoutBack );

	 m_btnLogin.setOnClickListener( new OnClickListener(){
		public void onClick(View arg0) {

			m_sUsername = m_email.getText().toString();
			m_sPassword = m_password.getText().toString();

//			m_sUsername = "chris@twotalltotems.com";
//			m_sPassword = "twotalltotems";
//			m_sUsername = "mmmdddjjj@gmail.com";
//			m_sPassword = "millet";
//			m_sUsername = "DaneECooper+test@gmail.com";
//			m_sPassword = "apple1224";

			if( m_sUsername.length() <= 0 )
			{
				m_errorMsg.setVisibility(View.VISIBLE);
				m_errorMsg.setText( LoginScreen.this.getResources().getString(R.string.error_login_username) );
				FlurryAgent.logEvent("Login - Didn't provide email");
				return;
			}
			if( m_sPassword.length() <= 0 )
			{
				m_errorMsg.setVisibility(View.VISIBLE);
				m_errorMsg.setText( LoginScreen.this.getResources().getString(R.string.error_login_password) );
				FlurryAgent.logEvent("Login - Didn't provide password");
				return;
			} 

			m_loginSpinner.setVisibility(View.VISIBLE);
			m_errorMsg.setVisibility(View.GONE);
		    m_application.glitch.login( m_sUsername, m_sPassword, LoginScreen.this );
		}
	 });
	 
	 m_tv_forgot_password.setOnClickListener( new OnClickListener(){
		public void onClick(View arg0) {
			
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.glitch.com/reset"));
			startActivity(browserIntent);
		}
	 });
	 
	 Util.startTranslateAnimation( m_background, 40000 );
	 
     String user = m_application.PreferenceGetString("username","");     
     String password = m_application.PreferenceGetString("password","");     

     if( !user.equalsIgnoreCase("")  && !password.equalsIgnoreCase("")  )
     {
         findViewById( R.id.login_layout).setVisibility(View.INVISIBLE);    	 
		 m_loginSpinner.setVisibility(View.VISIBLE);
		 m_application.glitch.login( user, password, LoginScreen.this );    	 
    	 return;
     }
	 
   }  
   
	private void setupTitlebar()
	{
		ImageView v = (ImageView)findViewById( R.id.img_title_bar );		
		if( v != null )
		{
			int [] nRes = { R.drawable.navbar_l, R.drawable.navbar_l, R.drawable.navbar_xl };
			int nType = Util.GetScreenSizeAttribute( this );
			v.setImageResource( nRes[nType] );
		}
	}

   @Override
   public void onResume()
   {
//     Log.i(LogTag.TAG_UI,"onResume");
     super.onResume();
   }

   @Override
   public void onStart()
   {
	   super.onStart();
	   FlurryAgent.onStartSession(this, "WCCPI1W5AYGMARQV2QQL");
   }
   
   @Override
   public void onStop()
   {
	 super.onStop();
	 FlurryAgent.onEndSession(this);
   }
   
   @Override
   public void onDestroy()
   {
	   super.onDestroy();
   }
   
	public void glitchLoginSuccess() 
	{
		m_loginSpinner.setVisibility(View.INVISIBLE);
	
		if( m_sUsername != null && m_sPassword != null )
		{
			m_application.PreferencePutString("username", m_sUsername);
			m_application.PreferencePutString("password", m_sPassword);
		}
		
		finish();	
		Intent intent = new Intent();
		intent.setClass(LoginScreen.this, HomeScreen.class);
		startActivity(intent);
	}

	public void glitchLoginFail() {
		
		m_loginSpinner.setVisibility(View.INVISIBLE);
		findViewById( R.id.login_layout).setVisibility(View.VISIBLE);    	 
		
		m_errorMsg.setVisibility(View.VISIBLE);
		m_errorMsg.setText( LoginScreen.this.getResources().getString(R.string.error_login_credential) );
	}

    public void glitchConnectionError()
    {
    	m_loginSpinner.setVisibility(View.INVISIBLE);
    	Util.Alert(this, R.string.error_connection_message, R.string.error_connection_title );
    }
	
   protected  void onActivityResult(int requestCode, int resultCode, Intent data) 
	{  
	   	Log.i( "onResult", " result: " + requestCode );
	   	if( requestCode == HomeScreenFLAG )
	   	{
	   		if( resultCode < 0 )
	   		{
	   		}
	   	}
	}

	public void glitchLoggedOut() {
		
	}
}
