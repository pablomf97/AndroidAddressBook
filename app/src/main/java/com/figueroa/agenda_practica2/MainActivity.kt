package com.figueroa.agenda_practica2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewSwitcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.figueroa.agenda_practica2.adapters.RecyclerViewAdapter
import com.figueroa.agenda_practica2.db.AgendaDB
import com.figueroa.agenda_practica2.model.Contact
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    // Floating action button
    private lateinit var fab: FloatingActionButton

    // Search view
    private lateinit var searchView: SearchView

    // Recyclerview
    private lateinit var contacts: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var data: MutableList<Contact>

    // Database
    private lateinit var db: AgendaDB

    // ViewSwitcher
    private var switcher: ViewSwitcher? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = AgendaDB(this)

        val appBar = findViewById<MaterialToolbar>(R.id.contact_form_appbar)
        setSupportActionBar(appBar)

        searchView = findViewById(R.id.recyclerview_search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {

                val aux: MutableList<Contact>? = db.findByName(newText)

                when {
                    aux.isNullOrEmpty() -> Toast.makeText(applicationContext,
                            "Could not find contact!",
                            Toast.LENGTH_SHORT).show()
                    else -> {
                        data.clear()
                        data.addAll(aux)
                        adapter.notifyDataSetChanged()
                    }
                }

                return true
            }
        })

        fab = findViewById(R.id.recyclerview_fab)
        fab.setOnClickListener(this)

        switcher = findViewById(R.id.recyclerview_viewswitcher)
        createRecyclerview()
    }

    override fun onResume() {
        super.onResume()
        updateRecyclerview()
    }

    override fun onClick(v: View) {
        if (v.id == R.id.recyclerview_fab) {
            val createContactIntent = Intent(this, ContactForm::class.java)
            startActivity(createContactIntent)
        }
    }

    private fun createRecyclerview() {
        data = ArrayList()
        // data = new ArrayList<>();
        // data.add(new Contact(1, "Funny Valentine", "D4C", "606060606", "funny.valentine@gmail.com", "Funny Valentine did nothing wrong"));
        contacts = findViewById(R.id.contacts_recyclerview)
        adapter = RecyclerViewAdapter(data, switcher)
        contacts.adapter = adapter
        contacts.layoutManager = LinearLayoutManager(this)
    }

    private fun updateRecyclerview() {
        data.clear()
        val contacts = db!!.getAllContactsSimple("name")
        if (contacts != null && contacts.isNotEmpty()) {
            data.addAll(contacts)
            adapter.notifyDataSetChanged()
            if (switcher!!.currentView !is RecyclerView) {
                switcher!!.showPrevious()
            }
        } else if (switcher!!.currentView !is TextView) {
            switcher!!.showNext()
        }
    }
}