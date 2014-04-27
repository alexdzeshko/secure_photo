package com.sckftr.android.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.utils.Function;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.List;

@EBean
public class BroadcastHandler {

    @RootContext
    Context mContext;

    public BroadcastHandler(Context context){
        mContext = context;
    }

    protected final Handler mHandler = new Handler();

    private List<BroadcastReceiver> receivers = new ArrayList<BroadcastReceiver>();

    public <T extends Parcelable> void registerReceiver(String action, final Function<T, Boolean> function) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {

                final T arg = intent.getParcelableExtra(AppConst.EXTRA.RESULT);
                final BroadcastReceiver rec = this;

                mHandler.post(new Runnable() {

                    @Override
                    public void run() {

                        Boolean needUnregister = function.apply(arg);

                        if (needUnregister) {
                            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(rec);
                        }

                    }

                });

            }
        };

        receivers.add(receiver);

        final IntentFilter filter = new IntentFilter();

        filter.addAction(action);

        LocalBroadcastManager.getInstance(mContext).registerReceiver(receiver, filter);
    }

    public void unregisterAll() {

        final LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mContext);

        for (BroadcastReceiver receiver : receivers) {
            lbm.unregisterReceiver(receiver);
        }

    }
}
