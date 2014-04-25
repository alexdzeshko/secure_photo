package com.sckftr.android.securephoto.processor;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.widget.Toast;

import com.sckftr.android.securephoto.Application;
import com.sckftr.android.securephoto.contract.Contracts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import by.deniotokiari.core.context.ContextHolder;
import by.deniotokiari.core.source.IProcessor;
import by.deniotokiari.core.utils.ContractUtils;
import by.deniotokiari.core.utils.IOUtils;


public class ImageProcessor implements IProcessor<Object[], ContentValues> {

    @Override
	public ContentValues process(Object[] objects) {
		Bitmap bitmap = (Bitmap) objects[0];
		String key = (String) objects[1];
		Uri uri = (Uri) objects[2];

		if (bitmap == null || key == null || uri == null) {
			return null;
		}

		Context context = ContextHolder.getInstance().getContext();

		ByteArrayOutputStream baos = null;
		FileOutputStream fileOutputStream = null;
		try {
			baos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 100, baos);
			bitmap.recycle();

			byte[] b = baos.toByteArray();
			byte[] encryptedData = Crypto.encrypt(b, key);
			File file = new File(uri.getPath());
			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(encryptedData);
		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		} finally {
			IOUtils.closeStream(baos);
			IOUtils.closeStream(fileOutputStream);
		}
		ContentValues contentValues = new ContentValues();
		contentValues.put(Contracts.ImageContract.KEY, key);
		contentValues.put(Contracts.ImageContract.URI, uri.getPath());
		return contentValues;
	}

	@Override
	public boolean cache(ContentValues contentValues, Context context) {
		if (contentValues == null) {
			return false;
		}
		try {
			context.getContentResolver().insert(ContractUtils.getUri(Contracts.ImageContract.class),
					contentValues);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String getKey() {
		return Application.PROCESSOR.IMAGE;
	}

}
