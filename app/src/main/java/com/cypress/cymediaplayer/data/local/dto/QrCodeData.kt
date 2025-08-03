package com.cypress.cymediaplayer.data.local.dto

data class QrCodeData(
    var ip: String? = "",
    val verificationCode: String = "",
    var salt: String = ""
)
