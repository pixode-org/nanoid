package org.pixode.nanoid

import java.math.BigInteger

fun NanoId.Companion.fromBytes(prefix: String, bytes: ByteArray): NanoId {
    require(bytes.size >= 14) { "The byte array must be at least 14 bytes long" }

    val base = BigInteger.valueOf(ALPHABET.length.toLong())
    val result = StringBuilder(18)

    var number = BigInteger(1, bytes)
    // Always produce exactly 18 digits: small values are zero-padded (ALPHABET[0]),
    // and any high-order bits beyond 18 digits are discarded.
    repeat(18) {
        val division: Array<out BigInteger> = number.divideAndRemainder(base)
        number = division[0]
        result.append(ALPHABET[division[1].toInt()])
    }

    // Reverse because remainders are calculated from least significant to most significant digit
    return NanoId(prefix, result.reverse().toString())
}
