package com.fraserbrooks.progresstracker.customviews;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.util.TypedValue;

import com.fraserbrooks.progresstracker.R;
import com.fraserbrooks.progresstracker.data.Tracker;
import com.sdsmdg.harjot.vectormaster.VectorMasterDrawable;

public class ColorUtils {

    public static GradientDrawable getGradientDrawable(Context context,int trackerColor){
        return getGradientDrawable(context, trackerColor, 0f);
    }

    public static GradientDrawable getGradientDrawable(Context context, int trackerColor,
                                                       float cornerRadius){

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();

        theme.resolveAttribute(R.attr.gradient_spread_type, typedValue, true);
        String gradientType = typedValue.string.toString();

        theme.resolveAttribute(R.attr.gradient_spread_value, typedValue, true);
        float gradientOffset = typedValue.getFloat();

        float[] hsv = new float[3];
        Color.colorToHSV(trackerColor, hsv);

        float baseVal = hsv[2];
        float baseSat = hsv[1];
        float baseHue = hsv[0];
        int gradientStart = 0;
        int gradientEnd = 0;

        switch (gradientType) {

            case "h":

            //todo

            case "s":

            case "v":
            default:

                hsv[2] = baseVal * (1f - gradientOffset);

                gradientStart = Color.HSVToColor(hsv);

                hsv[2] = baseVal * (1f + gradientOffset);
                if (hsv[2] > 1) hsv[2] = 1f;

                gradientEnd = Color.HSVToColor(hsv);
        }

        return getGradientDrawable(gradientStart, gradientEnd, cornerRadius);

    }


    private static GradientDrawable getGradientDrawable(int startColor,
                                                       int endColor,
                                                       float cornerRadius){

        GradientDrawable shape = new GradientDrawable();

        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(cornerRadius);
        shape.setColors(new int[]{startColor, endColor});
        shape.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);

        return shape;
    }

    public static VectorMasterDrawable getTrackerIcon(Context context, Tracker.TRACKER_ICON i) {

        switch (i) {
            case LEVEL_UP:
                return new VectorMasterDrawable(context, R.drawable.ico_gem);
            case BOOK:
                return new VectorMasterDrawable(context, R.drawable.ico_books);
            case STUDY:
                return new VectorMasterDrawable(context, R.drawable.ico_study);
            case HEART:
                return new VectorMasterDrawable(context, R.drawable.ico_heart);
            default:
                return new VectorMasterDrawable(context, R.drawable.ico_heart);

        }

    }

    public static int getTrackerColor(Context c, Tracker tracker){

        if(tracker.getIcon().equals(Tracker.TRACKER_ICON.LEVEL_UP)){
            return getLevelDefinedColor(c, tracker.getLevel());
        }else{
            return getUserDefinedColor(c, tracker.getColor());
        }

    }

    public static int getUserDefinedColor(Context c, int base) {
        if(base < 0) base = 0;
        if(base > 360) base = 360;

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = c.getTheme();
        theme.resolveAttribute(R.attr.base_saturation, typedValue, true);
        float baseSaturation = typedValue.getFloat();

        theme.resolveAttribute(R.attr.base_light_value, typedValue, true);
        float baseLightValue = typedValue.getFloat();

        float[] hsv = new float[]{((float) base), baseSaturation, baseLightValue};

        return Color.HSVToColor(hsv);
    }

    public static int getLevelDefinedColor(Context c, int level){
        if(level > 8) level = 8;

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = c.getTheme();

        switch (level) {

            case 1:
                theme.resolveAttribute(R.attr.level1_color, typedValue, true);
                break;
            case 2:
                theme.resolveAttribute(R.attr.level2_color, typedValue, true);
                break;
            case 3:
                theme.resolveAttribute(R.attr.level3_color, typedValue, true);
                break;
            case 4:
                theme.resolveAttribute(R.attr.level4_color, typedValue, true);
                break;
            case 5:
                theme.resolveAttribute(R.attr.level5_color, typedValue, true);
                break;
            case 6:
                theme.resolveAttribute(R.attr.level6_color, typedValue, true);
                break;
            case 7:
                theme.resolveAttribute(R.attr.level7_color, typedValue, true);
                break;
            case 8:
                theme.resolveAttribute(R.attr.level8_color, typedValue, true);
                break;
            default:
                theme.resolveAttribute(R.attr.level8_color, typedValue, true);

        }
        @ColorInt int color = typedValue.data;
        return color;
    }







}
