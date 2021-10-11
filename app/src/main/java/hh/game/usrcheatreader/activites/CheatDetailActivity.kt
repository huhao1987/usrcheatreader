package hh.game.usrcheatreader.activites

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hh.game.usrcheat_android.usrcheat.Gamedetail
import hh.game.usrcheat_android.usrcheat.UsrCheatUtils
import hh.game.usrcheatreader.R
import hh.game.usrcheatreader.adapters.GameFolderAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CheatDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat_detail)
        var gamedetail = intent.getParcelableExtra<Gamedetail>("gamedetail")
        var nextpointer = intent.getIntExtra("nextpointer", -1)
        if (nextpointer == -1) nextpointer = UsrCheatUtils.getEndPointer()
        findViewById<ImageView>(R.id.backbtn).setOnClickListener {
            finish()
        }
        if (gamedetail == null) {
            Toast.makeText(this, "Game data issues", Toast.LENGTH_LONG).show()
            finish()
        }
        gamedetail?.let {
            var title = findViewById<TextView>(R.id.title).apply {
                text = it.gameTitle
            }
            var id = findViewById<TextView>(R.id.id).apply {
                text = it.gameId + " " + it.gameIdNum
            }

            var cheatlist = findViewById<RecyclerView>(R.id.cheatlist)
            GlobalScope.launch {
                var list = UsrCheatUtils.getCheatCodes(gamedetail, nextpointer)
                var adapter = GameFolderAdapter(this@CheatDetailActivity,list, object : GameFolderAdapter.onClickListener {
                    override fun onclick(
                        view: View,
                        gamedetail: Gamedetail,
                        position: Int,
                        nextpointer: Int
                    ) {
                    }
                })
                runOnUiThread {
                    var llm = LinearLayoutManager(this@CheatDetailActivity)
                    llm!!.orientation = RecyclerView.VERTICAL
                    cheatlist.layoutManager = llm
                    cheatlist.adapter = adapter
                }
            }
        }
    }

}