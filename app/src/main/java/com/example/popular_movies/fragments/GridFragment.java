package com.example.popular_movies.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.popular_movies.ActivityDetails;
import com.example.popular_movies.Adapters.GridViewAdapter;
import com.example.popular_movies.Listeners.EndlessScrollListener;
import com.example.popular_movies.MainActivity;
import com.example.popular_movies.R;
import com.example.popular_movies.app.AppController;
import com.example.popular_movies.models.detailsModel;
import com.example.popular_movies.utils.Const;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GridFragment extends Fragment {

    private View rootView;

    private GridView gridview;

    private static final String TAG = "Volley";
    // Progress dialog
    private ProgressDialog pDialog;

    private ArrayList<detailsModel> DetailsList;

    private static String sort_by;

    private static String Resolution;

    public static String getResolution() {
        return Resolution;
    }

    public static void setResolution(String resolution) {
        Resolution = resolution;
    }

    static int last_position = 0;

    int screenOrientation;

    public GridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String Resolution = sharedPrefs.getString(
                getString(R.string.pref_resolution_key),
                getString(R.string.pref_Res_high));

        String sortBy = sharedPrefs.getString(
                getString(R.string.pref_sortBy_key),
                getString(R.string.pref_sortBy_mostPopular));

        if(!sortBy.equals(this.sort_by)){
            last_position = 0;
            makeJsonObjectRequest(1);
        }else if(!Resolution.equals(this.Resolution)){
            makeJsonObjectRequest(1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_grid, container, false);

        screenOrientation = getResources().getConfiguration().orientation;

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        gridview = (GridView) rootView.findViewById(R.id.gridview);

        makeJsonObjectRequest(1);

        /*gridview.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                //load next page when scroll down
                makeJsonObjectRequest(page);
            }
        });
*/
        return rootView;
    }


    /**
     * Method to hide the details pane
     */
    private void hideDetailsPane() {
        View alphaPane = rootView.findViewById(R.id.details);
        if (alphaPane.getVisibility() == View.VISIBLE) {
            alphaPane.setVisibility(View.GONE);
        }
    }

    /**
     * Method to show the details pane
     */
    private void showDetailsPane() {
        View alphaPane = rootView.findViewById(R.id.details);
        if (alphaPane.getVisibility() == View.GONE) {
            alphaPane.setVisibility(View.VISIBLE);
        }
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    void set_default_item_details(int position) {
        //to set the details for first item by default
        Bundle bundle = new Bundle();
        bundle.putString(Const.OMG_ID, DetailsList.get(position).getId());

        detailsFragmet fragment = new detailsFragmet();
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.details, fragment)
                .commit();
    }

    /**
     * Method to make json object request where json response starts wtih {
     */
    private void makeJsonObjectRequest(int page) {

        showpDialog();

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = sharedPrefs.getString(
                getString(R.string.pref_sortBy_key),
                getString(R.string.pref_sortBy_mostPopular));

        this.sort_by = sortBy;

        Uri builtUri = Uri.parse(Const.URL_API).buildUpon()
                .appendQueryParameter(Const.PAGE_TAG, Integer.toString(page))
                .appendQueryParameter(Const.SORT_BY_TAG, sortBy)
                .appendQueryParameter(Const.KEY_API_TAG, Const.URL_API_KEY)
                .build();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                builtUri.toString(), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    DetailsList = new ArrayList<detailsModel>();
                    // Parsing json object response
                    JSONArray resultArray = response.getJSONArray(Const.OMG_RESULTS);
                    for (int i = 0; i < resultArray.length(); i++) {

                        JSONObject Object = resultArray.getJSONObject(i);
                        detailsModel temp = new detailsModel();
                        temp.setId(Object.getString(Const.OMG_ID));
                        temp.setPosterImage_url(Object.getString(Const.OMG_POSTER_PATH));

                        DetailsList.add(temp);
                    }

                    gridview.setAdapter(new GridViewAdapter(getActivity(), DetailsList));

                    gridview.setOnItemClickListener(new Listeners(getActivity().getApplicationContext(), gridview));

                    gridview.setSelection(last_position);

                    if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE && MainActivity.density > 600) {
                        set_default_item_details(last_position);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
//                    Toast.makeText(getActivity(),
//                            "Error: " + e.getMessage(),
//                            Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Error: " + e.getMessage());
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error Res: " + error.getMessage());
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    // listeners
    private class Listeners implements AdapterView.OnItemClickListener {

        private Context mContext;
        private GridView mgridview;

        public Listeners(Context context , GridView gridView) {
            mContext = context;
            mgridview = gridView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            GridFragment.last_position = position;

            mgridview.setSelected(true);
            mgridview.setSelection(position);
            mgridview.setItemChecked(position,true);

            int screenOrientation = getResources().getConfiguration().orientation;

            Bundle bundle = new Bundle();
            bundle.putString(Const.OMG_ID, DetailsList.get(position).getId());

            if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                startActivity(new Intent(mContext, ActivityDetails.class).putExtras(bundle));
            }else if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE && MainActivity.density < 600){
                startActivity(new Intent(mContext, ActivityDetails.class).putExtras(bundle));
            }else if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE && MainActivity.density > 600){
                detailsFragmet fragment = new detailsFragmet();
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.details, fragment)
                        .commit();
            }
        }
    }


}
