package com.example.rxandroidsample.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.rxandroidsample.model.Post
import com.example.rxandroidsample.network.Repository
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.POST
import java.util.concurrent.Future

class MainViewModel() : ViewModel() {
    private var repository:Repository = Repository.getInstance()

    public fun makeFutureQuery():Future<Observable<ResponseBody>> {
        return repository.makeFutureQuery()
    }

    public fun makeQuery():LiveData<ResponseBody>{
        return repository.makeReactiveQuery()
    }

    public fun getUsers():Observable<ResponseBody>{
        return repository.getUsers()
    }
    public fun getPost(id:Int):Observable<Post>{
        return repository.getPost(id)
    }
}