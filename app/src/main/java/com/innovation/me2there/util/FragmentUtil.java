package com.innovation.me2there.util;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ashley on 9/7/15.
 */
public class FragmentUtil {

    public static void removeView(View toRemoveView) {
        if(toRemoveView != null) {
            ViewGroup layout = (ViewGroup) toRemoveView.getParent();
            if (null != layout) { //for safety only  as you are doing onClick
                layout.removeView(toRemoveView);
            }
        }
    }
    public static void addView(ViewGroup layout, View toRemoveView) {
        if(toRemoveView != null && layout != null) {
            layout.addView(toRemoveView);
        }
    }
}
