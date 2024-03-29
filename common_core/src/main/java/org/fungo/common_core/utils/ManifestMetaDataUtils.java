/**
 * Copyright (c) 2013 NewSenceNetwork.Co.Ltd. 
 * 
 * All rights reserved.
 */
package org.fungo.common_core.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * @author Xutao
 *
 */
public class ManifestMetaDataUtils {
    private static Object readKey(Context context, String keyName) {
        try {
            ApplicationInfo appi =  context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = appi.metaData;
            Object value = bundle.get(keyName);
            return value;
        } catch (Exception e) {
            return "null";
        }
    }
    
    public static int getInt(Context context, String keyName) {
        return (Integer)readKey(context, keyName);
    }
    
    public static String getString(Context context, String keyName ) {
        return readKey(context, keyName) + "";
    }
    
    public static Boolean getBoolean(Context context, String keyName) {
        return (Boolean) readKey(context, keyName);
    }
    
    public static Object get(Context context, String keyName) {
        return readKey(context, keyName);
    }
}