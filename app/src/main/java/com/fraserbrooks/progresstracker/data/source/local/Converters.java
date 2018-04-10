package com.fraserbrooks.progresstracker.data.source.local;

import android.arch.persistence.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by Fraser on 08/04/2018.
 */

class Converters {

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

}
