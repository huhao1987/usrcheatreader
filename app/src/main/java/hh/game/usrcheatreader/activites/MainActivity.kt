package hh.game.usrcheatreader.activites

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hh.game.usrcheat_android.usrcheat.Gamedetail
import hh.game.usrcheat_android.usrcheat.UsrCheatUtils
import hh.game.usrcheatreader.R
import hh.game.usrcheatreader.adapters.GameTitleListAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.readfile).setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type="*/*"
            startActivityForResult(intent, 11);
        }
    }

    fun initCheatDB(){
        findViewById<TextView>(R.id.dbname).text = UsrCheatUtils.getCheatDBname()
        var gametitlelist = findViewById<RecyclerView>(R.id.gametitlelist)
        GlobalScope.launch {
            try {
                var list = UsrCheatUtils.getGametitles()
                var adapter =
                    GameTitleListAdapter(list, object : GameTitleListAdapter.onClickListener {
                        override fun onclick(
                            view: View,
                            gamedetail: Gamedetail,
                            position: Int,
                            nextpointer: Int
                        ) {
                            startActivity(
                                Intent(
                                    this@MainActivity,
                                    CheatDetailActivity::class.java
                                ).also {
                                    it.putExtra("gamedetail", gamedetail)
                                    it.putExtra("nextpointer", nextpointer)
                                })
                        }
                    })
                runOnUiThread {
                    var llm = LinearLayoutManager(this@MainActivity)
                    llm!!.orientation = RecyclerView.VERTICAL
                    gametitlelist.layoutManager = llm
                    gametitlelist.adapter = adapter
                }
            }
            catch (e:Exception){
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error happened with wrong file(maybe)", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //init with the file in assets folder
    fun initFileFromAssets(){
        val file = "usrcheat.dat"
        UsrCheatUtils.initwithAsset(this, file)
        initCheatDB()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 11 && resultCode == RESULT_OK) {
            data?.apply {
                var uri=data.data
                UsrCheatUtils.init(this@MainActivity,uri)
                initCheatDB()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}