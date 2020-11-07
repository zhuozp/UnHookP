package com.gibbon.unhookp.manager;

import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by zhipengzhuo on 2020/10/29 14:59
 */
public class HotResManager {

    public static final String TAG = HotResManager.class.getSimpleName();

    private static volatile HotResManager sInstance;
    private ConcurrentHashMap<String, String> mResHashMap = new ConcurrentHashMap<>();

    private HotResManager() {

    }

    public static HotResManager getInstance() {
        if (sInstance == null) {
            synchronized (HotResManager.class) {
                if (sInstance == null) {
                    sInstance = new HotResManager();
                }
            }
        }

        return sInstance;
    }

    public void initRes(HashMap<String, String> resHashMap) {
        String value = null;
        for (Map.Entry<String, String> entry : resHashMap.entrySet()) {
            value = entry.getValue();
            if (!TextUtils.isEmpty(value)) {
                value = value.replaceAll("\\$@", "\\$s");
                value = value.replaceAll("%lu", "%d");
                value = value.replaceAll("%@", "%s");
                Log.d(TAG, "value is: " + value);
                mResHashMap.put(entry.getKey(), value);
            }
        }
    }

    public void release() {
        if (mResHashMap != null) {
            mResHashMap.clear();
        }
    }

    public void updateRes(HashMap<String, String> resHashMap) {
        for (Map.Entry<String, String> entry : resHashMap.entrySet()) {
            mResHashMap.put(entry.getKey(), entry.getValue());
        }
    }

    public void update(String name, String value) {
        mResHashMap.put(name, value);
    }

    public String getResValueWithName(String name) {
        return mResHashMap.get(name);
    }
}
