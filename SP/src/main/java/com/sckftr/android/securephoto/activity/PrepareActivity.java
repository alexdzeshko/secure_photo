package com.sckftr.android.securephoto.activity;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.db.Image;
import com.sckftr.android.securephoto.helper.ImageHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_prepare_photo)
public class PrepareActivity extends Activity implements OnClickListener {

    @ViewById(R.id.preview) ImageView mImageView;

    @Extra Image image;

    @AfterViews void onAfterViews() {

        getActionBar().hide();

        ImageHelper.load(image.getURI(), mImageView);

        AppConst.Log.d("PREPARE", "uri: %s", image.getUri());

    }


    @Click(R.id.submit)
    public void onClick(View v) {

        AppConst.API.data().cryptonize(image, null);

        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //todo remove as per stackoverflow
//        TakePhotoHelper.deleteImage(image.getURI());
    }

    public static void start(Context context, Image image) {
        PrepareActivity_.intent(context).image(image).start();
    }

}
