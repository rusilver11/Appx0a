package com.example.pt10a

import android.app.Activity
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.pt10a.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.imUpload ->{
                val intent = Intent()
                intent.setType("image/*")
                intent.setAction(Intent.ACTION_GET_CONTENT)
                startActivityForResult(intent,mediaHelper.getRcGallery())
            }
            R.id.btnInsert -> {
                queryInsertUpdateDelete("insert")
            }
            R.id.btnDelete -> {
                queryInsertUpdateDelete("delete")
            }
            R.id.btnUpdate -> {
                queryInsertUpdateDelete("update")
            }
            R.id.btnFind ->{
                showDataMhs(edNamaMhs.text.toString().trim())
            }
        }
    }
    lateinit var mediaHelper: MediaHelper
    lateinit var mhsAdapter : AdapterDataMhs
    lateinit var prodiAdapter : ArrayAdapter<String>
    var daftarMhs = mutableListOf<HashMap<String,String>>()
    var daftarProdi = mutableListOf<String>()
    var urles = "10.244.10.59"
    val url1 = "http://$urles/kampus/show_data_x0a.php"
    val url2 = "http://$urles/kampus/get_nama_prodi.php"
    val url3 = "http://$urles/kampus/query_upd_del_ins.php"
    var imStr = ""
    var pilihProdi = ""
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if(requestCode == mediaHelper.getRcGallery()){
                imStr = mediaHelper.getBitmapToString(data!!.data, imUpload)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mhsAdapter = AdapterDataMhs(daftarMhs)
        mediaHelper = MediaHelper(this)
        listMhs.layoutManager = LinearLayoutManager(this)
        listMhs.adapter = mhsAdapter

        prodiAdapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line, daftarProdi)
        spinProdi.adapter = prodiAdapter
        spinProdi.onItemSelectedListener = itemSelected

        imUpload.setOnClickListener(this)
        listMhs.addOnItemTouchListener(itemTouch)
        btnUpdate.setOnClickListener(this)
        btnInsert.setOnClickListener(this)
        btnDelete.setOnClickListener(this)
        btnFind.setOnClickListener(this)



    }

    override fun onStart() {
        super.onStart()
        showDataMhs("")
        getNamaProdi()

    }

    val itemSelected = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(parent: AdapterView<*>?) {
            spinProdi.setSelection(0)
            pilihProdi = daftarProdi.get(0)
        }
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            pilihProdi = daftarProdi.get(position)
        }


    }

    val itemTouch = object : RecyclerView.OnItemTouchListener{
        override fun onTouchEvent(p0: RecyclerView, p1: MotionEvent) {

        }

        override fun onInterceptTouchEvent(p0: RecyclerView, p1: MotionEvent): Boolean {
            val view = p0.findChildViewUnder(p1.x, p1.y)
            val tag = p0.getChildAdapterPosition(view!!)
            val pos = daftarProdi.indexOf(daftarMhs.get(tag).get("nama_prodi"))
            spinProdi.setSelection(pos)
            edNim.setText(daftarMhs.get(tag).get("nim").toString())
            edNamaMhs.setText(daftarMhs.get(tag).get("nama").toString())
            Picasso.get().load(daftarMhs.get(tag).get("url")).into(imUpload)
            return false
        }

        override fun onRequestDisallowInterceptTouchEvent(p0: Boolean) {

        }
    }

    fun queryInsertUpdateDelete(mode : String){
        val request = object : StringRequest(Method.POST,url3,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val error = jsonObject.getString("kode")
                if (error.equals("000")){
                    Toast.makeText(this,"Operasi Berhasil", Toast.LENGTH_LONG).show()
                    showDataMhs("")
                }else{
                    Toast.makeText(this,"Operasi GAGAL",Toast.LENGTH_LONG).show()
                }

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Tidak dapat terhubung ke server",Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                val nmFile = "DC"+SimpleDateFormat("yyyymmddHHmmss", Locale.getDefault()).format(Date())+",jpg"
                when(mode){
                    "insert" ->{
                        hm.put("mode","insert")
                        hm.put("nim",edNim.text.toString())
                        hm.put("nama",edNamaMhs.text.toString())
                        hm.put("image",imStr)
                        hm.put("file",nmFile)
                        hm.put("nama_prodi",pilihProdi)
                    }
                    "update" ->{
                        hm.put("mode","update")
                        hm.put("nim",edNim.text.toString())
                        hm.put("nama",edNamaMhs.text.toString())
                        hm.put("image",imStr)
                        hm.put("file",nmFile)
                        hm.put("nama_prodi",pilihProdi)
                    }
                    "delete" ->{
                        hm.put("mode","delete")
                        hm.put("nim",edNim.text.toString())
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun getNamaProdi(){
        val request = StringRequest(Request.Method.POST,url2,
            Response.Listener { response ->
                daftarProdi.clear()
                val jsonArray = JSONArray(response)
                for(x in 0 .. (jsonArray.length()-1)){
                    val jsonObject = jsonArray.getJSONObject(x)
                    daftarProdi.add(jsonObject.getString("nama_prodi"))
                }
                prodiAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->  }
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun showDataMhs(namaMhs : String){
        val request = object : StringRequest(Method.POST,url1,
            Response.Listener{response ->
                daftarMhs.clear()
                val jsonArray = JSONArray(response)
                for(x in 0 .. (jsonArray.length()-1)){
                    val jsonObject = jsonArray.getJSONObject(x)
                    var mhs = HashMap<String,String>()
                    mhs.put("nim",jsonObject.getString("nim"))
                    mhs.put("nama",jsonObject.getString("nama"))
                    mhs.put("nama_prodi",jsonObject.getString("nama_prodi"))

                    mhs.put("url",jsonObject.getString("url"))
                    daftarMhs.add(mhs)
                }
                mhsAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Terjadi kesalahan Koneksi Server",Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String,String>()
                hm.put("nama",namaMhs)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

}
