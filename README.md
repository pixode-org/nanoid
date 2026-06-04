# NanoId

<a href="https://central.sonatype.com/artifact/org.pixode/nanoid">![Maven Central Version](https://img.shields.io/maven-central/v/org.pixode/nanoid)</a>

A Kotlin class representing a prefixed nano ID — a tiny, secure, URL-friendly unique string ID generator with typed prefixes.

## ID format

Each ID consists of a lowercase prefix and an 18-character base-62 random part, separated by an underscore:

```
user_a1B2c3D4e5F6g7H8i9
```

- **Prefix**: one or more lowercase alphanumeric characters, starting with a letter (e.g. `user`, `order`, `txn`)
- **Identifier**: exactly 18 characters from `[A-Za-z0-9]`, generated randomly using cryptographically secure random bytes

## Installation

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("org.pixode:nanoid:VERSION")
}
```

## Usage

### Generate a random ID

```kotlin
val id = NanoId.random("user")
// e.g. user_a1B2c3D4e5F6g7H8i9
```

### Parse from a string

```kotlin
val id = NanoId("user_a1B2c3D4e5F6g7H8i9")
println(id.prefix)  // user
println(id.identifier)  // a1B2c3D4e5F6g7H8i9
```

### Create from a prefix and identifier

```kotlin
val id = NanoId("user", "a1B2c3D4e5F6g7H8i9")
// user_a1B2c3D4e5F6g7H8i9
```

### Derive an ID by hashing a string

```kotlin
val id = NanoId.fromHashedString("user", "alice@example.com")
// user_dBxrYpY3tixdomTAiy
```

### Serialization

`NanoId` supports `kotlinx.serialization` out of the box and serializes as a plain JSON string:

```kotlin
@Serializable
data class User(val id: NanoId, val name: String)

val json = Json.encodeToString(user)
// { "id": "user_a1B2c3D4e5F6g7H8i9", "name": "Alice" }
```

## Requirements

- Kotlin 2.x
- JVM 8+

## License

Copyright 2026 Flavien Charlon

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.

