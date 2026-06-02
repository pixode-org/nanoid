package org.pixode.nanoid

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import java.math.BigInteger

class NanoIdExtensionsTest : FunSpec({
    context("fromBytes") {
        test("converts all-zero bytes to a NanoId of all-A characters") {
            val result = NanoId.fromBytes("abc", ByteArray(14))
            result.nanoId shouldBe "AAAAAAAAAAAAAAAAAA"
        }

        test("zero-pads small values with A characters on the left") {
            // BigInteger 1 encodes to 'B' (index 1), padded to 18 digits: 17×'A' + 'B'
            val result = NanoId.fromBytes("abc", byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1))
            result.nanoId shouldBe "AAAAAAAAAAAAAAAAAB"
        }

        test("encodes a two-digit base-62 value correctly") {
            // BigInteger 62 = 1×62 + 0, encodes to 16×'A' + 'B' + 'A' = "AAAAAAAAAAAAAAAABA"
            val result = NanoId.fromBytes("abc", byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 62))
            result.nanoId shouldBe "AAAAAAAAAAAAAAAABA"
        }

        test("encodes a 18-digit base-62 value correctly") {
            val bytes: ByteArray = BigInteger.valueOf(62).pow(19).subtract(BigInteger.ONE).toByteArray()
            val result = NanoId.fromBytes("abc", bytes)
            result.nanoId shouldBe "999999999999999999"
        }

        test("ignores high-order bits beyond 18 base-62 digits for large values") {
            val bytes: ByteArray = BigInteger.valueOf(62).pow(19).add(BigInteger.ONE).toByteArray()
            val result = NanoId.fromBytes("abc", bytes)
            result.nanoId shouldBe "AAAAAAAAAAAAAAAAAB"
        }

        test("accepts a byte array longer than 14 bytes") {
            val result = NanoId.fromBytes("abc", ByteArray(20))
            result.nanoId shouldBe "AAAAAAAAAAAAAAAAAA"
        }

        test("returns a NanoId with the given prefix") {
            NanoId.fromBytes("abc", ByteArray(14)).prefix shouldBe "abc"
        }

        test("is deterministic for the same bytes") {
            val bytes = byteArrayOf(8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            NanoId.fromBytes("abc", bytes) shouldBe NanoId.fromBytes("abc", bytes)
        }

        test("produces different results for different byte arrays") {
            val bytes1 = byteArrayOf(8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            val bytes2 = byteArrayOf(8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1)
            NanoId.fromBytes("abc", bytes1) shouldNotBe NanoId.fromBytes("abc", bytes2)
        }

        mapOf(
            "throws when the byte array has 13 bytes" to { NanoId.fromBytes("abc", ByteArray(13)) },
            "throws when the byte array is empty" to { NanoId.fromBytes("abc", ByteArray(0)) },
            "throws when the prefix is invalid" to { NanoId.fromBytes("1invalid", ByteArray(14)) },
        ).forEach { (name: String, execute: () -> NanoId) ->
            test(name) {
                shouldThrow<IllegalArgumentException>(execute)
            }
        }
    }
})