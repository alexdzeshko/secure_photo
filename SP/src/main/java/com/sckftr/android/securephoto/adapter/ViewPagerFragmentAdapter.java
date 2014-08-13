package com.sckftr.android.securephoto.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.sckftr.android.app.adapter.CursorFragmentPagerAdapter;
import com.sckftr.android.securephoto.contract.Contracts;
import com.sckftr.android.securephoto.fragment.ViewPagerItemFragment;
import com.sckftr.android.utils.CursorUtils;

/**
 * Created by dzianis_roi on 22.07.2014.
 */
public class ViewPagerFragmentAdapter extends CursorFragmentPagerAdapter {

    private boolean mFromSystemGallery;

    public ViewPagerFragmentAdapter(Context ctx, FragmentManager fm, Cursor c, boolean fromSystemGallery) {
        super(ctx, fm, c);

        mFromSystemGallery = fromSystemGallery;
    }

    @Override
    public Fragment getItem(Context context, Cursor cursor) {

        return ViewPagerItemFragment.build(CursorUtils.getString(mFromSystemGallery ? MediaStore.Images.Media.DATA : Contracts.ImageContract.URI, cursor),
                mFromSystemGallery ? null : CursorUtils.getString(Contracts.ImageContract.KEY, cursor));

    }
}
