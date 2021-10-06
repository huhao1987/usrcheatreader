package hh.game.usrcheatreader

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hh.game.usrcheat_android.usrcheat.Gamedetail
import hh.game.usrcheat_android.usrcheat.UsrCheatUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val file = "usrcheat.dat"
        UsrCheatUtils.init(this, file)

        var cheatlist = findViewById<RecyclerView>(R.id.cheatlist)
        GlobalScope.launch {
            var list = UsrCheatUtils.gettestGames()
            var adapter = CheatListAdapter(list, object : CheatListAdapter.onClickListener {
                override fun onclick(
                    view: View,
                    gamedetail: Gamedetail,
                    position: Int,
                    nextpointer: Int
                ) {
                    startActivity(Intent(this@MainActivity,CheatDetailActivity::class.java).also {
                        it.putExtra("gamedetail",gamedetail)
                        it.putExtra("nextpointer",nextpointer)
                    })
                }
            })
            runOnUiThread {
                var llm = LinearLayoutManager(this@MainActivity)
                llm!!.orientation = RecyclerView.VERTICAL
                cheatlist.layoutManager = llm
                cheatlist.adapter = adapter
            }
        }

    }
}