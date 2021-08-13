package com.gibbon.unhookp.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.DexFile;

import static android.os.Build.VERSION.SDK_INT;

/**
 * Created by zhipengzhuo on 2021/8/13 15:34
 */
public class UpdateResHook {

    private static final String DEX = "ZGV4CjAzNQA1d5uXENpDR4ETcf0o2dcY6YTVVN3/eXfsCAAAcAAAAHhWNBIAAAAAAAAAABwIAAA3AAAAcAAAABAAAABMAQAABwAAAIwBAAAJAAAA4AEAAAwAAAAoAgAAAgAAAIgCAAAkBgAAyAIAAG4EAABwBAAAdQQAAHsEAACFBAAAjQQAAJ0EAACpBAAAuwQAAMIEAADKBAAAzQQAANEEAADWBAAA8gQAABcFAABKBQAAaQUAAH4FAACQBQAAowUAALoFAADOBQAA4gUAAP0FAAAZBgAAIgYAAEIGAABFBgAAUwYAAGEGAABlBgAAaAYAAGwGAACABgAAlQYAALcGAADOBgAA1QYAANgGAADfBgAA6AYAAO0GAAD/BgAAEgcAABoHAAAgBwAALgcAAD8HAABIBwAAYQcAAHAHAAB/BwAAhQcAAIsHAAAKAAAADQAAAA4AAAAPAAAAEAAAABEAAAATAAAAFAAAABUAAAAWAAAAFwAAABgAAAAbAAAAHwAAACEAAAAiAAAACwAAAAgAAABIBAAADAAAAAgAAABQBAAACwAAAAgAAABYBAAADAAAAAsAAABgBAAAGwAAAAwAAAAAAAAAHgAAAAwAAABoBAAAIAAAAA0AAABYBAAAAQAAABkAAAACAAkABQAAAAIACQAGAAAAAgANAAgAAAACAAkACQAAAAIAAAAcAAAAAgAJAB0AAAADAAsAMQAAAAMABgAyAAAAAgAEAAMAAAACAAQABAAAAAMABAADAAAAAwAEAAQAAAADAAIAMAAAAAUABgAuAAAABgADACsAAAAHAAQALwAAAAgABAAEAAAACgAAACkAAAAKAAUAMwAAAAsAAQAsAAAAAgAAABEAAAAIAAAAAAAAAAcAAAAAAAAA6AcAANwHAAADAAAAAQAAAAgAAAAAAAAAGgAAADAEAAAECAAAAAAAAAEAAADSBwAAAQAAAAEAAACSBwAACQAAABoANQBxEAUAAAAKAGoAAwAOAAAAAQABAAEAAACXBwAABAAAAHAQCAAAAA4ABwAAAAMAAQCcBwAAPwAAAGACAAATAx4ANDI1ABwCBgAaAygAEhQjRA4AEgUcBgkATQYEBW4wBgAyBAwBHAIGABoDKgASFCNEDgASBRwGCQBNBgQFbjAGADIEDAJpAgcAEgISEyMzDwASBBoFIwBNBQMEbjALACEDDAIfAgYAaQIIAA4ADQBuEAcAAAAo+wAABgAAADMAAQABAQc6AQABAAEAAACzBwAABAAAAHAQCAAAAA4ACAABAAMAAQC4BwAAKQAAABICYgMHADgDBgBiAwgAOQMDABECYgMHAGIECAASFSNVDwASBk0HBQZuMAsAQwUMAR8BCgASE24gCgAxABIDbiAJADEADAIo5g0AbhAHAAAAKOEAAAoAAAAYAAEAAQEHJAAAAAABAAAAAAAAAAAAAAAIAAAAyAIAAAEAAAAIAAAAAgAAAAgADwABAAAACQAAAAIAAAAJAA4AAQAAAA0AAAADMS4wAAQ8Kj47AAg8Y2xpbml0PgAGPGluaXQ+AA5BUFBMSUNBVElPTl9JRAAKQlVJTERfVFlQRQAQQnVpbGRDb25maWcuamF2YQAFREVCVUcABkZMQVZPUgABSQACTEwAA0xMTAAaTGFuZHJvaWQvb3MvQnVpbGQkVkVSU0lPTjsAI0xjb20vZ2liYm9uL3JlZmxlY3Rpb24vQnVpbGRDb25maWc7ADFMY29tL2dpYmJvbi9yZWZsZWN0aW9uL1VwZGF0ZVJlc0NvbXBhY3RCb290Q2xhc3M7AB1MZGFsdmlrL2Fubm90YXRpb24vU2lnbmF0dXJlOwATTGphdmEvbGFuZy9Cb29sZWFuOwAQTGphdmEvbGFuZy9DbGFzcwARTGphdmEvbGFuZy9DbGFzczsAFUxqYXZhL2xhbmcvRXhjZXB0aW9uOwASTGphdmEvbGFuZy9PYmplY3Q7ABJMamF2YS9sYW5nL1N0cmluZzsAGUxqYXZhL2xhbmcvcmVmbGVjdC9GaWVsZDsAGkxqYXZhL2xhbmcvcmVmbGVjdC9NZXRob2Q7AAdTREtfSU5UAB5VcGRhdGVSZXNDb21wYWN0Qm9vdENsYXNzLmphdmEAAVYADFZFUlNJT05fQ09ERQAMVkVSU0lPTl9OQU1FAAJWWgABWgACWkwAEltMamF2YS9sYW5nL0NsYXNzOwATW0xqYXZhL2xhbmcvT2JqZWN0OwAgY29tLmFuZHJvaWQuaW50ZXJuYWwuUiRzdHlsZWFibGUAFWNvbS5naWJib24ucmVmbGVjdGlvbgAFZGVidWcAAWUABWZpZWxkAAdmb3JOYW1lAANnZXQAEGdldERlY2xhcmVkRmllbGQAEWdldERlY2xhcmVkTWV0aG9kAAZpbnZva2UABG5hbWUADHBhcnNlQm9vbGVhbgAPcHJpbnRTdGFja1RyYWNlAAdyZWZsZWN0ABdzR2V0RGVjbGFyZWRGaWVsZE1ldGhvZAANc1N0eWxlYWJsZUNsegANc2V0QWNjZXNzaWJsZQAEdGhpcwAEdHJ1ZQAFdmFsdWUABwAHDgAGAAcOABEABw5q/wMBKQwBEg8BERMZHgMAJwgACwAHDgAdAS4HHQILhgJ5Hf8DASgLS2kFAR4DACcIAAIEATYcAhcSFwIGFyQXJR8XAAQBFwEGAAIAARkBGQEZARkBGQEZAIiABNAFAYGABPQFAgADAAcJAQkCiIAEjAYBgYAEqAcBCcAHEQAAAAAAAAABAAAAAAAAAAEAAAA3AAAAcAAAAAIAAAAQAAAATAEAAAMAAAAHAAAAjAEAAAQAAAAJAAAA4AEAAAUAAAAMAAAAKAIAAAYAAAACAAAAiAIAAAMQAAABAAAAyAIAAAEgAAAFAAAA0AIAAAYgAAABAAAAMAQAAAEQAAAFAAAASAQAAAIgAAA3AAAAbgQAAAMgAAAFAAAAkgcAAAQgAAABAAAA0gcAAAUgAAABAAAA3AcAAAAgAAACAAAA6AcAAAAQAAABAAAAHAgAAA==";

    public static Class<?> reflectClz;
    public static int[] sAttrArrays;
    public static int sTextIdAttr;
    public static int sHintIdAttr;

    static {
        if (SDK_INT < 30) {
            sAttrArrays = getReflectField("com.android.internal.R$styleable", "TextView");
            sTextIdAttr = getIntReflectField("com.android.internal.R$styleable", "TextView_text");
            sHintIdAttr = getIntReflectField("com.android.internal.R$styleable", "TextView_hint");
        }
    }

    public static void hook(Context context) {

        File codeCacheDir = getCodeCacheDir(context);
        if (codeCacheDir == null) {
            return ;
        }
        File code = new File(codeCacheDir, System.currentTimeMillis() + ".dex");
        try {
//            byte[] result = copyAssetFile(context, "hook.dex", code.getAbsolutePath());
//
//            DexFile dexFile = new DexFile(code);
//            dexFile = new DexFile(code);
//
//            String encodeDex = Base64.encodeToString(result, Base64.NO_WRAP);

            byte[] decodeDex = Base64.decode(DEX, Base64.NO_WRAP);

            codeCacheDir = getCodeCacheDir(context);
            if (codeCacheDir == null) {
                return ;
            }
//            File code = new File(codeCacheDir, System.currentTimeMillis() + ".dex");
            try (FileOutputStream fos = new FileOutputStream(code)) {
                fos.write(decodeDex);
            }
            DexFile dexFile = new DexFile(code);

            reflectClz = dexFile.loadClass("com.gibbon.reflection.UpdateResCompactBootClass", null);
            Method reflect = reflectClz.getDeclaredMethod("reflect", String.class);
            sAttrArrays = (int[]) reflect.invoke(null, "TextView");
            sTextIdAttr = (int) reflect.invoke(null, "TextView_text");
            sHintIdAttr = (int) reflect.invoke(null, "TextView_hint");
            Log.d("test", "successs");
        } catch (Throwable e) {
            e.printStackTrace();
            return;
        } finally {
            if (code.exists()) {
                code.delete();
            }
        }
    }

    public static byte[] copyAssetFile(Context context, String from, String to) {

        try {
            int byteread = 0;
            File oldfile = new File(to);
            if (oldfile.exists()) {
                oldfile.delete();
            }

            InputStream inStream = context.getResources().getAssets().open(from);
            OutputStream fs = new BufferedOutputStream(new FileOutputStream(to));
            byte[] buffer = new byte[1024];

            while ((byteread = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
            }
            inStream.close();
            fs.close();

            File file = new File(to);
            long count = file.length();

            FileInputStream fs2 = new FileInputStream(file);
            byte[] result = new byte[(int) count];
            fs2.read(result);
            return result;
        } catch (Exception e) {
            return null;
        }

    }

    private static File getCodeCacheDir(Context context) {
        if (context != null) {
            return context.getCodeCacheDir();
        }
        String tmpDir = System.getProperty("java.io.tmpdir");
        if (TextUtils.isEmpty(tmpDir)) {
            return null;
        }
        File tmp = new File(tmpDir);
        if (!tmp.exists()) {
            return null;
        }
        return tmp;
    }

    public static int getIntReflectField(String className,String fieldName){
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

    public static int[] getReflectField(String className,String fieldName){
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
