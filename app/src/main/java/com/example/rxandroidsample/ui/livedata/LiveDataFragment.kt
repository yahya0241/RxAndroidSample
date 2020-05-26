package com.example.rxandroidsample.ui.livedata

import android.os.Bundle
import android.util.Log
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
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.io.IOException
import java.util.concurrent.ExecutionException

class LiveDataFragment : Fragment() {
    val TAG = "FragmentLiveData"

    lateinit var reactiveTextView: TextView
    lateinit var futureTextView: TextView
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
                        Log.d(TAG, "onComplete: called!")
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.e(TAG, "onSubscribe: called!")
                    }

                    override fun onNext(responseBody: ResponseBody) {
                        futureTextView.text = responseBody.string()

                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "OnError: called!")

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
            androidx.lifecycle.Observer { responseBody ->
                Log.d(TAG, "onChanged: this is a live data response!")
                try {
                    reactiveTextView.text = responseBody.string()
                    Log.d(TAG, "onChanged: " + responseBody.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            })

    }

}