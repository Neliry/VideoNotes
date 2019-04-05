package com.neliry.banancheg.videonotes.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.neliry.banancheg.videonotes.models.Theme
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.Conspectus
import com.neliry.banancheg.videonotes.models.Page
import com.neliry.banancheg.videonotes.utils.OnViewClickListener

class FirebaseAdapter(private var onViewClickListener: OnViewClickListener,
                      private val list: List<BaseItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    companion object {
        const val TYPE_THEME = 0
        const val TYPE_CONSPECTUS = 1
        const val TYPE_PAGE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_THEME->{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.theme_item, parent, false)
                ThemeViewHolder(view)
            }
            TYPE_CONSPECTUS ->{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.conspectus_item, parent, false)
                ConspectusViewHolder(view)
            }
            TYPE_PAGE ->{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.page_item, parent, false)
                PageViewHolder(view)
            }
            else-> throw NullPointerException()
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ThemeViewHolder-> holder.bind(list[position] as Theme)
            is ConspectusViewHolder -> holder.bind(list[position] as Conspectus)
            is PageViewHolder -> holder.bind(list[position] as Page)
        }
    }



    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {

           if (list[position] is Theme) return TYPE_THEME
        return if (list[position] is Conspectus) TYPE_CONSPECTUS
        else TYPE_PAGE

    }

    inner class ThemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        override fun onClick(view: View) {
            onViewClickListener.onViewClicked(view, list[layoutPosition])
        }

        init{
            itemView.setOnClickListener(this)
        }

        internal var name: TextView = itemView.findViewById(R.id.theme_name_textview)


        fun bind(theme: Theme) {
            name.text = theme.name
        }
    }

    inner class ConspectusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {


        internal var name: TextView = itemView.findViewById(R.id.conspectus_name_textview)


        fun bind(conspectus: Conspectus) {
            name.text = conspectus.name
        }

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            onViewClickListener.onViewClicked(view, list[layoutPosition])
        }
    }

    inner class PageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{
        internal var name: TextView = itemView.findViewById(R.id.page_name_textview)


        fun bind(page: Page) {
            name.text = page.name
        }

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            onViewClickListener.onViewClicked(view, list[layoutPosition])
        }

    }
}