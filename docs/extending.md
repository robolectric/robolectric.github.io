# Shadows

Robolectric works by creating a runtime environment that includes the real Android framework code.
This means when your tests or code under test calls into the Android framework, you get a more
realistic experience as for the most part the same code is executed as would be on a real device.
However, there are limitations:

1. Android native code cannot execute on your development machine.
2. There are no Android system services running on your development machine.
3. Android includes next to no APIs suitable for testing.

Robolectric fills these gaps with a set of classes known as Shadows. Each shadow can modify or
extend the behavior of a corresponding class in the Android OS. When an Android class is
instantiated, Robolectric looks for a corresponding shadow class, and if it finds one, it creates a
shadow object to associate with it.

Using byte code instrumentation, Robolectric is able to weave in cross-platform fake implementations
to substitute for native code and add additional APIs to make testing possible.

## What's in a Name?

Why "Shadow"? Shadow objects are not quite [Proxies][proxy-pattern], not quite [Fakes][fake-object],
not quite [Mocks or Stubs][mocks-arent-stubs]. Shadows are sometimes hidden, sometimes seen, and can
lead you to the real object. At least we didn't call them "sheep", which we were considering.

## Shadow Classes

Shadow classes always need a public no-arg constructor so that the Robolectric framework can
instantiate them. They are associated to the class that they Shadow with an
[`@Implements`][implements-documentation] annotation on the class declaration.

Shadow classes should mimic the production classes' inheritance hierarchy. For example, if you are
implementing a Shadow for [`ViewGroup`][view-group-documentation], `ShadowViewGroup`, then your
Shadow class should extend `ViewGroup`'s superclass' Shadow, `ShadowView`.

/// tab | Java

```java
@Implements(ViewGroup.class)
public class ShadowViewGroup extends ShadowView {
}
```

///

/// tab | Kotlin

```kotlin
@Implements(ViewGroup::class)
class ShadowViewGroup : ShadowView
```

///

## Methods

Shadow objects implement methods that have the same signature as the corresponding Android class.
Robolectric will invoke the method on a Shadow object when a method with the same signature on the
Android object is invoked.

Suppose an application defined the following line of code:

/// tab | Java

```java
imageView.setImageResource(R.drawable.robolectric_logo);
```

///

/// tab | Kotlin

```kotlin
imageView.setImageResource(R.drawable.robolectric_logo)
```

///

Under test, the `ShadowImageView#setImageResource(int resId)` method on the Shadow instance would be
invoked.

Shadow methods must be marked with the [`@Implementation`][implementation-documentation] annotation.
Robolectric includes a lint test to help ensure this is done correctly.

/// tab | Java

```java
@Implements(ImageView.class)
public class ShadowImageView extends ShadowView {
  @Implementation
  protected void setImageResource(int resId) {
    // Implementation goes here
  }
}
```

///

/// tab | Kotlin

```kotlin
@Implements(ImageView::class)
class ShadowImageView : ShadowView {
  @Implementation
  protected fun setImageResource(resId: Int) {
    // Implementation goes here
  }
}
```

///

Robolectric supports shadowing all methods on the original class, including `private`, `static`,
`final` or `native`.

Typically `@Implementation` methods should have the `protected` modifier. The intention is to reduce
the API surface area of the Shadows; the test author should always call such methods on the Android
framework class directly.

It is important that shadow methods are implemented on the corresponding shadow of the class in
which they were originally defined. Otherwise, Robolectric's lookup mechanism will not find them
(even if they have been declared on a shadow subclass). For example, the method
[`setEnabled()`][view-set-enabled] is defined on [`View`][view-documentation]. If a `setEnabled()`
method is defined on `ShadowViewGroup` instead of `ShadowView` then it will not be found at run time
even when `setEnabled()` is called on an instance of `ViewGroup`.

## Shadowing Constructors

Once a Shadow object is instantiated, Robolectric will look for a method named  `__constructor__`
and annotated with `@Implementation` which has the same arguments as the constructor that was
invoked on the real object.

For instance, if the application code was to invoke the [`TextView`][text-view-documentation]
constructor which receives a [`Context`][context-documentation]:

/// tab | Java

```java
new TextView(context);
```

///

/// tab | Kotlin

```kotlin
TextView(context)
```

///

Robolectric would invoke the following  `__constructor__` method that receives a `Context`:

/// tab | Java

```java
@Implements(TextView.class)
public class ShadowTextView {
  @Implementation
  protected void __constructor__(Context context) {
    this.context = context;
  }
}
```

///

/// tab | Kotlin

```kotlin
@Implements(TextView::class)
class ShadowTextView {
  @Implementation
  protected fun __constructor__(context: Context) {
    this.context = context
  }
```

///

## Getting access to the real instance

Sometimes Shadow classes may want to refer to the object they are shadowing, e.g., to manipulate
fields. A Shadow class can achieve this by declaring a field annotated with
[`@RealObject`][real-object-documentation]:

/// tab | Java

```java
@Implements(Point.class)
public class ShadowPoint {
  @RealObject private Point realPoint;

  public void __constructor__(int x, int y) {
    realPoint.x = x;
    realPoint.y = y;
  }
}
```

///

/// tab | Kotlin

```kotlin
@Implements(Point::class)
class ShadowPoint {
  @RealObject private lateinit var realPoint: Point

  fun __constructor__(x: Int, y: Int) {
    realPoint.x = x
    realPoint.y = y
  }
}
```

///

Robolectric will set `realPoint` to the actual instance of [`Point`][point-documentation] before
invoking any other methods.

It is important to note that methods called on the real object will still be intercepted and
redirected by Robolectric. This does not often matter in test code, but it has important
implications for Shadow class implementors. Since the Shadow class inheritance hierarchy does not
always mirror that of their associated Android classes, it is sometimes necessary to make calls
through these real objects so that the Robolectric runtime will have the opportunity to route them
to the correct Shadow class based on the actual class of the object. Otherwise, methods on Shadows
of base classes would be unable to access methods on the Shadows of their subclasses.

Methods on your shadow class are able to call through to the Android OS code, using
[`Shadow.directlyOn()`][shadow-directly-on].

## Custom Shadows

Robolectric is a work in progress and we rely, welcome and strongly
encourage [contributions](contributing.md) from the community for bug fixes and feature gaps.
However, if you wish to modify shadow behavior in a way that is not appropriate for sharing, or you
can't wait for a new release to include a critical fix we do support custom shadows.

### Writing a Custom Shadow

Custom shadows are structured much the same as normal shadow classes. They must include the
`@Implements` annotation on the class definition. You can use the normal shadow implementation
options, such as shadowing instance methods using `@Implementation` or shadowing constructors using
`__constructor__()` methods. Your shadow class may also extend one of the stock Robolectric shadows
if you like.

/// tab | Java

```java
@Implements(Bitmap.class)
public class MyShadowBitmap {
  @RealObject private Bitmap realBitmap;
  private int bitmapQuality = -1;

  @Implementation
  public boolean compress(Bitmap.CompressFormat format, int quality, OutputStream stream) {
    bitmapQuality = quality;
    return realBitmap.compress(format, quality, stream);
  }

  public int getQuality() {
    return bitmapQuality;
  }
}
```

///

/// tab | Kotlin

```kotlin
@Implements(Bitmap::class)
class MyShadowBitmap {
  @RealObject private lateinit var realBitmap: Bitmap
  private var bitmapQuality: Int = -1

  @Implementation
  fun compress(format: Bitmap.CompressFormat, quality: Int, stream: OutputStream): Boolean {
    bitmapQuality = quality
    return realBitmap.compress(format, quality, stream)
  }

  fun getQuality(): Int {
    return bitmapQuality
  }
}
```

///

### Using a Custom Shadows

Custom Shadows get hooked up to Robolectric using the [`@Config`][config-documentation] annotation
on the test class or test method, using the [`shadows`][config-shadows] array attribute. To use the
`MyShadowBitmap` class mentioned in the previous section, you would annotate the test in question
with `@Config(shadows = { MyShadowBitmap.class })`. This causes Robolectric to recognize and use
your custom shadow when executing code against the class you shadowed.

If you would like your custom shadows to be applied to all tests in your suite or a certain package,
you can configure shadows through the
[`robolectric.properties`](configuring.md#robolectricproperties-file) file.

Note, by default `Shadows.shadowOf()` method will not work with custom shadows. You can instead
use [`Shadow.extract()`][shadow-extract] and cast the return value to the custom Shadow class you
implemented.

### Building a library of Custom Shadows

If you find yourself building a library of custom shadows, you should consider running Robolectric's
shadow annotation processor on your library of shadows. This provides a number of benefits such as:

1. Generating `shadowOf` methods for each of your shadows.
2. Generating a `ServiceLoader` so your custom shadows are automatically applied if found on the
   classpath.
3. Invoking any `static` [`@Resetter`][resetter-documentation] methods on teardown to enable you to
   reset static state.
4. Perform additional validation and checking on your shadows.

/// tab | Groovy

```groovy
android {
  defaultConfig {
    javaCompileOptions {
      annotationProcessorOptions {
        className 'org.robolectric.annotation.processing.RobolectricProcessor'
        arguments = ['org.robolectric.annotation.processing.shadowPackage': 'com.example.myshadowpackage']
      }
    }
  }
}

dependencies {
  annotationProcessor 'org.robolectric:processor:{{ robolectric.version.current }}'
}
```

///

/// tab | Kotlin
When you write your shadows in Kotlin, configure [`kapt`][kapt-documentation]:

```kotlin
plugins {
  id("kotlin-kapt")
}

kapt {
  arguments {
    arg("org.robolectric.annotation.processing.shadowPackage", "com.example.myshadowpackage")
  }
}

dependencies {
  kapt("org.robolectric:processor:{{ robolectric.version.current }}")
}
```

///

## Best practices

### Limit API surface area of shadows

Since [Robolectric 3.7][robolectric-3.7-release] `@Implementation` methods including
`__constructor__` methods can be made `protected`. This is desirable as test code has no business
calling these methods, by making your `@Implementation` methods protected you encourage test writers
to call the public Android APIs instead.

### Don't override `equals`, `hashCode` and `toString` in shadows

Avoid this unless you are mimicking behaviour in the class being shadowed. To test equality for
comparisons in tests prefer helpers or assertion library extensions. Prefer adding a `describe()`
method instead of shadowing `toString()`.

### Write high quality shadows that promote testing behavior rather than implementation

Rather than using shadows as glorified argument captors, prefer writing a shadow that encourages
testing behaviour. For example, don't add a method that exposes registered listeners, rather add an
`@Implementation` for a method that would invoke those listeners.

### Use caution when shadowing your own code

Robolectric provides a lot of power which requires responsible usage. Shadows are ideal for testing
interaction with the Android framework as the framework doesn't support dependency injection and
makes liberal use of static code. Before writing custom shadows for your own code, consider if you
can't better refactor your code and use a popular mocking library such as [Mockito][mockito].

### Support the community

Please [contribute](contributing.md) your enhancements to Robolectric. This will help the community
and reduce the bloat in your own codebase.

[config-documentation]: javadoc/latest/org/robolectric/annotation/Config.html
[config-shadows]: javadoc/latest/org/robolectric/annotation/Config.html#shadows()
[context-documentation]: https://developer.android.com/reference/android/content/Context
[fake-object]: https://c2.com/cgi/wiki?FakeObject "Fake Object"
[implementation-documentation]: javadoc/latest/org/robolectric/annotation/Implementation.html
[implements-documentation]: javadoc/latest/org/robolectric/annotation/Implements.html
<!-- markdownlint-disable-next-line MD053 -->
[kapt-documentation]: https://kotlinlang.org/docs/kapt.html
[mockito]: https://site.mockito.org/
[mocks-arent-stubs]: https://martinfowler.com/articles/mocksArentStubs.html#TheDifferenceBetweenMocksAndStubs "Mocks Aren't Stubs"
[point-documentation]: https://developer.android.com/reference/android/graphics/Point
[proxy-pattern]: https://en.wikipedia.org/wiki/Proxy_pattern "Proxy pattern - Wikipedia, the free encyclopedia"
[real-object-documentation]: javadoc/latest/org/robolectric/annotation/RealObject.html
[resetter-documentation]: javadoc/latest/org/robolectric/annotation/Resetter.html
[robolectric-3.7-release]: https://github.com/robolectric/robolectric/releases/tag/robolectric-3.7
[shadow-directly-on]: javadoc/latest/org/robolectric/shadow/api/Shadow.html#directlyOn(java.lang.Class,java.lang.String,org.robolectric.util.ReflectionHelpers.ClassParameter...)
[shadow-extract]: javadoc/latest/org/robolectric/shadow/api/Shadow.html#extract(java.lang.Object)
[text-view-documentation]: https://developer.android.com/reference/android/widget/TextView
[view-documentation]: https://developer.android.com/reference/android/view/View
[view-group-documentation]: https://developer.android.com/reference/android/view/ViewGroup
[view-set-enabled]: https://developer.android.com/reference/android/view/View#setEnabled(boolean)
