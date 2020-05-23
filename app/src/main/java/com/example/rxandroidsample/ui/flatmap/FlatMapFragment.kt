package com.example.rxandroidsample.ui.flatmap

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rxandroidsample.R
import com.example.rxandroidsample.model.Comment
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
import io.reactivex.schedulers.Schedulers
import java.util.*


class FlatMapFragment : Fragment() {
    private val TAG = "PostActivity"

    lateinit var recyclerView: RecyclerView
    private val disposable = CompositeDisposable()
    lateinit var recyclerAdapter: FlatMapRecyclerAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_flatmap, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        initRecyclerView()

        val inOrder = true
        fetchData(inOrder)
    }

    private fun fetchData(inOrder: Boolean) {
        var postObservables = getPostObservables()
        postObservables = postObservables.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        postObservables = if (inOrder){
            postObservables.concatMap(object  : Function<Post, ObservableSource<Post>>{
                override fun apply(post: Post): ObservableSource<Post> {
                    return getCommentObservable(post)
                }

            })
        }else {
            postObservables.flatMap(object : Function<Post, ObservableSource<Post>> {
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
            .getPostComments(post.id)
            .map(object : Function<List<Comment>, Post> {
                override fun apply(comments: List<Comment>): Post {
                    val delay = (Random().nextInt(5) + 1) * 1000L
//                    Thread.sleep(delay)
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
        recyclerAdapter =
            FlatMapRecyclerAdapter()
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(VerticalSpaceItemDecoration(15))
        recyclerView.adapter = recyclerAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}