package com.sckftr.android.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseCursorAdapter extends CursorAdapter {

    protected abstract void bindData(View view, Context context, Cursor cursor);

    public BaseCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (!mCursor.moveToPosition(position)) {

            throw new IllegalStateException("couldn't move cursor to position " + position);

        }

        View view = convertView;

        if (view == null) {

            view = newView(mContext, mCursor, parent);

        }

        bindView(view, mContext, mCursor);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        bindData(view, context, cursor);
    }

}
