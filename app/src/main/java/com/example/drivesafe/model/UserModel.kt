package com.example.drivesafe.model



data class UserModel (
    val id : String="",
    val name : String="",
    val email : String="",
    val contact : String="",

    ){
    fun toMap(): Map<String,Any?>{
        return mapOf(
            "name" to name,
            "email" to email,
            "contact" to contact
        )
    }
}
