package com.example.rxandroidsample.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.rxandroidsample.network.Repository
import io.reactivex.Observable
import okhttp3.ResponseBody
import java.util.concurrent.Future

class MainViewModel() : ViewModel() {
    private var repository:Repository = Repository.getInstance()

    public fun makeFutureQuery():Future<Observable<ResponseBody>> {
        return repository.makeFutureQuery()
    }

    public fun makeQuery():LiveData<ResponseBody>{
        return repository.makeReactiveQuery()
    }
}