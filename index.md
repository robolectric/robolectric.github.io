---
# You don't need to edit this file, it's empty on purpose.
# Edit theme's home layout instead if you wanna make some changes
# See: https://jekyllrb.com/docs/themes/#overriding-theme-defaults
# layout: home
front: true
---

Running tests on an Android emulator or device is slow! Building, deploying, and launching the app often takes a minute or more. That's no way to do TDD. There must be a better way.

Wouldn't it be nice to run your Android tests directly from inside your IDE? Perhaps you've tried, and been thwarted by the dreaded `java.lang.RuntimeException: Stub!`?

<i id="front-android" class="fa fa-android" aria-hidden="true"></i>
<img src="/images/logo-with-bubbles.png" class="flip-horizontal"/>

<!-- Section -->
<section>
    <header class="major">
        <h2>Erat lacinia</h2>
    </header>
    <div class="features">
        <article>
            <span class="icon fa-diamond"></span>
            <div class="content">
                <h3>Portitor ullamcorper</h3>
                <p>Aenean ornare velit lacus, ac varius enim lorem ullamcorper dolore. Proin aliquam facilisis ante interdum. Sed nulla amet lorem feugiat tempus aliquam.</p>
            </div>
        </article>
        <article>
            <span class="icon fa-paper-plane"></span>
            <div class="content">
                <h3>Sapien veroeros</h3>
                <p>Aenean ornare velit lacus, ac varius enim lorem ullamcorper dolore. Proin aliquam facilisis ante interdum. Sed nulla amet lorem feugiat tempus aliquam.</p>
            </div>
        </article>
        <article>
            <span class="icon fa-rocket"></span>
            <div class="content">
                <h3>Quam lorem ipsum</h3>
                <p>Aenean ornare velit lacus, ac varius enim lorem ullamcorper dolore. Proin aliquam facilisis ante interdum. Sed nulla amet lorem feugiat tempus aliquam.</p>
            </div>
        </article>
        <article>
            <span class="icon fa-signal"></span>
            <div class="content">
                <h3>Sed magna finibus</h3>
                <p>Aenean ornare velit lacus, ac varius enim lorem ullamcorper dolore. Proin aliquam facilisis ante interdum. Sed nulla amet lorem feugiat tempus aliquam.</p>
            </div>
        </article>
    </div>
</section>

[Robolectric](http://robolectric.org/) is a unit test framework that de-fangs the Android SDK jar so you can test-drive the development of your Android app.  Tests run inside the JVM on your workstation in seconds. With Robolectric you can write tests like this:

```java
@RunWith(RobolectricTestRunner.class)
public class MyActivityTest {

  @Test
  public void clickingButton_shouldChangeResultsViewText() throws Exception {
    MyActivity activity = Robolectric.setupActivity(MyActivity.class);

    Button button = (Button) activity.findViewById(R.id.button);
    TextView results = (TextView) activity.findViewById(R.id.results);

    button.performClick();
    assertThat(results.getText().toString()).isEqualTo("Robolectric Rocks!");
  }
}
```

Robolectric makes this possible by rewriting Android SDK classes as they're being loaded and making it possible for them to run on a regular JVM.

### SDK, Resources, & Native Method Emulation

Robolectric handles inflation of views, resource loading, and lots of other stuff that's implemented in native C code on Android devices. This allows tests to do most things you could do on a real device. It's easy to provide our own implementation for specific SDK methods too, so you could simulate error conditions or real-world sensor behavior, for example.

### Run Tests Outside of the Emulator

Robolectric lets you run your tests on your workstation, or on your Continuous Integration environment in a regular JVM,
without an emulator. Because of this, the dexing, packaging, and installing-on-the emulator steps aren't necessary,
reducing test cycles from minutes to seconds so you can iterate quickly and refactor your code with confidence.

### No Mocking Frameworks Required

An alternate approach to Robolectric is to use mock frameworks such as [Mockito](http://code.google.com/p/mockito/) or to mock out the Android SDK. While this is a valid approach, it
often yields tests that are essentially reverse implementations of the application code.

Robolectric allows a test style that is closer to black box testing, making the tests more effective for refactoring and allowing the tests to focus on the behavior of the application instead of the implementation of Android. You can still use a mocking framework along with Robolectric if you like.
