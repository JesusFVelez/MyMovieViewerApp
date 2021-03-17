package com.example.mymovieviewer.database;


import androidx.room.TypeConverter;

public class BooleanConverter {
    @TypeConverter
    public static boolean toBoolean(String truthValue){
     return truthValue == "true";
    }

    @TypeConverter
    public static String toString(boolean truthValue){
        if(truthValue){
            return "true";
        }else{
            return "false";
        }
    }
}
