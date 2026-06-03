package org.pixode.nanoid

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.json.Json

private const val VALID_ID = "a1B2c3D4e5F6g7H8i9"

class NanoIdTest : FunSpec({
    context("secondary constructor") {
        test("accepts valid prefix and nanoId") {
            NanoId("abc", VALID_ID).prefix shouldBe "abc"
            NanoId("abc", VALID_ID).nanoId shouldBe VALID_ID
        }

        test("accepts prefix with digits after first character") {
            NanoId("abc123", VALID_ID).prefix shouldBe "abc123"
        }

        mapOf(
            "rejects empty prefix" to { NanoId("", VALID_ID) },
            "rejects prefix starting with a digit" to { NanoId("1abc", VALID_ID) },
            "rejects prefix starting with uppercase letter" to { NanoId("Abc", VALID_ID) },
            "rejects prefix with uppercase letters" to { NanoId("aBc", VALID_ID) },
            "rejects prefix with underscores" to { NanoId("a_b", VALID_ID) },
            "rejects prefix with non alphanumeric character" to { NanoId("abç", VALID_ID) },
            "rejects nanoId shorter than 18 characters" to { NanoId("abc", "short") },
            "rejects nanoId longer than 18 characters" to { NanoId("abc", VALID_ID + "X") },
            "rejects nanoId with underscore" to { NanoId("abc", "a1b2c3d4e5f6g7h8i9_0") },
            "rejects nanoId with non-base62 characters" to { NanoId("abc", "a1b2c3d4e5f6g7h8i9*0") },
        ).forEach { (name: String, execute: () -> NanoId) ->
            test(name) {
                shouldThrow<IllegalArgumentException>(execute)
            }
        }
    }

    context("primary constructor") {
        test("splits on the last underscore when prefix contains none") {
            NanoId("myprefix_$VALID_ID") shouldBe NanoId("myprefix", VALID_ID)
        }

        test("throws when there is no underscore") {
            shouldThrow<IllegalArgumentException> { NanoId("abc$VALID_ID") }
        }

        test("throws when the string has more than one underscore") {
            shouldThrow<IllegalArgumentException> { NanoId("abc_def_$VALID_ID") }
        }

        test("throws when the nanoId part is invalid") {
            shouldThrow<IllegalArgumentException> { NanoId("abc_short") }
        }
    }

    context("toString") {
        test("returns the underlying value string") {
            NanoId("abc", VALID_ID).toString() shouldBe "abc_$VALID_ID"
        }
    }

    context("equality") {
        test("equal when value strings are identical") {
            NanoId("abc", VALID_ID) shouldBe NanoId("abc", VALID_ID)
        }

        test("not equal when nanoId differs") {
            NanoId("abc", VALID_ID) shouldNotBe NanoId("abc", VALID_ID.reversed())
        }

        test("not equal when nanoId differs only by case") {
            NanoId("abc", "a1B2c3D4e5F6g7H8i9") shouldNotBe NanoId("abc", "A1b2C3d4E5f6G7h8I9")
        }

        test("not equal when prefix differs") {
            NanoId("abc", VALID_ID) shouldNotBe NanoId("xyz", VALID_ID)
        }

        test("hashCode is consistent with equals") {
            NanoId("abc", VALID_ID).hashCode() shouldBe NanoId("abc", VALID_ID).hashCode()
        }
    }

    context("serialization") {
        test("serializes to a JSON string") {
            Json.encodeToString(NanoId.serializer(), NanoId("abc", VALID_ID)) shouldBe "\"abc_$VALID_ID\""
        }

        test("deserializes from a JSON string") {
            Json.decodeFromString(NanoId.serializer(), "\"abc_$VALID_ID\"") shouldBe NanoId("abc", VALID_ID)
        }

        test("roundtrips through JSON") {
            val id = NanoId.random("prefix")
            Json.decodeFromString(NanoId.serializer(), Json.encodeToString(NanoId.serializer(), id)) shouldBe id
        }
    }
})
