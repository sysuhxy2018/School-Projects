package com.example.myapplication.ui.main;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.R;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MainPagerAdapter extends PagerAdapter {

    private Context mContext;
    private int tabNums;
    // It is strange that we must apply a collection to cache the views or it may be removed from the viewPager during scrolling
    private Map<Integer, View> pageMap = null;

    @Override
    public int getCount() {
        return tabNums;
    }

    public MainPagerAdapter(Context context, int cnt) {
        mContext = context;
        tabNums = cnt;
        pageMap = new HashMap<>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.activity_main, container, false);
        if (pageMap.containsKey(position)) {
            container.addView(this.getView(position), 0);
            return this.getView(position);
        }
        else {
            container.addView(itemView, 0);
            pageMap.put(position, itemView);
            return itemView ;
        }

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

    // Solution to the problem when notifyDataSetChanged the page is still not refreshed
    @Override
    public int getItemPosition(Object object) {
        for (Map.Entry<Integer, View> mk : pageMap.entrySet()) {
            if (mk.getValue() == object)
                return mk.getKey();
        }
        return POSITION_NONE;
    }

    public View getView(int pos) {
        return pageMap.get(pos);
    }

    public void removeView(int pos) {
        pageMap.remove(pos);
        Map<Integer, View> nPageMap = new HashMap<>();
        for (Map.Entry<Integer, View> mk : pageMap.entrySet()) {
            if (mk.getKey() < pos)
                nPageMap.put(mk.getKey(), mk.getValue());
            else if (mk.getKey() > pos)
                nPageMap.put(mk.getKey() - 1, mk.getValue());
        }
        this.pageMap = nPageMap;
        tabNums--;
        this.notifyDataSetChanged();
    }
}