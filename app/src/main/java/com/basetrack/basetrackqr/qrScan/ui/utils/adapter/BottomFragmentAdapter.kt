package com.basetrack.basetrackqr.qrScan.ui.utils.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.basetrack.basetrackqr.R
import com.basetrack.basetrackqr.db.QrResult
import com.basetrack.basetrackqr.tracking.ui.tracking.TrackingFragment
import io.realm.OrderedCollectionChangeSet
import io.realm.RealmResults
import io.realm.rx.CollectionChange

class BottomFragmentAdapter(var qr: MutableList<QrResult>) : RecyclerView.Adapter<BottomFragmentAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.button_sheets_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(qr[position])
        holder.delete.setOnClickListener {
                deleteItemByPosition(position)
        }
    }

    override fun getItemCount(): Int {
        return qr.size
    }

    private fun dataOffset() = 0;

    fun updateValues(collectionChange: CollectionChange<RealmResults<QrResult>>) {
        val changeSet = collectionChange.changeset
        qr = collectionChange.collection
        if (changeSet == null || changeSet.state == OrderedCollectionChangeSet.State.INITIAL) {
            notifyDataSetChanged()
            return
        }
        // For deletions, the adapter has to be notified in reverse order.
        val deletions = changeSet.deletionRanges
        for (i in deletions.indices.reversed()) {
            val range = deletions[i]
            notifyItemRangeRemoved(range.startIndex + dataOffset(), range.length)
        }

        val insertions = changeSet.insertionRanges
        for (range in insertions) {
            notifyItemRangeInserted(range.startIndex + dataOffset(), range.length)
        }


        val modifications = changeSet.changeRanges
        for (range in modifications ) {
            notifyItemRangeChanged(range.startIndex + dataOffset(), range.length)
        }
    }

    private fun deleteItemByPosition(position: Int) {
        qr.removeAt(position)
        if(TrackingFragment.qrList.size>position)
        TrackingFragment.qrList.removeAt(position)
        notifyDataSetChanged()
    }



    fun deleteItem(model: QrResult?) {
        qr.remove(model)
        notifyDataSetChanged()
    }


   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
         val id: TextView = itemView.findViewById(R.id.id_text)
         val contains: TextView = itemView.findViewById(R.id.content)
         val delete: Button = itemView.findViewById(R.id.delete)

        fun bind(qrCode: QrResult) {
            if(qrCode.secondQrResult == "Kit not found"&&qrCode.date == ""){
                itemView.setBackgroundResource(R.color.grey)
                id.setBackgroundResource(R.color.grey)
                contains.setBackgroundResource(R.color.grey)
            }
            id.text = qrCode.firstQrResult
            contains.text = qrCode.secondQrResult
        }


    }
}

