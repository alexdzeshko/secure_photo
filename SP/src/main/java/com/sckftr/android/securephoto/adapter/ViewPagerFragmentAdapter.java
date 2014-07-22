package com.sckftr.android.securephoto.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;

import com.sckftr.android.app.adapter.CursorFragmentPagerAdapter;
import com.sckftr.android.securephoto.contract.Contracts;
import com.sckftr.android.securephoto.fragment.ImageFragment;

import by.deniotokiari.core.helpers.CursorHelper;

/**
 * Created by dzianis_roi on 22.07.2014.
 */
public class ViewPagerFragmentAdapter extends CursorFragmentPagerAdapter {

    public ViewPagerFragmentAdapter(Context ctx, FragmentManager fm, Cursor c) {
        super(ctx, fm, c);
    }

    @Override
    public Fragment getItem(Context context, Cursor cursor) {

        return ImageFragment.build(CursorHelper.getString(cursor, Contracts.ImageContract.URI), CursorHelper.getString(cursor, Contracts.ImageContract.KEY));

    }
}
