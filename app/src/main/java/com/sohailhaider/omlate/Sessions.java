package com.sohailhaider.omlate;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Sohail Haider on 09-Dec-16.
 */
public class Sessions {
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    public Sessions(Context context) {
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        editor = settings.edit();
    }
    public void putString(String name, String Value) {
        editor.putString(name, Value);
    }
    public String getString(String name) {
        return settings.getString(name, "");
    }
    public void removeString(String name) {
        editor.remove(name);
    }


}
