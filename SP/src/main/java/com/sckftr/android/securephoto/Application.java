package com.sckftr.android.securephoto;

import com.sckftr.android.securephoto.processor.ImageProcessor;
import com.sckftr.android.securephoto.source.ThroughSource;

import org.androidannotations.annotations.EApplication;

import by.deniotokiari.core.app.CoreApplication;

@EApplication
public class Application extends CoreApplication {

	public static final class SOURCE {

		public static final String THROUGH = "source:ThroughSource";

	}

	public static final class PROCESSOR {

		public static final String IMAGE = "processor:ImageProcessor";

	}

	@Override
	public void register() {
		// PLUGINS

        AppConst.API.init(this);

		registerService(new ThroughSource());
		registerService(new ImageProcessor());
	}

}
