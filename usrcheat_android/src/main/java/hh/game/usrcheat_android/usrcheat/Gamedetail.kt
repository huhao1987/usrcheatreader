package hh.game.usrcheat_android.usrcheat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Gamedetail (
    //The start pointer of a game block
    var pointer:Int=0,
    //Is the game enabled
    var isgameEnabled:Boolean = false,
    //The master code
    var masterCode:ArrayList<ArrayList<Byte>> = ArrayList(),
    //Game title
    var gameTitle: String? = null,
    //Game id title, usually it is 4 alphabets
    var gameId: String? = null,
    //It is a 16 hex number
    var gameIdNum:String?=null,
    //Total number of all items, includes folder and code
    var numItems:Int = 0,
    //The start pointer of the code(ignore all features above)
    var codepointer:Int=0,
    //Codes as a list
    var items:ArrayList<GameCode> = ArrayList()
) : Parcelable