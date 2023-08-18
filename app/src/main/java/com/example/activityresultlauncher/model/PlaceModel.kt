package com.example.activityresultlauncher.model

data class PlaceModel (var locale:String = "",var subLocality:String ="",var postalCode:String ="",val inputType:String = "",
    val option:ArrayList<Option> = arrayListOf(),val countryList:ArrayList<Country> = arrayListOf()
)

data class Option(
    var title:String = ""
) {
    override fun toString(): String {
        return title
    }
}
data class Country(
    val value:String = ""
)