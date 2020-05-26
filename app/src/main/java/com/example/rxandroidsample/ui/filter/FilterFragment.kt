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
import java.util.ArrayList

class FilterFragment : Fragment(), View.OnClickListener {

    private lateinit var taskObservable: Observable<Task>
    lateinit var listView: RecyclerView
    lateinit var adapter: FilterAdapter
    val disposable = CompositeDisposable()
    lateinit var taskList : ArrayList<String>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frament_filter, container, false)
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
            R.id.buffer_btn
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
        taskObservable = Observable
            .fromIterable(DataSource.createTaskList())
            //.subscribeOn(Schedulers.newThread()) //new Thread
            .subscribeOn(Schedulers.io()) //run on background thread.
            .observeOn(AndroidSchedulers.mainThread())
        disposable.clear()

        when (v.id) {
            R.id.filter_btn -> {
                taskObservable.filter(object : Predicate<Task> {
                    override fun test(t: Task): Boolean {
                        return t.isComplete
                    }
                })
                observeTasks()
            }
            R.id.distinct_btn -> {
                taskObservable.distinct(object : Function<Task, Task> {
                    override fun apply(t: Task): Task {
                        return t //filter tasks based on equals method. we can filter based on one field
                    }
                })
                observeTasks()
            }

            R.id.take_btn -> {
                taskObservable.take(3)
                observeTasks()
            }
            R.id.take_while_btn -> {
                taskObservable.takeWhile(object : Predicate<Task> {
                    override fun test(t: Task): Boolean {
                        return t.isComplete
                    }
                })
                observeTasks()
            }
            R.id.map_btn -> {
                taskObservable.map(object : Function<Task, String> {
                    override fun apply(t: Task): String {
                        return t.description
                    }
                })
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
            R.id.buffer_btn -> {
                taskObservable.buffer(2)
                    .subscribe(object : Observer<List<Task>>{
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
        }

    }

    private fun observeTasks() {
        taskObservable.subscribe(object : Observer<Task> {
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