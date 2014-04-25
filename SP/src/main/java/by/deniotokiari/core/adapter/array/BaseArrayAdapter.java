package by.deniotokiari.core.adapter.array;

import java.util.ArrayList;
import java.util.List;

import by.deniotokiari.core.adapter.ViewHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public abstract class BaseArrayAdapter<T> extends ArrayAdapter<T> {
	
	protected abstract int[] getViewsIds();

	protected abstract void bindData(View view, Context context, int position, T item,
			ViewHolder holder);

	protected abstract View newView(int position, T item, View convertView,
			ViewGroup parent);

	public BaseArrayAdapter(Context context) {
		super(context, -1);
	}
	
	public BaseArrayAdapter(Context context, AttributeSet attributeSet) {
		super(context, -1);
	}
	
	public BaseArrayAdapter(Context context, T[] items) {
		super(context, -1, items);
	}

	public BaseArrayAdapter(Context context, List<T> items) {
		super(context, -1, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			view = newView(position, getItem(position), convertView, parent);
			ViewHolder holder = new ViewHolder(view, getViewsIds());
			view.setTag(holder);
		} else {
			view = convertView;
		}
		bindData(view, getContext(), position, getItem(position), (ViewHolder) view.getTag());
		return view;
	}
	
	
	private boolean mMultiSelection = false;
	private ArrayList<Integer> mSelectedItems =  new ArrayList<Integer>();
	private int mSelected = -1;	
	
	public void setMultiSelection(boolean flag) {
		mMultiSelection = flag;
	}
	
	public boolean isSelected(int position) {
		if (mMultiSelection) {
			return mSelectedItems.contains(Integer.valueOf(position));
		}  else {
			return mSelected == position;
		}
	}
	
	public void setSelected(int position, boolean flag) {
		if (mMultiSelection) {
			mSelectedItems.add(Integer.valueOf(position));
		} else {
			mSelected = position;
		}
		notifyDataSetChanged();
	}

	
}
