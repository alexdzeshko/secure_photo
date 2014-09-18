package com.sckftr.android.securephoto.activity.manager;

import com.sckftr.android.utils.Procedure;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dzianis_Roi on 18.09.2014.
 */
@EBean
public class RefreshingManager {

    private List<Procedure<Boolean>> mRefreshingListeners = new ArrayList<Procedure<Boolean>>();

    public void subscribe(Procedure<Boolean> listener) {
        mRefreshingListeners.add(listener);
    }

    public void unSubscribe(Procedure<Boolean> listener) {
        mRefreshingListeners.remove(listener);
    }

    public void refreshing(boolean refreshing) {
        for (Procedure<Boolean> listener : mRefreshingListeners) listener.apply(refreshing);
    }
}
