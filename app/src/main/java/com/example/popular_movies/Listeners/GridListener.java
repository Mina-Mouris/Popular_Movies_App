package com.example.popular_movies.Listeners;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.popular_movies.ActivityDetails;
import com.example.popular_movies.MainActivity;
import com.example.popular_movies.R;
import com.example.popular_movies.fragments.GridFragment;
import com.example.popular_movies.fragments.detailsFragmet;
import com.example.popular_movies.models.detailsModel;
import com.example.popular_movies.utils.Const;
import com.example.popular_movies.utils.ConstStrings;

import java.util.ArrayList;

/**
 * Created by geekpro on 9/29/15.
 */
public class GridListener implements AdapterView.OnItemClickListener {

    private Context mContext;
    private GridView mgridview;
    // references to our images
    private ArrayList<detailsModel> mThumbIds;

    public GridListener(Context context, GridView gridView ,ArrayList<detailsModel> mThumbIds) {
        mContext = context;
        mgridview = gridView;
        this.mThumbIds = mThumbIds;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GridFragment.last_position = position;

        mgridview.setSelected(true);
        mgridview.setSelection(position);
        mgridview.setItemChecked(position, true);

        int screenOrientation = mContext.getResources().getConfiguration().orientation;

        Bundle bundle = new Bundle();
        bundle.putString(ConstStrings.OMG_ID, mThumbIds.get(position).getId());

        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            mContext.startActivity(new Intent(mContext, ActivityDetails.class).putExtras(bundle));
        } else if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE && Const.getDensity() < 600) {
            mContext.startActivity(new Intent(mContext, ActivityDetails.class).putExtras(bundle));
        } else if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE && Const.getDensity() > 600) {
            detailsFragmet fragment = new detailsFragmet();
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.details, fragment)
                    .commit();
        }
    }
}
