package com.fraserbrooks.progresstracker.data.appcolor;

import android.graphics.Color;

public class colorutils {

    public static int getUserDefinedColor(int hue, float sat, float light_value){
        if(hue > 360 || hue < 0){
            throw new IllegalArgumentException();
        }

        float[] hsv = { (float) hue,  sat, light_value};
        return Color.HSVToColor(hsv);
    }

}
