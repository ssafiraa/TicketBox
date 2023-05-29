package com.example.ticketbox.models

import android.graphics.Bitmap

class Client {
    var clientID: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var phone: String? = null
    var creditCard: String? = null
    var image: Bitmap? = null

    constructor() {}
    constructor(firstName: String?, lastName: String?, phone: String?, creditCard: String?) {
        this.firstName = firstName
        this.lastName = lastName
        this.phone = phone
        this.creditCard = creditCard
    }
}