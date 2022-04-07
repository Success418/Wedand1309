package co.za.wedwise.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private final SharedPreferences.Editor editor;
    Context mcontext;
    SharedPreferences sharedPreferences;

    public Prefs(Context mcontext) {
        this.mcontext = mcontext;
        sharedPreferences = mcontext.getSharedPreferences("wedwise", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setPrefString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getPrefString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void setPrefInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public int getPrefInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public void setPrefBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getPrefBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }
}
