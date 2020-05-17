package com.example.rxandroidsample

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import com.example.rxandroidsample.model.Task
import com.example.rxandroidsample.viewmodels.MainViewModel
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.reactivestreams.Subscription
import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private val TAG = "TAG"
    val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val searchView = findViewById<SearchView>(R.id.search)
        SearchViewObserver(searchView)
        val bufferBtn = findViewById<Button>(R.id.buffer)
        val throttleBtn = findViewById<Button>(R.id.throttle)
        ButtonObserver(bufferBtn, throttleBtn)

        val viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

//        observeRemoteDataWithReactiveStream(viewModel)
//        observeRemoteDataWithFuture(viewModel)

        filterList()
//        createFlowable()
//        createSingleObservable()
//        justObservableTest()
//        rangeRepeatObservableTest()
//        intervalObservableTest()
//        timerObservableTest()
    }


    private fun observeRemoteDataWithFuture(viewModel: MainViewModel) {
        try {
            viewModel.makeFutureQuery().get()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ResponseBody> {
                    override fun onComplete() {
                        Log.d(TAG, "onComplete: called!")
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.e(TAG, "onSubscribe: called!")
                    }

                    override fun onNext(responseBody: ResponseBody) {
                        Log.d(TAG, "onNext: get data from server")
                        try {
                            Log.d(TAG, "onNext: " + responseBody.string())
                        } catch (e: IOException) {
                            e.printStackTrace();
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "OnError: called!")

                    }
                })
        } catch (e: ExecutionException) {
            e.printStackTrace();
        } catch (e: InterruptedException) {
            e.printStackTrace();
        }
    }

    private fun observeRemoteDataWithReactiveStream(viewModel: MainViewModel) {
         viewModel.makeQuery().observe(this,
             androidx.lifecycle.Observer { responseBody ->
                 Log.d(TAG, "onChanged: this is a live data response!")
                 try {
                     Log.d(TAG, "onChanged: " + responseBody.string())
                 } catch (e: IOException) {
                     e.printStackTrace()
                 }
             })

    }

    private fun filterList() {

        val taskObservable = Observable
            .fromIterable(DataSource.createTaskList())//fromArray fromCallable -> for database transaction
            .filter(object : Predicate<Task> {
                override fun test(t: Task): Boolean {
                    return t.isComplete
                }

            })
            .distinct(object : Function<Task, Task> {
                override fun apply(t: Task): Task {
                    return t //filter tasks based on equals method. we can filter based on one field
                }

            })
            .take(3)
            .takeWhile(object : Predicate<Task> {
                override fun test(t: Task): Boolean {
                    return t.isComplete
                }

            })
            .map(object : Function<Task, String> {
                override fun apply(t: Task): String {
                    return t.description
                }

            })
            .buffer(2)
            //.subscribeOn(Schedulers.newThread())
            .subscribeOn(Schedulers.io()) //run on background thread.
            .observeOn(AndroidSchedulers.mainThread())


        taskObservable.subscribe(object : Observer<List<String>> {
            override fun onComplete() {
                Log.d(TAG, "onComplete called.")
            }

            override fun onSubscribe(d: Disposable) {
                disposable.add(d)
                Log.d(TAG, "onSubscribe called.")

            }

            override fun onNext(strings: List<String>) {
                Log.d(TAG, "onNext: " + Thread.currentThread().name)
                for (s in strings) {
                    Log.d(TAG, "onNext: $s")
                }
            }

            override fun onError(e: Throwable) {
                Log.d(TAG, "onError called.")

            }

        })
//        val flowable = taskObservable.toFlowable(BackpressureStrategy.BUFFER)
    }

    private fun timerObservableTest() {
        //The Timer operator creates an Observable that emits one particular item after a span of time that you specify.
        Observable
            .timer(3, TimeUnit.SECONDS)
            .repeat(2)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Long> {
                var time: Long = 0 // variable for demonstating how much time has passed

                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe: called")
                    time = System.currentTimeMillis() / 1000
                }

                override fun onNext(i: Long) {
                    Log.d(
                        TAG,
                        "onNext: " + ((System.currentTimeMillis() / 1000) - time) + " seconds have elapsed."
                    )
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ", e)
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: done...")
                }
            })
    }

    private fun intervalObservableTest() {
        // The Interval operator returns an Observable that emits an infinite sequence of ascending integers,
        // with a constant interval of time of your choosing between emissions.
        Observable
            .interval(5, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .takeWhile { aLong -> aLong <= 5 }
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe: called")
                }

                override fun onNext(i: Long) {
                    Log.d(TAG, "onNext: $i")
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ", e)
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: done...")
                }
            })
    }

    private fun rangeRepeatObservableTest() {
        Observable.range(0, 3)
            .repeat(2) //tow time repeat 0 to 3
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int> {
                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe: called")
                }

                override fun onNext(i: Int) {
                    Log.d(TAG, "onNext: $i")
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ", e)
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: done...")
                }
            })

    }

    private fun justObservableTest() {
        // just can get list with at most 10 member.
        Observable.just("first", "second", "third")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe: called")
                }

                override fun onNext(s: String) {
                    Log.d(TAG, "onNext: $s")
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ", e)
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: done...")
                }
            })
    }

    private fun createSingleObservable() {
        val task =
            Task("Walk the dog", false, 4)

        val singleTaskObservable = Observable.create(
            ObservableOnSubscribe<Task> { emitter ->
                // Inside the subscribe method iterate through the list of tasks and call onNext(task)
                //list can have at most ten member
                /* for (task in DataSource.createTaskList()) {
                     if (!emitter.isDisposed) {
                         emitter.onNext(task!!)
                     }
                 }*/

                if (!emitter.isDisposed) {
                    emitter.onNext(task)
                    emitter.onComplete()
                }
            }
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        singleTaskObservable.subscribe(object : Observer<Task> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: Task) {
                Log.d(TAG, "onNext: single task: " + task.description)
            }

            override fun onError(e: Throwable) {
            }
        })
    }

    private fun createFlowable() {
        val flowable = Flowable.range(0, 100)
            .onBackpressureBuffer()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : FlowableSubscriber<Int?> {

                override fun onComplete() {
                }

                override fun onNext(t: Int?) {
                    Log.d(TAG, "onNext: $t")
                }

                override fun onError(t: Throwable?) {
                    Log.e(TAG, "onError: ", t)
                }

                override fun onSubscribe(s: Subscription) {
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}
