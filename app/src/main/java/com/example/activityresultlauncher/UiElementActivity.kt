package com.example.activityresultlauncher

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.example.activityresultlauncher.databinding.ActivityUiElementBinding
import com.example.activityresultlauncher.databinding.ButtonLayoutBinding
import com.example.activityresultlauncher.model.Option
import com.google.gson.JsonArray
import com.google.gson.JsonObject


class UiElementActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener,
    RadioGroup.OnCheckedChangeListener {
    private lateinit var binding: ActivityUiElementBinding
    lateinit var btnBinding: ButtonLayoutBinding
    var jsonObject = JsonObject()
    private val jsonArrayCheckBox = JsonArray()
    private var chekBoxValue: String = ""
    private var genderValue: String = ""
    private val arrayListRadioPosition: ArrayList<Int> = ArrayList()
    private val checkBoxPosition:ArrayList<Int> = ArrayList()
    val ratingValue:Float? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUiElementBinding.inflate(layoutInflater)
        btnBinding = ButtonLayoutBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_ui_element)
        val arrayListPosition: ArrayList<Int> = ArrayList()
        val arrayPositionSpinner: ArrayList<Int> = ArrayList()
        for (inputPosition in Utils.list().indices) {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(30, 30, 30, 30)
            val ratingTitle = TextView(applicationContext)
            params.setMargins(25, 20, 25, 0)
            if (Utils.list()[inputPosition].inputType == "text") {
                val textTitle = TextView(applicationContext)
                textTitle.text = Utils.list()[inputPosition].subLocality
                binding.formData.addView(textTitle)
                val edtLiL = LinearLayout(applicationContext)
                edtLiL.orientation = LinearLayout.VERTICAL
                val editText = EditText(applicationContext)
                editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                editText.hint = Utils.list()[inputPosition].subLocality
                editText.inputType = InputType.TYPE_CLASS_TEXT
                editText.textSize = 12f
                editText.setPadding(28)
                editText.background = resources.getDrawable(R.drawable.edt_bg)
                edtLiL.addView(editText)
                arrayListPosition.add(inputPosition)
                binding.formData.addView(edtLiL, params)
            }
            if (Utils.list()[inputPosition].inputType == "radio") {
                val titleGender = TextView(applicationContext)
                titleGender.text = Utils.list()[inputPosition].subLocality
                genderValue = Utils.list()[inputPosition].subLocality
                binding.formData.addView(titleGender)
                val radioGroup = RadioGroup(applicationContext)
                for (gender in Utils.list()[inputPosition].option.indices) {
                    val radioButton = RadioButton(applicationContext)
                    radioButton.text = Utils.list()[inputPosition].option[gender].title
                    radioGroup.addView(radioButton)
                }
                binding.formData.addView(radioGroup, params)
                radioGroup.setOnCheckedChangeListener(this)
            }
            if (Utils.list()[inputPosition].inputType == "dropDown") {
                val lable = TextView(applicationContext)
                lable.text = Utils.list()[inputPosition].subLocality
                binding.formData.addView(lable)
                val linearLySpinner = LinearLayout(applicationContext)
                linearLySpinner.orientation = LinearLayout.VERTICAL
                val spinner = Spinner(applicationContext)
                spinner.setPadding(21)
                spinner.background = resources.getDrawable(R.drawable.edt_bg)
                val adapter: ArrayAdapter<Option> = ArrayAdapter<Option>(
                    this,
                    android.R.layout.simple_spinner_item, Utils.list()[inputPosition].option
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
                spinner.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View, position: Int, id: Long
                    ) {
                        spinner.tag = Utils.list()[inputPosition].option[position].title
                        Log.e(
                            javaClass.simpleName,
                            "onItemSelected: " + Utils.list()[inputPosition].option[position].title
                        )
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // write code to perform some action
                    }

                }
                linearLySpinner.addView(spinner)
                binding.formData.addView(linearLySpinner, params)
                arrayPositionSpinner.add(inputPosition)
                if (spinner.tag != null) {
                    jsonObject.addProperty(
                        Utils.list()[inputPosition].subLocality,
                        spinner.tag.toString()
                    )
                }
            }
            if (Utils.list()[inputPosition].inputType == "checkBox") {
                val checkLable = TextView(applicationContext)
                checkLable.text = Utils.list()[inputPosition].subLocality
                binding.formData.addView(checkLable)
                chekBoxValue = Utils.list()[inputPosition].subLocality
                val country = Utils.list()[inputPosition].countryList
                for (j in country.indices) {
                    val checkBox = CheckBox(this)
                    Log.e(javaClass.simpleName, "onCreate: " + country[j].value)
                    checkBox.text = country[j].value
                    checkBox.setOnCheckedChangeListener(this)
                    binding.formData.addView(checkBox, params)
                }

                jsonObject.add(chekBoxValue, jsonArrayCheckBox)
            }
            if (Utils.list()[inputPosition].inputType == "rating") {

                ratingTitle.text = Utils.list()[inputPosition].subLocality
                val rating = RatingBar(applicationContext)
                rating.stepSize = 0.5f
                rating.layoutParams = layoutParams
                binding.formData.addView(ratingTitle)
                rating.setOnRatingBarChangeListener { _, ratingValue, _ ->
                    jsonObject.addProperty("rating", ratingValue)
                }
                binding.ratingLayout.addView(rating)
            }
            var count = 0
            btnBinding.btnSave.setOnClickListener {
                count = 0
                for (position in arrayListPosition) {
                    val ll: LinearLayout =
                        binding.formData.getChildAt(position + position + 1) as LinearLayout
                    var textInput: EditText? = null
                    textInput = ll.getChildAt(0) as EditText
                    val textLabel = Utils.list()[position].subLocality
                    val type = Utils.list()[position].inputType
                    Log.d("SSDDDDD", type)
                    Log.e(javaClass.simpleName, "textLabel: $textLabel")
                    Log.e("value", textInput.text.toString())
                    jsonObject.addProperty(textLabel, textInput.text.toString())
                    if (textInput.text.toString().isNotEmpty()) {
                        count++
                        if (count == arrayListPosition.size) {
                            if (isValid(type, textInput.text.toString())) {
                                if (arrayListRadioPosition.size <=0){
                                    Toast.makeText(this, "Select At list one radioGroup item ", Toast.LENGTH_SHORT).show()
                                }
                                else if (checkBoxPosition.size<=0){
                                    Toast.makeText(this, "Select At list one checkBox item ", Toast.LENGTH_SHORT).show()
                                }
                                else if (arrayPositionSpinner.size == 0) {
                                    Toast.makeText(
                                        this,
                                        "Select At list one item",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    Log.e(javaClass.simpleName, "onCreate: $jsonObject")
                                    Toast.makeText(applicationContext, "$jsonObject", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, ImagePiker::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                        Log.e(javaClass.simpleName, "onCreate: $count")
                    } else if (textInput.text.isEmpty()) {
                        Toast.makeText(
                            applicationContext,
                            "All Field is Require",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        btnBinding.btnCancel.setOnClickListener {
            finish()
            Toast.makeText(applicationContext, "Cancel", Toast.LENGTH_SHORT).show()
        }

        binding.layout.addView(btnBinding.root)
        setContentView(binding.root)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            jsonArrayCheckBox.add(buttonView?.text.toString())
            if (buttonView != null) {
                checkBoxPosition.add(buttonView.id)
                Log.e(javaClass.simpleName, "onCheckedChanged: "+buttonView.id )
            }
        }
        jsonObject.add(chekBoxValue, jsonArrayCheckBox)

    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        val selectedRadioText = binding.root.findViewById<RadioButton?>(checkedId).text.toString()
        jsonObject.addProperty(genderValue, selectedRadioText)
        arrayListRadioPosition.add(checkedId)
        Log.e(javaClass.simpleName, "RadioGroup$checkedId")
    }

    fun isValid(inputType: String, value: String): Boolean {
        when (inputType) {
            "text" -> {
                if (value.isEmpty()) {
                    Toast.makeText(this, "Mobile number is Require", Toast.LENGTH_SHORT).show()
                }
                if (value.length != 10) {
                    Toast.makeText(this, "Mobile number is not valid ", Toast.LENGTH_SHORT).show()
                } else {
                    return true
                }
            }
        }
        return false
    }

}

