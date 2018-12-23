package com.fraserbrooks.progresstracker.data.source.local;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import com.fraserbrooks.progresstracker.data.Tracker;
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
    public static UserSetting.Setting settingFromInt(int i){

        switch(i){

            case 1:
                return UserSetting.Setting.CALENDAR_TARGET_1;
            case 2:
                return UserSetting.Setting.CALENDAR_TARGET_2;
            case 3:
                return UserSetting.Setting.CALENDAR_TARGET_3;
            default:
                Log.e(TAG, "setting fromInt: unknown setting");
                throw  new IllegalArgumentException();
        }

    }

    public static class TrackerConverters{

        @TypeConverter
        public static int intFromTrackerType(Tracker.TRACKER_TYPE t) {

            switch (t) {
                case LEVEL_UP:
                    return 1;
                case GRAPH:
                    return 2;
                case YES_NO:
                    return 3;
                default:
                    Log.e(TAG, "intFromTrackerType: unknown type");
                    throw new IllegalArgumentException();
            }

        }

        @TypeConverter
        public static Tracker.TRACKER_TYPE trackerTypeFromInt(int i){

            switch(i){

                case 1:
                    return Tracker.TRACKER_TYPE.LEVEL_UP;
                case 2:
                    return Tracker.TRACKER_TYPE.GRAPH;
                case 3:
                    return Tracker.TRACKER_TYPE.YES_NO;
                default:
                    Log.e(TAG, "trackerTypeFromInt: unknown setting");
                    throw  new IllegalArgumentException();
            }

        }

        @TypeConverter
        public static int intFromGraphType(Tracker.GRAPH_TYPE t) {

            switch (t) {
                case DAY:
                    return 1;
                case WEEK:
                    return 2;
                case MONTH:
                    return 3;
                case YEAR:
                    return 4;
                default:
                    Log.e(TAG, "intFromGraphType: unknown type");
                    throw new IllegalArgumentException();
            }

        }

        @TypeConverter
        public static Tracker.TRACKER_ICON trackerIconFromInt(int i){

            switch(i){

                case 1:
                    return Tracker.TRACKER_ICON.LEVEL_UP;
                case 2:
                    return Tracker.TRACKER_ICON.BOOK;
                case 3:
                    return Tracker.TRACKER_ICON.HEART;
                case 4:
                    return Tracker.TRACKER_ICON.STUDY;
                case 5:
                    return Tracker.TRACKER_ICON.PHONE;
                case 6:
                    return Tracker.TRACKER_ICON.APPLE;
                case 7:
                    return Tracker.TRACKER_ICON.PEOPLE;
                case 8:
                    return Tracker.TRACKER_ICON.TEXT;
                case 9:
                    return Tracker.TRACKER_ICON.COMPUTER;
                case 10:
                    return Tracker.TRACKER_ICON.PENCIL;
                default:
                    Log.e(TAG, "trackerIconFromInt: unknown setting");
                    throw  new IllegalArgumentException();
            }

        }

        @TypeConverter
        public static int intFromTrackerIcon(Tracker.TRACKER_ICON i) {

            switch (i) {
                case LEVEL_UP:
                    return 1;
                case BOOK:
                    return 2;
                case HEART:
                    return 3;
                case STUDY:
                    return 4;
                case PHONE:
                    return 5;
                case APPLE:
                    return 6;
                case PEOPLE:
                    return 7;
                case TEXT:
                    return 8;
                case COMPUTER:
                    return 9;
                case PENCIL:
                    return 10;
                default:
                    Log.e(TAG, "intFromTrackerIcon: unknown icon");
                    throw new IllegalArgumentException();
            }

        }

        @TypeConverter
        public static Tracker.GRAPH_TYPE graphTypeFromInt(int i){

            switch(i){

                case 1:
                    return Tracker.GRAPH_TYPE.DAY;
                case 2:
                    return Tracker.GRAPH_TYPE.WEEK;
                case 3:
                    return Tracker.GRAPH_TYPE.MONTH;
                case 4:
                    return Tracker.GRAPH_TYPE.YEAR;
                default:
                    Log.e(TAG, "graphTypeFromInt: unknown setting");
                    throw  new IllegalArgumentException();
            }

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
        public static Date dateFromTimeString(String timeString){
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
