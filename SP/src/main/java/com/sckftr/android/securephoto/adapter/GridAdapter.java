package com.sckftr.android.securephoto.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.helper.TakePhotoHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

import by.deniotokiari.core.app.CoreApplication.PLUGIN;
import by.deniotokiari.core.utils.AppUtils;

public class GridAdapter extends ArrayAdapter<Uri> {

	private List<Uri> mPhotoUris;
	private final int adapter_res;
	private Picasso mImageLoader;

	public GridAdapter(Context context, int resource) {
		super(context, resource);
		adapter_res = resource;
		mPhotoUris = TakePhotoHelper.getAllImages();
		mImageLoader = (Picasso) AppUtils.get(getContext(),
				PLUGIN.UNIVERSAL_IMAGE_LOADER);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(adapter_res, null);
		}
		ImageView iv = (ImageView) v.findViewById(R.id.image_view_grid);
		Uri uri = mPhotoUris.get(position);
		// set image
		mImageLoader.load(uri).into(iv);
		return v;
	}

	@Override
	public int getCount() {
		return mPhotoUris.size();
	}

	@Override
	public void notifyDataSetChanged() {
		mPhotoUris = TakePhotoHelper.getAllImages();
		super.notifyDataSetChanged();
	}

}
