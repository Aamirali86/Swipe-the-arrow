package com.techytec.swipethearrow;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreferences {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static final String PREF_FILE_NAME = "MyPrefs";

    public void saveClassicBestScore(int bestScore, Context context) {
        preferences = context.getSharedPreferences(PREF_FILE_NAME,
                Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putInt("classicBestScore", bestScore);
        editor.commit();
    }

    public int retreiveClassicBestScore(Context context) {
        preferences = context.getSharedPreferences(PREF_FILE_NAME,
                Context.MODE_PRIVATE);
        return preferences.getInt("classicBestScore", 0);
    }

    public void saveExpertBestScore(int bestScore, Context context) {
        preferences = context.getSharedPreferences(PREF_FILE_NAME,
                Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putInt("expertBestScore", bestScore);
        editor.commit();
    }

    public int retreiveExpertBestScore(Context context) {
        preferences = context.getSharedPreferences(PREF_FILE_NAME,
                Context.MODE_PRIVATE);
        return preferences.getInt("expertBestScore", 0);
    }

    public void saveTotalStars(int remainingStars, Context context) {
        preferences = context.getSharedPreferences(PREF_FILE_NAME,
                Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putInt("totalStars", remainingStars);
        editor.commit();
    }

    public int retreiveTotalStars(Context context) {
        preferences = context.getSharedPreferences(PREF_FILE_NAME,
                Context.MODE_PRIVATE);
        return preferences.getInt("totalStars", 20);
    }

    public boolean isNewUser(Context context) {
        preferences = context.getSharedPreferences(PREF_FILE_NAME,
                Context.MODE_PRIVATE);
        return preferences.getBoolean("isNew", true);
    }

    public void setNewUser(boolean isNew, Context context) {
        preferences = context.getSharedPreferences(PREF_FILE_NAME,
                Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean("isNew", isNew);
        editor.commit();
    }

    public boolean isPremiumUser(Context context) {
        preferences = context.getSharedPreferences(PREF_FILE_NAME,
                Context.MODE_PRIVATE);
        return preferences.getBoolean("isPremium", false);
    }

    public void setPremiumUser(boolean isPremium, Context context) {
        preferences = context.getSharedPreferences(PREF_FILE_NAME,
                Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean("isPremium", isPremium);
        editor.commit();
    }

}
