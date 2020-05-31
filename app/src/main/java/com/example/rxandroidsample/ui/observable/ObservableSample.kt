package com.example.rxandroidsample.ui.observable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rxandroidsample.DataSource
import com.example.rxandroidsample.R
import com.example.rxandroidsample.model.Task
import com.example.rxandroidsample.ui.VerticalSpaceItemDecoration
import com.example.rxandroidsample.ui.filter.FilterAdapter
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class ObservableSample : Fragment(), View.OnClickListener {

    lateinit var listView: RecyclerView
    lateinit var adapter: FilterAdapter
    val disposable = CompositeDisposable()
    var taskList = DataSource().getTaskList()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_observable, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById(R.id.filter_listView)
        initListView()
        val buttons = listOf(
            R.id.delay_btn,
            R.id.interval_btn,
            R.id.just_btn,
            R.id.create_btn,
            R.id.range_btn,
            R.id.flowable_btn
        )
        setOnClickListener(view, buttons)
    }

    override fun onClick(v: View?) {
        adapter.clearData()
        when (v!!.id) {
            R.id.delay_btn -> {
                adapter.addItem(
                    "The Delay operator creates an Observable that start emitting items" +
                            "after a span of time that you specify.combine with other operator line fromIterable()"
                )
                delayObservable()
            }
            R.id.interval_btn -> {
                adapter.addItem(
                    "The Interval operator returns an Observable that emits an infinite sequence of ascending integers," +
                            " with a constant interval of time of your choosing between emissions." +
                            " combine it with map or flatmap operator to emit items by interval."
                )
                intervalObservable()
            }
            R.id.just_btn -> {
                adapter.addItem("Just operator returns an Observable that emits items with at most 10 member.")
                justObservable()
            }
            R.id.create_btn -> {
                adapter.addItem(
                    "We can crate an observable form anything you want," +
                            " here we create an observable from your click on 'CREATE' button and emit an item randomly." +
                            " please click 'CREATE' button again to see what happened."
                )
                createObservable()
            }
            R.id.range_btn -> {
                adapter.addItem(
                    "Range operator returns an Observable that emits a sequence of Integers within a specified range." +
                            " Repeat operator returns an Observable that repeats the sequence of items emitted by the source ObservableSource indefinitely." +
                            "here we get items in range 1 to 3 and repeat two time."
                )
                rangeRepeatObservable()
            }
            R.id.flowable_btn -> {
                adapter.addItem("We just create a Flowable that iterate over list")
                createFlowable()
            }

        }
    }

    private fun delayObservable() {
        Observable
            .fromIterable(taskList)
            .delay(3, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Task> {
                var time = 0L

                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                    time = System.currentTimeMillis() / 1000
                }

                override fun onNext(i: Task) {
                    val sb = StringBuffer()
                    sb.append("${((System.currentTimeMillis() / 1000) - time)} seconds have elapsed\n")
                    time = System.currentTimeMillis() / 1000
                    sb.append(i.toString())
                    adapter.addItem(sb.toString())
                }

                override fun onError(e: Throwable) {}

                override fun onComplete() {}
            })
    }

    private fun intervalObservable() {

        Observable
            .interval(4, TimeUnit.SECONDS)
            .takeWhile { aLong -> aLong <= 5 }
            .map(object : Function<Long, Task> {
                override fun apply(t: Long): Task? {
                    return taskList[t.toInt()]
                }

            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Task> {
                var time = 0L
                override fun onSubscribe(d: Disposable) {
                    time = System.currentTimeMillis() / 1000

                    disposable.add(d)
                }

                override fun onNext(task: Task) {
                    val sb = StringBuffer()
                    sb.append("${((System.currentTimeMillis() / 1000) - time)} seconds have elapsed\n")
                    time = System.currentTimeMillis() / 1000
                    sb.append(task.toString())
                    adapter.addItem(sb.toString())
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }
            })
    }

    private fun justObservable() {

        Observable.just(
            taskList[0],
            taskList[1],
            taskList[2],
            taskList[3],
            taskList[4],
            taskList[5],
            taskList[6]
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Task> {
                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onNext(task: Task) {
                    adapter.addItem(task.toString())
                }

                override fun onError(e: Throwable) {}
                override fun onComplete() {}
            })
    }

    private fun createObservable() {
        val createBtn = view?.findViewById<Button>(R.id.create_btn)
        Observable
            .create(
                ObservableOnSubscribe<Task> { emitter ->
                    createBtn!!.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(v: View?) {
                            if (!emitter.isDisposed) {
                                val index = Random.nextInt(0, 10)
                                emitter.onNext(taskList[index])
                            }
                        }

                    })
                }
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Task> {
                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onNext(t: Task) {
                    adapter.addItem(t.toString())
                }

                override fun onError(e: Throwable) {}
                override fun onComplete() {}
            })
    }

    private fun rangeRepeatObservable() {
        Observable.range(0, 3)
            .repeat(2) //tow time repeat 0 to 3
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(i: Int) {
                    adapter.addItem(taskList[i].toString())
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }
            })

    }

    private fun createFlowable() {
        Flowable.fromIterable(taskList)
            .onBackpressureBuffer()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSubscriber<Task>() {
                override fun onError(t: Throwable?) {
                }

                override fun onComplete() {}

                override fun onNext(t: Task) {
                    adapter.addItem(t.toString())
                }
            })
    }


    private fun initListView() {
        adapter = FilterAdapter()
        listView.adapter = adapter
        listView.layoutManager = LinearLayoutManager(activity!!)
        listView.addItemDecoration(VerticalSpaceItemDecoration(15))

    }

    private fun setOnClickListener(view: View, buttonIds: List<Int>) {
        for (id in buttonIds) {
            val button = view.findViewById<Button>(id)
            button!!.setOnClickListener(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }


}