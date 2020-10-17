package com.example.pt10a

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class AdapterDataMhs (val dataMhs : List<HashMap<String,String>>) : RecyclerView.Adapter<AdapterDataMhs.HolderDataMhs>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AdapterDataMhs.HolderDataMhs {
    val v = LayoutInflater.from(p0.context).inflate(R.layout.row_mhs,p0,false)
        return HolderDataMhs(v)
    }

    override fun getItemCount(): Int {
    return dataMhs.size
    }

    override fun onBindViewHolder(p0: AdapterDataMhs.HolderDataMhs, p1: Int) {
        val data = dataMhs.get(p1)
        p0.txNim.setText(data.get("nim"))
        p0.txNama.setText(data.get("nama"))
        p0.txProdi.setText(data.get("nama_prodi"))

        if(!data.get("url").equals(""))
            Picasso.get().load(data.get("url")).into(p0.photo)



    }
    class HolderDataMhs (v : View) : RecyclerView.ViewHolder(v){
        val txNim = v.findViewById<TextView>(R.id.txNim)
        val txNama = v.findViewById<TextView>(R.id.txNama)
        val txProdi = v.findViewById<TextView>(R.id.txProdi)
        val photo = v.findViewById<ImageView>(R.id.imageView)
    }

}