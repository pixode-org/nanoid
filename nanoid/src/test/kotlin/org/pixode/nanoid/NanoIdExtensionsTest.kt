package org.pixode.nanoid

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigInteger

class NanoIdExtensionsTest : FunSpec({
    context("fromBytes") {
        mapOf(
            0.toByte() to "000000000000000000",
            1.toByte() to "000000000000000001",
            9.toByte() to "000000000000000009",
            10.toByte() to "00000000000000000a",
            35.toByte() to "00000000000000000z",
            36.toByte() to "00000000000000000A",
            61.toByte() to "00000000000000000Z",
            62.toByte() to "000000000000000010",
            123.toByte() to "00000000000000001Z",
        ).forEach { (lastByte: Byte, expected: String) ->
            test("converts 00..$lastByte to $expected") {
                val result = NanoId.fromBytes("abc", byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, lastByte))
                result.nanoId shouldBe expected
            }
        }

        test("encodes a 18-digit base-62 value correctly") {
            val bytes: ByteArray = BigInteger.valueOf(62).pow(19).subtract(BigInteger.ONE).toByteArray()
            check(bytes.size == 15)
            val result = NanoId.fromBytes("abc", bytes)
            result.nanoId shouldBe "ZZZZZZZZZZZZZZZZZZ"
        }

        test("ignores high-order bits beyond 18 base-62 digits for large values") {
            val bytes: ByteArray = BigInteger.valueOf(62).pow(19).add(BigInteger.ONE).toByteArray()
            check(bytes.size == 15)
            val result = NanoId.fromBytes("abc", bytes)
            result.nanoId shouldBe "000000000000000001"
        }

        test("accepts a byte array longer than 14 bytes") {
            val bytes: ByteArray = BigInteger.valueOf(62).pow(26).plus(BigInteger.ONE).toByteArray()
            check(bytes.size == 20)
            val result = NanoId.fromBytes("abc", bytes)
            result.nanoId shouldBe "000000000000000001"
        }

        test("returns a NanoId with the given prefix") {
            NanoId.fromBytes("abc", ByteArray(15)).prefix shouldBe "abc"
        }

        test("is deterministic for the same bytes") {
            val bytes = byteArrayOf(8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            NanoId.fromBytes("abc", bytes) shouldBe NanoId.fromBytes("abc", bytes)
        }

        test("produces different results for different byte arrays") {
            val bytes1 = byteArrayOf(8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            val bytes2 = byteArrayOf(8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1)
            NanoId.fromBytes("abc", bytes1) shouldNotBe NanoId.fromBytes("abc", bytes2)
        }

        mapOf(
            "throws when the byte array has 13 bytes" to { NanoId.fromBytes("abc", ByteArray(14)) },
            "throws when the byte array is empty" to { NanoId.fromBytes("abc", ByteArray(0)) },
            "throws when the prefix is invalid" to { NanoId.fromBytes("1invalid", ByteArray(15)) },
        ).forEach { (name: String, execute: () -> NanoId) ->
            test(name) {
                shouldThrow<IllegalArgumentException>(execute)
            }
        }
    }
})