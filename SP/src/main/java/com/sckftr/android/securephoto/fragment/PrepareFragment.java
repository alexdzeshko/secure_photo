package com.sckftr.android.securephoto.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.sckftr.android.securephoto.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.activity_prepare_photo)
public class PrepareFragment extends Fragment {

    @FragmentArg Bundle result;

    @ViewById(R.id.preview) ImageView mImageView;

    @AfterViews void init(){

//        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) result.get("data");
        mImageView.setImageBitmap(imageBitmap);
    }

    public static Fragment build(Bundle result){
        return PrepareFragment_.builder().result(result).build();
    }
}
