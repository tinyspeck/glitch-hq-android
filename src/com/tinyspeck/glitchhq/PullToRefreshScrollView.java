package com.tinyspeck.glitchhq;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;



import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class PullToRefreshScrollView extends ScrollView {

    private static final int TAP_TO_REFRESH = 1;
    private static final int PULL_TO_REFRESH = 2;
    private static final int RELEASE_TO_REFRESH = 3;
    private static final int REFRESHING = 4;
    private static final int SHOW_FOOTER = 5;
    
    private static final int SCROLL_STATE_TOUCH_DOWN=1;
    private static final int SCROLL_STATE_TOUCH_MOVE=2;
    private static final int SCROLL_STATE_TOUCH_RELEASE=3;
    
    private int m_scrollState;
    
    private static final String TAG = "PullToRefreshScrollView";

    private OnRefreshListener mOnRefreshListener;

    /**
     * Listener that will receive notifications every time the list scrolls.
     */

    private View mRefreshView;
    private TextView mRefreshViewText, mRefreshViewText_bottom;
    private ImageView mRefreshViewImage;
    private ProgressBar mRefreshViewProgress, mRefreshViewProgress_bottom;
    private TextView mRefreshViewLastUpdated;
    private View m_rootImage;

    private int mRefreshState;
    private int m_startScrollPos = 0;
    private boolean m_bBottomHasMore=true;
    
    private RotateAnimation mFlipAnimation;
    private RotateAnimation mReverseFlipAnimation;

    private int mRefreshViewHeight;
    private int mRefreshOriginalTopPadding;
    private int mLastMotionY;
    
    public PullToRefreshScrollView(Context context) {
        super(context);
    }

    public PullToRefreshScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(Activity context) {
        // Load all of the animations we need in code rather than through XML
        mFlipAnimation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(250);
        mFlipAnimation.setFillAfter(true);
        mReverseFlipAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(250);
        mReverseFlipAnimation.setFillAfter(true);

        mRefreshView = context.findViewById( R.id.pull_to_refresh_header );
        
        mRefreshViewText = (TextView) context.findViewById( R.id.pull_to_refresh_text );
        m_rootImage = (View) context.findViewById( R.id.list_footer );
        mRefreshViewText_bottom = (TextView) context.findViewById( R.id.pull_to_load_more_label );
        
        mRefreshViewImage = (ImageView) context.findViewById( R.id.pull_to_refresh_image );
        mRefreshViewProgress = (ProgressBar) context.findViewById( R.id.pull_to_refresh_progress );
        mRefreshViewProgress_bottom = (ProgressBar) context.findViewById( R.id.pull_to_refresh_progress_bottom );
        		
        mRefreshViewLastUpdated = (TextView) context.findViewById( R.id.pull_to_refresh_updated_at );
        
        mRefreshViewImage.setMinimumHeight(50);
        mRefreshOriginalTopPadding = mRefreshView.getPaddingTop();

        mRefreshState = TAP_TO_REFRESH;

//      mRefreshView.setVisibility(View.GONE);
//      m_rootImage.setVisibility(View.GONE);
        
        measureView( mRefreshView );
        
        mRefreshViewHeight = mRefreshView.getMeasuredHeight();
        
        setSmoothScrollingEnabled(true);
        
        //setOverScrollMode(  OVER_SCROLL_IF_CONTENT_SCROLLS );
    }

    /**
     * Register a callback to be invoked when this list should be refreshed.
     * 
     * @param onRefreshListener The callback to run.
     */
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public void setBottomHasMore( boolean bHasMore )
    {
    	m_bBottomHasMore = bHasMore;
    }
    
    /**
     * Set a text to represent when the list was last updated. 
     * @param lastUpdated Last updated at.
     */
    public void setLastUpdated(CharSequence lastUpdated) {
        if (lastUpdated != null) {
            mRefreshViewLastUpdated.setVisibility(View.VISIBLE);
            mRefreshViewLastUpdated.setText(lastUpdated);
        } else {
            mRefreshViewLastUpdated.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                m_scrollState = SCROLL_STATE_TOUCH_RELEASE;
            	if (!isVerticalScrollBarEnabled()) {
                    setVerticalScrollBarEnabled(true);
                }
                if ( mRefreshState != REFRESHING ) 
                {
                	if( mRefreshState == SHOW_FOOTER )
                	{	
                		Log.i("state", "to refresh");
                        mRefreshState = REFRESHING;
                        prepareForRefresh(false);
                        onRefresh(false);
                	}
                    if ((mRefreshView.getBottom() > mRefreshViewHeight
                            || mRefreshView.getTop() >= 0)
                            && mRefreshState == RELEASE_TO_REFRESH) {
                        // Initiate the refresh
                        mRefreshState = REFRESHING;
                        prepareForRefresh(true);
                        onRefresh(true);
                    } else if ( mRefreshState == PULL_TO_REFRESH )                        //  (mRefreshView.getBottom() < mRefreshViewHeight
                    {																	  //  || mRefreshView.getTop() < 0) {
                        resetHeader(true);
                    }
                }else
                	resetHeaderPadding();
                break;
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = y;
                m_scrollState = SCROLL_STATE_TOUCH_DOWN;
                m_startScrollPos = getScrollY();
                break;
                
            case MotionEvent.ACTION_MOVE:
                m_scrollState = SCROLL_STATE_TOUCH_MOVE;
            	View v = getChildAt(getChildCount()-1);
            	if( m_bBottomHasMore && v.getBottom() ==  getHeight() + m_startScrollPos && ( event.getY() - mLastMotionY < 0 ) )
            	{
            		Log.i("state", "set show footer");
            		
            		mRefreshState = SHOW_FOOTER;
            		m_rootImage.setVisibility( View.VISIBLE );
            	}
            	else	
            		if( m_startScrollPos == 0 )
            			applyHeaderPadding(event);
                break;
        }
        return super.onTouchEvent(event);
    }

    private void applyHeaderPadding(MotionEvent ev) {
        // Workaround for getPointerCount() which is unavailable in 1.5
        // (it's always 1 in 1.5)

    	int pointerCount = 1;
        try {
            Method method = MotionEvent.class.getMethod("getPointerCount");
            pointerCount = (Integer)method.invoke(ev);
        } catch (NoSuchMethodException e) {
            pointerCount = 1;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (IllegalAccessException e) {
            System.err.println("unexpected " + e);
        } catch (InvocationTargetException e) {
            System.err.println("unexpected " + e);
        }

        for (int p = 0; p < pointerCount; p++) {
//            if (mRefreshState == RELEASE_TO_REFRESH) 
            {
                if (isVerticalFadingEdgeEnabled()) {
                    setVerticalScrollBarEnabled(false);
                }

//               int historicalY = (int) ev.getY();   // ev.getHistoricalY(p);

                int topPadding = (int) ( ( ev.getY() - mLastMotionY ) / 1.7 );

                if( topPadding > 0 && mRefreshState != RELEASE_TO_REFRESH )
                {
                	mRefreshView.setVisibility(View.VISIBLE);
                	mRefreshViewImage.setVisibility(View.VISIBLE);

                	Log.i("Pading","motionYYY" + m_startScrollPos +  " top: " + mRefreshView.getPaddingTop() + " new: " + topPadding  + " hY " + ev.getY() + " lastY: " + mLastMotionY );
                	
	                mRefreshViewText.setText(R.string.pull_to_refresh_release_label);
	                mRefreshViewImage.clearAnimation();
	                mRefreshViewImage.startAnimation(mFlipAnimation);
	                mRefreshState = RELEASE_TO_REFRESH;
                }   
	            if( mRefreshState == RELEASE_TO_REFRESH )
	            {	
	                mRefreshView.setPadding(
	                        mRefreshView.getPaddingLeft(),
	                        topPadding,
	                        mRefreshView.getPaddingRight(),
	                        mRefreshView.getPaddingBottom()); 
	            }
            }
        }
    }

    /**
     * Sets the header padding back to original size.
     */
    private void resetHeaderPadding() {

        mRefreshView.setPadding(
                mRefreshView.getPaddingLeft(),
                mRefreshOriginalTopPadding,
                mRefreshView.getPaddingRight(),
                mRefreshView.getPaddingBottom());
    }

    /**
     * Resets the header to the original state.
     */
    private void resetHeader( boolean scrollToTop ) 
    {
        if (mRefreshState != TAP_TO_REFRESH) {
            mRefreshState = TAP_TO_REFRESH;

//        	mRefreshView.setVisibility(View.GONE);
            resetHeaderPadding();

            // Set refresh view text to the pull label
            mRefreshViewText.setText(R.string.pull_to_refresh_tap_label);
            mRefreshViewText_bottom.setText(R.string.pull_to_load_more_label);
            
//            mRefreshViewText.setVisibility(View.GONE);
            // Replace refresh drawable with arrow drawable
            mRefreshViewImage.setImageResource(R.drawable.ic_pulltorefresh_arrow);
            // Clear the full rotation animation
            mRefreshViewImage.clearAnimation();
            // Hide progress bar and arrow.
            mRefreshViewImage.setVisibility(View.GONE);
            mRefreshViewProgress.setVisibility(View.GONE);
	        
            if( scrollToTop )
            	this.scrollTo( 0, 0 );
            else
            	this.fullScroll( ScrollView.FOCUS_DOWN );
        }
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0,
                0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    protected void onScrollChanged( int x, int y, int oldx, int oldy )
    {
    	Log.i("scroll", " x " + x + " y: " + y + " oldx: " + oldx + " oldy: " + oldy  +  " get top: " + mRefreshView.getTop() + " bottom: " + mRefreshView.getBottom() + " vH: " + mRefreshViewHeight );

    	super.onScrollChanged(x, y, oldx, oldy );

/*    	if( y == 0 && m_startScrollPos > 0 && m_scrollState == SCROLL_STATE_TOUCH_RELEASE )
    	{
    		super.smoothScrollTo(x, 80 );
    	} */
    	
        if ( mRefreshState == PULL_TO_REFRESH || mRefreshState == RELEASE_TO_REFRESH )      // mRefreshState != REFRESHING) 
        {
        	{
        		mRefreshViewImage.setVisibility(View.VISIBLE);
        		if (  ( y <= 15                                           
                || mRefreshView.getTop() >= 0)
                && mRefreshState != RELEASE_TO_REFRESH)
        		{
		            mRefreshViewText.setText(R.string.pull_to_refresh_release_label);
		            mRefreshViewImage.clearAnimation();
		            mRefreshViewImage.startAnimation(mFlipAnimation);
		            mRefreshState = RELEASE_TO_REFRESH;
        		} else if (  y > 15                                         
        				&& mRefreshState != PULL_TO_REFRESH)
        		{
        			mRefreshViewText.setText(R.string.pull_to_refresh_pull_label);
        			if (mRefreshState != TAP_TO_REFRESH) {
        				mRefreshViewImage.clearAnimation();
        				mRefreshViewImage.startAnimation(mReverseFlipAnimation);
        			}
        			mRefreshState = PULL_TO_REFRESH;
        		} 
        	}
        } 
    }

    public void prepareForRefresh( boolean bTop )
    {
    	if( bTop )
    	{
	        resetHeaderPadding();
	
	        mRefreshViewImage.setVisibility(View.GONE);
	        // We need this hack, otherwise it will keep the previous drawable.
	        mRefreshViewImage.setImageDrawable(null);
	        mRefreshViewProgress.setVisibility(View.VISIBLE);
	
	        // Set refresh view text to the refreshing label
	        //mRefreshViewText.setVisibility(View.VISIBLE);
	        mRefreshViewText.setText(R.string.pull_to_refresh_refreshing_label);
    	}else
    	{
	        mRefreshViewProgress_bottom.setVisibility(View.VISIBLE);
	        mRefreshViewText_bottom.setText(R.string.pull_to_refresh_refreshing_label);
    	}
        mRefreshState = REFRESHING;
    }

    public void onRefresh(boolean bTop) {
        Log.d(TAG, "onRefresh");

        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh(bTop);
        }
    }

    public void onRefreshComplete(CharSequence lastUpdated) {
        setLastUpdated(lastUpdated);
        onRefreshComplete();
    }

    public void onRefreshComplete() {        
        Log.d(TAG, "onRefreshComplete");

        if( m_rootImage.getVisibility() == View.VISIBLE )
        {
//        	m_rootImage.setVisibility( View.GONE );
	        mRefreshViewProgress_bottom.setVisibility(View.GONE);
        	
            resetHeader(false);
        }else
        	resetHeader(true);
    }

    public interface OnRefreshListener {
        public void onRefresh(boolean bTop);
    }
}
