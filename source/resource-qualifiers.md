---
title: Using Qualified Resources
group: User Guide
order: 5
---

# Using Qualified Resources

As described [in the android developer docs](http://developer.android.com/guide/topics/resources/providing-resources.html#AlternativeResources), resource qualifiers allow you to change how your resources are loaded based on such factors as the language on the device, to the screen size, to whether it is day or night.  While these changes are often tedious to test rigorously (every string has a translation for all supported languages), you may find yourself wishing to run tests in different resource qualified contexts.

## Specifying resources in test

Specifying a resource qualifier is quite simple: simply add the desired qualifiers to the @Config annotation on your test case or test class, depending on whether you would like to change the resource qualifiers for the whole file, or simply one test.

Given the following resources,

*values/strings.xml*

```xml
<string name="not_overridden">Not Overridden</string>
<string name="overridden">Unqualified value</string>
<string name="overridden_twice">Unqualified value</string>
```

*values-en/strings.xml*

```xml
<string name="overridden">English qualified value</string>
<string name="overridden_twice">English qualified value</string>
```

*values-en-port/strings.xml*

```xml
<string name="overridden_twice">English portrait qualified value</string>
```

this Robolectric test would pass, using the Android resource qualifier resolution rules.


```java
@Test
@Config(qualifiers="en-port")
public void thisUsesEnglishAndPortraitResources() {
  assertTrue(Robolectric.application.getString(R.id.not_overridden).equals("Not Overridden"));
  assertTrue(Robolectric.application.getString(R.id.overridden).equals("English qualified value"));
  assertTrue(Robolectric.application.getString(R.id.overridden_twice).equals("English portrait qualified value"));
}
```

Multiple qualifiers should be separated by dashes and provided in the order put forth in [this list](http://developer.android.com/guide/topics/resources/providing-resources.html#table2).
