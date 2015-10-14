package com.innovation.me2there.fragments;

import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntegerRes;
import android.support.v4.app.Fragment;

/**
 * Created by ashley on 6/10/15.
 */


public class Mee2ThereCardFragmentBase extends Fragment {

    public int dimen(@DimenRes int resId) {
        return (int) getResources().getDimension(resId);
    }

    public int color(@ColorRes int resId) {
        return getResources().getColor(resId);
    }

    public int integer(@IntegerRes int resId) {
        return getResources().getInteger(resId);
    }

}
