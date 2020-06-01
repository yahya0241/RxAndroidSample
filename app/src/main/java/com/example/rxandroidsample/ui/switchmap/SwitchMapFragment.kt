package com.example.rxandroidsample.ui.switchmap

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rxandroidsample.R
import com.example.rxandroidsample.model.Post
import com.example.rxandroidsample.network.ServiceGen
import com.example.rxandroidsample.ui.VerticalSpaceItemDecoration
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
import java.util.*
import java.util.concurrent.TimeUnit


class SwitchMapFragment : Fragment(), SwitchMapAdapter.OnPostClickListener {

    val TAG = "SwitchMapPostActivity"

    //ui
    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar

    //vars
    lateinit var adapter: SwitchMapAdapter
    val compositeDisposable = CompositeDisposable()
    private val publishSubject = PublishSubject.create<Post>()
    val PERIOD = 100

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_switchmap, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_view_switch_map)
        progressBar = view.findViewById(R.id.switch_map_pb)

        val explainTextView = view.findViewById<TextView>(R.id.switch_map_explain)
        explainTextView.text = getString(R.string.switch_map_explain)

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
                    navDialogView(post.toString())
                }

                override fun onError(e: Throwable) {
                    navDialogView(e.message.toString())
                }

            })
    }


    private fun initRecyclerView() {
        adapter = SwitchMapAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(VerticalSpaceItemDecoration(15))
        recyclerView.adapter = adapter
    }

    fun navDialogView(text: String) {
        val dialogBuilder = AlertDialog.Builder(activity!!)

        dialogBuilder.setTitle("title")
        dialogBuilder.setMessage(text)
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

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
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