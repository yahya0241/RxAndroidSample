package com.example.rxandroidsample.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.example.rxandroidsample.model.Post
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class Repository {

    companion object {
        private var instance: Repository? = null
        fun getInstance(): Repository {
            if (instance == null) {
                instance = Repository()
            }
            return instance!!
        }
    }

    fun makeReactiveQuery():LiveData<ResponseBody>{
        return LiveDataReactiveStreams.fromPublisher(ServiceGen.getRequestApi()!!.makeQuery().subscribeOn(Schedulers.io()))
    }
    fun getUsers():Observable<ResponseBody>{
        return ServiceGen.getRequestApi()!!.getUsers().subscribeOn(Schedulers.io())
    }
    fun getPost(id:Int):Observable<Post>{
        return ServiceGen.getRequestApi()!!.getPost(id).subscribeOn(Schedulers.io())
    }
    fun makeFutureQuery(): Future<Observable<ResponseBody>> {
        val executor = Executors.newSingleThreadExecutor()

        val myNetworkCallable: Callable<Observable<ResponseBody>> =
            Callable<Observable<ResponseBody>> { ServiceGen.getRequestApi()!!.makeObservableQuery() }


        return object : Future<Observable<ResponseBody>> {
            override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
                if (mayInterruptIfRunning) {
                    executor.shutdown()
                }
                return false
            }

            override fun isCancelled(): Boolean {
                return executor.isShutdown
            }

            override fun isDone(): Boolean {
                return executor.isTerminated
            }

            override fun get(): Observable<ResponseBody> {
                return executor.submit(myNetworkCallable).get()
            }


            override fun get(timeout: Long, unit: TimeUnit?): Observable<ResponseBody> {
                return executor.submit(myNetworkCallable)[timeout, unit]
            }
        }
    }
}