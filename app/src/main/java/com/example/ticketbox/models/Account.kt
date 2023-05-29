package com.example.ticketbox.models

class Account {
    var accountID = 0
    var email: String? = null
    var password: String? = null

    constructor() {}
    constructor(email: String?) {
        this.email = email
    }
}