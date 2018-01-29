---
title: Shadows
group: Customizing
order: 1
---

# Shadows

Robolectric works by creating a runtime environment that includes the real Android framework code. This means when your tests or code under test calls into the Android framework you get a more realistic experience as for the most part the same code is executed as would be on a real device. There are limitations however:
1. Native code - Android native code cannot execute on your development machine.
2. Out of process calls - There are no Android system services running on your development machine.
3. Inadequate testing APIs - Android includes next to no APIs suitable for testing

Robolectric fills these gaps with a set of classes known as Shadows. Each shadow can modify or extend the behavior of a corresponding class in the Android OS. When an Android class is instantiated, Robolectric looks for a corresponding shadow class, and if it finds one it creates a shadow object to associate with it.

Using byte code instrumentation Robolectric is able to weave in cross platform fake implementations to substitute for native code and add additional APIs to make testing possible.

### What's in a Name?

Why "Shadow?" Shadow objects are not quite [Proxies](http://en.wikipedia.org/wiki/Proxy_pattern "Proxy pattern - Wikipedia, the free encyclopedia"), not quite [Fakes](http://c2.com/cgi/wiki?FakeObject "Fake Object"), not quite [Mocks or Stubs](http://martinfowler.com/articles/mocksArentStubs.html#TheDifferenceBetweenMocksAndStubs "Mocks Aren't Stubs"). Shadows are sometimes hidden, sometimes seen, and can lead you to the real object. At least we didn't call them "sheep", which we were considering.

### Adding Functionality

If the shadow classes provided with Robolectric don't do what you want, it's possible to change their behavior for a single test, a group of tests, or for your whole suite. Simply declare a class (let's say <code>ShadowFoo</code>) and annotate it <code>@Implements(Foo.class)</code>. Your shadow class may extend one of the stock Robolectric shadows if you like. To let Robolectric know about your shadow, annotate your test method or class with the <code>@Config(shadows=ShadowFoo.class)</code>, or create a file called <code>org.robolectric.Config.properties</code> containing the line <code>shadows=my.package.ShadowFoo</code>.

### Shadow Classes

Shadow classes always need a public no-arg constructor so that the Robolectric framework can instantiate them. They are associated to the class that they Shadow with an `@Implements` annotation on the class declaration. In general, they should be implemented as if from scratch, the facilities of the classes they Shadow have almost always been removed and their data members are difficult to access. The methods on a Shadow class usually either Shadow the methods on the original class or facilitate testing by setting up return values or providing access to internal state or logged method calls.

Shadow classes should mimic the production classes' inheritance hierarchy. For example, if you are implementing a Shadow for `ViewGroup`, `ShadowViewGroup`, then your Shadow class should extend `ViewGroup`'s superclass's Shadow, `ShadowView`.  

```java
  ...
  @Implements(ViewGroup.class)
  public class ShadowViewGroup extends ShadowView {
  ...
```

### Methods

Shadow objects implement methods that have the same signature as the Android class. Robolectric will invoke the method on a Shadow object when a method with the same signature on the Android object is invoked.

Suppose an application defined the following line of code:
```java
  ...
  this.imageView.setImageResource(R.drawable.pivotallabs_logo);
  ...
```

Under test the `ShadowImageView#setImageResource(int resId)` method on the Shadow instance would be invoked.

Shadow methods must be marked with the `@Implementation` annotation. Robolectric includes a lint test to help ensure this is done correctly.

```java
@Implements(ImageView.class)
public class ShadowImageView extends ShadowView {
  ...	
  @Implementation
  protected void setImageResource(int resId) {
    // implementation here.
  }
}
```

Robolectric supports shadowing all methods on the original class, including `private`, `static`, `final` or `native`.

Typically `@Implementation` methods should also have the protected modifier. The intention is to reduce the API surface area of the Shadows and the test author should prefer calling such methods on the Android framework class directly.

It is important Shadow methods are implemented on the corresponding Shadow of the class in which they were originally defined. Otherwise Robolectric's lookup mechanism will not find them (even if they have been declared on a Shadow subclass.) For example, the method `setEnabled()` is defined on View. If a `setEnabled()` method is defined on `ShadowViewGroup` instead of `ShadowView` then it will not be found at run time even when `setEnabled()` is called on an instance of `ViewGroup`. 

### Shadowing Constructors

Once a Shadow object is instantiated, Robolectric will look for a method named  `__constructor__` and annotated with `@Implementation` which has the same arguments as the constructor that was invoked on the real object.

For instance, if the application code were to invoke the TextView constructor which receives a Context:

```java
new TextView(context);
```

Robolectric would invoke the following  `__constructor__` method that receives a Context:

```java
@Implements(TextView.class)
public class ShadowTextView {
  ...
  @Implementation
  public void __constructor__(Context context) {
    this.context = context;
  }
  ...
```

### Getting access to the real instance

Sometimes Shadow classes may want to refer to the object they are shadowing, e.g. to manipulate fields. A Shadow class can accomplish this by declaring a field annotated `@RealObject`:

```java
@Implements(Point.class)
public class ShadowPoint {
  @RealObject private Point realPoint;
  ...
  public void __constructor__(int x, int y) {
    realPoint.x = x;
    realPoint.y = y;
  }
}
```

Robolectric will set realPoint to the actual instance of `Point` before invoking any other methods.

It is important to note that methods called on the real object will still be intercepted and redirected by Robolectric. This does not often matter in test code, but it has important implications for Shadow class implementors. Since the Shadow class inheritance hierarchy does not always mirror that of their associated Android classes, it is sometimes necessary to make calls through these real objects so that the Robolectric runtime will have the opportunity to route them to the correct Shadow class based on the actual class of the object. Otherwise methods on Shadows of base classes would be unable to access methods on the Shadows of their subclasses.

Methods on your shadow class are able to call through to the Android OS code, using <code>Shadow.directlyOn()</code>.


Robolectric is a work in progress, and we welcome contributions from the community. We encourage developers to [use the standard GitHub workflow](http://help.github.com/fork-a-repo/ "Help.GitHub - Fork A Repo") to fork, enhance, and submit pull requests to us.
