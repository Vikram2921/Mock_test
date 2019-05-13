package com.example.mock_test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper
{
    public long ConvertToMillis(int hour,int minutes,int second)
    {
        long millis=0;
        long hts=hour*3600;
        long mts=minutes*60;
        long secon=second+hts+mts;
        millis=secon*1000;
        return millis;
    }
    public String GetnewEUID()
    {
        String key="";
        String date=getTodaydate("yyyy/MM/dd");
        date=date.replaceAll("/","");
        String time=getCurrentTime("HH:mm:ss:SSS");
        time=time.replaceAll(":","");
        String abb=getCuurentAbb();
        key = date + "" + time;
        return key;
    }
    public String getTodaydateinformat()
    {
        String dd=getTodaydate("dd");
        String mm=getTodaydate("MM");
        String yyyy=getTodaydate("yyyy");
        String date=convertoTwo(Integer.parseInt(dd))+""+convertoTwo((Integer.parseInt(mm)-1))+""+yyyy;
        return date;
    }
    public String getCurrentTime(String format)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String currentTime = sdf.format(new Date());
        currentTime=currentTime.replace(".","");
        currentTime=currentTime.toUpperCase();
        return currentTime;
    }
    public String getTodaydate(String format)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String currentTime = sdf.format(new Date());
        return currentTime;
    }
    public String getCuurentAbb()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("a");
        String currentTime = sdf.format(new Date());
        return currentTime;
    }
    public Date getDate(String date)
    {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss aa");
        Date date1 = null;
        try
        {
            date1 = format.parse(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date1;
    }
    public String convertoTwo(int val)
    {
        String n=val+"";
        if(val<10)
        {
            n="0"+val;
        }
        return n;
    }
}
