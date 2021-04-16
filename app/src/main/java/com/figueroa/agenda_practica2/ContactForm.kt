package com.figueroa.agenda_practica2

import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.figueroa.agenda_practica2.db.AgendaDB
import com.figueroa.agenda_practica2.model.Contact
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class ContactForm : AppCompatActivity() {
    // Fields
    private lateinit var name: EditText
    private lateinit var nameLayout: TextInputLayout
    private lateinit var alias: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var phoneNumberLayout: TextInputLayout
    private lateinit var email: EditText
    private lateinit var emailLayout: TextInputLayout
    private lateinit var notes: EditText

    // Some booleans to check things...
    private var isNameReady = false
    private var isPhoneReady = false
    private var isEmailReady = true

    // The DB
    private var db: AgendaDB? = null

    // The image icon
    private var tag: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_form)
        val appBar = findViewById<MaterialToolbar>(R.id.contact_form_appbar)
        setSupportActionBar(appBar)
        db = AgendaDB(this)
        name = findViewById(R.id.contact_form_name)
        nameLayout = findViewById(R.id.contact_form_input_layout_name)
        alias = findViewById(R.id.contact_form_alias)
        phoneNumber = findViewById(R.id.contact_form_phone)
        phoneNumberLayout = findViewById(R.id.contact_form_input_layout_phone)
        email = findViewById(R.id.contact_form_email)
        emailLayout = findViewById(R.id.contact_form_input_layout_email)
        notes = findViewById(R.id.contact_form_notes)
        tag = findViewById(R.id.contact_form_tag)
        addTextWatchers()
    }

    override fun onSaveInstanceState(
            outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putString("name", name.text.toString())
        outState.putString("alias", alias.text.toString())
        outState.putString("phone", phoneNumber.text.toString())
        outState.putString("email", email.text.toString())
        outState.putString("notes", notes.text.toString())
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onRestoreInstanceState(
            savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)

        val savedName = savedInstanceState?.getString("name", "")
        val savedAlias = savedInstanceState?.getString("alias", "")
        val savedPhone = savedInstanceState?.getString("phone", "")
        val savedEmail = savedInstanceState?.getString("email", "")
        val savedNotes = savedInstanceState?.getString("notes", "")

        if (savedName != "")
            name.setText(savedName)
        if (savedAlias != "")
            alias.setText(savedInstanceState?.getString("alias", ""))
        if (savedPhone != "")
            phoneNumber.setText(savedInstanceState?.getString("phone", ""))
        if (savedEmail != "")
            email.setText(savedInstanceState?.getString("email", ""))
        if (savedNotes != "")
            notes.setText(savedInstanceState?.getString("notes", ""))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.contact_form_save, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.contact_form_save) {
            if (isNameReady && isPhoneReady && isEmailReady) {
                val toSave = Contact(name.text.toString().trim { it <= ' ' },
                        alias.text.toString().trim { it <= ' ' },
                        phoneNumber.text.toString().trim { it <= ' ' },
                        email.text.toString().trim { it <= ' ' },
                        notes.text.toString().trim { it <= ' ' })
                if (db!!.insertContact(toSave)) {
                    finish()
                } else {
                    Toast.makeText(this, "Could not save the contact! Please try again",
                            Toast.LENGTH_SHORT).show()
                }
            } else {
                if (!isNameReady) {
                    nameLayout.error = "Name cannot be empty!"
                    nameLayout.isErrorEnabled = true
                    isNameReady = false
                }
                if (!isPhoneReady) {
                    phoneNumberLayout.error = "Phone cannot be empty!"
                    phoneNumberLayout.isErrorEnabled = true
                    isPhoneReady = false
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addTextWatchers() {
        name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val name = s.toString().trim { it <= ' ' }
                nameLayout.error = "Name cannot be empty!"
                if (name.isEmpty()) {
                    nameLayout.isErrorEnabled = true
                    isNameReady = false
                } else {
                    nameLayout.isErrorEnabled = false
                    isNameReady = true
                    getTag(s.toString())
                }
            }
        })
        phoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val phone = s.toString().trim { it <= ' ' }
                phoneNumberLayout.error = "You must provide a valid phone number!"
                if (!Patterns.PHONE.matcher(phone).matches()) {
                    phoneNumberLayout.isErrorEnabled = true
                    isPhoneReady = false
                } else {
                    phoneNumberLayout.isErrorEnabled = false
                    isPhoneReady = true
                }
            }
        })
        email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val email = s.toString().trim { it <= ' ' }
                emailLayout.error = "You must provide a valid email address!"
                if (email.isEmpty()) {
                    emailLayout.isErrorEnabled = false
                    isEmailReady = true
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailLayout.isErrorEnabled = true
                    isEmailReady = false
                } else {
                    emailLayout.isErrorEnabled = false
                    isEmailReady = true
                }
            }
        })
    }

    private fun getTag(letter: String) {
        val tagLetter = letter.trim { it <= ' ' }[0]
        val random = Random()
        val color = Color.argb(255, random.nextInt(256),
                random.nextInt(256), random.nextInt(256))
        tag!!.text = tagLetter.toString().toUpperCase(Locale.ROOT)
        val shape = ShapeDrawable(OvalShape())
        shape.paint.color = color
        tag!!.background = shape
    }
}