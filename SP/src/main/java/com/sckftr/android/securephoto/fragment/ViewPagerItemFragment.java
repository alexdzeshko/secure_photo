package com.sckftr.android.securephoto.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.sckftr.android.app.fragment.BaseFragment;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.activity.DetailActivity;
import com.sckftr.android.securephoto.db.Image;
import com.sckftr.android.securephoto.image.CryptoBitmapLoader;
import com.sckftr.android.utils.UI;
import com.sckftr.android.utils.UiUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import by.mcreader.imageloader.view.RecyclingImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

@EFragment(R.layout.view_pager_item)
public class ViewPagerItemFragment extends BaseFragment implements ViewTreeObserver.OnGlobalLayoutListener, View.OnClickListener {

    @FragmentArg
    Image image;

    @ViewById
    RecyclingImageView imageView;

    private final CryptoBitmapLoader mCryptoLoader = new CryptoBitmapLoader();

    @AfterViews
    void onAfterViews() {

        imageView.setOnClickListener(this);
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    @Override
    public void onGlobalLayout() {
        Bundle params = new Bundle();

        params.putString(EXTRA.IMAGE, image.getKey());
        params.putInt(EXTRA.ORIENTATION, image.getOrientation());

        UI.displayImage(imageView, image.getFileUri().toString(), imageView.getWidth(), imageView.getHeight(), params, null, mCryptoLoader);

//        new PhotoViewAttacher(imageView).update();

        UiUtil.removeOnGlobalLayoutListener(imageView, this);
    }

    public static ViewPagerItemFragment build(Image image) {
        return ViewPagerItemFragment_.builder().image(image).build();
    }

    @Override
    public void onClick(View v) {
        ((DetailActivity) getBaseActivity()).toggleNavigation();
    }
}
