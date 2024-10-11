package com.antonmakov.myweatherapp

class Consts {
    companion object {
        val API_KEY =
            "ba692958a28682ed8d5ec93409ebdcd0" //ideally needs to be encrypted for security
        val imageURL = "https://openweathermap.org/img/wn/10d@2x.png"
        val requestURL =
            "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=$API_KEY"

        val LOCATION_PERMISSION_REQUEST = 0
    }
}