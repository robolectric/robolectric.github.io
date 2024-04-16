---
hide:
- footer
- toc
---

# Writing Your First Test

Let's say you have an activity layout that represents a welcome screen:

```xml
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

We want to write a test that asserts that when a user clicks on a button, the app launches the LoginActivity.

```java
public class WelcomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        final View button = findViewById(R.id.login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            }
        });
    }
}
```

In order to test this, we can check that when a user clicks on the "Login" button, we start the correct intent. Because Robolectric is a unit testing framework, the LoginActivity will not actually be started, but we can check that the WelcomeActivity fired the correct intent:

```java
@RunWith(RobolectricTestRunner.class)
public class WelcomeActivityTest {
    @Test
    public void clickingLogin_shouldStartLoginActivity() {
        try (ActivityController<WelcomeActivity> controller = Robolectric.buildActivity(WelcomeActivity.class)) {
            controller.setup(); // Moves Activity to RESUMED state
            WelcomeActivity activity = controller.get();

            activity.findViewById(R.id.login).performClick();
            Intent expectedIntent = new Intent(activity, LoginActivity.class);
            Intent actual = shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
            assertEquals(expectedIntent.getComponent(), actual.getComponent());
        }
    }
}
```

## Test APIs

Robolectric extends the Android framework with a large set of test APIs, which offer extra configurability and access to the internal state and history of Android components that's useful for tests.

Many test APIs are extensions to individual Android classes, and can be accessed using the `shadowOf()` method:

```java
// retrieves all the toasts that have been displayed...
List<Toast> toasts = shadowOf(application).getShownToasts();
```

Additional test APIs are accessible as static methods on special classes called shadows, which correspond to Android framework classes:

```java
// simulates a new display being plugged into the device...
ShadowDisplay.addDisplay("xlarge-port");
```
