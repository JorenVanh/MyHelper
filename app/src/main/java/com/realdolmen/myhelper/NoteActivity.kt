package com.realdolmen.myhelper

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.realdolmen.myhelper.LoginActivity
import com.realdolmen.myhelper.MedicationActivity
import com.realdolmen.myhelper.R
import com.realdolmen.myhelper.adapter.MediAdapter
import com.realdolmen.myhelper.adapter.NoteAdapter
import com.realdolmen.myhelper.data.Medi
import com.realdolmen.myhelper.data.Note
import kotlinx.android.synthetic.main.content_medication.*
import kotlinx.android.synthetic.main.content_note.*
import java.util.ArrayList

class NoteActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")
    var id: String ?= null
    var currentemail: String ?= null
    var noteList: ArrayList<Note> ?= null
    private var adapter: NoteAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            addNotes()
        }

        getNotes()
    }

    fun addNotes(){
        val addAlert = AlertDialog.Builder(this)
        addAlert.setTitle("Add note")
        var input = EditText(this)
        addAlert.setView(input )

// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_CLASS_TEXT
        addAlert.setView(input);

        addAlert.setPositiveButton("Add") { dialogInterface: DialogInterface, i: Int ->
            var nextid: Int = 1
            var note: Note = Note()
            note.data = input.text.toString()
            note.name = "Your note"
            db.collection("users/$id/notes").get().addOnSuccessListener{
                nextid =it.size() +1
                note.id = nextid.toString()
                db.collection("users/$id/notes").document("${nextid}").set(note)


            }
            startActivity( Intent(this, NoteActivity::class.java))
        }

        addAlert.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
        }

       val alertDialog: AlertDialog = addAlert.create()
        alertDialog.show()

    }


    private fun getNotes() {
        auth = FirebaseAuth.getInstance()
        currentemail = auth.currentUser!!.email
        userCollection.whereEqualTo("email", currentemail).get().addOnSuccessListener {
            id = it.documents.get(0).id
            getNotes2()
        }
    }
        fun getNotes2(){
            noteList = ArrayList()
            db.collection("users/$id/notes").get().addOnSuccessListener {

                for(i in it.documents){
                    var noteToAdd = Note()
                    noteToAdd!!.data = i["data"] as String
                    noteToAdd!!.name = i["name"] as String
                    noteToAdd!!.id = i.id
                    noteList!!.add(noteToAdd)
                }


                layoutManager = LinearLayoutManager(this)
                adapter = NoteAdapter(noteList!!, this)
                rv_note.layoutManager = layoutManager
                rv_note.adapter = adapter

                adapter!!.notifyDataSetChanged()
            }
        }


    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_logout ->
                logout()
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
            R.id.nav_medication -> {
                startActivity(Intent(this, MedicationActivity::class.java))

            }
            R.id.nav_notes -> {
                startActivity(Intent(this, NoteActivity::class.java))


            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout(): Boolean {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        return true
    }
}
