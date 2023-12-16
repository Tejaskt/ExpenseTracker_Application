package com.tejaskt.expensetracker.model

class Data {
    var amount = 0
    var type: String? = null
    var note: String? = null
    var id: String? = null

    constructor() {}
    constructor(amount: Int, type: String?, note: String?, id: String?, date: String?) {
        this.amount = amount
        this.type = type
        this.note = note
        this.id = id
        this.date = date
    }

    var date: String? = null
}