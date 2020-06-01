package com.example.rxandroidsample.ui.debounce

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Layout
import android.text.TextUtils
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class DebounceFragment : Fragment() {
    lateinit var searchView: SearchView
    lateinit var textView: TextView

    private val disposable = CompositeDisposable()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_debounce, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchView = view.findViewById(R.id.search)
        textView = view.findViewById(R.id.search_textView)
        textView.movementMethod = ScrollingMovementMethod()
        val explainTxtView = view.findViewById<TextView>(R.id.debounce_explain)
        explainTxtView.text = getString(R.string.debounce_explain_txt)

        val viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        var timeSinceLastRequest = 0L
        createObservable()
            .debounce(5, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                    timeSinceLastRequest = System.currentTimeMillis();

                }

                override fun onNext(text: String) {
                    val diffTime = (System.currentTimeMillis() - timeSinceLastRequest) / 1000L
                    timeSinceLastRequest = System.currentTimeMillis();

                    if (!isValidValue(text)) return
                    appendTextAndScroll(
                        textView,
                        ">>> query server with ID \"$text\" after $diffTime sec"
                    )

                    val userId = text.toInt()
                    viewModel.getPost(userId).subscribe(object : Observer<Post> {

                        override fun onSubscribe(d: Disposable) {
                            disposable.add(d)
                        }

                        override fun onNext(post: Post) {
                            appendTextAndScroll(textView, "Server response: \n $post")
                        }

                        override fun onError(e: Throwable) {
                            appendTextAndScroll(textView, "Error: \t ${e.message.toString()}")
                        }

                        override fun onComplete() {
                        }
                    })
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
        try {
            if (text == "" || text.isBlank() || text.isEmpty()) {
                return false
            } else if (!TextUtils.isDigitsOnly(text)) {
                appendTextAndScroll(textView, "Error: enter digit only.")
                return false
            } else if (text.toInt() > 100) {
                appendTextAndScroll(textView, "Error: enter userId lower than 100.")
                return false
            }
        } catch (e: Exception) {
            appendTextAndScroll(textView, "Error: enter valid number, not too big.")
            return false
        }
        return true
    }

    private fun appendTextAndScroll(textView: TextView, text: String) {
        textView.append("$text\n")
        val layout: Layout = textView.layout
        val scrollDelta = (layout.getLineBottom(textView.lineCount - 1)
                - textView.scrollY - textView.height)
        if (scrollDelta > 0) {
            textView.scrollBy(0, scrollDelta)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

}