package org.pixode.nanoid

import java.security.SecureRandom
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class NanoId(val value: String) {
    constructor(prefix: String, nanoId: String) : this("${prefix}_$nanoId")

    val prefix: String get() = value.substring(0, value.lastIndexOf('_'))
    val nanoId: String get() = value.substring(value.lastIndexOf('_') + 1)

    init {
        require(value.matches(regex)) { "The value provided does not match a nano ID format" }
    }

    override fun toString(): String = value

    companion object {
        internal const val ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private val regex = Regex("^[a-z][a-z0-9]*_[a-zA-Z0-9]{18}$")
    }
}
