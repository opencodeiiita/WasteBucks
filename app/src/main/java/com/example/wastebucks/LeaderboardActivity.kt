package com.example.wastebucks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class LeaderboardEntry(
    val rank: Int,
    val userName: String,
    val points: Int,
)

class LeaderboardAdapter(private val leaderboardList: List<LeaderboardEntry>) :
    RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textRank: TextView = itemView.findViewById(R.id.leaderboard_entry_rank)
        val textUserName: TextView = itemView.findViewById(R.id.leaderboard_entry_name)
        val textPoints: TextView = itemView.findViewById(R.id.leaderboard_entry_points)
        private val rankImage: ImageView = itemView.findViewById(R.id.leaderboard_entry_rank_image)
        fun bind(entry : LeaderboardEntry)
        {
            if(entry.rank == 1) itemView.setBackgroundResource(R.color.topInLeaderboard)
            else itemView.setBackgroundResource(R.color.generalLeaderboardItem)

            if(entry.rank == 1 || entry.rank == 2 || entry.rank == 3 )
            {
                rankImage.visibility = View.VISIBLE
                textRank.visibility = View.GONE

                when (entry.rank) {
                    1-> rankImage.setImageResource(R.drawable.golden)
                    2-> rankImage.setImageResource(R.drawable.silver)
                    3 -> rankImage.setImageResource(R.drawable.bronze)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.leaderboard_entry, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = leaderboardList[position]
        holder.textRank.text = entry.rank.toString()
        holder.textUserName.text = entry.userName
        holder.textPoints.text = entry.points.toString()

        holder.bind(entry)
    }

    override fun getItemCount(): Int {
        return leaderboardList.size
    }
}


class LeaderboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)
        supportActionBar?.hide()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val leaderboardList = mutableListOf(
            LeaderboardEntry(1, "User1", 999),
            LeaderboardEntry(2, "User2", 850),
            LeaderboardEntry(3, "User3", 750),
            LeaderboardEntry(4,"User4", 100),
            LeaderboardEntry(5,"User4", 100),
            LeaderboardEntry(6,"User5", 100),
            LeaderboardEntry(7,"User6", 100),
            LeaderboardEntry(8,"User7", 100),
            LeaderboardEntry(9,"User8", 100),
            LeaderboardEntry(10,"User9", 100),
            LeaderboardEntry(11,"User10", 100),
            LeaderboardEntry(12,"User11", 100),
            LeaderboardEntry(13,"User12", 100),
            LeaderboardEntry(14,"User13", 100),

            // Add more entries as needed
        )

        val recyclerView = findViewById<RecyclerView>(R.id.leaderboard_recycle)
        val adapter = LeaderboardAdapter(leaderboardList)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}
