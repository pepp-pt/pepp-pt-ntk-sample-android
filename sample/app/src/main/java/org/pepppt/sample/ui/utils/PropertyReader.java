package org.pepppt.sample.ui.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {
    public static String TAG = PropertyReader.class.getSimpleName();
    private Context context;
    private Properties properties;

    public PropertyReader(Context context) {
        this.context = context;
        properties = new Properties();
    }

    public Properties getMyProperties(String file) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(file);
            properties.load(inputStream);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return properties;
    }
}
