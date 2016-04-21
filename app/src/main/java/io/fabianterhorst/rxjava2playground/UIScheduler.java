package io.fabianterhorst.rxjava2playground;

import io.reactivex.Scheduler;
import io.reactivex.internal.schedulers.ExecutorScheduler;

/**
 * Created by fabianterhorst on 21.04.16.
 */
public class UIScheduler extends Scheduler {

    private static final UIScheduler INSTANCE = new UIScheduler();

    public static UIScheduler instance() {
        return INSTANCE;
    }

    @Override
    public Worker createWorker() {
        return new ExecutorScheduler.ExecutorWorker(UIThreadExecutor.instance());
    }
}
