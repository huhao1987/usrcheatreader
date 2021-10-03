package hh.game.usrcheatreader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hh.game.usrcheatreader.usrcheat.UsrCheatUtils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val file = "usrcheat_test.dat"
        var usercheatutilinstance = UsrCheatUtils(this).init(file)
        usercheatutilinstance.gettestGames()
    }
}