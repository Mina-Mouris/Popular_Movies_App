package com.example.popular_movies.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.popular_movies.Adapters.ReviewListAdapter;
import com.example.popular_movies.MainActivity;
import com.example.popular_movies.R;
import com.example.popular_movies.Adapters.TrailerListAdapter;
import com.example.popular_movies.app.AppController;
import com.example.popular_movies.models.detailsModel;
import com.example.popular_movies.models.reviewModel;
import com.example.popular_movies.models.trailerModel;
import com.example.popular_movies.utils.Const;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by geekpro on 9/1/15.
 */
public class detailsFragmet extends android.support.v4.app.Fragment {

    private View rootView;
    private Bundle bundle;
    // Progress dialog
    private ProgressDialog pDialog;

    private detailsModel temp;

    ListView trailerList;
    ListView reviewList;
    ArrayList<trailerModel> trailerListNames;
    ArrayList<reviewModel> reviewListNames;

    private static final String TAG = "Volley";

    public detailsFragmet() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_details, container, false);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        Intent intent = getActivity().getIntent();

        bundle = intent.getExtras();
        if (bundle != null && !bundle.isEmpty()) {
            buildDetailsScreen(bundle);
        } else {
            bundle = getArguments();
            if (bundle != null && !bundle.isEmpty()) {
                buildDetailsScreen(bundle);
            }
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String Resolution = sharedPrefs.getString(getString(R.string.pref_resolution_key), getString(R.string.pref_Res_high));

        if(!Resolution.equals(GridFragment.getResolution())){
            buildDetailsScreen(bundle);
        }

    }

    void buildDetailsScreen(Bundle bundel) {
        detailsModel model = new detailsModel();
        model.setId(bundel.getString(Const.OMG_ID));

        makeJsonObjectRequest_to_get_movie_details(model.getId());

    }

    private void showpDialog() {
        if (!pDialog.isShowing())
             pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    /**
     * Method to make json object request where json response starts wtih {
     */
    private void makeJsonObjectRequest_to_get_movie_details(final String id) {

        showpDialog();

        Uri builtUri = Uri.parse(Const.URL_API_FOR_DETAILS + id + "?").buildUpon()
                .appendQueryParameter(Const.KEY_API_TAG, Const.URL_API_KEY)
                .build();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                builtUri.toString(), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response

                    if(response != null) {

                        temp = new detailsModel();
                        temp.setPosterImage_url(response.getString(Const.OMG_POSTER_PATH));
                        temp.setBackImage_url(response.getString(Const.OMG_BACKDROP_PATH));
                        temp.setVote(response.getString(Const.OMG_VOTE));
                        temp.setTitle(response.getString(Const.OMG_TITLE));
                        temp.setOverView(response.getString(Const.OMG_OVERVIEW));
                        temp.setDate(response.getString(Const.OMG_DATE));
                        temp.setRunTime(response.getString(Const.OMG_RUNTIME));

                        ImageView imageview = ((ImageView) rootView.findViewById(R.id.image));

                        SharedPreferences sharedPrefs =
                                PreferenceManager.getDefaultSharedPreferences(getActivity());
                        String Resolution = sharedPrefs.getString(getString(R.string.pref_resolution_key),getString(R.string.pref_Res_high));

                        if(Resolution.equals("high")){
                            Picasso.with(getActivity()).load(Const.URL_IMAGE_PATH_high + temp.getBackImage_url()).into(imageview);
                        }else if(Resolution.equals("medium")){
                            Picasso.with(getActivity()).load(Const.URL_IMAGE_PATH_medium + temp.getBackImage_url()).into(imageview);
                        }else if(Resolution.equals("low")){
                            Picasso.with(getActivity()).load(Const.URL_IMAGE_PATH_low + temp.getBackImage_url()).into(imageview);
                        }

                        imageview.setSelected(true);

                        ((TextView) rootView.findViewById(R.id.TitleTextView)).setText(temp.getTitle());
                        ((TextView) rootView.findViewById(R.id.overView)).setText(temp.getOverView());
                        ((TextView) rootView.findViewById(R.id.DateView)).setText(temp.getDate());
                        ((TextView) rootView.findViewById(R.id.Vote)).setText(temp.getVote() + "/10");
                        ((TextView) rootView.findViewById(R.id.Runtime)).setText(temp.getRunTime() + "min");

                        makeJsonObjectRequest_to_get_movie_video(id);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
//                    Toast.makeText(getActivity().getApplicationContext(),
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
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error Res: " + error.getMessage());
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void makeJsonObjectRequest_to_get_movie_video(final String id) {

        showpDialog();

        Uri builtUri = Uri.parse(Const.URL_API_FOR_DETAILS + id + Const.PAR_VIDEO).buildUpon()
                .appendQueryParameter(Const.KEY_API_TAG, Const.URL_API_KEY)
                .build();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                builtUri.toString(), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    trailerListNames = new ArrayList<trailerModel>();
                    // Parsing json object response
                    JSONArray resultArray = response.getJSONArray(Const.OMG_RESULTS);
                    trailerModel temp;
                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject Object = resultArray.getJSONObject(i);
                        temp = new trailerModel();
                        temp.setName(Object.getString(Const.OMG_VIDEO_name));
                        temp.setKey(Object.getString(Const.OMG_VIDEO_KEY));
                        trailerListNames.add(temp);
                    }

                    trailerList =(ListView) rootView.findViewById(R.id.listView);

                    TrailerListAdapter adapter = new TrailerListAdapter(getActivity(), trailerListNames);
                    trailerList.setAdapter(adapter);
                    trailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Const.URL_VIDEO + trailerListNames.get(position).getKey())));
                        }
                    });

                    trailerList.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            int action = event.getAction();
                            switch (action) {
                                case MotionEvent.ACTION_DOWN:
                                    // Disallow ScrollView to intercept touch events.
                                    v.getParent().requestDisallowInterceptTouchEvent(true);
                                    break;

                                case MotionEvent.ACTION_UP:
                                    // Allow ScrollView to intercept touch events.
                                    v.getParent().requestDisallowInterceptTouchEvent(false);
                                    break;
                            }

                            // Handle ListView touch events.
                            v.onTouchEvent(event);
                            return true;
                        }
                    });

                    final ScrollView scroll = (ScrollView) getActivity().findViewById(R.id.scroll);
                    scroll.scrollTo(0, 0);

                    makeJsonObjectRequest_to_get_movie_review(id);


                } catch (Exception e) {
                    e.printStackTrace();
                    /*Toast.makeText(getActivity(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();*/
                    Log.d(TAG, "Error: " + e.getMessage());
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error Res: " + error.getMessage());
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void makeJsonObjectRequest_to_get_movie_review(String id) {

        showpDialog();

        Uri builtUri = Uri.parse(Const.URL_API_FOR_DETAILS + id + Const.PAR_REVIEW).buildUpon()
                .appendQueryParameter(Const.KEY_API_TAG, Const.URL_API_KEY)
                .build();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                builtUri.toString(), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    reviewListNames = new ArrayList<reviewModel>();
                    // Parsing json object response
                    JSONArray resultArray = response.getJSONArray(Const.OMG_RESULTS);
                    reviewModel temp;
                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject Object = resultArray.getJSONObject(i);
                        temp = new reviewModel();
                        temp.setAuthor(Object.getString(Const.OMG_REVIEW_AUTHOR));
                        temp.setContent(Object.getString(Const.OMG_REVIEW_CONTENT));
                        reviewListNames.add(temp);
                    }

                    reviewList =(ListView) rootView.findViewById(R.id.ReviewlistView);

                    ReviewListAdapter adapter=new ReviewListAdapter(getActivity(), reviewListNames);
                    reviewList.setAdapter(adapter);
                    reviewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Const.URL_VIDEO + trailerListNames.get(position).getKey())));
                        }
                    });

                    reviewList.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            int action = event.getAction();
                            switch (action) {
                                case MotionEvent.ACTION_DOWN:
                                    // Disallow ScrollView to intercept touch events.
                                    v.getParent().requestDisallowInterceptTouchEvent(true);
                                    break;

                                case MotionEvent.ACTION_UP:
                                    // Allow ScrollView to intercept touch events.
                                    v.getParent().requestDisallowInterceptTouchEvent(false);
                                    break;
                            }

                            // Handle ListView touch events.
                            v.onTouchEvent(event);
                            return true;
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                    /*Toast.makeText(getActivity(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();*/
                    Log.d(TAG, "Error: " + e.getMessage());
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(),
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
