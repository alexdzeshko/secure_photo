package com.sckftr.android.securephoto.source;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;

import com.sckftr.android.securephoto.Application;
import com.sckftr.android.securephoto.helper.ImageHelper;

import java.io.InputStream;

import by.deniotokiari.core.context.ContextHolder;
import by.deniotokiari.core.source.ISource;
import by.deniotokiari.core.utils.IOUtils;


public class ThroughSource implements ISource<Object[], Object[]> {

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

	@Override
	public Object[] getSource(Object[] objects) throws Exception {
		if (objects.length == 2) {
			Uri uri = (Uri) objects[0];
			String key = (String) objects[1];
			Object[] result = new Object[3];
			result[1] = key;
			InputStream inputStream = null;
			try {
				inputStream = ContextHolder.getInstance().getContext()
						.getContentResolver().openInputStream(uri);
				Options options = new Options();
                options.inSampleSize = ImageHelper.getScaleFactor(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null,
						options);
				result[0] = bitmap;
				result[2] = uri;
			} finally {
				IOUtils.closeStream(inputStream);
			}
			return result;
		} else {
			return null;
		}
	}

	@Override
	public String getKey() {
		return Application.SOURCE.THROUGH;
	}
}
