package com.honeydo5.honeydo.app;

import android.support.v7.app.AppCompatActivity;

public abstract class HoneyDoActivity extends AppCompatActivity implements IActivityTag {
    protected String tag = "";

    @Override
    public String getTag(){
        return tag;
    }

    @Override
    public String setTag(String tag){
        this.tag = tag;
        return this.tag;
    }
}
