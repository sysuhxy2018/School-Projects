package com.example.myapplication.ui.main;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.example.myapplication.R;

import java.util.HashMap;
import java.util.Map;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends PagerAdapter {

    private Context mContext;
    private int tabNums;
    // It is strange that we must apply a collection to cache the views or it may be removed from the viewPager during scrolling
    private Map<Integer, View> pageMap;

    @Override
    public int getCount() {
        return tabNums;
    }

    public SectionsPagerAdapter(Context context, int cnt) {
        mContext = context;
        tabNums = cnt;
        pageMap = new HashMap<>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = null;
        switch (position) {
            case 0 : itemView = inflater.inflate(R.layout.fragment_today, container, false); break;
            case 1 : itemView = inflater.inflate(R.layout.fragment_weekly, container, false); break;
            case 2 : itemView = inflater.inflate(R.layout.fragment_photos, container, false); break;
        }
        container.addView(itemView);
        pageMap.put(position, itemView);
        return itemView ;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
        pageMap.remove(position);
    }

    @Override
    public boolean isViewFromObject (View view, Object object) {
        return view == object;
    }

    public View getView(int pos) {
        return pageMap.get(pos);
    }

}