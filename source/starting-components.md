---
layout: default
title: Starting Other Components
---

# Starting Other Components

In Android, it is very common to start services and activities because of user interaction.  While Robolectric
will not start those components for you, it is possible to write tests that verify that the components have
been started.

Let's say you have an activity layout like this that represents a welcome screen:

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

When the user clicks on the button, we want to navigate to the LoginActivity.

```java
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        View button = findViewById(R.id.login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            }
        });
    }
}
```

In order to test this, we can check that when a user clicks on the "Login" button, we start the correct intent.
Because Robolectric is a unit testing framework, the LoginActivity will not actually be started, but we can
check that the WelcomeActivity fired the correct intent:

```java
@RunWith(RobolectricTestRunner.class)
public class WelcomeActivityTest {

    private final ActivityController<WelcomeActivity> controller = buildActivity(WelcomeActivity.class);

    @Test
    public void clickingLogin_shouldStartLoginActivity() {
        WelcomeActivity activity = controller.create().start().resume().get();
        activity.findViewById(R.id.login).performClick();

        Intent expectedIntent = new Intent(activity, WelcomeActivity.class);
        assertThat(shadowOf(activity).getNextStartedActivity()).isEqualTo(expectedIntent);
    }
}
```

This philosophy extends to all situations in which an Intent is fired. Checking that a Service was started is
similar to checking an Activity, except you call `getNextStartedService()` on the ShadowActivity.