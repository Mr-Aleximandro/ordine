package com.example.ordine.model

import com.android.volley.VolleyError

interface IVolley {

    val thisIVolley: IVolley
        get() = this

    fun onResponse(response:String)
    fun onErrorResponse(response: Class<VolleyError>)
}