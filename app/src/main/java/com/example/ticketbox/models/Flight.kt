package com.example.ticketbox.models

import java.util.Date

class Flight {
    var flightID = 0
    var flightNumber = 0
    var origin: String
    var destination: String
    var departureDate: Date
    var arrivalDate: Date? = null
    var departureTime: Date? = null
    var arrivalTime: Date? = null
    var fare: Double? = null
    var totalCost: Double? = null
    var travelTime = 0
    var flightClass: String? = null

    constructor(
        flightNumber: Int,
        origin: String,
        destination: String,
        departureDate: Date,
        arrivalDate: Date?,
        departureTime: Date?,
        arrivalTime: Date?,
        fare: Double?,
        totalCost: Double?,
        travelTime: Int,
        flightClass: String?
    ) {
        this.flightNumber = flightNumber
        this.origin = origin
        this.destination = destination
        this.departureDate = departureDate
        this.arrivalDate = arrivalDate
        this.departureTime = departureTime
        this.arrivalTime = arrivalTime
        this.fare = fare
        this.totalCost = totalCost
        this.travelTime = travelTime
        this.flightClass = flightClass
    }

    constructor(origin: String, destination: String, departureDate: Date) {
        this.origin = origin
        this.destination = destination
        this.departureDate = departureDate
    }
}