package com.example.rxandroidsample.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import org.json.JSONObject


class Post {

    @SerializedName("userId")
    @Expose
    public var userId = 0

    @SerializedName("id")
    @Expose
    public var id = 0

    @SerializedName("title")
    @Expose
    public var title: String? = null


    @SerializedName("body")
    @Expose
    public var body: String? = null

    public var comments: List<Comment>? = null


    constructor(userId: Int,
        id: Int,
        title: String?,
        body: String?,
        comments: List<Comment>?
    ) {
        this.userId = userId
        this.id = id
        this.title = title
        this.body = body
        this.comments = comments
    }

    override fun toString(): String {
        val json = JSONObject()
        json.put("userId", userId)
        json.put("id", id)
        json.put("title", title)
        json.put("body", body)
        json.put("comments", comments)
        return json.toString(2)
    }
}