package com.example.mock_test;

import android.content.Context;
import android.content.SharedPreferences;

public class Shared_handler
{
    Context context;
    public Shared_handler(Context context)
    {
        this.context=context;
    }
    public void saveinandsave(String name,String key,String value)
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences(name,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }
    public void Clear(String name)
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences(name,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
    public String getFrom(String name,String key)
    {
        String response="null";
        SharedPreferences sharedPreferences=context.getSharedPreferences(name,Context.MODE_PRIVATE);
        response=sharedPreferences.getString(key,"null");
        return response;
    }
}
