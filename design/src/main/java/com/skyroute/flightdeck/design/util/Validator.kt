package com.skyroute.flightdeck.design.util

import com.skyroute.flightdeck.common.util.PatternFileName
import com.skyroute.flightdeck.core.Clash

typealias Validator = (String) -> Boolean

val ValidatorAcceptAll: Validator = {
    true
}

val ValidatorFileName: Validator = {
    PatternFileName.matches(it) && it.isNotBlank()
}

val ValidatorNotBlank: Validator = {
    it.isNotBlank()
}

val ValidatorHttpUrl: Validator = {
    it.startsWith("https://", ignoreCase = true) || it.startsWith("http://", ignoreCase = true)
}

val ValidatorAutoUpdateInterval: Validator = {
    it.isEmpty() || (it.toLongOrNull() ?: 0) >= 15
}

val ValidatorAgeSecretKey: Validator = {
    it.isEmpty() || Clash.veritySecretKeys(it)
}