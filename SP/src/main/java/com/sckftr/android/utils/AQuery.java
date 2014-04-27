package com.sckftr.android.utils;

import android.app.Activity;
import android.content.Context;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

public class AQuery extends com.androidquery.AbstractAQuery<AQuery> {

    public AQuery(android.app.Activity act) {
        super(act);
    }

    public AQuery(Context context) {
        super(context);
    }

    public AQuery(View view) {
        super(view);
    }

    public AQuery(android.app.Activity act, android.view.View root) {
        super(act, root);

    }

    public AQuery addPaintFlags(int flags) {
        TextView v = this.getTextView();
        if (v != null) {
            v.setPaintFlags(v.getPaintFlags() | flags);
        }
        return this;
    }

    public AQuery value(Object value) {
        View v = this.getView();
        if (v instanceof HasValue) {
            ((HasValue) v).setValue(value);
        } else if (v instanceof TextView) {
            text((Spanned) value);
        }
        return this;
    }

    public AQuery display(boolean b) {

        visibility(b ? View.VISIBLE : View.GONE);

        return this;
    }

    public AQuery textAutoHide(int resId) {

        if (resId != 0) {
            super.text(resId);
        } else {
            text("");
        }

        display(resId != 0);

        return this;
    }

    public AQuery textAutoHide(CharSequence s) {
        final boolean empty = Strings.isEmpty(s);

        if (!empty) {
            super.text(s);
        } else {
            text("");
        }

        display(!empty);

        return this;
    }

    public String text() {
        return getText().toString();
    }

    public void alertText(final Activity context, final CharSequence title, final CharSequence body) {

        clicked(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                UI.showAlert(context, title, body, null);

            }
        });
    }

    public void alertText(Activity context, int i, CharSequence body) {

        alertText(context, context.getResources().getText(i), body);
    }

}
