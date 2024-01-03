package com.example.wastebucks

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class Post(val content: String, val likes: Int, val contentImageURL: String? = null, val id: String? = null, val likedUsers: List<String> = listOf(), val timestamp: Timestamp? = null)

class Community : Fragment() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUser: String = auth.currentUser?.uid.toString()
        val createPostButton: Button = view.findViewById(R.id.communityCreatePostButton)
        createPostButton.setOnClickListener {
            startActivityForResult(Intent(requireContext(), CreatePost::class.java), 1)
        }
        loadPosts()
    }

    private fun loadPosts() {
        val recyclerView: RecyclerView = requireView().findViewById(R.id.communityRecyclerView)
        val data = mutableListOf<Post>()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = CommunityAdapter(data)
        recyclerView.adapter = adapter
        firestore.collection("posts")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val content = document.getString("content") ?: ""
                    val likes = document.getLong("likes")?.toInt() ?: 0
                    val contentImageURL = document.getString("contentImageURL")
                    val id = document.id
                    val likedUsers = document.get("likedUsers") as? List<String> ?: listOf()
                    val timestamp = document.getTimestamp("timestamp")
                    val post = Post(content, likes, contentImageURL, id, likedUsers, timestamp)
                    data.add(post)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            loadPosts()
        }
    }
}

class CommunityAdapter(private val dataList: List<Post>) : RecyclerView.Adapter<CommunityAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postContent: TextView = itemView.findViewById(R.id.communityPostText)
        val postImage: ImageView = itemView.findViewById(R.id.communityImage)
        val likeCount: TextView = itemView.findViewById(R.id.communityLikeCount)
        val time: TextView = itemView.findViewById(R.id.communityTime)
        val favoriteButton: ImageView = itemView.findViewById(R.id.favoriteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_community_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataList[position]
        var isLiked: Boolean = currentItem.likedUsers.contains(FirebaseAuth.getInstance().currentUser?.uid.toString())

        holder.postContent.text = currentItem.content
        holder.likeCount.text = "${currentItem.likes} Likes"
        val timestamp = currentItem.timestamp
        if (timestamp != null) {
            val timeAgo = TimeAgo.using(timestamp.toDate().time)
            holder.time.text = timeAgo
        }

        if (currentItem.contentImageURL != null) {
            holder.postImage.visibility = View.VISIBLE
            Glide.with(holder.itemView.context).load(currentItem.contentImageURL).into(holder.postImage)
        } else {
            holder.postImage.visibility = View.GONE
        }

        if(isLiked) {
            holder.favoriteButton.setImageResource(R.drawable.favorite)
        } else {
            holder.favoriteButton.setImageResource(R.drawable.favorite_unfilled)
        }

        val postId: String = currentItem.id.toString()
        var likesCount: Int = currentItem.likes
        var likedUsers: List<String> = currentItem.likedUsers

        holder.favoriteButton.setOnClickListener {
            if (!isLiked) {
                likesCount++
                holder.likeCount.text = "${likesCount} Likes"
                likedUsers = likedUsers + FirebaseAuth.getInstance().currentUser?.uid.toString()
                updateLikesCount(likesCount, postId, likedUsers)
                holder.favoriteButton.setImageResource(R.drawable.favorite)
            } else {
                likesCount--
                likedUsers = likedUsers.filter { it != FirebaseAuth.getInstance().currentUser?.uid.toString() }
                holder.likeCount.text = "${likesCount} Likes"
                updateLikesCount(likesCount, postId, likedUsers)
                holder.favoriteButton.setImageResource(R.drawable.favorite_unfilled)
            }
            isLiked = !isLiked
        }
    }

    private fun updateLikesCount(count: Int, postId: String, likedUsers: List<String>) {
        FirebaseFirestore.getInstance().collection("posts").document(postId)
            .update("likes", count, "likedUsers", likedUsers)
            .addOnSuccessListener {
                Log.d("CommunityAdapter", "Likes count updated")

            }
            .addOnFailureListener {
                Log.d("CommunityAdapter", "Likes count updated")
            }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}