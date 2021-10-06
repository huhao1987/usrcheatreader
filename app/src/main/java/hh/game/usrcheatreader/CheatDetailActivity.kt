package hh.game.usrcheatreader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import hh.game.usrcheat_android.usrcheat.Gamedetail
import hh.game.usrcheat_android.usrcheat.UsrCheatUtils


class CheatDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat_detail)
        var gamedetail=intent.getParcelableExtra<Gamedetail>("gamedetail")
        var nextpointer=intent.getIntExtra("nextpointer",-1)
        if(nextpointer==-1)nextpointer=UsrCheatUtils.getEndPointer()
        if(gamedetail==null){
            Toast.makeText(this,"Game data issues",Toast.LENGTH_LONG).show()
            finish()
        }
        gamedetail?.let {
            var title=findViewById<TextView>(R.id.title).apply {
                text=it.gameTitle
            }
            var id=findViewById<TextView>(R.id.id).apply {
                text=it.gameId+" "+it.gameIdNum
            }
            UsrCheatUtils.getCheatCodes(gamedetail, nextpointer).forEach {
                Log.d("thecode:::",it.Name+ " "+it.Desc)
                it.code?.forEach {
                                when(it.size){
                                    0-> Log.d("thecode:::","Code empty")
                                    1->
                                        Log.d("thecode:::",it.get(0)+ " second code miss")
                                    2->
                                        Log.d("thecode:::",it.get(0)+ " "+it.get(1))
                                }
                }
            }
        }


    }
}