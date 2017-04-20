package com.tikkunolam.momentsintime;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;


public class DeviceManager {
    /**
     * class containing static methods for dealing with the device
     */


    public static boolean isDeviceATablet(Context context) {

        /**
         * DETERMINE IF THE DEVICE IS A TABLET
         * fetch the screen dimensions
         * convert them to dp units
         * if the smallest dimension is >= 600dp it's a tablet
         */

        // fetch the device display object and produce the metrics
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        // fetch the pixel density to convert pixels to dp
        float density = context.getResources().getDisplayMetrics().density;

        // get the dp values
        float dpWidth = outMetrics.widthPixels / density;
        float dpHeight = outMetrics.heightPixels / density;

        // return whether it's a tablet
        return (dpWidth >= 800 || dpHeight >= 800);

    }
}
