package hh.game.usrcheatreader.usrcheat

data class GameCode(
    //folder and single code both use name and desc, code is only for single code, codes is only for folder
    var isFolder:Boolean=false,
    var codes:ArrayList<GameCode>?=null,
    var numCodes: Int = 0,
    var numCodeChunks: Int = 0,
    var codeEnabled: Boolean = false,
    var Name: String? = null,
    var Desc: String? = null,
    var code: ArrayList<ArrayList<String>>? = null
)