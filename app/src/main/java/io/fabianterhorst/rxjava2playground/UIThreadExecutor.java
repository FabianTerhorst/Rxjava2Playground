package io.fabianterhorst.rxjava2playground;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by fabianterhorst on 21.04.16.
 */
public class UIThreadExecutor implements Executor {

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private static final UIThreadExecutor INSTANCE = new UIThreadExecutor();

    public static UIThreadExecutor instance() {
        return INSTANCE;
    }

    @Override
    public void execute(@NonNull Runnable command) {
        mHandler.post(command);
    }
}
