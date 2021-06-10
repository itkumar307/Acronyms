package com.kumar.acronyms.ui.view

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import com.kumar.acronyms.utils.AcronymsUtil
import com.kumar.acronyms.utils.Logger
import com.kumar.acronyms.utils.Status
import kotlinx.android.synthetic.main.activity_main.*

/**
 * This activity used to get acronyms abbreviation by using
 * http://www.nactem.ac.uk/software/acromine/dictionary.py?sf="SIM"
 */

class MainActivity : AppCompatActivity() {


    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: AcronymsAdapter
    val filtered = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViewModel()
        setupUI()
    }

    /**
     * Set viewmodel
     */
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

        edt_txt_search.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun afterTextChanged(s: Editable?) {
                if (AcronymsUtil.isNetworkAvailable(this@MainActivity)) {
                    setupObservers(s.toString())
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Network connection is not available",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    resetAdapter(filtered)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    private fun setupObservers(acronym_input: String?) {
        if (acronym_input != null) {
            viewModel.getAcronyms(acronym_input).observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            Logger.info("MainActivity", "service is getting success")
                            acronyms_recycler_view.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            updateAdapter(resource.data);
                        }
                        Status.ERROR -> {
                            Logger.info("MainActivity", "service is getting error")
                            acronyms_recycler_view.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            Logger.info(
                                "MainActivity",
                                "Message - ${it.message} , Status - ${it.status}"
                            )
                            resetAdapter(filtered)
                            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                        }
                        Status.LOADING -> {
                            Logger.info(
                                "MainActivity",
                                "service is getting response, loading stage"
                            )
                            progressBar.visibility = View.VISIBLE
                            acronyms_recycler_view.visibility = View.GONE
                        }
                    }
                }
            })
        }
    }

    private fun updateAdapter(acronymsResponse: List<AcronymsResponse>?) {
        val filtered = getAdapterAcronymValue(acronymsResponse)
        if (filtered?.size == 0) {
            Toast.makeText(this, "No Result found", Toast.LENGTH_LONG).show()
        }
        resetAdapter(filtered)
    }

    /**
     *get abbreviation value from service response
     */
    private fun getAdapterAcronymValue(acronymsResponse: List<AcronymsResponse>?): MutableList<String> {
        val filtered = mutableListOf<String>()
        if (acronymsResponse != null && acronymsResponse?.size > 0) {
            acronymsResponse.get(0).lfs.forEach { element -> element.lf?.let { filtered.add(it) } }
            Logger.info("MainActivity", "${Gson().toJson(filtered)}")
        }
        return filtered;
    }

    /**
     * Reset adapter when error occurred & no result found
     */

    private fun resetAdapter(filtered: MutableList<String>) {
        adapter.apply {
            addAcronym(filtered)
            notifyDataSetChanged()
        }
    }

}