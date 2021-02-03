package com.nurram.project.catatankeuangan

import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.nurram.project.catatankeuangan.db.Hutang
import com.nurram.project.catatankeuangan.db.Record
import com.nurram.project.catatankeuangan.utils.CurencyFormatter
import com.nurram.project.catatankeuangan.utils.DateUtil
import com.nurram.project.catatankeuangan.utils.PagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_dialog_layout.view.*
import kotlinx.android.synthetic.main.saldo_dialog_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.fragment, MainFragment()).commit()
    }
}
