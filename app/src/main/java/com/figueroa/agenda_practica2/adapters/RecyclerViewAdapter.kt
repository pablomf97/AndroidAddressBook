package com.figueroa.agenda_practica2.adapters

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewSwitcher
import androidx.recyclerview.widget.RecyclerView
import com.figueroa.agenda_practica2.ContactDetails
import com.figueroa.agenda_practica2.R
import com.figueroa.agenda_practica2.adapters.RecyclerViewAdapter.ContactsViewHolder
import com.figueroa.agenda_practica2.db.AgendaDB
import com.figueroa.agenda_practica2.model.Contact
import java.util.*

// The adapter for the recycler view in the main page
class RecyclerViewAdapter(private val contacts: MutableList<Contact>,
                          private val vs: ViewSwitcher?) : RecyclerView.Adapter<ContactsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val viewItem: View
        var contactsViewHolder: ContactsViewHolder? = null
        try {
            viewItem = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_contact, parent, false)
            contactsViewHolder = ContactsViewHolder(viewItem)
        } catch (e: Exception) {
            Log.e("RECYCLERVIEW_ERROR", e.message!!)
        }
        return contactsViewHolder!!
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val item = contacts[position]
        holder.itemView.setOnClickListener { v ->
            val detailsView = Intent(v.context, ContactDetails::class.java)
            detailsView.putExtra("contactId", contacts[position].id)
            v.context.startActivity(detailsView)
        }
        holder.itemView.setOnLongClickListener { v ->
            val alertDialog = AlertDialog.Builder(v.context)
            alertDialog.setTitle("Are you sure you want to delete " + contacts[position].name + "?")
            alertDialog.setNegativeButton("Cancel", null)
            alertDialog.setPositiveButton("Delete") { dialog, _ ->
                val db = AgendaDB(v.context)
                if (db.deleteContact(contacts[position])) {
                    Toast.makeText(v.context, "Successfully deleted contact", Toast.LENGTH_LONG).show()
                    contacts.clear()
                    contacts.addAll(db.getAllContactsSimple("name")!!)
                    notifyDataSetChanged()
                    if (vs != null && contacts != null && contacts.size == 0 && vs.currentView !is TextView) {
                        vs.showNext()
                    }
                } else {
                    Toast.makeText(v.context, "Could not delete contact, please try again", Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
            alertDialog.show()
            true
        }
        holder.bindContacts(item)
    }

    override fun getItemCount(): Int {
        return contacts!!.size
    }

    class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tag: TextView = itemView.findViewById(R.id.contact_recyclerview_tag)
        private val name: TextView = itemView.findViewById(R.id.recyclerview_contact_name_text)
        private val phoneNumber: TextView = itemView.findViewById(R.id.recyclerview_contact_phone_text)

        fun bindContacts(contact: Contact?) {
            val tagLetter = contact?.name!!.trim { it <= ' ' }[0]
            val random = Random()
            val color = Color.argb(255, random.nextInt(256),
                    random.nextInt(256), random.nextInt(256))
            tag.text = tagLetter.toString().toUpperCase(Locale.ROOT)
            val shape = ShapeDrawable(OvalShape())
            shape.paint.color = color
            tag.background = shape
            name.text = contact.name
            phoneNumber.text = contact.phoneNumber
        }

    }
}