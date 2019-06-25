package com.realdolmen.myhelper

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.realdolmen.myhelper.LoginActivity
import com.realdolmen.myhelper.MedicationActivity
import com.realdolmen.myhelper.R
import com.realdolmen.myhelper.data.Medi
import kotlinx.android.synthetic.main.content_addmedi.*

class AddMediActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")
    var id: String ?= null
    var currentemail: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addmedi)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        confirm_entry.setOnClickListener {
            addMedi()
        }
        navView.setNavigationItemSelectedListener(this)
    }

    private fun addMedi() {

       var medi: Medi = Medi()
            medi.naam   = name.text.toString()
        medi.dose = dose.text.toString()
        var daytoAdd: String? = ""
        if(cbmonday.isChecked)daytoAdd += "monday "
        if (cbtuesday.isChecked)daytoAdd += "tuesday "
        if (cbwednesday.isChecked)daytoAdd += "wednesday "
        if (cbthursday.isChecked)daytoAdd += "thursday "
        if (cbfriday.isChecked)daytoAdd += "friday "
        if (cbsaturday.isChecked)daytoAdd += "saturday "
        if (cbsunday.isChecked)daytoAdd += "sunday "
        medi.day = daytoAdd

        var timetoadd: String ?= ""
        if (cbmorning.isChecked)timetoadd += "morning "
        if (cbmidday.isChecked) timetoadd += "midday "
        if(cbevening.isChecked) timetoadd += "evening "
        if(cbnight.isChecked) timetoadd += "night "
        medi.time = timetoadd

        if(name.text.toString().trim().isEmpty() || dose.text.toString().trim().isEmpty() || timetoadd!!.trim().isEmpty() || daytoAdd!!.trim().isEmpty()){
            Toast.makeText(this, "Please fill in all te fields!", Toast.LENGTH_LONG).show()
        }else {
            auth = FirebaseAuth.getInstance()
            currentemail = auth.currentUser!!.email
            userCollection.whereEqualTo("email", currentemail).get().addOnSuccessListener {
                id = it.documents.get(0).id
                db.collection("users/$id/medi").add(medi)
                startActivity(Intent(this, MedicationActivity::class.java))
            }.addOnFailureListener {
                Toast.makeText(this, "Make sure you have a stable internet connection! failed to add", Toast.LENGTH_LONG)
                    .show()
            }

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
