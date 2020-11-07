package com.gibbon.unhookp.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.LayoutInflaterCompat;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.gibbon.unhookp.core.DecorateFactory;
import com.gibbon.unhookp.core.ProxyResource;
import com.gibbon.unhookp.manager.HotResManager;
import com.gibbon.unhookp.strategy.IUnHookPTextStrategy;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /**
         * init ProxyResource when activity create
         * */
        proxyResource = new ProxyResource(getAssets(), getResources().getDisplayMetrics(), getResources().getConfiguration(), getResources());
        proxyResource.setUnHookPTextStrategy(new IUnHookPTextStrategy() {
            @Override
            public boolean adaptText() {
                return true;
            }
        });
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        DecorateFactory decorateFactory = new DecorateFactory(this, proxyResource);
        LayoutInflaterCompat.setFactory2(layoutInflater, decorateFactory);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final Button button = findViewById(R.id.btn1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HotResManager.getInstance().update("btn_test1", "点击之后热更新的文本");
                button.setText(R.string.btn_test1);
            }
        });
    }

    private ProxyResource proxyResource;

    @Override
    public Resources getResources() {
        return proxyResource != null ? proxyResource : super.getResources();
    }
}