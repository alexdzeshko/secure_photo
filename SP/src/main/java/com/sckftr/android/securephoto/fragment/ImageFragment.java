package com.sckftr.android.securephoto.fragment;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.contract.Contracts;
import com.sckftr.android.securephoto.helper.ImageHelper;

import by.deniotokiari.core.helpers.CursorHelper;
import by.deniotokiari.core.utils.ContractUtils;

public class ImageFragment extends Fragment implements OnClickListener {

	public static final String KEY_ARG_POS = "key:pos";
	private TextView mTextView;
	private ImageView mImageView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.view_image, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mImageView = (ImageView) view.findViewById(R.id.imageView);
		mTextView = (TextView) view.findViewById(R.id.text_view_key);
		mTextView.setVisibility(View.INVISIBLE);

		if (getArguments() != null) {
			int pos = getArguments().getInt(KEY_ARG_POS);
			String[] uriKey = getUriKey(pos);
			if (uriKey.length > 0) {
				mImageView.setOnClickListener(this);

				mTextView.setText("key: " + uriKey[1]);

				ImageHelper.loadEncryptedFile(uriKey[1], uriKey[0], mImageView);
			}
		}
	}

	private String[] getUriKey(int pos) {
        String[] strings = null;
		Cursor cursor = getActivity().getContentResolver().query(
				ContractUtils.getUri(Contracts.ImageContract.class), null,
				null, null, null);
		if (cursor!= null && cursor.moveToPosition(pos)) {

            strings = new String[]{CursorHelper.get(cursor, Contracts.ImageContract.URI),CursorHelper.get(cursor, Contracts.ImageContract.KEY)};
		}
        CursorHelper.close(cursor);
		return strings;
	}

	@Override
	public void onClick(View v) {
		mTextView.setVisibility(mTextView.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
	}

}
