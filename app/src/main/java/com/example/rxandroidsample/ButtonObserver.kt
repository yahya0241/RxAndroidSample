package com.example.rxandroidsample

import android.util.Log
import android.widget.Button
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import java.util.concurrent.TimeUnit

class ButtonObserver(val bufferBtn: Button, val throttleBtn: Button) {
    private val TAG = "TAG"

    init {
        setBufferObserver(bufferBtn)
        setThrottleObserver(throttleBtn)
    }

    private fun setThrottleObserver(throttleBtn: Button) {
        var timeSinceLastRequest = System.currentTimeMillis();
        throttleBtn.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .throttleFirst(4000, TimeUnit.MILLISECONDS)
            .subscribe(object : Observer<Unit> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: Unit) {
                    val diffTime = System.currentTimeMillis() - timeSinceLastRequest
                    Log.d(
                        TAG,
                        "onNext: time since last clicked: ${diffTime / 1000} sec"
                    );
                    timeSinceLastRequest = System.currentTimeMillis()
                }

                override fun onError(e: Throwable) {
                }

            })
    }

    private fun setBufferObserver(bufferBtn: Button) {
        bufferBtn.clicks()
            .map(object : Function<Unit, Int> {
                override fun apply(t: Unit): Int {
                    return 1
                }

            })
            .buffer(4, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<Int>> {
                override fun onSubscribe(d: Disposable) {
                    //disposable.add(d) // add to disposables to you can clear in onDestroy

                }

                override fun onNext(t: List<Int>) {
                    Log.d(TAG, "onNext: You clicked " + t.size + " times in 4 seconds!")
                }

                override fun onError(e: Throwable) {}
                override fun onComplete() {}

            })
    }
}