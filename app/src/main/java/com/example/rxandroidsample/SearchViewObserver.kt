package com.example.rxandroidsample

import android.util.Log
import androidx.appcompat.widget.SearchView
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SearchViewObserver(val searchView: SearchView ) {
    private val TAG = "TAG"

    init {
        var timeSinceLastRequest = System.currentTimeMillis();

        val searchTextObservable =  Observable.create(object : ObservableOnSubscribe<String>{
            override fun subscribe(emitter: ObservableEmitter<String>) {
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (!emitter.isDisposed){
                            if (newText != null) {
                                emitter.onNext(newText)
                            }
                        }
                        return false
                    }

                })

            }

        })
            .debounce(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())

        searchTextObservable.subscribe(object : Observer<String>{

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                Log.d(TAG, "onNext: time  since last request: " + (System.currentTimeMillis() - timeSinceLastRequest));
                Log.d(TAG, "onNext: search query: $t");
                timeSinceLastRequest = System.currentTimeMillis();

                // method for sending a request to the server
            }

            override fun onError(e: Throwable) {
            }
            override fun onComplete() {
            }

        })
    }
}