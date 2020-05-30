package com.example.rxandroidsample.ui.filter

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
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers

class FilterFragment : Fragment(), View.OnClickListener {

    private var taskObservable: Observable<Task>? = null
    lateinit var listView: RecyclerView
    lateinit var adapter: FilterAdapter
    val disposable = CompositeDisposable()
    var taskList = DataSource().getTaskList()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById(R.id.filter_listView)
        initListView()
        val buttons = listOf(
            R.id.filter_btn,
            R.id.distinct_btn,
            R.id.take_btn,
            R.id.take_while_btn,
            R.id.map_btn,
            R.id.buffer_btn,
            R.id.tasks_btn
        )
        setOnClickListener(view, buttons)
    }

    private fun initListView() {
        adapter = FilterAdapter()
        listView.adapter = adapter
        listView.layoutManager = LinearLayoutManager(activity!!)
        listView.addItemDecoration(VerticalSpaceItemDecoration(15))

    }


    override fun onClick(v: View) {
        adapter.clearData()

        when (v.id) {
            R.id.tasks_btn -> {
                adapter.addItem("This list contain all tasks, we apply above option on them.")
                allTaskObservable()

            }
            R.id.filter_btn -> {
                adapter.addItem("With filter() method you can filter task items based on features or other condition." +
                        "we filter tasks if isComplete feature is true.")

                filterObservable()
            }
            R.id.distinct_btn -> {
                adapter.addItem("Distinct task items based on features or 'equals()' method.")
                distinctObservable()
            }

            R.id.take_btn -> {
                adapter.addItem("With take() method you can take any number of item from items. filter based on items count.")
                takeObservable()
            }
            R.id.take_while_btn -> {
                adapter.addItem("With takeWhile() method you can take items while the condition is met.")
                takeWhileObservable()
            }
            R.id.map_btn -> {
                adapter.addItem("With map() method you can map emitted items to any thing. convert it to another object, change features and so on.")
                mapObservable()
            }
            R.id.buffer_btn -> {
                adapter.addItem("With buffer() method you can buffer emitted items. If you call buffer(2), then this pass item to observer in double categories.")
                bufferObservable()
            }
        }

    }

    private fun allTaskObservable() {
        Observable
            .fromIterable(taskList)
            .subscribeOn(Schedulers.io()) //run on background thread.
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Task> {
                override fun onNext(task: Task) {
                    adapter.addItem(task.toString())
                }

                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onComplete() {}
                override fun onError(e: Throwable) {}
            })
    }

    private fun filterObservable() {
        Observable
            .fromIterable(taskList)
            .filter(object : Predicate<Task> {
                override fun test(t: Task): Boolean {
                    return t.isComplete
                }
            })
            .subscribeOn(Schedulers.io()) //run on background thread.
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Task> {
                override fun onNext(task: Task) {
                    adapter.addItem(task.toString())
                }

                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onComplete() {}
                override fun onError(e: Throwable) {}
            })
    }

    private fun distinctObservable() {
        Observable
            .fromIterable(taskList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .distinct(object : Function<Task, Task> {
                override fun apply(t: Task): Task {
                    return t
                }
            })
            .subscribe(object : Observer<Task> {
                override fun onNext(task: Task) {
                    adapter.addItem(task.toString())
                }

                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onComplete() {}
                override fun onError(e: Throwable) {}
            })
    }

    private fun takeObservable() {
        Observable
            .fromIterable(taskList)
            .take(3)
            .subscribeOn(Schedulers.io()) //run on background thread.
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Task> {
                override fun onNext(task: Task) {
                    adapter.addItem(task.toString())
                }

                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onComplete() {}
                override fun onError(e: Throwable) {}
            })
    }

    private fun takeWhileObservable() {
        Observable
            .fromIterable(taskList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .takeWhile(object : Predicate<Task> {
                override fun test(t: Task): Boolean {
                    return t.taskId != 5
                }
            })
            .subscribe(object : Observer<Task> {
                override fun onNext(task: Task) {
                    adapter.addItem(task.toString())
                }

                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onComplete() {}
                override fun onError(e: Throwable) {}
            })
    }

    private fun mapObservable() {
        Observable
            .fromIterable(taskList)
            .map(object : Function<Task, String> {
                override fun apply(t: Task): String {
                    return t.description
                }
            })
            .subscribeOn(Schedulers.io()) //run on background thread.
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onNext(s: String) {
                    adapter.addItem(s)
                }

                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onComplete() {}
                override fun onError(e: Throwable) {}
            })
    }

    private fun bufferObservable() {
        Observable
            .fromIterable(taskList)
            .buffer(2)
            .subscribeOn(Schedulers.io()) //run on background thread.
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<Task>> {
                override fun onNext(list: List<Task>) {
                    for (task in list) {
                        adapter.addItem(task.toString())
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onComplete() {}
                override fun onError(e: Throwable) {}

            })
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