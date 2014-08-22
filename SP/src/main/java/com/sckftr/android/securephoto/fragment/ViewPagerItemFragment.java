package com.sckftr.android.securephoto.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.sckftr.android.app.fragment.BaseFragment;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.db.Image;
import com.sckftr.android.securephoto.image.CryptoBitmapLoader;
import com.sckftr.android.utils.UI;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import uk.co.senab.photoview.PhotoViewAttacher;

@EFragment(R.layout.view_pager_item)
public class ViewPagerItemFragment extends BaseFragment {

    @FragmentArg
    Image image;

    private final CryptoBitmapLoader mCryptoLoader = new CryptoBitmapLoader();

    @AfterViews
    void onAfterViews() {

        View view = getView();

        if (view != null) {

            ImageView mImageView = (ImageView) view.findViewById(R.id.imageView);

            Bundle params = new Bundle(getBaseActivity().getClassLoader());

            params.putString(EXTRA.IMAGE, image.getKey());
            params.putInt(EXTRA.ORIENTATION, image.getOrientation());

            UI.displayImage(mImageView, image.getFileUri().toString(), mImageView.getWidth(), mImageView.getHeight(), params, null, mCryptoLoader);

            new PhotoViewAttacher(mImageView).update();
        }
    }

    public static ViewPagerItemFragment build(Image image) {

        return ViewPagerItemFragment_.builder().image(image).build();

    }

}
