package com.basetrack.basetrackqr.qrScan.ui.utils.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.basetrack.basetrackqr.R
import com.basetrack.basetrackqr.db.QrResult
import io.realm.OrderedCollectionChangeSet
import io.realm.RealmResults
import io.realm.rx.CollectionChange


class QrResultViewAdapter(var qr: List<QrResult>) :
    RecyclerView.Adapter<QrResultViewAdapter.ViewHolder>() {

    var mItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(key : String, value : String, delete: Boolean)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        this.mItemClickListener = mItemClickListener
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.qr_result_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setQr(qr[position])
    }

    override fun getItemCount(): Int {
        return qr.size
    }

    fun updateValues(list: RealmResults<QrResult>) {
        this.qr = list
        notifyDataSetChanged()
    }

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
        for (range in modifications) {
            notifyItemRangeChanged(range.startIndex + dataOffset(), range.length)
        }
    }

    private fun dataOffset() = 0;

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val idView: TextView = view.findViewById(R.id.id_text)
        val contentView: TextView = view.findViewById(R.id.content)
        var date: TextView = view.findViewById(R.id.date)
        var check: CheckBox = view.findViewById(R.id.check)

        init {
            check.setOnClickListener(this)
        }

        fun setQr(qrCode: QrResult) {
            idView.text = qrCode.firstQrResult
            contentView.text = qrCode.secondQrResult
            date.text = qrCode.date
        }

        override fun onClick(p0: View?) {
            when(p0?.id){
                R.id.check ->{
                    Log.e("ddd", check.isChecked.toString())
                    if (!check.isChecked) {
                        mItemClickListener?.onItemClick(
                            idView.text.toString(), contentView.text.toString(),
                            true
                        )
                    } else {
                        mItemClickListener?.onItemClick(
                            idView.text.toString(), contentView.text.toString(),
                            false
                        )
                    }
                }
            }
        }
    }

}
