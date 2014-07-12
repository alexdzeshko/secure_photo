/*
 * Copyright 2014 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sckftr.android.app.fragment;

import android.app.Fragment;
import android.graphics.Rect;

import com.sckftr.android.app.activity.BaseActivity;
import com.sckftr.android.app.activity.BaseSPActivity;


public abstract class InsetAwareFragment extends Fragment
        implements BaseSPActivity.OnActivityInsetsCallback {

    private final Rect mBaseInsets = new Rect();
    private Rect mAdditionalInsets;

    private final Rect mPopulatedInsets = new Rect();

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof BaseActivity) {
            ((BaseSPActivity) getActivity()).addInsetChangedCallback(this);
        }
    }

    @Override
    public void onPause() {
        if (getActivity() instanceof BaseSPActivity) {
            BaseSPActivity activity = ((BaseSPActivity) getActivity());
            activity.removeInsetChangedCallback(this);
            activity.resetInsets();
        }
        super.onPause();
    }

    @Override
    public final void onInsetsChanged(Rect insets) {
        mBaseInsets.set(insets);
        doPopulateInsets();
    }

    protected void populateInsets(Rect insets) {
    }

    public void setInsetTopAlpha(float alpha) {
        if (getActivity() instanceof BaseSPActivity) {
            ((BaseSPActivity) getActivity()).setInsetTopAlpha(alpha);
        }
    }

    public void setAdditionalInsets(final Rect rect) {
        mAdditionalInsets = rect;
        doPopulateInsets();
    }

//    protected void propogateAdditionalInsetsToChildren(final Rect rect) {
//        final List<Fragment> children = getChildFragmentManager().getFragments();
//        if (!PhilmCollections.isEmpty(children)) {
//            for (final Fragment fragment : children) {
//                if (fragment instanceof InsetAwareFragment) {
//                    ((InsetAwareFragment) fragment).setAdditionalInsets(rect);
//                }
//            }
//        }
//    }

    private void doPopulateInsets() {
        mPopulatedInsets.set(mBaseInsets);

        if (mAdditionalInsets != null) {
            mPopulatedInsets.left += mAdditionalInsets.left;
            mPopulatedInsets.top += mAdditionalInsets.top;
            mPopulatedInsets.right += mAdditionalInsets.right;
            mPopulatedInsets.bottom += mAdditionalInsets.bottom;
        }


        if (getView() != null) {
            populateInsets(mPopulatedInsets);
        }
    }

    protected Rect getInsets() {
        return mPopulatedInsets;
    }
}
