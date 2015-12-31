package utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;




public class PrefStore {

    private Context mAct;
    private String PREFS_NAME = "transport";

    public PrefStore(Context context) {
        this.mAct = context;
    }

    public void SaveLoginData(String mobile, String password) {
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("mobile", mobile);
        editor.putString("password", password);
        // Commit the edits!
        editor.commit();
        log("PrefStore" + "userName " + mobile);
    }

    // ================================ GET MOBILE NUMBER
    // ===================================
    public String getUserName() {
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        String userName = settings.getString("mobile", null);
        log("PrefStore" + "mobile " + userName);
        return userName;
    }

    public String getUserPassword() {
        // Restore preferences
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        String password = settings.getString("password", null);
        log("PrefStore" + "password " + password);
        return password;
    }

    public void RemoveLoginData() {
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("userName");
        editor.remove("password");
        // Commit the edits!
        editor.commit();
    }

    public int getInt(String key) {

        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        int userName = settings.getInt(key, 0);

        return userName;
    }
    public void setInt(String key, int value) {
        SharedPreferences mPrefs = mAct.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public String getLoginStatus() {
        // Restore preferences
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        String loginValid = settings.getString("auth_code", null);

        return loginValid;
    }

    public void setLoginStatus(String loginValid) {
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("auth_code", loginValid);
        // Commit the edits!
        editor.commit();
        log("PrefStore" + "loginValid " + loginValid);
    }

    public void setRememberMe(boolean rememberMe) {
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("rememberMe", rememberMe);
        editor.commit();
        log("PrefStore " + " rememberMe " + rememberMe);
    }

    public Boolean getRememberMe() {
        // Restore preferences
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        boolean rememberMe = settings.getBoolean("rememberMe", false);
        log(" PrefStore " + " rememberMe " + rememberMe);
        return rememberMe;
    }

    public void setShowUserID(String id, boolean b) {
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(id, b);
        // Commit the edits!
        editor.commit();
        log("PrefStore " + " rememberMe " + b);
    }

    public void log(String string) {
     /*   if (BuildConfig.DEBUG)*/
            Log.i("twipply", string);

    }

	/*
	 * public String getFBToken() { SharedPreferences mPrefs =
	 * mAct.getSharedPreferences(PREFS_NAME, 0); String access_token =
	 * mPrefs.getString("access_token", null); return access_token; }
	 * 
	 * public long getFBTokenExpire() { SharedPreferences mPrefs =
	 * mAct.getSharedPreferences(PREFS_NAME, 0); long expires =
	 * mPrefs.getLong("access_expires", 0); return expires;
	 * 
	 * }
	 */

    public ArrayList<String> getFBId() {
        ArrayList<String> hm = new ArrayList<String>();
        SharedPreferences mPrefs = mAct.getSharedPreferences(PREFS_NAME, 0);
        String fb_id = mPrefs.getString("fb_id", null);
        String fb_email = mPrefs.getString("fb_email", null);
        hm.add(fb_id);
        hm.add(fb_email);

        return hm;

    }

	/*
	 * public void setFBData(String accessToken, long accessExpires, String id,
	 * String email) {
	 * 
	 * SharedPreferences mPrefs = mAct.getSharedPreferences(PREFS_NAME, 0);
	 * SharedPreferences.Editor editor = mPrefs.edit();
	 * editor.putString("access_token", accessToken); editor.putString("fb_id",
	 * id); editor.putString("fb_email", email);
	 * editor.putLong("access_expires", accessExpires); editor.commit();
	 * 
	 * }
	 */

	/*
	 * public void cleanFBSession() { SharedPreferences settings =
	 * mAct.getSharedPreferences(PREFS_NAME, 0); SharedPreferences.Editor editor
	 * = settings.edit(); editor.remove("access_token");
	 * editor.remove("access_expires"); // Commit the edits! editor.commit(); }
	 */

    public String getEmailID() {
        // Restore preferences
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        String userName = settings.getString("emailID", null);
        log("PrefStore" + "userName " + userName);
        return userName;
    }

    public void setEmailID(String email) {

        SharedPreferences mPrefs = mAct.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("emailID", email);
        editor.commit();

    }

    public String getCommonUserName(String userName) {
        // Restore preferences
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        String mUserName = settings.getString(userName, null);
        log("PrefStore" + "userName " + mUserName);
        return mUserName;
    }

    public void setCommonUserName(String title, String userName) {

        SharedPreferences mPrefs = mAct.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(title, userName);
        editor.commit();

    }

    public void setUserName(String userName) {

        SharedPreferences mPrefs = mAct.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("userName", userName);
        editor.commit();

    }

    // ======================= SAVE LANGAUGE PREFERENCE
    // ================================
    public void saveLanguagePreference(String language) {
        SharedPreferences mPrefs = mAct.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("Language", language);
        editor.commit();
    }

    // ========================= GET LANGAUGE PREFERENCE
    // ===============================
    public String getLanguagePreference() {
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        String mLanguageSelected = settings.getString("Language", null);
        log("PrefStore" + "Language selected is  " + mLanguageSelected);
        return mLanguageSelected;
    }

    // ================ GET & SET STRING =================================//

    public String getString(String key) {

        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        String userName = settings.getString(key, "");

        return userName;
    }

    public void setString(String key, String value) {
        SharedPreferences mPrefs = mAct.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void clear()
    {
        SharedPreferences mPrefs = mAct.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.clear();
        editor.commit();
    }
    public void setBoolean(String removeAds, boolean mIsPremium) {
        SharedPreferences mPrefs = mAct.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(removeAds, mIsPremium);
        editor.commit();

    }

}