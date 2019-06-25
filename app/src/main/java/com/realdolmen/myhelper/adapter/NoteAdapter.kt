package com.realdolmen.myhelper.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.realdolmen.myhelper.NoteActivity
import com.realdolmen.myhelper.R
import com.realdolmen.myhelper.data.Medi
import com.realdolmen.myhelper.data.Note

class NoteAdapter (private val list: ArrayList<Note>, private val context: Context): RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")
    var currentemail: String? = null
    var userid: String? = null
    var auth:FirebaseAuth?= null

    fun getUser() {
        currentemail = FirebaseAuth.getInstance().currentUser!!.email
        userCollection.whereEqualTo("email", currentemail).get().addOnSuccessListener {
            userid = it.documents.get(0).id
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(note: Note) {
            var data: TextView = itemView.findViewById(R.id.tvdata) as TextView
            data.text = note.data

            getUser()
            var bin_button: Button = itemView.findViewById(R.id.bin_button) as Button
            bin_button.setOnClickListener {
                val deleteAlert = AlertDialog.Builder(context)
                deleteAlert.setTitle("Delete note")
                deleteAlert.setMessage(
                    "Are you sure you want to delete this note?"
                )
                deleteAlert.setPositiveButton("Delete") { dialogInterface: DialogInterface, i: Int ->
                    db.collection("users/$userid/notes").document("$note.id").delete()
                    val intent = Intent(context, NoteActivity::class.java)
                    context.startActivity(intent)
                }

                deleteAlert.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
                }

                val alertDialog: AlertDialog = deleteAlert.create()
                alertDialog.show()

            }
        }
    }


        override fun onCreateViewHolder(parent: ViewGroup, position: Int): NoteAdapter.ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.recyclerview_note, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: NoteAdapter.ViewHolder, position: Int) {
            holder.bindItem(list[position])
        }

}