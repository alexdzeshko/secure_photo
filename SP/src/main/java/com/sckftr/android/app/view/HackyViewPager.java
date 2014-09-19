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

package com.sckftr.android.app.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class HackyViewPager extends ViewPager {

    private static final String LOG_TAG = HackyViewPager.class.getSimpleName();

    private GestureDetector gestureDetector;

    private OnSingleTapListener listener;

    public HackyViewPager(Context context) {
        super(context);
    }

    public HackyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        GestureDetector.SimpleOnGestureListener singleTapListener = new GestureDetector.SimpleOnGestureListener() {
//            @Override public boolean onSingleTapUp(MotionEvent e) {
//                AppConst.Log.d(null, "hacky pager gd on tap up");
//
//                return super.onSingleTapUp(e);
//            }
//
//            @Override public boolean onSingleTapConfirmed(MotionEvent e) {
//
//
//                AppConst.Log.d(null, "hacky pager gd on tap conf");
//
//                if (listener != null) listener.onSingleTap(HackyViewPager.this);
//
//                return super.onSingleTapConfirmed(e);
//            }

            //strange, but works as single tap
            @Override public void onLongPress(MotionEvent e) {
                //AppConst.Log.d(null, "hacky pager gd on long press");
                if (listener != null) {
                    listener.onSingleTap(HackyViewPager.this);
                    //AppConst.Log.d(null, "listener triggered");
                }
                super.onLongPress(e);
            }
        };

        gestureDetector = new GestureDetector(getContext(), singleTapListener);
    }

    public void setOnSingleTapListener(OnSingleTapListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
//            AppConst.Log.d(null, "hacky intercept touch");

            gestureDetector.onTouchEvent(ev);

            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, "onInterceptTouchEvent in IllegalArgumentException");
            return false;
        }
    }

    public interface OnSingleTapListener {
        public void onSingleTap(View view);
    }
}
