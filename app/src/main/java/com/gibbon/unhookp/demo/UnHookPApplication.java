package com.gibbon.unhookp.demo;

import android.app.Application;

import com.gibbon.unhookp.manager.HotResManager;

import java.util.HashMap;

/**
 * Created by zhipengzhuo on 2020/11/7 19:59
 */
public class UnHookPApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        HashMap<String, String> initRes = new HashMap<>();
        initRes.put("btn_test2", "我的原始文本是--->这是初始文本2");
        initRes.put("btn_test3", "我的原始文本是--->这是初始文本3");
        HotResManager.getInstance().initRes(initRes);
    }
}
