package com.sckftr.android.securephoto.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sckftr.android.app.fragment.BaseFragment;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.helper.ImageHelper;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageFragment extends BaseFragment implements OnClickListener {

	public static final String KEY_ARG_VALUE = "key:pos";
	private ImageView mImageView;
    private PhotoViewAttacher mAttacher;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.view_image, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mImageView = (ImageView) view.findViewById(R.id.imageView);

		if (getArguments() != null) {
            String[] uriKey = getArguments().getStringArray(KEY_ARG_VALUE);
			if (uriKey.length > 0) {
				mImageView.setOnClickListener(this);

				ImageHelper.loadEncryptedFile(uriKey[1], uriKey[0], mImageView);

                mAttacher = new PhotoViewAttacher(mImageView);
                mAttacher.update();
			}
		}
	}

	@Override
	public void onClick(View v) {

	}

}
