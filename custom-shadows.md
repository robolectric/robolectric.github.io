---
title: Creating Custom Shadows
group: Customizing
order: 2
---

# Creating Custom Shadows

Custom shadows are a Robolectric feature that allows you to make targeted changes in the way Android functions under
test.  This could be anything from capturing simply that a method was called, to inserting code that interacts with
test objects, to doing nothing at all.

Custom shadows allow you to include shadow functionality in only some of your test code, as opposed to adding or
modifying a Shadow in Robolectric source.  They also allow your shadow to refer to domain specific context, like
domain objects in your test classes.

## Writing a Custom Shadow

Custom shadows are structured much the same as normal shadow classes.  They must include the `@Implements(AndroidClassName.class)`
annotation on the class definition.  You can use the normal shadow implementation options, such as shadowing instance
methods using `@Implementation` or shadowing constructors using `public void __constructor__(...)`.

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
}
```

## Using a Custom Shadow

Custom Shadows get hooked up to Robolectric using the @Config annotation on the test class or test method, using
the `shadows` array attribute.  To use the MyShadowBitmap class mentioned in the previous section, you would annotate
the test in question with `@Config(shadows={MyShadowBitmap.class})`, and to include multiple custom shadows:
`@Config(shadows={MyShadowBitmap.class, MyOtherCustomShadow.class})`.  This causes Robolectric to recognize and use
your custom shadow when executing code against the class you shadowed.

However, the `Robolectric.shadowOf()` method will not work with custom shadows, as it has to be implemented in
Robolectric for each shadow class.  You can instead use `Robolectric.shadowOf_()` and cast the return value to the
custom Shadow class you implemented.

Also, if you choose to shadow an Android class that already is shadowed in Robolectric, you will replace the
Robolectric shadow.  You could try inheriting from the Robolectric shadow if you still need the base shadow
functionality.
