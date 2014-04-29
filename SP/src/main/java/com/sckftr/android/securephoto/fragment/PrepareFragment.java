package com.sckftr.android.securephoto.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.sckftr.android.securephoto.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.activity_prepare_photo)
public class PrepareFragment extends Fragment {

    @FragmentArg Bundle result;

    @ViewById(R.id.image_view_prepare) ImageView mImageView;

    @ViewById(R.id.edit_text_prepare) EditText mEditText;

    @AfterViews void init(){

        mEditText.setText(String.valueOf(System.currentTimeMillis()));

//        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) result.get("data");
        mImageView.setImageBitmap(imageBitmap);
    }

    public static Fragment build(Bundle result){
        return PrepareFragment_.builder().result(result).build();
    }
}
