package com.simcoder.uber.Driver

import androidx.appcompat.app.AppCompatActivity
import com.simcoder.uber.Objects.TypeObject
import android.os.Bundle
import com.simcoder.uber.R
import android.content.Intent
import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.EditText
import com.simcoder.uber.Objects.DriverObject
import com.simcoder.uber.Driver.DriverChooseTypeActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.simcoder.uber.Adapters.TypeAdapter
import com.simcoder.uber.Utils.Utils
import java.util.ArrayList

/**
 * Activity responsible for letting the driver chose the service type
 * they offer.
 *
 * It displays a recyclerView with the possible service type options
 */
class DriverChooseTypeActivity : AppCompatActivity() {
    private var mAdapter: TypeAdapter? = null
    var typeArrayList = ArrayList<TypeObject>()
    var mConfirm: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_choose_type)
        setupToolbar()
        mConfirm = findViewById(R.id.confirm)
        mConfirm?.setOnClickListener(View.OnClickListener { view: View? -> confirmEntry() })
        initRecyclerView()
        val service = intent.getStringExtra("service")
        for (mType in typeArrayList) {
            if (mType.id == service) {
                mAdapter!!.selectedItem = mType
            }
        }
        mAdapter!!.notifyDataSetChanged()
    }

    /**
     * Sets up toolbar with custom text and a listener
     * to go back to the previous activity
     */
    private fun setupToolbar() {
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.title = getString(R.string.type)
        myToolbar.setTitleTextColor(resources.getColor(R.color.white))
        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
        myToolbar.setNavigationOnClickListener { v: View? -> finish() }
    }

    private fun confirmEntry() {
        val returnIntent = Intent()
        returnIntent.putExtra("result", mAdapter!!.selectedItem.id)
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    /**
     * Initializes the recyclerview that shows the costumer the
     * available car types
     */
    private fun initRecyclerView() {
        typeArrayList = Utils.getTypeList(this@DriverChooseTypeActivity)
        val mRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this@DriverChooseTypeActivity)
        mRecyclerView.layoutManager = mLayoutManager
        mAdapter = TypeAdapter(typeArrayList, this@DriverChooseTypeActivity, null)
        mRecyclerView.adapter = mAdapter
    }
}