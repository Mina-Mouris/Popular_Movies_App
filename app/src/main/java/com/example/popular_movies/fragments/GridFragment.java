package com.example.popular_movies.fragments;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.popular_movies.Adapters.GridViewAdapter;
import com.example.popular_movies.Listeners.EndlessScrollListener;
import com.example.popular_movies.Listeners.GridListener;
import com.example.popular_movies.MainActivity;
import com.example.popular_movies.R;
import com.example.popular_movies.app.AppController;
import com.example.popular_movies.models.detailsModel;
import com.example.popular_movies.utils.Const;
import com.example.popular_movies.utils.ConstStrings;

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

    public static int last_position = 0;
    protected static int temp = 0;

    public static boolean changed = false;

    int screenOrientation;

    public GridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_grid, container, false);

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String Resolution = sharedPrefs.getString(
                getString(R.string.pref_resolution_key),
                getString(R.string.pref_Res_high));

        Const.setResolution(Resolution);

        screenOrientation = getResources().getConfiguration().orientation;

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        gridview = (GridView) rootView.findViewById(R.id.gridview);

        makeJsonObjectRequest(1);

        temp = last_position;

        gridview.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                //load next page when scroll down
                last_position = totalItemsCount - 8;
                Log.d("LOG", "" + "in scroll change" + last_position);
                makeJsonObjectRequest(page);
            }
        });
        return rootView;
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
        bundle.putString(ConstStrings.OMG_ID, DetailsList.get(position).getId());

        detailsFragmet fragment = new detailsFragmet();
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.details, fragment)
                .commit();
    }

    /**
     * Method to make json object request where json response starts wtih {
     */
    private void makeJsonObjectRequest(final int page) {

        showpDialog();

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = sharedPrefs.getString(
                getString(R.string.pref_sortBy_key),
                getString(R.string.pref_sortBy_mostPopular));

        Const.setSort_by(sortBy);

        Uri builtUri = Uri.parse(ConstStrings.URL_API).buildUpon()
                .appendQueryParameter(ConstStrings.PAGE_TAG, Integer.toString(page))
                .appendQueryParameter(ConstStrings.SORT_BY_TAG, sortBy)
                .appendQueryParameter(ConstStrings.KEY_API_TAG, ConstStrings.URL_API_KEY)
                .build();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                builtUri.toString(), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    if(changed || DetailsList == null) {
                        changed = false;
                        DetailsList = new ArrayList<detailsModel>();
                    }
                    // Parsing json object response
                    JSONArray resultArray = response.getJSONArray(ConstStrings.OMG_RESULTS);
                    for (int i = 0; i < resultArray.length(); i++) {

                        JSONObject Object = resultArray.getJSONObject(i);
                        detailsModel temp = new detailsModel();
                        temp.setId(Object.getString(ConstStrings.OMG_ID));
                        temp.setPosterImage_url(Object.getString(ConstStrings.OMG_POSTER_PATH));

                        DetailsList.add(temp);
                    }

                    gridview.setAdapter(new GridViewAdapter(getActivity(), DetailsList));

                    gridview.setOnItemClickListener(new GridListener(getActivity(), gridview ,DetailsList));

                    Log.d("LOG", "" + "in background" + last_position);
                    if(temp > last_position){
                        last_position = temp;
                    }

                    gridview.setSelection(last_position);

                    if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE && Const.getDensity() > 600) {
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

}
