//package com.sckftr.android.securephoto.processor;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.net.Uri;
//
//import com.sckftr.android.securephoto.Application;
//import com.sckftr.android.securephoto.contract.Contracts;
//
//import by.deniotokiari.core.source.IProcessor;
//import by.deniotokiari.core.utils.ContractUtils;
//
//
//public class ImageProcessor implements IProcessor<Object[], ContentValues> {
//
//    @Override
//    public ContentValues process(Object[] objects) {
//        Bitmap bitmap = (Bitmap) objects[0];
//        String key = (String) objects[1];
//        Uri uri = (Uri) objects[2];
//
//        if (Cryptograph.encrypt(bitmap, uri, key)) {
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(Contracts.ImageContract.KEY, key);
//            contentValues.put(Contracts.ImageContract.URI, uri.getPath());
//            return contentValues;
//        } else return null;
//    }
//
//    @Override
//    public boolean cache(ContentValues contentValues, Context context) {
//        if (contentValues == null) {
//            return false;
//        }
//        try {
//            context.getContentResolver().insert(ContractUtils.getUri(Contracts.ImageContract.class), contentValues);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    @Override
//    public String getKey() {
//        return Application.PROCESSOR.IMAGE;
//    }
//
//}
