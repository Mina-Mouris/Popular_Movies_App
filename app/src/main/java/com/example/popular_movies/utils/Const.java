package com.example.popular_movies.utils;

/**
 * Created by geekpro on 10/2/15.
 */
public class Const {
    private static float density;

    private static String sort_by;

    private static String Resolution;

    public static boolean isTwoPane() {
        return twoPane;
    }

    public static void setTwoPane(boolean twoPane) {
        Const.twoPane = twoPane;
    }

    private static boolean twoPane;

    public static float getDensity() {return density;}

    public static void setDensity(float density) {Const.density = density;}

    public static String getResolution() {
        return Resolution;
    }

    public static void setResolution(String resolution) {
        Resolution = resolution;
    }

    public static String getSort_by() {
        return sort_by;
    }

    public static void setSort_by(String sort_by) {Const.sort_by = sort_by;}
}
