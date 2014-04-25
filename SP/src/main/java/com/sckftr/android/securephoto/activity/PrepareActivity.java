package com.sckftr.android.securephoto.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sckftr.android.securephoto.Application;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.helper.ImageHelper;
import com.sckftr.android.securephoto.helper.TakePhotoHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import by.deniotokiari.core.helpers.ToastHelper;
import by.deniotokiari.core.service.Request;
import by.deniotokiari.core.service.SourceService;

@EActivity(R.layout.activity_prepare_photo)
public class PrepareActivity extends Activity implements OnClickListener {

    @ViewById(R.id.image_view_prepare) ImageView mImageView;
    @ViewById(R.id.edit_text_prepare) EditText mEditText;

    @Extra Uri mUri;

    @AfterViews void onAfterViews(){
        getActionBar().hide();

        ImageHelper.setBitmapByUri(mUri, mImageView);

        mEditText.setText(String.valueOf(System.currentTimeMillis()));
    }


    @Click(R.id.button_prepare_ok)
    public void onClick(View v) {
        String key = mEditText.getText().toString();
        if (key != null && key.trim().length() != 0) {
            Request<Object[], Object[], ContentValues> request = new Request<Object[], Object[], ContentValues>(new Object[]{mUri, key},
                    Application.SOURCE.THROUGH, Application.PROCESSOR.IMAGE, true);
            SourceService.execute(this, request);
            try {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
            } catch (Exception e) {
                // kitkat security issue
            }
            finish();
        } else {
            ToastHelper.show(this, "Input key!", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        TakePhotoHelper.deleteImage(mUri);
    }

    public static void start(Context context, Uri uri){
        PrepareActivity_.intent(context).mUri(uri).start();
    }

}
