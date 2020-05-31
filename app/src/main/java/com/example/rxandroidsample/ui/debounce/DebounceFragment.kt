package com.example.rxandroidsample.ui.debounce

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class DebounceFragment : Fragment() {
    lateinit var searchView: SearchView
    private val disposable = CompositeDisposable()


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

        createObservable()
            .debounce(5, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onNext(text: String) {
                    val diffTime = System.currentTimeMillis() - timeSinceLastRequest
                    timeSinceLastRequest = System.currentTimeMillis();
                    val sb = StringBuilder().append("\n --->")
                        .append(" diff time between to query: ", diffTime / 1000L, " sec")
                        .append("\n")
                    sb.append(" you userID entry: ", text).append("\n")

                    if (!isValidValue(text)) return

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
                    textView.append(sb.toString())
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }

            })
    }


    private fun createObservable(): Observable<String> {
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

    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun isValidValue(text: String): Boolean {
        if (text == "" || text.isBlank() || text.isEmpty()) {
            return false
        } else if (!TextUtils.isDigitsOnly(text)) {
            activity!!.runOnUiThread {
                Toast.makeText(activity!!, "enter digit only :)", Toast.LENGTH_SHORT).show()
            }
            return false
        } else if (text.toInt() > 100) {
            activity!!.runOnUiThread {
                Toast.makeText(activity!!, "enter userId lower than 100 :)", Toast.LENGTH_SHORT)
                    .show()
            }
            return false
        }
        return true
    }


    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}