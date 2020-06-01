package com.example.rxandroidsample.ui.livedata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.rxandroidsample.R
import com.example.rxandroidsample.viewmodels.MainViewModel
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.io.IOException
import java.util.concurrent.ExecutionException

class LiveDataFragment : Fragment() {

    lateinit var reactiveTextView: TextView
    lateinit var futureTextView: TextView
    val disposable = CompositeDisposable()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_livedata, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        reactiveTextView = view.findViewById(R.id.reactive_textView)
        futureTextView = view.findViewById(R.id.future_textView)

        val viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        observeRemoteDataWithFuture(viewModel)
        observeRemoteDataWithReactiveStream(viewModel)
    }

    private fun observeRemoteDataWithFuture(viewModel: MainViewModel) {
        try {
            viewModel.makeFutureQuery().get()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ResponseBody> {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                        disposable.add(d)
                    }

                    override fun onNext(responseBody: ResponseBody) {
                        futureTextView.text = responseBody.string()

                    }

                    override fun onError(e: Throwable) {
                        futureTextView.text = e.toString()
                    }
                })
        } catch (e: ExecutionException) {
            e.printStackTrace();
        } catch (e: InterruptedException) {
            e.printStackTrace();
        }
    }

    private fun observeRemoteDataWithReactiveStream(viewModel: MainViewModel) {
        viewModel.makeQuery().observe(activity!!,
            object : androidx.lifecycle.Observer<ResponseBody> {
                override fun onChanged(responseBody: ResponseBody) {
                    try {
                        reactiveTextView.text = responseBody.string()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            })

    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

}