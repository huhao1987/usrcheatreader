package hh.game.usrcheat_android.usrcheat

import android.content.Context
import android.util.Log
import hh.game.usrcheatreader.*
import java.io.ByteArrayInputStream
import java.lang.Exception
import kotlin.collections.ArrayList

/**
 * The util for usrcheat.dat
 */
class UsrCheatUtils() {
    companion object{
        private val headersize = 0x100

        //The real title is start from the address
        private val titlestartaddress = 0x10

        //The end address of title(Should be the limitation)
        private val titleendaddress = 0x4a
        private var cheatdbtitle: String? = null
        private var file: String? = null
        private var header: ByteArray? = null
        private var wholedatabase: ByteArray? = null


        /**
         * Init the class
         */
        fun init(context:Context,file: String) {
            this.file = file
            context.assets.open(file).apply {
                header = ByteArray(headersize)
                read(header)
                reset()
                wholedatabase = readBytes()
                close()
            }
        }

        @Throws(Exception::class)
        fun getEndPointer():Int{
            return wholedatabase!!.size
        }

        fun getCheatDBname(): String? {
            if (cheatdbtitle == null)
                header?.apply {
                    var titletotalheader = copyOfRange(titlestartaddress, titleendaddress)
                    return getStringBytearray(titletotalheader).toString(Charsets.UTF_8)
                }
            else return cheatdbtitle
            return null
        }

        fun getStringBytearray(bytes: ByteArray, startadd: Int = 0, indexat: Int = 0): ByteArray {
            var endadd = bytes.indexOf(indexat.toByte())

            if (endadd == 0) endadd = bytes.size - 1
            return bytes.copyOfRange(startadd, endadd)
        }

        fun getCodeOffset() = header?.copyOfRange(0x0C, 0x10)!!.little2int()

        fun getSinglePointer(singleblock: ByteArray): Gamedetail {
            var gamedetail = Gamedetail()
            gamedetail.gameId = (singleblock.copyOfRange(0, 4).toString(Charsets.UTF_8))
            gamedetail.gameIdNum = singleblock.copyOfRange(0x04, 0x08).toList().reversed().toByteArray().toHex()
            gamedetail.pointer = singleblock.copyOfRange(0x08, 0x0C).little2int()
            return gamedetail
        }


        fun SplitGames(): ArrayList<Gamedetail> {
            var gamePointers = ArrayList<Gamedetail>()
            val input = ByteArrayInputStream(wholedatabase)
            input.skip(getCodeOffset().toLong())
            var tmp = ByteArray(0x10)
            while (true) {
                input.read(tmp)
                if (tmp[0].toInt() == 0) {
                    break
                }
                gamePointers.add(getSinglePointer(tmp))
            }
            input.close()
            return gamePointers
        }


        fun getSingleTitle(gamedetail: Gamedetail, nextpointer:Int):Gamedetail{
            var tempbytelist=wholedatabase!!.copyOfRange(gamedetail.pointer,nextpointer).toList()

            //game title
            var startindex=0
            var endindex=tempbytelist.indexOf(0)
            gamedetail.gameTitle=tempbytelist.subList(startindex,endindex).toByteArray().toString(Charsets.UTF_8)

            //number of codes
            startindex=endindex.align()
            endindex=startindex+2
            gamedetail.numItems = tempbytelist.subList(startindex,endindex).toByteArray().little2short().toInt()

            //Is game enabled
            startindex=endindex
            endindex=startindex+2
            var intgamenabled = tempbytelist.subList(startindex,endindex).toByteArray().get(1).toString(16)
            gamedetail.isgameEnabled = if (intgamenabled.equals("0")) false else true

            //Master code for the game
            startindex=endindex
            endindex=endindex+(4 * 8)
            gamedetail.masterCode = tempbytelist.subList(startindex,endindex).chunked(4) as ArrayList<ArrayList<Byte>>
            gamedetail.codepointer=gamedetail.pointer+endindex
            return gamedetail
        }

        fun getCheatCodes(gamedetail: Gamedetail, nextpointer:Int): ArrayList<GameCode>{
            var gamecodelist=ArrayList<GameCode>()
            var templist =wholedatabase!!.toList().subList(gamedetail.codepointer,nextpointer)
            for (n in 0..gamedetail.numItems - 1) {
                var gamecode=GameCode()
                var codechuck = templist.subList(0, 2).toByteArray().little2short()
                templist = templist.drop(2).toMutableList()
                var checkisfolder = templist.subList(0, 2).toByteArray().get(1).toString(16)
                templist = templist.drop(2).toMutableList()
                var tempgamecode=GameCode()
                when(checkisfolder){
                    //11 is folder with single chosen,10 is folder without single chosen
                    "10","11"->{
                        gamecode.isFolder=true
                        processFolder(templist).apply {
                            templist=get("list") as List<Byte>
                            gamecode.Name=get("name") as String
                            gamecode.Desc=get("des") as String
                        }

                    }
                    //01 for cheat code with enable, 00 for cheat code with disable
                    "0","1"->{
                        processCheatCode(templist).apply {
                            gamecode.isFolder=false
                            templist=get("list") as List<Byte>
                            gamecode.Name=get("name") as String
                            gamecode.Desc=get("des") as String
                            gamecode.code=get("codelist") as ArrayList<ArrayList<String>>
//                            gamecode.code?.forEach {
//                                when(it.size){
//                                    0-> Log.d("thecode","Code empty")
//                                    1->
//                                        Log.d("thecode",it.get(0)+ " second code miss")
//                                    2->
//                                        Log.d("thecode",it.get(0)+ " "+it.get(1))
//                                }
//                            }
                        }
                    }
                }
                gamecodelist.add(gamecode)
            }
            return gamecodelist
        }


        fun getGameDetail(gamedetail: Gamedetail, nextpointer:Int) {
            Log.d("thepointer:::","start pointer:"+gamedetail.pointer+ " end pointer:"+nextpointer)
            var templist =wholedatabase!!.toMutableList().subList(gamedetail.pointer,nextpointer)

            var end = templist.indexOf(0)
            //game title
            gamedetail.gameTitle = templist.subList(0, end).toByteArray().toString(Charsets.UTF_8)
            templist = templist.drop(end.align()).toMutableList()

            //number of codes
            gamedetail.numItems = templist.subList(0, 2).toByteArray().little2short().toInt()
            templist = templist.drop(2).toMutableList()

            //Is game enabled
            var intgamenabled = templist.subList(0, 2).toByteArray().get(1).toString(16)

            gamedetail.isgameEnabled = if (intgamenabled.equals("0")) false else true
            templist = templist.drop(2).toMutableList()

            Log.d(
                "thegamecodes:::",
                gamedetail.gameTitle + " " + gamedetail.numItems + " " + gamedetail.isgameEnabled
            )

            //Master code for the game
            gamedetail.masterCode = templist.subList(0, 4 * 8).chunked(4) as ArrayList<ArrayList<Byte>>
            var mastercode = gamedetail.masterCode
            templist = templist.drop(4 * 8).toMutableList()
            for (n in 0..gamedetail.numItems - 1) {
                var codechuck = templist.subList(0, 2).toByteArray().little2short()
                templist = templist.drop(2).toMutableList()
                var checkisfolder = templist.subList(0, 2).toByteArray().get(1).toString(16)
                templist = templist.drop(2).toMutableList()
                var tempgamecode=GameCode()
                when(checkisfolder){
                    //11 is folder with single chosen,10 is folder without single chosen
                    "10","11"->{
                        processFolder(templist).apply {
                            templist=get("list") as MutableList<Byte>
                            tempgamecode.Name=get("name") as String
                            tempgamecode.Desc=get("des") as String
                            Log.d("thefolderdetail:::",tempgamecode.Name+ " "+tempgamecode.Desc)
                        }
                    }
                    //01 for cheat code with enable, 00 for cheat code with disable
                    "0","1"->{
                        processCheatCode(templist).apply {
                            templist=get("list") as MutableList<Byte>
                            tempgamecode.Name=get("name") as String
                            tempgamecode.Desc=get("des") as String
                            Log.d("thecheatcode:::",tempgamecode.Name+ " "+tempgamecode.Desc)
                            tempgamecode.code=get("codelist") as ArrayList<ArrayList<String>>
                            tempgamecode.code?.forEach {
                                when(it.size){
                                    0-> Log.d("thecode","Code empty")
                                    1->
                                        Log.d("thecode",it.get(0)+ " second code miss")
                                    2->
                                        Log.d("thecode",it.get(0)+ " "+it.get(1))
                                }
                            }
                        }
                    }
                }
            }

        }
        fun processFolder(orignlist:List<Byte>):HashMap<String,Any>{
            var templist=orignlist
            var foldernameend=templist.indexOf(0)
            var name=templist.subList(0,foldernameend).toByteArray().toString(Charsets.UTF_8)
            templist=templist.drop(foldernameend+1).toList()
            var folderdesend=templist.indexOf(0)
            var des=templist.subList(0,folderdesend).toByteArray().toString(Charsets.UTF_8)
            templist=orignlist

            templist=templist.drop((foldernameend+1+folderdesend+1).align()).toList()

            var tempmap= HashMap<String,Any>().also {
                it.set("list", templist)
                it.set("name", name)
                it.set("des",des)
            }
            return tempmap
        }

        fun processCheatCode(orignlist:List<Byte>):HashMap<String,Any>{
            var templist=orignlist
            var codenameend=templist.indexOf(0)
            var name=templist.subList(0,codenameend).toByteArray().toString(Charsets.UTF_8)
            templist=templist.drop(codenameend+1).toList()
            var codedesend=templist.indexOf(0)
            var des=templist.subList(0,codedesend).toByteArray().toString(Charsets.UTF_8)
            templist=orignlist
            templist=templist.drop((codenameend+1+codedesend+1).align()).toList()
            //Each 4 bytes are inclued in one block, xxxxxxxx yyyyyyyy, xxxxxxxx and yyyyyyyy are both blocks, so two blocks are for one cheat code
            var numcodeblock=templist.subList(0,2).toByteArray().little2short().toInt()
            templist=templist.drop(2.align()).toList()
            var cheatlist=ArrayList<ArrayList<String>>()
            var temponecheat=ArrayList<String>()
            Log.d("thenumblock:::",numcodeblock.toString())
            var temp=templist.subList(0,numcodeblock*4).chunked(4)
            templist=templist.drop((numcodeblock*4)).toList()
            temp.forEachIndexed { index, list ->
                var cheatcode=list.reversed().toByteArray().toHex()
                temponecheat.add(cheatcode)
                if(index%2!=0||index==temp.size-1){
                    cheatlist.add(temponecheat)
                    temponecheat=ArrayList()
                }
            }

            var tempmap= HashMap<String,Any>().also {
                it.set("list", templist)
                it.set("name", name)
                it.set("des",des)
                it.set("codelist",cheatlist)
            }
            return tempmap
        }


        fun ByteArray.toHex(): String = asUByteArray().joinToString("") {
            it.toString(16).padStart(2, '0') }

        interface GameTitlesListener{
            fun onResult(gamedetails:ArrayList<Gamedetail>)
        }
        fun getGameTitles(block:ArrayList<Gamedetail>):ArrayList<Gamedetail>{
            var gamedetails=ArrayList<Gamedetail>()
            block.forEachIndexed { index, gamedetail ->
                gamedetails.add(getSingleTitle(gamedetail,if(index<block.size-1) block[index+1].pointer else wholedatabase!!.size))
            }
            return gamedetails
        }
        fun gettestGames():ArrayList<Gamedetail> {
            var block = SplitGames()
            return getGameTitles(block)



//        GlobalScope.launch {
//            block.forEachIndexed { index, gamedetail ->
//                    Log.d(
//                        "thegamedetails:::", gamedetail.gameId + " " + gamedetail.gameIdNum
//                    )
////                    Get single game detail
////                getGameDetail(gamedetail,if(index<block.size-1) block[index+1].pointer else wholedatabase!!.size)
//                getTest(gamedetail,if(index<block.size-1) block[index+1].pointer else wholedatabase!!.size)
//            }
//        }
        }
    }

}