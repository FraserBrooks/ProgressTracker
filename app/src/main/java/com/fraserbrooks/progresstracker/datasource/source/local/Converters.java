package com.fraserbrooks.progresstracker.datasource.source.local;

import androidx.room.TypeConverter;
import android.util.Log;

import com.fraserbrooks.progresstracker.calendar.CalendarUtils;
import com.fraserbrooks.progresstracker.settings.domain.model.UserSetting;
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

@SuppressWarnings("unused,WeakerAccess")
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


    public static class DayConverters{
        @TypeConverter
        public static Calendar fromTimeString(String timeString){
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
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
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            return format1.format(cal.getTime());
        }

        @TypeConverter
        public static Date dateFromTimeString(String timeString){
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
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


    }

    public static class WeekConverters{
        @TypeConverter
        public static Calendar fromTimeString(String timeString){
            Calendar cal = Calendar.getInstance();
            if(timeString == null){ cal = null; }else{
                try {
                    long l = Long.parseLong(timeString);
                    cal.setTime(new Date(l));
                } catch (NumberFormatException e) {
                    cal = null;
                }
            }
            return cal;
        }


        @TypeConverter
        public static String calendarToTimeStamp(Calendar calendar){

            if(calendar == null)return null;

            Calendar cal = (Calendar) calendar.clone();

            String ret = "" + cal.getTimeInMillis();
            Log.d(TAG, "calendarToTimeStamp: asd11111: " + ret );

            // Set calendar to very start of week
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            CalendarUtils.setMidnight(cal);

            ret = "" + cal.getTimeInMillis();
            Log.d(TAG, "calendarToTimeStamp: asd22222: " + ret );

            return ret;
        }
    }


    public static class MonthConverters{
        @TypeConverter
        public static Calendar fromTimeString(String timeString){
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
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
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM", Locale.getDefault());

            return format1.format(cal.getTime());
        }
    }

    public static class YearConverters{
        @TypeConverter
        public static Calendar fromTimeString(String timeString){
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy", Locale.getDefault());
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
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy", Locale.getDefault());

            return format1.format(cal.getTime());
        }
    }





}
