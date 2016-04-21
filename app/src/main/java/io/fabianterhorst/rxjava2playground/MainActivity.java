package io.fabianterhorst.rxjava2playground;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView tv = (TextView) findViewById(R.id.tv);
        Observable<Object> observable = Observable.create(observer -> {
            observer.onNext(new Object());
            observer.onSubscribe(() -> {
                Log.d("Disposable", "got disposed");
                //here you can remove some listeners to prevent memory leaks
            });
            //let´s simulate a network request
            Runnable runnable = () -> {
                Log.d("network", "done");
                //the disposable won´t get completed when a error appears but it got disposed
                observer.onError(new Exception("Error"));
                //disposable on complete disposed the disposable and call the on complete runnable
                observer.onComplete();
            };
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(runnable, 1000);
        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                //.observeOn(Schedulers.from(UIThreadExecutor.instance()))
                .observeOn(UIScheduler.instance());


        disposable = observable.subscribe(
                o -> {
                    Log.d("Disposable", "got a new message");
                    if (tv != null) {
                        tv.setText(R.string.sample_string);
                    }
                },
                throwable -> Log.d("Disposable", "got a new error"),
                () -> Log.d("Disposable", "is completed"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
