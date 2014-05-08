package com.sckftr.android.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

import com.sckftr.android.securephoto.R;

public class CheckableRelativeLayout extends RelativeLayout implements Checkable {
    private boolean mChecked;

    public CheckableRelativeLayout(Context context) {
        super(context);
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
        setBackgroundResource(checked ? R.drawable.checkable_layout : 0);
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void toggle() {
        setChecked(!mChecked);
    }

}