package com.sckftr.android.securephoto.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.sckftr.android.app.adapter.BaseArrayAdapter;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.fragment.base.BaseSettingsFragment;
import com.sckftr.android.securephoto.helper.UserHelper;
import com.sckftr.android.utils.Procedure;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

import java.util.Random;

/**
 * Created by Dzianis_Roi on 29.08.2014.
 */
@EFragment
public class SettingsFragment extends BaseSettingsFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = "SettingsFragment";
    public static final String KEY_ALGORITHM = "algorithm";

    @AfterViews
    void onAfterViews() {

        setTitle(R.string.label_settings);

        UserHelper.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (UserHelper.isPhotosRestoring()) aq.id(R.id.changePassword).enabled(false);
    }

    @Click
    void changePassword() {
        getBaseActivity().loadFragment(ChangePasswordFragment.build(), true, ChangePasswordFragment.TAG);
    }

    @Click
    void changeAlgorithm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Change cipher algorithm")
                .setSingleChoiceItems(new BaseArrayAdapter<Algorithm>(getContext(), Algorithm.values()) {
                    @Override
                    protected int getLayoutRes() {
                        return android.R.layout.simple_list_item_checked;
                    }

                    @Override
                    public void bindView(View view, Algorithm model) {
                        ((TextView) view.findViewById(android.R.id.text1)).setText(model.name());
                    }
                }, API.get().getPreferenceInt(KEY_ALGORITHM, 0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int i = API.get().getPreferenceInt(KEY_ALGORITHM, 0);
                        if (i != which) {
                            API.get().putPreference(KEY_ALGORITHM, which);
                            showRecryptingDialog();
                        }
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    private void showRecryptingDialog() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Recrypting...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.show();
        API.data().getCryptedImagesCount(getContext(), new Procedure<Integer>() {
            @Override
            public void apply(final Integer dialog) {
                progressDialog.setMax(dialog);
                progressDialog.show();

                new AsyncTask<Integer, Integer, Void>() {
                    @Override
                    protected Void doInBackground(Integer... params) {
                        Algorithm current = Algorithm.getCurrent();
                        int count = params[0];
                        int ready = 0;
                        while (count >= 0) {
                            try {
                                int i = new Random().nextInt(2);
                                i = i < 1 ? 1 : i;
                                Thread.sleep(current.time * 10 * i);
                                ready++;
                                publishProgress(ready);
                                count--;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(Integer... values) {
                        progressDialog.setProgress(values[0]);
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        progressDialog.dismiss();
                    }
                }.execute(dialog);

            }
        });

    }

    @Override
    protected Drawable getBackground() {
        return new ColorDrawable(getResources().getColor(android.R.color.white));
    }

    @Override
    protected String getFragmentTag() {
        return TAG;
    }

    public static SettingsFragment build() {
        return SettingsFragment_.builder().build();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        boolean restored = sharedPreferences.getBoolean(KEYS.PREF_PHOTOS_RESTORING, false);

        Log.d(TAG, restored + "");

        aq.id(R.id.changePassword).enabled(!restored);
    }

    public enum Algorithm {
        AES(100),
        DES(200);

        int time;

        Algorithm(int time) {
            this.time = time;
        }

        public static Algorithm getCurrent() {
            return values()[API.get().getPreferenceInt(KEY_ALGORITHM, 0)];
        }

    }
}
