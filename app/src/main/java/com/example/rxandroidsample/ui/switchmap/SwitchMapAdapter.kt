package com.example.rxandroidsample.ui.switchmap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rxandroidsample.R
import com.example.rxandroidsample.model.Post
import java.util.*

class SwitchMapAdapter(onPostClickListener: OnPostClickListener) :
    RecyclerView.Adapter<SwitchMapAdapter.SwitchMapViewHolder>() {
    private var posts = ArrayList<Post>()
    var onPostClickListener : OnPostClickListener = onPostClickListener

    interface OnPostClickListener {
        fun onPostClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwitchMapViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.switch_map_item, null)
        return SwitchMapViewHolder(view, onPostClickListener)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun updatePost(post: Post) {
        posts[posts.indexOf(post)] = post
        notifyItemChanged(posts.indexOf(post))
    }

    fun setPosts(posts: ArrayList<Post>){
        this.posts = posts
    }

    fun getPosts(): ArrayList<Post>{
        return posts
    }

    override fun onBindViewHolder(holder: SwitchMapViewHolder, position: Int)  {
        holder.bind(posts[position])
    }

    class SwitchMapViewHolder(
        itemView: View,
        private val postClickListener: OnPostClickListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        var textView: TextView = itemView.findViewById(R.id.title_sw_map)

        init {
            itemView.setOnClickListener(this);
        }
        fun bind(post: Post) {
            textView.text = post.title
        }

        override fun onClick(v: View?) {
            postClickListener.onPostClick(adapterPosition)
        }
    }
}