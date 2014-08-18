package com.sckftr.android.securephoto.provider;

import com.sckftr.android.securephoto.contract.Contracts;

import com.sckftr.android.securephoto.db.base.content.CoreProvider;

public class ImagesProvider extends CoreProvider {

    @Override
    protected Class<?> getContract() {
        return Contracts.ImageContract.class;
    }

}
