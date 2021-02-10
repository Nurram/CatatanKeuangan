package com.nurram.project.pencatatkeuangan

import com.nurram.project.pencatatkeuangan.db.Record

class GraphModel(
    val date: String,
    val records: MutableList<Record>
)