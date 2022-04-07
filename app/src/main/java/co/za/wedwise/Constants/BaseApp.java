package co.za.wedwise.Constants;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;

/**
 * Created by za on 12/12/2018.
 */

public class BaseApp extends Application {
    private static BaseApp mInstance;
    public SharedPreferences preferences;
    public String prefName = "GoEstate";
    public BaseApp() {
        mInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
//        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized BaseApp getInstance() {
        return mInstance;
    }

    public void saveIsLogin(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putBoolean("IsLoggedIn", flag);
        editor.apply();
    }

    public boolean getIsLogin() {
        preferences = this.getSharedPreferences(prefName, 0);
        boolean isLogin = preferences.getBoolean("IsLoggedIn",false);
        return preferences.getBoolean("IsLoggedIn", false);
    }
}
