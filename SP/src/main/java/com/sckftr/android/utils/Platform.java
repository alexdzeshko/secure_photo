package com.sckftr.android.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.BuildConfig;


public class Platform {
	
	/** The default prefix used to resolve layout resources ids. */
	public final static String RESOURCE_TYPE_LAYOUT = "layout";

	/** The default prefix used to resolve drawable resources ids. */
	public final static String RESOURCE_TYPE_DRAWABLE = "drawable";

	/** The default prefix used to resolve view resources ids. */
	public final static String RESOURCE_TYPE_ID = "id";

	/** The default prefix used to resolve menu resources ids. */
	public static final String RESOURCE_TYPE_MENU = "menu";
	
	/**
	 * Helper method to setup the default strict mode if running in debug mode
	 */
	public static void setupStrictMode() {
		if (BuildConfig.DEBUG) {
			StrictMode.enableDefaults();
		}
	}
    public static int getSdkInt() {
        if (Build.VERSION.RELEASE.startsWith("1.5"))
            return 3;

        return Build.VERSION.SDK_INT;
    }

    /**
     * Gets application meta value by name.
     *
     * @param key meta name
     * @param defValue default value
     * @return meta valye ir defValue if not found.
     */
    public static String meta(Context context, String packageName, String key, String defValue) {
        String result = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            result = bundle==null ? null :bundle.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            //STAConst.Log.e(TAG, "Failed to load meta-data, NameNotFound: " + key, e);
        } catch (NullPointerException e) {
            //STAConst.Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage(), e);
        }
        return result==null ? defValue : result;
    }

    /**
     * Gets the onActivityStop/fragment name key that can be used for default addressing in many features. For example: getting resources for UI layout, menu, title,
     * resolving keys for help, analytics etc.
     *
     * By default it's based on onActivityStop's short class name excluding "Activity" postfix.
     *
     *
     * @return the onActivityStop/fragment name key
     */
    public static String getNameKey(Class<?> aClass) {

        String s = aClass.getSimpleName();
        s = Strings.getNotEmpty(Strings.stringBeforeOrNull(s, "Activity"), Strings.stringBeforeOrNull(s, "Fragment"), s);
        return Strings.getKeyByName(s);
    }


    /**
     * Gets the default menu layout id based on key from {@code #getNameKey()} concatenated with {@code MENU_LAYOUT_NAME_POSTFIX}.
     *
     * @return the menu layout id or zero if not exists
     */
    public static <E> int getResourceIdFor(Object target, String type) {
        return getResourceIdFor(target, type, 0);

    }

    public static <E> int getResourceIdFor(Object target, String type, int defId) {
        final int id = AppConst.API.get().getId(getNameKey(target.getClass()), type);
        return id==0?defId:id;
    }

    public static boolean hasCamera(Context context){
        return context != null && context.getPackageManager() != null
                && (hasJB42() ? context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) : context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA));
    }

    public static boolean hasKK(){
        return getSdkInt() >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasJB43(){
        return getSdkInt() >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    public static boolean hasJB42(){
        return getSdkInt() >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static boolean hasJB(){
        return getSdkInt() >= Build.VERSION_CODES.JELLY_BEAN;
    }
}
