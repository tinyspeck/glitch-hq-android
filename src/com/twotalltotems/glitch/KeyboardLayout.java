package com.twotalltotems.glitch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class KeyboardLayout extends RelativeLayout
{
	int m_lastErrorState = View.GONE;

	public KeyboardLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
//      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		inflater.inflate(R.layout.main, this);
	}
    
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    	int errorState = this.findViewById( R.id.tv_login_error ).getVisibility();
    	if( errorState != m_lastErrorState )
    	{
    		m_lastErrorState = errorState;
    	} else {
    		
    		final int height = MeasureSpec.getSize(heightMeasureSpec);
    		Activity activity = (Activity)getContext();
    		Rect rect = new Rect();
    		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
    		final int statusBarHeight = rect.top;
    		final int screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();
    		final int diff = (screenHeight - statusBarHeight) - height;            

    		// Assume all soft keyboards are at least 128 pixels high
            if (diff > 128) {
	            // Keyboard is shown
	        	this.findViewById( R.id.copyright).setVisibility(View.GONE);
	        } else {
	            // Keyboard is hidden
	        	this.findViewById( R.id.copyright).setVisibility(View.VISIBLE);        	
	        }
    	}
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}