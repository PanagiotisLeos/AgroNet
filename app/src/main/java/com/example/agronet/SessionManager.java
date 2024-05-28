package com.example.agronet;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.agronet.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class SessionManager {
    public static final String PREF_NAME = "AppSession";
    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USER_ID = "UserId";
    public static final String KEY_USER_TYPE = "UserType";

    SharedPreferences pref;
    static SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME,   PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String userId, String userType) {
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

    public String getUserId() {
        return pref.getString(KEY_USER_ID,null);
    }

    public String getUserType() {
        return KEY_USER_TYPE;
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    public User fetchUserDetails(String userId, String userType) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        String query = userType.equals("0") ?
                "SELECT * FROM customer WHERE customer_id = ?" :
                "SELECT * FROM farmer WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if ("0".equals(userType)) {
                    return new Customer(
                            rs.getInt("customer_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getInt("phone")

                    );
                } else if ("1".equals(userType)) {
                    return new Farmer(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("location"),
                            rs.getBytes("prof_image"),
                            rs.getString("farmer_type"),
                            rs.getString("description")

                    );
                }
            }
        }
        return null;
    }
}
