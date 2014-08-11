package com.sckftr.android.securephoto.image;

import android.graphics.BitmapFactory;

import com.sckftr.android.securephoto.AppConst;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import by.grsu.mcreader.mcrimageloader.imageloader.BaseBitmapSourceLoader;
import by.grsu.mcreader.mcrimageloader.imageloader.utils.BitmapSizeUtil;
import by.grsu.mcreader.mcrimageloader.imageloader.utils.IOUtils;

/**
 * Created by dzianis_roi on 23.07.2014.
 */
public class FileBitmapSourceLoader extends BaseBitmapSourceLoader<FileInputStream> {

    private static final String TAG = FileBitmapSourceLoader.class.getSimpleName();

    @Override
    protected FileInputStream getSource(String url, BitmapFactory.Options options) {

        try {
            return new FileInputStream(url);
        } catch (FileNotFoundException e) {
            AppConst.Log.d(TAG, e.getMessage());
        }
        return null;
    }
}
