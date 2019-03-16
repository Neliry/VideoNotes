package com.neliry.banancheg.videonotes.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.adapter.FirebaseAdapter
import com.neliry.banancheg.videonotes.adapter.ItemDecorator
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.viewmodels.ConspectusViewModel
import com.neliry.banancheg.videonotes.viewmodels.ThemeViewModel
import kotlinx.android.synthetic.main.activity_conspectus.*
import kotlinx.android.synthetic.main.activity_theme.*

class ConspectusActivity : BaseNavigationDrawerActivity() , View.OnClickListener {
    override fun onClick(v: View?) {
        //
    }

    private lateinit var conspectusViewModel: ConspectusViewModel

    override fun getMainContentLayout(): Int {
        return R.layout.activity_conspectus
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //button_logout.setOnClickListener(this)


        //val numberOfColumns =  2
        //val layoutManager =  GridLayoutManager (this, numberOfColumns)
        val layoutManager=  LinearLayoutManager(this)
        recycler_view_conspectuses.layoutManager = layoutManager
        recycler_view_conspectuses.addItemDecoration(ItemDecorator(20))


        conspectusViewModel = ViewModelProviders.of(this).get(ConspectusViewModel::class.java!!)
        conspectusViewModel.getItems().observe(this,
            Observer<List<BaseItem>> { items ->
                Log.d("myTag", "ON CHANGED")
                recycler_view_conspectuses.adapter = (FirebaseAdapter(conspectusViewModel,items!!))
            })

    }
}
