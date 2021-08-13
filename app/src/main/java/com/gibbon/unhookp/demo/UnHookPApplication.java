package com.gibbon.unhookp.demo;

import android.app.Application;
import android.content.Context;

import com.gibbon.unhookp.core.UpdateResHook;
import com.gibbon.unhookp.manager.HotResManager;

import java.util.HashMap;

/**
 * Created by zhipengzhuo on 2020/11/7 19:59
 */
public class UnHookPApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        PHook.unseal(base);
        UpdateResHook.hook(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();


        HashMap<String, String> initRes = new HashMap<>();
        initRes.put("btn_test2", "我的原始文本是--->这是初始文本2");
        initRes.put("btn_test3", "我的原始文本是--->这是初始文本3");
        HotResManager.getInstance().initRes(initRes);
    }
}
