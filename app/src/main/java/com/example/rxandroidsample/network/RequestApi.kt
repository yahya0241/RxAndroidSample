package com.example.rxandroidsample.network

import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET


interface RequestApi {

    @GET("todos/1")
    fun makeObservableQuery(): Observable<ResponseBody>

    @GET("todos/2")
    fun makeQuery(): Flowable<ResponseBody>

    @GET("/users/1")
    fun getUsers():Observable<ResponseBody>
}