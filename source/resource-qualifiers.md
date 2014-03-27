---
layout: default
title: Using qualified resources
---

# What are qualified resources

As described [in the android developer docs](http://developer.android.com/guide/topics/resources/providing-resources.html#AlternativeResources), resource qualifiers allow you to change how your resources are loaded based on such factors as the language on the device, to the screen size, to whether it is day or night.  While these changes are often tedious to test rigorously (every string has a translation for all supported languages), you may find yourself wishing to run tests in different resource qualified contexts.

# Specifying resources in test

Specifying a resource qualifier is quite simple: simply add the desired qualifiers to the @Config annotation on your test case or test class, depending on whether you would like to change the resource qualifiers for the whole file, or simply one test.  

```java
@Test
@Config(qualifiers="en-port")
public void thisUsesEnglishAndPortraitResources() {
  //here resources under *-en, *-port, and *-en-port can all be accessed.
}
```

Multiple qualifiers should be separated by dashes and provided in the order put forth in [this list](http://developer.android.com/guide/topics/resources/providing-resources.html#table2).
