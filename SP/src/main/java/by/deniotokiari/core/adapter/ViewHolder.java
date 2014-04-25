package by.deniotokiari.core.adapter;

import android.util.SparseArray;
import android.view.View;

public class ViewHolder {
	
	private SparseArray<View> mViews;
	
	public ViewHolder(View view, int[] ids) {
		mViews = new SparseArray<View>(ids.length);
		for (int id : ids) {
			add(id, view.findViewById(id));
		}
	}

	public ViewHolder() {
		mViews = new SparseArray<View>();
	}

	public void add(int id, View view) {
		mViews.put(id, view);
	}

	public View getViewById(int id) {
		return mViews.get(id);
	}
	
}
