package io.fabianterhorst.rxjava2playground;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.reactivestreams.Subscription;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Optional;
import io.reactivex.Single;
import io.reactivex.Try;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView tv = (TextView) findViewById(R.id.tv);
        Flowable<Object> flowable = Flowable.create(s -> {
            s.onNext(new Object());
            s.onSubscribe(new Subscription() {
                              @Override
                              public void request(long n) {
                                  Log.d("flowable", "request");
                                  if (n < 0) {
                                      s.onError(new Exception());
                                  } else if (n == 2) {
                                      Log.d("flowable", "hello subscription");
                                      s.onNext(new Object());
                                  } else {
                                      s.onComplete();
                                  }
                              }

                              @Override
                              public void cancel() {
                                  Log.d("flowable", "got canceled");
                              }
                          }
            );
        });

        Disposable disposable2 = flowable.subscribe(o1 -> {
            Log.d("disposable", "got new message");
        }, throwable1 -> {
            Log.d("Disposable", "got a new error");
        }, () -> {
            Log.d("Disposable", "is completed");
        }, subscription -> {
            subscription.request(2);
        });

        disposable2.dispose();

        Completable completable = Completable.create(completableSubscriber -> {
            completableSubscriber.onSubscribe(() -> {

            });
            completableSubscriber.onComplete();
        });

        completable.subscribe(new Completable.CompletableSubscriber() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onSubscribe(Disposable d) {

                d.dispose();
            }
        });

        Optional<String> text = Optional.of("bla");

        Try<String> text2 = Try.ofValue("bla2");

        Single<Object> single = Single.create(singleSubscriber -> {
            singleSubscriber.onSuccess(new Object());
            singleSubscriber.onSubscribe(() -> {
                Log.d("Disposable", "got disposed or successful");
            });
        });

        Disposable disposable3 = single.subscribe(o -> {
            Log.d("Disposable", "got the only message");
        });

        disposable3.dispose();

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
