package com.nurram.project.pencatatkeuangan.model

import com.nurram.project.pencatatkeuangan.db.Record
import java.util.*

class GraphModel(
    val date: Date,
    val records: MutableList<Record>
)