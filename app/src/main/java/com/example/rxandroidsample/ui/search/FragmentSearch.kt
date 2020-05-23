package com.example.rxandroidsample.ui.search

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.rxandroidsample.R
import com.example.rxandroidsample.model.Post
import com.example.rxandroidsample.viewmodels.MainViewModel
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class FragmentSearch : Fragment() {
    private val TAG = "TAG"
    lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchView = view.findViewById(R.id.search)
        val textView = view.findViewById<TextView>(R.id.search_textView)
        textView.movementMethod = ScrollingMovementMethod()

        val viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        var timeSinceLastRequest = System.currentTimeMillis();

        val searchTextObservable = createObservable()

        searchTextObservable!!.subscribe(object : Observer<String> {

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(text: String) {
                val diffTime = System.currentTimeMillis() - timeSinceLastRequest
                timeSinceLastRequest = System.currentTimeMillis();
                val sb =
                    StringBuilder().append("\n --------------------").append("time: ", diffTime)
                        .append("\n")
                sb.append(text)

                if (text == "" || text.isBlank() || text.isEmpty()) {
                    return
                }

                val userId = text.toInt()
                viewModel.getPost(userId).subscribe(object : Observer<Post> {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: Post) {
                        textView.append(t.toString())
                    }

                    override fun onError(e: Throwable) {
                    }

                })

                activity!!.runOnUiThread(Runnable {
                    textView.append(sb.toString())
                })
                // method for sending a request to the server
            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete() {
            }

        })
    }

    private fun createObservable(): Observable<String>? {
        return Observable.create(object : ObservableOnSubscribe<String> {
            override fun subscribe(emitter: ObservableEmitter<String>) {
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (!emitter.isDisposed) {
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
    }
}