package com.example.agronet;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    private static final String PREF_NAME = "AppSession";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_USER_ID = "UserId";
    private static final String KEY_USER_TYPE = "UserType";

    SharedPreferences pref;
    static SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static void createLoginSession(String userId, String userType) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_TYPE, userType);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
        user.put(KEY_USER_TYPE, pref.getString(KEY_USER_TYPE, null));
        return user;
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

}
