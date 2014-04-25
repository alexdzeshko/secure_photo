package by.deniotokiari.core.fragment;

import android.os.Bundle;

public class FragmentPageInfo {

	private Class<?> mClass;
	private Bundle mBundle;
	private String mPageTitle;

	public FragmentPageInfo(Class<?> cls, Bundle bundle, String pageTitle) {
		mClass = cls;
		mBundle = bundle;
		mPageTitle = pageTitle;
	}

	public String getTitle() {
		return mPageTitle;
	}

	public Bundle getBundle() {
		return mBundle;
	}

	public Class<?> getClazz() {
		return mClass;
	}

}
