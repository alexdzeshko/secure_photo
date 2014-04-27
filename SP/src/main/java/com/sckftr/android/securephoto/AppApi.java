package com.sckftr.android.securephoto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.MutableContextWrapper;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sckftr.android.utils.UI;
import com.sckftr.android.utils.net.Network;
import com.squareup.picasso.Picasso;

import java.util.Locale;


/**
 * Common application-scope AppApi API.
 *
 * @author Aliaksandr_Litskevich
 */

public class AppApi extends MutableContextWrapper implements AppConst {

    static final String ID_TYPE_STRING = "string";

    private Picasso picasso;

    private Gson gson;

    public Gson gson() {
        if(gson == null) gson = new GsonBuilder().create();
        return gson;
    }

    public AppApi(Context context) {
        super(context.getApplicationContext());

        picasso = Picasso.with(getApplicationContext());
    }

    public Picasso images() {
        return picasso;
    }

    public String string(int resId, Object... args) {
        return resId > 0 ? getString(resId, args) : null;
    }

    public String qstring(int resId, int i) {
        return resId > 0 ? getResources().getQuantityString(resId, i, i) : null;
    }

	public int color(int resId) {
		return resId > 0 ? getResources().getColor(resId) : getResources().getColor(android.R.color.darker_gray);
	}

    public String[] stringArray(int resId) {
        return getResources().getStringArray(resId);
    }

    public String putPreference(String key, String value) {
        preferences().edit().putString(key, value).commit();
        return value;
    }

    public int putPreference(String key, int value) {
        preferences().edit().putInt(key, value).commit();
        return value;
    }

    public boolean putPreference(String key, boolean value) {
        preferences().edit().putBoolean(key, value).commit();
        return value;
    }

    public SharedPreferences preferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    public String getPreferenceString(String key, String defValue) {
        return preferences().getString(key, defValue);
    }

    public int getPreferenceInt(String key, int defValue) {
        return preferences().getInt(key, defValue);
    }

    public boolean getPreferenceBool(String key, boolean defValue) {
        return preferences().getBoolean(key, defValue);
    }

    /**
     * Resolves type/key into resource Id.
     *
     * @param key  resource name
     * @param type resource type
     * @return resId or 0 if nor found.
     */
    public int getId(String key, String type) {
        return getResources().getIdentifier(key, type, getPackageName());
    }

    /**
     * @return application locale
     */
    public Locale getLocale() {
        return Locale.ROOT;
    }

    public void showError(Activity context, ERROR key) {
        UI.showAlert(context
                , string(getId("ERR_" + key.name() + "_Title", ID_TYPE_STRING))
                , string(getId("ERR_" + key.name(), ID_TYPE_STRING))
                , null);
    }


    public boolean isConnected(Activity activity) {

        if (!Network.checkConnected(this)) {
            API.get().showError(activity, ERROR.NO_NETWORK);
            return false;
        }

        return true;

    }

    public void call(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getBaseContext().startActivity(intent);
    }

    public void email(Activity context, String emailAddress) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("mailto:"+emailAddress));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "Send via..."));
    }

    public void web(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
