package com.fraserbrooks.progresstracker.data.source.local;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import com.fraserbrooks.progresstracker.data.UserSetting;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Fraser on 08/04/2018.
 */

public class Converters {

    private static final String TAG = "TypeConverters";

    @TypeConverter
    public static HashMap<String, Integer> fromString(String value){

        Type listType = new TypeToken<HashMap<String, Integer>>(){}.getType();

        return new Gson().fromJson(value, listType);
    }


    @TypeConverter
    public static String fromHashMap(HashMap<String, Integer> map){

        Gson gson = new Gson();

        return gson.toJson(map);
    }


    @TypeConverter
    public static int fromSetting(UserSetting.Setting setting){

        switch (setting){

            case CALENDAR_TARGET_1:
                return 1;
            case CALENDAR_TARGET_2:
                return 2;
            case CALENDAR_TARGET_3:
                return 3;
                default:
                    Log.e(TAG, "fromSetting: unknown setting read from database");
                    throw  new IllegalArgumentException();
        }

    }

    @TypeConverter
    public static UserSetting.Setting fromInt(int i){

        switch(i){

            case 1:
                return UserSetting.Setting.CALENDAR_TARGET_1;
            case 2:
                return UserSetting.Setting.CALENDAR_TARGET_2;
            case 3:
                return UserSetting.Setting.CALENDAR_TARGET_3;
                default:
                    Log.e(TAG, "fromInt: unknown setting");
                    throw  new IllegalArgumentException();
        }

    }

    public static class DayConverters{
        @TypeConverter
        public static Calendar fromTimeString(String timeString){
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Calendar cal = Calendar.getInstance();
            if(timeString == null){ cal = null; }else{
                try {
                    cal.setTime(format1.parse(timeString));
                } catch (ParseException e) {
                    cal = null;

                }
            }
            return cal;
        }

        @TypeConverter
        public static String calendarToTimeStamp(Calendar cal){

            if(cal == null)return null;

            //we want to store by day, so we shave off the time information
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

            return format1.format(cal.getTime());
        }

        @TypeConverter
        public static Date datefromTimeString(String timeString){
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Calendar cal = Calendar.getInstance();
            if(timeString == null){ cal = null; }else{
                try {
                    cal.setTime(format1.parse(timeString));
                } catch (ParseException e) {
                    cal = null;

                }
            }
            if(cal != null) return cal.getTime();
            else return null;
        }


        @TypeConverter
        public static String dateToTimeStamp(Date date){

            if(date == null)return null;

            //we want to store by day, so we shave off the time information
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

            return format1.format(date);
        }

    }

    public static class WeekConverters{
        @TypeConverter
        public static Calendar fromTimeString(String timeString){
            SimpleDateFormat format1 = new SimpleDateFormat("YYYY-ww", Locale.ENGLISH);
            Calendar cal = Calendar.getInstance();
            if(timeString == null){ cal = null; }else{
                try {
                    cal.setTime(format1.parse(timeString));
                } catch (ParseException e) {
                    cal = null;

                }
            }
            return cal;
        }


        @TypeConverter
        public static String calendarToTimeStamp(Calendar cal){

            if(cal == null)return null;

            //we want to store by day, so we shave off the time information
            SimpleDateFormat format1 = new SimpleDateFormat("YYYY-ww", Locale.ENGLISH);

            return format1.format(cal.getTime());
        }
    }

    public static class MonthConverters{
        @TypeConverter
        public static Calendar fromTimeString(String timeString){
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
            Calendar cal = Calendar.getInstance();
            if(timeString == null){ cal = null; }else{
                try {
                    cal.setTime(format1.parse(timeString));
                } catch (ParseException e) {
                    cal = null;

                }
            }
            return cal;
        }


        @TypeConverter
        public static String calendarToTimeStamp(Calendar cal){

            if(cal == null)return null;

            //we want to store by day, so we shave off the time information
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);

            return format1.format(cal.getTime());
        }
    }

    public static class YearConverters{
        @TypeConverter
        public static Calendar fromTimeString(String timeString){
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy", Locale.ENGLISH);
            Calendar cal = Calendar.getInstance();
            if(timeString == null){ cal = null; }else{
                try {
                    cal.setTime(format1.parse(timeString));
                } catch (ParseException e) {
                    cal = null;

                }
            }
            return cal;
        }


        @TypeConverter
        public static String calendarToTimeStamp(Calendar cal){

            if(cal == null)return null;

            //we want to store by day, so we shave off the time information
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy", Locale.ENGLISH);

            return format1.format(cal.getTime());
        }
    }





}
