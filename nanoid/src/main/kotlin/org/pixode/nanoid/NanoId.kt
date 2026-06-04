package org.pixode.nanoid

import kotlinx.serialization.Serializable

/**
 * Represents a prefixed nano ID, consisting of an alphanumeric prefix and an 18-character
 * identifier, separated by an underscore (e.g. `user_aB3kL9mNpQ7rS2tUv1`).
 */
@Serializable
@JvmInline
value class NanoId(
    /** The full string representation of this [NanoId], e.g. `user_aB3kL9mNpQ7rS2tUv1`. */
    val value: String,
) {
    /**
     * Creates a [NanoId] from a [prefix] and a pre-generated [identifier] string, combining them
     * as `<prefix>_<identifier>`.
     *
     * @param prefix The alphanumeric prefix.
     * @param identifier An 18-character base-62 string.
     * @throws IllegalArgumentException if the resulting value does not match the nano ID format.
     */
    constructor(prefix: String, identifier: String) : this("${prefix}_$identifier")

    /** The prefix portion of this [NanoId]. */
    val prefix: String get() = value.substring(0, value.lastIndexOf('_'))

    /** The 18-character base-62 identifier portion of this [NanoId]. */
    val identifier: String get() = value.substring(value.lastIndexOf('_') + 1)

    init {
        require(value.matches(regex)) { "The value provided does not match a nano ID format" }
    }

    /** Returns the full string representation of this nano ID, equivalent to [value]. */
    override fun toString(): String = value

    companion object {
        internal const val ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private val regex = Regex("^[a-z][a-z0-9]*_[a-zA-Z0-9]{18}$")
    }
}
