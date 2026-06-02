# NanoId for Kotlin

A Kotlin implementation of NanoId — a tiny, secure, URL-friendly unique string ID generator with typed prefixes.

## ID format

Each ID consists of a lowercase prefix and an 18-character base-62 random part, separated by an underscore:

```
user_a1B2c3D4e5F6g7H8i9
```

- **Prefix**: one or more lowercase alphanumeric characters, starting with a letter (e.g. `user`, `order`, `txn`)
- **Random part**: exactly 18 characters from `[A-Za-z0-9]`, generated randomly using cryptographically secure random bytes

## Installation

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("org.pixode:nanoid:1.0.0")
}
```

## Usage

### Generate a random ID

```kotlin
val id = NanoId.randomId("user")
// e.g. user_a1B2c3D4e5F6g7H8i9
```

### Parse from a string

```kotlin
val id = NanoId.fromString("user_a1B2c3D4e5F6g7H8i9")
println(id.prefix)  // user
println(id.nanoId)  // a1B2c3D4e5F6g7H8i9
```

### Derive an ID from bytes (deterministic)

Converts at least 14 bytes into a base-62 encoded ID. Useful for deriving a stable NanoId from an existing identifier such as a UUID.

```kotlin
val bytes: ByteArray = uuid.toByteArray()
val id = NanoId.fromBytes("order", bytes)
```

### Serialization

`NanoId` supports `kotlinx.serialization` out of the box and serializes as a plain JSON string:

```kotlin
@Serializable
data class User(val id: NanoId, val name: String)

val json = Json.encodeToString(user)
// {"id":"user_a1B2c3D4e5F6g7H8i9","name":"Alice"}
```

## Requirements

- Kotlin 2.x
- JVM 8+

