package com.figueroa.agenda_practica2

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.figueroa.agenda_practica2.db.AgendaDB
import com.figueroa.agenda_practica2.helpers.SdHelper
import com.figueroa.agenda_practica2.model.Contact
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.gson.GsonBuilder
import java.util.*

class ContactDetails : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {
    /** DB things.  */
    private var db: AgendaDB? = null

    /** Contact to retrieve from the db.  */
    private var contact: Contact? = null

    /** Button.  */
    private lateinit var options: MaterialButton

    /** Tag TextView.  */
    private lateinit var tag: TextView

    /** Name TextView.  */
    private lateinit var name: TextView

    /** Alias TextView.  */
    private lateinit var alias: TextView

    /** Phone TextView.  */
    private lateinit var phone: TextView

    /** Email TextView.  */
    private lateinit var email: TextView

    /** Notes TextView.  */
    private lateinit var notes: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_details)

        // The custom appbar of the app...
        val appBar = findViewById<MaterialToolbar>(R.id.contact_form_appbar)
        setSupportActionBar(appBar)

        // Getting the id of the contact that the user wants to view...
        val id = intent.getIntExtra("contactId", -1)

        // Creating a new connection to the db...
        db = AgendaDB(this)

        // Checking if the id was retrieved correctly..
        if (id == -1) {
            // If it was not, we build a new dialog to notify the user...
            createAndShowAlert()
        } else {
            // Otherwise, we try to get the contact from the DB...
            contact = db!!.getContactById(id, arrayOf("_id", "name", "alias",
                    "phoneNumber", "email", "notes"))

            // And check that it was retrieved successfully...
            if (contact != null) {
                // If it was, we build the view...
                tag = findViewById(R.id.contact_details_tag)
                name = findViewById(R.id.contact_details_name)
                alias = findViewById(R.id.contact_details_alias)
                phone = findViewById(R.id.contact_details_phone)
                email = findViewById(R.id.contact_details_email)
                notes = findViewById(R.id.contact_details_info)

                // Setting the tag...
                getTag(contact!!.name!![0].toString())

                // Setting the fields...
                name.text = contact!!.name!!
                alias.text = if (contact!!.alias != null && contact!!.alias!!.isEmpty()) "Whoops! This contact has no alias..." else contact!!.alias
                phone.text = contact!!.phoneNumber
                email.text = if (contact!!.email != null && contact!!.email!!.isEmpty()) "Whoops! This contact has no email..." else contact!!.email
                notes.text = if (contact!!.notes != null && contact!!.notes!!.isEmpty()) "Whoops! This contact has no notes..." else contact!!.notes

                // Setting the options...
                options = findViewById(R.id.contact_details_actions)
                options.setOnClickListener { v: View ->
                    val popup = PopupMenu(v.context, v)
                    val inflater = popup.menuInflater
                    inflater.inflate(R.menu.contact_details_actions_menu,
                            popup.menu)
                    popup.setOnMenuItemClickListener(this)
                    popup.show()
                }
            } else {
                // If it was not, we finish the activity...
                createAndShowAlert()
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (item.itemId == R.id.contact_details_call) {
            val callIntent = Intent(Intent.ACTION_DIAL,
                    Uri.parse("tel:" + contact?.phoneNumber))
            startActivity(callIntent)
        } else if (item.itemId == R.id.contact_details_message) {
            val messageIntent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("sms:" + contact?.phoneNumber))
            startActivity(messageIntent)
        } else if (item.itemId == R.id.contact_details_export) {
            checkPermissionsAndExport(contact)
        }
        return false
    }

    private fun getTag(letter: String) {
        val tagLetter = letter.trim { it <= ' ' }[0]
        val random = Random()
        val color = Color.argb(255, random.nextInt(256),
                random.nextInt(256), random.nextInt(256))
        tag.text = Character.toString(tagLetter).toUpperCase(Locale.ROOT)
        val shape = ShapeDrawable(OvalShape())
        shape.paint.color = color
        tag.background = shape
    }

    private fun createAndShowAlert() {
        // Creating the alert...
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Could not retrieve contact...")

        // When this dialog gets dismissed, the activity will be finished...
        alert.setNegativeButton("Go back") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        alert.setOnDismissListener { finish() }

        // We show the dialog...
        alert.show()
    }

    private fun checkPermissionsAndExport(contact: Contact?) {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Toast.makeText(this, "This version is not Android 6 or later "
                    + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show()
        } else {
            if (ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat
                        .requestPermissions(
                                this, arrayOf(permission),
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
            } else {
                val gson = GsonBuilder().setPrettyPrinting().create()
                val json = gson.toJson(contact)
                if (SdHelper.writeToSd(json, contact?.name)) {
                    Toast.makeText(applicationContext,
                            "Contact exported to Downloads folder",
                            Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,
                        "Storage permission granted, now you can export contacts",
                        Toast.LENGTH_SHORT)
                        .show()
            } else {
                Toast.makeText(this,
                        "Storage permission denied",
                        Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }
}