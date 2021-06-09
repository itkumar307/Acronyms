package com.kumar.acronyms.ui.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.kumar.acronyms.R
import com.kumar.acronyms.data.api.ApiHelper
import com.kumar.acronyms.data.api.RetrofitBuilder
import com.kumar.acronyms.data.model.AcronymsResponse
import com.kumar.acronyms.ui.adapter.AcronymsAdapter
import com.kumar.acronyms.ui.base.ViewModelFactory
import com.kumar.acronyms.ui.viewmodel.MainViewModel
import com.kumar.acronyms.utils.Logger
import com.kumar.acronyms.utils.Status
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: AcronymsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViewModel()
       // setupUI()
        setupObservers()

    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(MainViewModel::class.java)
    }

    private fun setupUI() {
        acronyms_recycler_view.layoutManager = LinearLayoutManager(this)
        adapter = AcronymsAdapter(arrayListOf())
        acronyms_recycler_view.addItemDecoration(
            DividerItemDecoration(
                acronyms_recycler_view.context,
                (acronyms_recycler_view.layoutManager as LinearLayoutManager).orientation
            )
        )
        acronyms_recycler_view.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.getAcronyms().observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        Logger.info("MainActivity", "service is getting success")
                        acronyms_recycler_view.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        resource.data?.let { acronyms -> retrieveList(acronyms) }
                    }
                    Status.ERROR -> {
                        Logger.info("MainActivity", "service is getting error")
                        acronyms_recycler_view.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        Logger.info(
                            "MainActivity",
                            "Message - ${it.message} , Status - ${it.status}"
                        )
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        Logger.info("MainActivity", "service is getting response, loading stage")
                        progressBar.visibility = View.VISIBLE
                        acronyms_recycler_view.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun retrieveList(acronymsResponse: List<AcronymsResponse>) {

        val filtered = mutableListOf<String>()
        acronymsResponse.get(0).lfs.forEach { element -> element.lf?.let { filtered.add(it) } }

        Logger.info("MainActivity", "${Gson().toJson(filtered)}")
        acronyms_recycler_view.layoutManager = LinearLayoutManager(this)
        adapter = AcronymsAdapter(filtered as ArrayList<String>)
        acronyms_recycler_view.addItemDecoration(
            DividerItemDecoration(
                acronyms_recycler_view.context,
                (acronyms_recycler_view.layoutManager as LinearLayoutManager).orientation
            )
        )
        acronyms_recycler_view.adapter = adapter

//        adapter.apply {
//            addAcronym(filtered)
//            notifyDataSetChanged()
//        }
    }

}