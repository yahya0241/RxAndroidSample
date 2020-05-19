package com.example.rxandroidsample.ui.switchmap

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rxandroidsample.R
import com.example.rxandroidsample.model.Post
import com.example.rxandroidsample.network.ServiceGen
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.ArrayList
import java.util.concurrent.TimeUnit
import android.content.Intent
import com.example.rxandroidsample.ui.FlatMapPostActivity


class SwitchMapPostActivity : AppCompatActivity(), SwitchMapRecyclerAdapter.OnPostClickListener {

    val TAG = "SwitchMapPostActivity"

    //ui
    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar

    //vars
    lateinit var adapter: SwitchMapRecyclerAdapter
    val compositeDisposable = CompositeDisposable()
    private val publishSubject = PublishSubject.create<Post>()
    val PERIOD = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_switchmap)
        recyclerView = findViewById(R.id.recycler_view_switch_map)
        progressBar = findViewById(R.id.switch_map_pb)

        initRecyclerView()
        retrievePosts()
    }

    private fun retrievePosts() {
        ServiceGen
            .getRequestApi()!!
            .getPosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<Post>> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(posts: List<Post>) {
                    adapter.setPosts(posts as ArrayList<Post>)
                    adapter.notifyDataSetChanged()
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "onError: $e")
                }

            })
    }

    private fun initSwitchMapDemo() {
        publishSubject
            .switchMap(object : Function<Post, ObservableSource<Post>> {
                override fun apply(post: Post): ObservableSource<Post> {
                    return Observable
                        .interval(PERIOD.toLong(), TimeUnit.MILLISECONDS)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .takeWhile(object : Predicate<Long> {
                            override fun test(t: Long): Boolean {
                                progressBar.max = 3000 - PERIOD
                                progressBar.progress = (t * PERIOD).toInt()
                                return t <= (3000 / PERIOD)
                            }

                        })
                        .filter(object : Predicate<Long> {
                            override fun test(t: Long): Boolean {
                                return t >= (3000 / PERIOD)
                            }

                        })
                        .subscribeOn(Schedulers.io())
                        .flatMap(object : Function<Long, ObservableSource<Post>> {
                            override fun apply(t: Long): ObservableSource<Post> {
                                return ServiceGen.getRequestApi()!!.getPost(post.id)
                            }

                        })
                        .observeOn(AndroidSchedulers.mainThread())
                }

            })

            .subscribe(object : Observer<Post> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(post: Post) {
                    Log.d(TAG, "onNext: done.");
                    navDialogView(post)
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ", e);
                }

            })
    }


    private fun initRecyclerView() {
        adapter = SwitchMapRecyclerAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    fun navDialogView(post: Post) {
        val dialogBuilder = AlertDialog.Builder(this@SwitchMapPostActivity)

        dialogBuilder.setTitle(post.id.toString())
        dialogBuilder.setMessage(post.title.toString())
        dialogBuilder.setPositiveButton("OK") { dialog, whichButton ->
            dialog.dismiss()
            progressBar.progress = 0
        }
        val b = dialogBuilder.create()
        b.show()
    }

    override fun onResume() {
        super.onResume()
        progressBar.progress = 0
        initSwitchMapDemo()
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }

    override fun onPostClick(position: Int) {
        val post = adapter.getPosts()[position]
        publishSubject.onNext(post);
    }
}