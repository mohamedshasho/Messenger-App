package com.example.messengerapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.messengerapp.R

class MoreFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val textview_title  =activity?.findViewById<TextView>(R.id.title_toolbar_textview)
        textview_title?.text="Discover"
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

}