package com.example.todolist

import java.sql.Timestamp

class ModelTask {
    var title:String=""
    var date:String=""
    var time:String=""
    var timestamp:Long=0

    constructor()

    constructor(title: String, date: String, time: String,timestamp: Long) {
        this.title = title
        this.date = date
        this.time = time
        this.timestamp=timestamp
    }
}
