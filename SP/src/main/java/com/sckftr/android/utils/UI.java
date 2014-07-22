package com.sckftr.android.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.utils.net.Network;

/**
 * Bundle of UI-related helper methods.
 * <p/>
 * Used to avoid duplicate code and for readability.
 *
 * @author Aliaksandr_Litskevic
 */
public class UI implements AppConst {

    public static final String NO_BACK_STACK = null;

    public static final LayoutParams FILL_LAYOUT = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT);


    public static int addFragment(FragmentManager fragmentManager, int id, Fragment fragment, String key) {

        if (id == 0) {
            id = R.id.frame;
        }

        if (key == NO_BACK_STACK) {
            return fragmentManager.beginTransaction().add(id, fragment).commit();
        }

        fragmentManager.popBackStack(key, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        return fragmentManager.beginTransaction()
                .add(id, fragment, key).addToBackStack(key)
                .commit();

    }

    public static void showHint(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public static void showHint(Context context, int resId) {
        showHint(context, context.getString(resId));
    }

    public static void setVisible(View view, boolean flag) {
        if (null != view) {
            view.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        }

    }

    public static void setShown(View view, boolean flag) {
        if (null != view) {
            view.setVisibility(flag ? View.VISIBLE : View.GONE);
        }

    }

    public static String getText(View view) {
        return (view == null) ? null : ((TextView) view).getText().toString();
    }

    public static boolean setTextColor(View v, int color) {
        if (v instanceof TextView) {
            ((TextView) v).setTextColor(color);
            return true;
        }

        return false;
    }

    public static boolean setText(View view, CharSequence text) {
        if (view != null) {
            ((TextView) view).setText(text);
            return true;
        }
        return false;
    }

    /**
     * Displays a simple alert dialog with the given text and title.
     *
     * @param ctx      the context, required
     * @param title    Alert dialog title
     * @param message  Alert dialog message
     * @param callback OnDismissListener callback instance
     * @return the alert dialog created and display
     */
    public static AlertDialog showAlert(final Activity ctx, final CharSequence title, final CharSequence message, DialogInterface.OnDismissListener callback) {
        Builder alertBuilder = new Builder(ctx);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        final AlertDialog alert = alertBuilder.create();

        alert.setOnDismissListener(callback);

        ctx.runOnUiThread(new Runnable() {
            public void run() {
                alert.show();
            }
        });

        return alert;
    }

    public static AlertDialog showAlert(final Activity ctx, final CharSequence title, final CharSequence message, DialogInterface.OnClickListener onOkCallback, DialogInterface.OnClickListener onCancelCallback) {
        Builder alertBuilder = new Builder(ctx);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.setNegativeButton(android.R.string.ok, onOkCallback)
                .setPositiveButton(android.R.string.cancel, onCancelCallback);
        final AlertDialog alert = alertBuilder.create();

        ctx.runOnUiThread(new Runnable() {
            public void run() {
                alert.show();
            }
        });

        return alert;
    }

    /**
     * Creates the select dialog.
     *
     * @param title       the title
     * @param items       the mItems
     * @param checkedItem the checked item
     * @param delegate    the delegate
     * @param activity
     * @return the dialog
     */
    public Dialog createSelectDialog(final String title, final CharSequence[] items, final int checkedItem,
                                     final DialogInterface.OnClickListener delegate, Context activity) {

        Builder builder = new Builder(activity);
        if (title != null) {
            builder.setTitle(title);
        }

        builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                delegate.onClick(dialog, which);
            }
        });

        builder.setNegativeButton(activity.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(final DialogInterface d, final int w) {

                d.dismiss();
            }
        });

        return builder.create();
    }

    /**
     * Creates the select dialog.
     */
    public Dialog createConfirmDialog(final String title, final CharSequence message, final CharSequence positiveButtonCaption,
                                      final DialogInterface.OnClickListener delegate, Context activity) {

        return (new Builder(activity)).setTitle(title == null ? Strings.EMPTY : title).setMessage(message == null ? Strings.EMPTY : message)
                .setPositiveButton(positiveButtonCaption, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        delegate.onClick(dialog, which);
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                }).create();
    }

    public static boolean getChecked(View view) {
        return view != null && ((CheckBox) view).isChecked();

    }

    /**
     * Method used to hide/unhide status bar.
     *
     * @param activityRef
     * @param isStatusBarRequired
     */
    public static void toggleStatusBar(Activity activityRef, boolean isStatusBarRequired) {
        WindowManager.LayoutParams attrs = activityRef.getWindow().getAttributes();
        if (isStatusBarRequired) {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        } else {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        activityRef.getWindow().setAttributes(attrs);
    }


    public static InputMethodManager getInputManager(Context context) {
        return ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE));
    }

    public static void sendBroadcast(Context context, Intent intent) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

//    public static void displayImage(String url, View view) {
//        displayImage(url, (ImageView) view, null);
//    }

//    public static void displayImage(final String uri, final ImageView icon, final ProgressBar progress) {
//
//        if (uri == null) {
//            icon.setImageResource(R.drawable.logo);
//            return;
//        }
//
//        STAConst.API.images().displayImage(uri, icon, new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String imageUri, View view) {
//
//                setVisible(progress, true);
//
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//
//                setVisible(progress, false);
//
//                ImagesData.Size lower = ImagesData.Size.lower(imageUri);
//
//                if (lower != null) {
//
//                    displayImage(UiUtil.resizedImageUrl(uri, lower), icon, progress);
//
//                } else {
//
//                    icon.setImageResource(R.drawable.logo);
//
//                }
//            }
//
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//
//                setVisible(progress, false);
//
//            }
//
//            @Override
//            public void onLoadingCancelled(String imageUri, View view) {
//            }
//        });
//
//    }


    public static void sendBroadcast(Context context, String action, Parcelable result) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA.RESULT, result);
        sendBroadcast(context, intent);

    }

    public static void sendSimpleStringBroadcast(Context context, String action, String value) {
        Bundle params = new Bundle();
        params.putString(EXTRA.VALUES, value);
        sendBroadcast(context, action, params);
    }

    /**
     * To work in pair with `sendSimpleStringBroadcast`.
     *
     * @param fn function to hanle string passed
     * @return wrapper function
     */
    public static Function<Bundle, Boolean> simpleStringReciever(final Function<String, Boolean> fn) {
        return new Function<Bundle, Boolean>() {
            @Override
            public Boolean apply(Bundle params) {
                return fn.apply(params.getString(EXTRA.VALUES));
            }
        };
    }

    public static void showError(Activity context, String errorMessage, final Procedure<DialogInterface> cb) {
        showAlert(context, context.getString(R.string.ERR_UNKNOWN_TITLE), errorMessage, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (cb != null) {
                    cb.apply(dialog);
                }
            }
        });
    }

    public static void startLocationSettings(Activity activity) {

        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        activity.startActivity(intent);

    }

    public static boolean ensureNetwork(Activity activity) {

        boolean flag = Network.checkConnected(activity);

        if (!flag) {

            API.get().showError(activity, ERROR.NO_NETWORK);//TODO
        }

        return flag;
    }

    public static void initTabs(final ActionBar actionBar, String[] modes, final Procedure<Integer> handler) {

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                handler.apply(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        // Add 2 tabs, specifying the tab's text and TabListener
        for (int i = 0; i < modes.length; i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setTag(i)
                            .setText(modes[i])
                            .setTabListener(tabListener)
            );
        }
    }

    public static void initTabs(final ActionBar actionBar, String[] modes, ActionBar.TabListener listener) {

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Add 2 tabs, specifying the tab's text and TabListener
        for (int i = 0; i < modes.length; i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setTag(i)
                            .setText(modes[i])
                            .setTabListener(listener)
            );
        }
    }

    public static PopupWindow showPopupText(View v, Spanned s) {

        Context ctx = v.getContext();
        int unit3 = UiUtil.getUnitInPixels(ctx) * 3;

        TextView tv;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(unit3, unit3, unit3, unit3);

        tv = new TextView(ctx);
        tv.setPadding(unit3, unit3, unit3, unit3);
        tv.setText(s);

        RelativeLayout layout1 = new RelativeLayout(ctx);
        layout1.addView(tv, params);
        layout1.setBackgroundColor(ctx.getResources().getColor(R.color.bg_popup));

        PopupWindow popUp = new PopupWindow(layout1, 50, 50);
        popUp.setWindowLayoutMode(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        popUp.showAsDropDown(v, 0, UiUtil.getPx(ctx, 5f));

        return popUp;
    }

}
