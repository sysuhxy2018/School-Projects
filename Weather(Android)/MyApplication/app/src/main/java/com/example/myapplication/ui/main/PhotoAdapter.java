package com.example.myapplication.ui.main;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private ArrayList<String> photoUrls;
    private Context context;

    public PhotoAdapter(ArrayList<String> photoUrls, Context context) {
        this.photoUrls = photoUrls;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.photo_item, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String url = photoUrls.get(position);
//        Glide.with(context).load(url).fitCenter().centerCrop().into(holder.im);
        Picasso.get().load(url).fit().centerCrop().into(holder.im, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
                /**
                 * It seems sometimes picasso or glide fail to fetch specific resources on https web.
                 * Had to exclude these sites on google custom search.
                 */
                Log.e("error", e.getMessage());
            }
            });
    }

    @Override
    public int getItemCount() {
        return photoUrls.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView im;
        public ViewHolder(View view) {
            super(view);
            im = (ImageView) view.findViewById(R.id.photoItem);
        }
    }

}
