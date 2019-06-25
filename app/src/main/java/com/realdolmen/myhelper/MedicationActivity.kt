package com.realdolmen.myhelper

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.google.firebase.auth.AdditionalUserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.realdolmen.myhelper.adapter.MediAdapter
import com.realdolmen.myhelper.data.Medi

import kotlinx.android.synthetic.main.activity_medication.*
import kotlinx.android.synthetic.main.app_bar_medication.*
import kotlinx.android.synthetic.main.content_medication.*
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.*


class MedicationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")
    var id: String ?= null
    var currentemail: String ?= null
    private var mediList: ArrayList<Medi>? = null
    private var adapter: MediAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    var day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

    var selectedday: String ?= null
    var selectedtime: String ?= null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication)
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

        fab.setOnClickListener { view ->
            startActivity(Intent(this, AddMediActivity::class.java))
        }

        val days = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        spinner_day.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, days)

        spinner_day.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented")
                selectedday = "Monday"
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedday = days[p2]
                getMedi2()
            }

        }

        val period = arrayOf("Morning", "Midday" , "Evening", "Night")
        spinner_period.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, period)

        spinner_period.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                selectedtime = "Morning"
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedtime = period[p2]
                getMedi2()
            }

        }
        auth = FirebaseAuth.getInstance()
        currentemail = auth.currentUser!!.email
        println("de dag =" + day)

        getMedics()

    }

    fun getMedics(){
        userCollection.whereEqualTo("email", currentemail ).get().addOnSuccessListener {
            id = it.documents.get(0).id
            getMedi2()
        }




    }
    fun getMedi2(){
        mediList = ArrayList<Medi>()


        db.collection("users/$id/medi").get().addOnSuccessListener {
            var mediToAdd : Medi
            for(i in it.documents){
                if (i["day"].toString().contains("${selectedday}", true) && i["time"].toString().contains("${selectedtime}", true)){
                    mediToAdd = i.toObject(Medi::class.java)!!
                    mediList!!.add(mediToAdd)

                }
                /*println(" db: " + i["day"] +"vandaag: "+ day + "char: "+day.toChar())
                println(i["day"].toString().indexOfAny(charArrayOf(day.toChar())))*/

                }


            layoutManager = LinearLayoutManager(this)
            adapter = MediAdapter(mediList!!, this)
            rv_medi.layoutManager = layoutManager
            rv_medi.adapter = adapter

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
            R.id.action_logout -> logout()
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun logout(): Boolean {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        return true
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


}
