package com.twotalltotems.glitch;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListViewUtil 
{
        public static int getListViewHeightBasedOnChildren(ListView listView) 
        {
            ListAdapter listAdapter = listView.getAdapter(); 
            if (listAdapter == null) {
                return -1;
            }

            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                Log.i( "measure",  " i: " + i + " size: " + listItem.getMeasuredHeight() );
                totalHeight += listItem.getMeasuredHeight();
            }
            return totalHeight;
        }

        public static void setListViewHeight( ListView listView, int totalHeight )
        {
            ListAdapter listAdapter = listView.getAdapter(); 
            if (listAdapter == null) {
                return;
            }

        	ViewGroup.LayoutParams params = listView.getLayoutParams();
	        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	        listView.setLayoutParams(params);
        }
}	