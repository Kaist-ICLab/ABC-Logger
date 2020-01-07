package kaist.iclab.abclogger.foreground.listener

import androidx.recyclerview.widget.RecyclerView
import android.view.View

abstract class BaseViewHolder<V: View, D>(val view: V, val onClick: (position: Int, view: View) -> Unit) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view){
    init {
        view.setOnClickListener {
            onClick(adapterPosition, view)
        }
    }

    abstract fun bindView(data: D)

    abstract fun clearView()
}