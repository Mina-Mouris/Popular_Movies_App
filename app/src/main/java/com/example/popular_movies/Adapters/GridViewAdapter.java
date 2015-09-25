package com.example.popular_movies.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.popular_movies.R;
import com.example.popular_movies.fragments.GridFragment;
import com.example.popular_movies.models.detailsModel;
import com.example.popular_movies.utils.Const;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {
    private Context mContext;
    // references to our images
    private ArrayList<detailsModel> mThumbIds;

    public GridViewAdapter(Context c, ArrayList<detailsModel> mThumbIds) {
        mContext = c;
        this.mThumbIds = mThumbIds;
    }

    public int getCount() {
        return mThumbIds.size();
    }

    public Object getItem(int position) {
        return mThumbIds.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        viewHolderItem holder;

        if (convertView == null) {

            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.grid_item, parent, false);

            // well set up the ViewHolder
            holder = new viewHolderItem();
            holder.image = (ImageView) convertView.findViewById(R.id.grid_item_image);

            convertView.setTag(holder);

        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            holder = (viewHolderItem) convertView.getTag();
        }

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        String Resolution = sharedPrefs.getString(mContext.getString(R.string.pref_resolution_key),mContext.getString(R.string.pref_Res_high));

        GridFragment.setResolution(Resolution);

        if(Resolution.equals("high")){
            Picasso.with(mContext)
                    .load(Const.URL_IMAGE_PATH_high + mThumbIds.get(position).getPosterImage_url())
                    .placeholder(R.drawable.load)
                    .error(R.drawable.wrong)
                    .into(holder.image);
        }else if(Resolution.equals("medium")){
            Picasso.with(mContext)
                    .load(Const.URL_IMAGE_PATH_medium + mThumbIds.get(position).getPosterImage_url())
                    .placeholder(R.drawable.load)
                    .error(R.drawable.wrong)
                    .into(holder.image);
        }else if(Resolution.equals("low")){
            Picasso.with(mContext)
                    .load(Const.URL_IMAGE_PATH_low + mThumbIds.get(position).getPosterImage_url())
                    .placeholder(R.drawable.load)
                    .error(R.drawable.wrong)
                    .into(holder.image);
        }

        return convertView;
    }

    static class viewHolderItem {
        ImageView image;
    }
}