package com.example.activityresultlauncher

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.activityresultlauncher.databinding.FragmentTestBinding
import kotlin.concurrent.thread


class TestFragment : Fragment() {
    private lateinit var myTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view:View = inflater.inflate(R.layout.fragment_test, container, false)
        // Inflate the layout for this fragment
        myTextView = view.findViewById<View>(R.id.email) as TextView

        val bundle = arguments
        if (bundle!=null){
            val message = bundle.getString("email")
            myTextView.text = message
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thread {
            val bundle = Bundle()
            val email = bundle.getString("email")
        }



    }

}