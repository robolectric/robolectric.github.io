---
hide:
- toc
---

# Using qualified resources

As described [in the Android developer docs][android-providing-alternative-resources], resource
qualifiers allow you to change how your resources are loaded based on such factors as the language
on the device, to the screen size, to whether it is day or night. While these changes are often
tedious to test rigorously (every string has a translation for all supported languages), you may
find yourself wishing to run tests in different resource qualified contexts.

## Specifying resources in test

Specifying a resource qualifier is quite simple: simply add the desired qualifiers to the
[`@Config`][config-documentation] annotation on your test case or test class, depending on whether
you would like to change the resource qualifiers for the whole file, or simply one test.

Given the following resources:

/// tab | `values/strings.xml`

```xml
<string name="not_overridden">Not overridden</string>
<string name="overridden">Overridden</string>
<string name="overridden_twice">Overridden twice</string>
```

///

/// tab | `values-en/strings.xml`

```xml
<string name="overridden">Overridden in en</string>
<string name="overridden_twice">Overridden twice in en</string>
```

///

/// tab | `values-en-port/strings.xml`

```xml
<string name="overridden_twice">Overridden twice in en-port</string>
```

///

this Robolectric test would pass, using the Android resource qualifier resolution rules:

/// tab | Java

```java
@Test
@Config(qualifiers = "en-port")
public void shouldUseEnglishAndPortraitResources() {
  final Context context = RuntimeEnvironment.application;
  assertThat(context.getString(R.id.not_overridden)).isEqualTo("Not overridden");
  assertThat(context.getString(R.id.overridden)).isEqualTo("Overridden in en");
  assertThat(context.getString(R.id.overridden_twice)).isEqualTo("Overridden twice in en-port");
}
```

///

/// tab | Kotlin

```kotlin
@Test
@Config(qualifiers = "en-port")
fun shouldUseEnglishAndPortraitResources() {
    val context = RuntimeEnvironment.application
    assertThat(context.getString(R.id.not_overridden)).isEqualTo("Not overridden")
    assertThat(context.getString(R.id.overridden)).isEqualTo("Overridden in en")
    assertThat(context.getString(R.id.overridden_twice)).isEqualTo("Overridden twice in en-port")
}
```

///

Multiple qualifiers should be separated by dashes and provided in the order put forth in
[this list][android-resources-qualifiers-order].

[android-providing-alternative-resources]: https://developer.android.com/guide/topics/resources/providing-resources.html#AlternativeResources
[android-resources-qualifiers-order]: https://developer.android.com/guide/topics/resources/providing-resources.html#table2
[config-documentation]: javadoc/latest/org/robolectric/annotation/Config.html
