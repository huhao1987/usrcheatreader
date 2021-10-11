package hh.game.usrcheatreader.adapters

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hh.game.usrcheat_android.usrcheat.GameCode
import hh.game.usrcheat_android.usrcheat.Gamedetail
import hh.game.usrcheatreader.R

class GameCodeAdapter(var context: Context, var gamecodelist:ArrayList<GameCode>, var onclickListener: onClickListener):RecyclerView.Adapter<GameCodeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.row_cheat, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var gamecode=gamecodelist[position]
        holder.cheat.text=gamecode.Name+" "+gamecode.Desc
        var strcodelist=ArrayList<String>()
        gamecode.codes?.forEach {
            strcodelist.add(TextUtils.join(" ",it))
        }
        holder.cheatcode.text=TextUtils.join("\n",strcodelist)
    }

    override fun getItemCount(): Int {
        return gamecodelist.size
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        var view=view
        var cheat=view.findViewById<TextView>(R.id.cheat)
        var cheatcode=view.findViewById<TextView>(R.id.cheatcode)
    }
    interface onClickListener{
        fun onclick(view: View,gamedetail: Gamedetail,position: Int,nextpointer:Int)
    }
}