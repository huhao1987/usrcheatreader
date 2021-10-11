package hh.game.usrcheatreader.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hh.game.usrcheat_android.usrcheat.Gamedetail
import hh.game.usrcheat_android.usrcheat.UsrCheatUtils
import hh.game.usrcheatreader.R

class GameTitleListAdapter(var gamedetaillist:ArrayList<Gamedetail>, var onclickListener: onClickListener):RecyclerView.Adapter<GameTitleListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.row_gametitle, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var gamedetail=gamedetaillist[position]
        holder.gametitle.text=gamedetail.gameTitle+"\nnumber of codes:"+gamedetail.numItems
        holder.view.setOnClickListener {
            onclickListener.onclick(it,gamedetail,position,if(position<gamedetaillist.size-1) gamedetaillist[position+1].pointer else UsrCheatUtils.getEndPointer())
        }
    }

    override fun getItemCount(): Int {
        return gamedetaillist.size
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        var view=view
        var gametitle=view.findViewById<TextView>(R.id.gametitle)
    }
    interface onClickListener{
        fun onclick(view: View,gamedetail: Gamedetail,position: Int,nextpointer:Int)
    }
}