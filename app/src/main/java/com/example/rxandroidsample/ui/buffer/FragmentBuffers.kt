package com.example.rxandroidsample.ui.buffer

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.rxandroidsample.R
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import java.util.concurrent.TimeUnit

class FragmentBuffers : Fragment() {

    private lateinit var bufferTextView: TextView
    private lateinit var throttleTextView: TextView
    val disposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_buttons, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bufferButton = view.findViewById<Button>(R.id.buffer_button)
        val throttleButton = view.findViewById<Button>(R.id.throttle_button)

        bufferTextView = view.findViewById(R.id.buffer_textView)
        throttleTextView = view.findViewById(R.id.throttle_textView)
        bufferTextView.movementMethod = ScrollingMovementMethod()
        throttleTextView.movementMethod = ScrollingMovementMethod()

        setBufferObserver(bufferButton)
        setThrottleObserver(throttleButton)
    }

    private fun setBufferObserver(bufferBtn: Button) {
        bufferBtn.clicks()
            .map(object : Function<Unit, Int> {
                override fun apply(t: Unit): Int {
                    return 1
                }

            })
            .buffer(4, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<Int>> {
                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onNext(t: List<Int>) {
                    bufferTextView.append(" You clicked ${t.size} times in 4 sec\n")
                }

                override fun onError(e: Throwable) {}
                override fun onComplete() {}

            })
    }

    private fun setThrottleObserver(throttleBtn: Button) {
        var timeSinceLastRequest = System.currentTimeMillis()
        throttleBtn.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .throttleFirst(4000, TimeUnit.MILLISECONDS)
            .subscribe(object : Observer<Unit> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onNext(t: Unit) {
                    val diffTime = System.currentTimeMillis() - timeSinceLastRequest
                    timeSinceLastRequest = System.currentTimeMillis()
                    throttleTextView.append(" time since last clicked: ${diffTime / 1000} sec \n")
                }

                override fun onError(e: Throwable) {
                }

            })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

}