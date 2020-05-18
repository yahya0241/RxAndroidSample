package com.example.rxandroidsample.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rxandroidsample.R
import com.example.rxandroidsample.model.Comment
import com.example.rxandroidsample.model.Post
import com.example.rxandroidsample.network.ServiceGen
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.util.*


class PostActivity : AppCompatActivity() {
    private val TAG = "PostActivity"

    lateinit var recyclerView: RecyclerView
    private val disposable = CompositeDisposable()
    lateinit var recyclerAdapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        recyclerView = findViewById(R.id.recycler_view)
        initRecyclerView()

        val inOrder = true
        fetchData(inOrder)


    }

    private fun fetchData(inOrder: Boolean) {
        var postObservables = getPostObservables()
        postObservables = postObservables.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            if (inOrder){
                postObservables = postObservables.concatMap(object  : Function<Post, ObservableSource<Post>>{
                    override fun apply(post: Post): ObservableSource<Post> {
                        return getCommentObservable(post)
                    }

                })
            }else {
                postObservables = postObservables.flatMap(object : Function<Post, ObservableSource<Post>> {
                        override fun apply(post: Post): ObservableSource<Post> {
                            return getCommentObservable(post)
                        }

                    })
            }
        postObservables.subscribe(object : Observer<Post> {
            override fun onSubscribe(d: Disposable) {
                disposable.add(d)
            }

            override fun onNext(post: Post) {
                updatePost(post)
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "onError: ", e);
            }

            override fun onComplete() {
            }

        })
    }

    private fun getPostObservables(): Observable<Post> {
        return ServiceGen.getRequestApi()!!
            .getPosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(object : Function<List<Post>, ObservableSource<Post>> {
                override fun apply(posts: List<Post>): ObservableSource<Post> {
                    recyclerAdapter.setPosts(posts as ArrayList<Post>);
                    recyclerAdapter.notifyDataSetChanged()
                    return Observable.fromIterable(posts)
                        .subscribeOn(Schedulers.io());
                }
            });

    }

    private fun getCommentObservable(post: Post): Observable<Post> {
        return ServiceGen.getRequestApi()!!
            .getComments(post.id)
            .map(object : Function<List<Comment>, Post> {
                override fun apply(comments: List<Comment>): Post {
                    val delay = (Random().nextInt(5) + 1) * 1000L
                    Thread.sleep(delay)
                    post.comments = comments
                    return post
                }

            })
            .subscribeOn(Schedulers.io())

    }


    private fun updatePost(post: Post) {
        if (!recyclerView.isComputingLayout) {
            recyclerAdapter.updatePost(post);
        }
    }

    private fun initRecyclerView() {
        recyclerAdapter = RecyclerAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        disposable.clear()
    }
}