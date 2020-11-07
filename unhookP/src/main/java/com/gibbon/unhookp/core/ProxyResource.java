package com.gibbon.unhookp.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.TintContextWrapper;
import androidx.appcompat.widget.VectorEnabledTintResources;

import com.gibbon.unhookp.manager.HotResManager;
import com.gibbon.unhookp.strategy.IUnHookPBgColorStrategy;
import com.gibbon.unhookp.strategy.IUnHookPColorStrategy;
import com.gibbon.unhookp.strategy.IUnHookPDrawableStrategy;
import com.gibbon.unhookp.strategy.IUnHookPTextStrategy;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Created by zhipengzhuo on 2020/10/28 10:31
 */
public class ProxyResource extends Resources {

    private static final String TAG = ProxyResource.class.getSimpleName();

    private Resources resources;
    private IUnHookPTextStrategy iUnHookPTextStrategy;
    private IUnHookPColorStrategy iUnHookPColorStrategy;
    private IUnHookPBgColorStrategy iUnHookPBgColorStrategy;
    private IUnHookPDrawableStrategy iUnHookPDrawableStrategy;

    /**
     * Create a new Resources object on top of an existing set of assets in an
     * AssetManager.
     *
     * @param assets  Previously created AssetManager.
     * @param metrics Current display metrics to consider when
     *                selecting/computing resource values.
     * @param config  Desired device configuration to consider when
     * @deprecated Resources should not be constructed by apps.
     * See {@link Context#createConfigurationContext(Configuration)}.
     */
    public ProxyResource(AssetManager assets, DisplayMetrics metrics, Configuration config, Resources resources) {
        super(assets, metrics, config);
        this.resources = resources;
    }

    public IUnHookPTextStrategy getUnHookPTextStrategy() {
        return iUnHookPTextStrategy;
    }

    public void setUnHookPTextStrategy(IUnHookPTextStrategy iUnHookPTextStrategy) {
        this.iUnHookPTextStrategy = iUnHookPTextStrategy;
    }

    public IUnHookPColorStrategy getUnHookPColorStrategy() {
        return iUnHookPColorStrategy;
    }

    public void setiUnHookPColorStrategy(IUnHookPColorStrategy iUnHookPColorStrategy) {
        this.iUnHookPColorStrategy = iUnHookPColorStrategy;
    }

    public IUnHookPBgColorStrategy getUnHookPBgColorStrategy() {
        return iUnHookPBgColorStrategy;
    }

    public void seiUnHookPBgColorStrategy(IUnHookPBgColorStrategy iUnHookPBgColorStrategy) {
        this.iUnHookPBgColorStrategy = iUnHookPBgColorStrategy;
    }

    public IUnHookPDrawableStrategy getUnHookPDrawableStrategy() {
        return iUnHookPDrawableStrategy;
    }

    public void setUnHookPDrawableStrategy(IUnHookPDrawableStrategy iUnHookPDrawableStrategy) {
        this.iUnHookPDrawableStrategy = iUnHookPDrawableStrategy;
    }

    @NonNull
    @Override
    public String getString(int id) throws NotFoundException {
        String res = resources.getResourceEntryName(id);
        if (!TextUtils.isEmpty(HotResManager.getInstance().getResValueWithName(res))) {
            return Objects.requireNonNull(HotResManager.getInstance().getResValueWithName(res));
        }
        return super.getString(id);
    }

    @NonNull
    @Override
    public String getString(int id, Object... formatArgs) throws NotFoundException {
        final String raw = getString(id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return String.format(resources.getConfiguration().getLocales().get(0), raw,
                    formatArgs);
        } else {
            return String.format(resources.getConfiguration().locale, raw, formatArgs);
        }
    }

    @NonNull
    @Override
    public CharSequence getText(int id) throws NotFoundException {
        String res = resources.getResourceEntryName(id);
        String desRes;
        if (!TextUtils.isEmpty(desRes = HotResManager.getInstance().getResValueWithName(res))) {
            Log.d(TAG,  "textId: " + id + " is match hot res: " + desRes);
            return desRes;
        }
        return super.getText(id);
    }

    @Override
    public CharSequence getText(int id, CharSequence def) {
        if (id != 0) {
            return getText(id);
        }

        return def;
    }

    @SuppressLint("RestrictedApi")
    public void adaptView(View view, View parent, @NonNull Context context,
                          @NonNull AttributeSet attrs, boolean inheritContext,
                          boolean readAndroidTheme) {
        if (iUnHookPTextStrategy != null && iUnHookPTextStrategy.adaptText() && view instanceof TextView) {

            int[] attrArrays = getReflectField("com.android.internal.R$styleable", "TextView");

            if (inheritContext && parent != null) {
                context = parent.getContext();
            }
            if (readAndroidTheme) {
                // We then apply the theme on the context, if specified
                context = DecorateAppCompatViewInflater.themifyContext(context, attrs, readAndroidTheme, true);
            }
            if (VectorEnabledTintResources.shouldBeUsed()) {
                context = TintContextWrapper.wrap(context);
            }
            final Theme theme = TintContextWrapper.wrap(context).getTheme();
            TypedArray a = theme.obtainStyledAttributes(attrs, attrArrays, android.R.attr.textViewStyle, 0);

            int textIdAttr = getIntReflectField("com.android.internal.R$styleable", "TextView_text");
            int hintIdAttr = getIntReflectField("com.android.internal.R$styleable", "TextView_hint");

            int textId = a.getResourceId(textIdAttr, -1);
            int hindId = a.getResourceId(hintIdAttr, -1);

            if (textId != -1) {
                String textIdName = context.getResources().getResourceEntryName(textId);
                String res;
                if (!TextUtils.isEmpty(res = HotResManager.getInstance().getResValueWithName(textIdName))) {
                    Log.d(TAG,  "textIdName: " + textIdName + " is match hot res: " + res);
                    ((TextView)view).setText(res);
                }
            }

            if (hindId != -1) {
                String textIdName = context.getResources().getResourceEntryName(hindId);
                String res;
                if (!TextUtils.isEmpty(res = HotResManager.getInstance().getResValueWithName(textIdName))) {
                    Log.d(TAG,  "textIdName: " + textIdName + " is match hot res: " + res);
                    ((TextView)view).setText(res);
                }
            }

            a.recycle();
        }
    }

    public int getIntReflectField(String className,String fieldName){
        int result = -1;
        try {
            Class<?> clz = Class.forName(className);
            Field field = clz.getField(fieldName);
            field.setAccessible(true);
            result = field.getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public int[] getReflectField(String className,String fieldName){
        int[] result = new int[0];
        try {
            Class<?> clz = Class.forName(className);
            Field field = clz.getField(fieldName);
            field.setAccessible(true);
            result = (int[]) field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
