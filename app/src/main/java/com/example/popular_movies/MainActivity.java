package com.example.popular_movies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.popular_movies.fragments.GridFragment;



public class MainActivity extends ActionBarActivity {

    int screenOrientation;

    public static float density;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridFragment fragment = new GridFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.grid, fragment)
                .commit();

        screenOrientation = getResources().getConfiguration().orientation;
        density = getdensity();

        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            hideDetailsPane();
        }

        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE && getdensity() < 600) {
            hideDetailsPane();
        }
    }

    float getdensity (){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        float scaleFactor = metrics.density;

        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;

        float smallestWidth = Math.min(widthDp, heightDp);
        return smallestWidth;
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        String Resolution = sharedPrefs.getString(
                getString(R.string.pref_resolution_key),
                getString(R.string.pref_Res_high));

        String sortBy = sharedPrefs.getString(
                getString(R.string.pref_sortBy_key),
                getString(R.string.pref_sortBy_mostPopular));

        if (!sortBy.equals(GridFragment.getSort_by())) {
            GridFragment.changed = true;
            GridFragment.last_position = 0;
            GridFragment fragment = new GridFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.grid, fragment)
                    .commit();
        } else if (!Resolution.equals(GridFragment.getResolution())) {
            GridFragment.changed = true;
            GridFragment fragment = new GridFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.grid, fragment)
                    .commit();
        }
    }

    /**
     * Method to hide the details pane
     */
    private void hideDetailsPane() {
        View alphaPane = findViewById(R.id.details);
        if (alphaPane.getVisibility() == View.VISIBLE) {
            alphaPane.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
