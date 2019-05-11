package com.example.mock_test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class feedback extends Fragment
{
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.feedback, container, false);
        v=view;
        start();
        return view;
    }
    public void start()
    {

    }
}
