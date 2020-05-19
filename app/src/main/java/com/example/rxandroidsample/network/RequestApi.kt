package com.example.rxandroidsample.network

import com.example.rxandroidsample.model.Comment
import com.example.rxandroidsample.model.Post
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path


interface RequestApi {

    @GET("todos/1")
    fun makeObservableQuery(): Observable<ResponseBody>

    @GET("todos/2")
    fun makeQuery(): Flowable<ResponseBody>

    @GET("/users/1")
    fun getUsers():Observable<ResponseBody>

    @GET("posts")
    fun getPosts():Observable<List<Post>>

    @GET("/posts/{id}")
    fun getPost(@Path("id") id :Int):Observable<Post>

    @GET("posts/{id}/comments")
    fun getComments(@Path("id") id: Int):Observable<List<Comment>>
}