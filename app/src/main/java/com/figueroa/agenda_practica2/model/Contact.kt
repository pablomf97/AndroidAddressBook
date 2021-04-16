package com.figueroa.agenda_practica2.model

class Contact {
    // Getters & Setters
    // Attributes
    var id = 0
    var name: String? = null
    var alias: String? = null
    var phoneNumber: String? = null
    var email: String? = null
    var notes: String? = null

    // Constructors
    constructor() {}
    constructor(name: String?, alias: String?, phoneNumber: String?, email: String?, notes: String?) {
        this.name = name
        this.alias = alias
        this.phoneNumber = phoneNumber
        this.email = email
        this.notes = notes
    }

    constructor(id: Int, name: String?, alias: String?, phoneNumber: String?, email: String?, notes: String?) {
        this.id = id
        this.name = name
        this.alias = alias
        this.phoneNumber = phoneNumber
        this.email = email
        this.notes = notes
    }

    // toString
    override fun toString(): String {
        return "Contact{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", notes='" + notes + '\'' +
                '}'
    }
}