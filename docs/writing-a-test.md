---
hide:
- toc
---

# Writing Your First Test

Let's say that you have an [`Activity`][activity-documentation] that represents a welcome screen:

/// tab | Java

```java
public class WelcomeActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        final Button button = findViewById(R.id.login);
        button.setOnClickListener((view) -> {
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class))
        });
    }
}
```

///

/// tab | Kotlin

```kotlin
class WelcomeActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_activity)

        val button = findViewById<Button>(R.id.login)
        button.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
```

///

```xml title="welcome_activity.xml"
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <Button
        android:id="@+id/login"
        android:text="Login"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
</LinearLayout>
```

We want to write a test that asserts that when the user clicks on the "Login" button, the app
launches the `LoginActivity`.

To achieve this, we can check that the correct [`Intent`][intent-documentation] is started when the
click is performed. Since Robolectric is a unit testing framework, the `LoginActivity` will not
actually be started.

/// tab | Java

```java
@RunWith(RobolectricTestRunner.class)
public class WelcomeActivityTest {
    @Test
    public void clickingLogin_shouldStartLoginActivity() {
        try (ActivityController<WelcomeActivity> controller = Robolectric.buildActivity(WelcomeActivity.class)) {
            controller.setup(); // Moves the Activity to the RESUMED state

            WelcomeActivity activity = controller.get();
            activity.findViewById<Button>(R.id.login).performClick();

            Intent expectedIntent = new Intent(activity, LoginActivity.class);
            Intent actual = shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
            assertEquals(expectedIntent.getComponent(), actual.getComponent());
        }
    }
}
```

///

/// tab | Kotlin

```kotlin
@RunWith(RobolectricTestRunner::class)
class WelcomeActivityTest {
    @Test
    fun clickingLogin_shouldStartLoginActivity() {
        Robolectric.buildActivity(WelcomeActivity::class.java).use { controller ->
            controller.setup() // Moves the Activity to the RESUMED state

            val activity = controller.get()
            activity.findViewById<Button>(R.id.login).performClick()

            val expectedIntent = Intent(activity, LoginActivity::class.java)
            val actual = shadowOf(RuntimeEnvironment.application).nextStartedActivity
            assertEquals(expectedIntent.component, actual.component)
        }
    }
}
```

///

## Test APIs

Robolectric extends the Android framework with a large set of test APIs, which offer extra
configurability and access to the internal state and history of Android components that are useful
for tests.

Many test APIs are extensions to individual Android classes, and can be accessed using the
`shadowOf()` method:

/// tab | Java

```java
// Retrieve all the toasts that have been displayed
List<Toast> toasts = shadowOf(application).getShownToasts();
```

///

/// tab | Kotlin

```kotlin
// Retrieve all the toasts that have been displayed
val toasts = shadowOf(application).shownToasts
```

///

Additional test APIs are accessible as static methods on special classes called
[shadows](extending.md), which correspond to Android framework classes:

/// tab | Java

```java
// Simulate a new display being plugged into the device
ShadowDisplayManager.addDisplay("xlarge-port");
```

///

/// tab | Kotlin

```kotlin
// Simulate a new display being plugged into the device
ShadowDisplayManager.addDisplay("xlarge-port")
```

///

[activity-documentation]: https://developer.android.com/reference/android/app/Activity
[intent-documentation]: https://developer.android.com/reference/android/content/Intent
