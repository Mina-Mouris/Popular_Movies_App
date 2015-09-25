package com.example.popular_movies.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popular_movies.R;
import com.example.popular_movies.models.reviewModel;
import com.example.popular_movies.models.trailerModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by geekpro on 9/8/15.
 */
public class ReviewListAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<reviewModel> Labels;

    public ReviewListAdapter(Context c , ArrayList<reviewModel> list) {
        mContext = c ;
        Labels = list;
    }

    @Override

    public int getCount() {
        return Labels.size();
    }

    @Override
    public Object getItem(int position) {
        return Labels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolderItem holder;

        if (convertView == null) {

            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.review_list_item, parent, false);

            // well set up the ViewHolder
            holder = new viewHolderItem();
            holder.image = (ImageView) convertView.findViewById(R.id.icon);
            holder.author = (TextView) convertView.findViewById(R.id.name);
            holder.content = (TextView) convertView.findViewById(R.id.content);

            convertView.setTag(holder);

        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            holder = (viewHolderItem) convertView.getTag();
        }

        Picasso.with(mContext).load(R.drawable.ic_stars_black_24dp).into(holder.image);
        holder.author.setText(Labels.get(position).getAuthor());
        holder.content.setText(Labels.get(position).getContent());


        return convertView;
    }

    static class viewHolderItem {
        ImageView image;
        TextView author;
        TextView content;
    }
}

