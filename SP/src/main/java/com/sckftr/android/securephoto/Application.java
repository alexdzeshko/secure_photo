package com.sckftr.android.securephoto;

import com.sckftr.android.securephoto.processor.ImageProcessor;
import com.sckftr.android.securephoto.source.BitmapFileSource;

import org.androidannotations.annotations.EApplication;

import by.deniotokiari.core.app.CoreApplication;

@EApplication
public class Application extends CoreApplication {

	public interface SOURCE {

		public static final String BITMAPFILE = "source:BitmapFileSource";

	}

	public interface PROCESSOR {

		public static final String IMAGE = "processor:ImageProcessor";

	}

	@Override
	public void register() {
		// PLUGINS

        AppConst.API.init(this);

		registerService(new BitmapFileSource());
		registerService(new ImageProcessor());
	}

    public static Application get(){
        return Application_.getInstance();
    }
}
