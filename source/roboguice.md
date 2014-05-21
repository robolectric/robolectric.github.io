---
title: Integrating with RoboGuice
group: Customizing
order: 3
---

# Integrating with RoboGuice

The [RobolectricSample application](https://github.com/robolectric/RobolectricSample) includes an
example of how to test Android applications that are built using the [RoboGuice](http://code.google.com/p/roboguice/)
dependency injection framework. This article explains what we did and how you can add RoboGuice to your own projects.

## The Sample Activity

For the most part RoboGuice will just work when injected instances of classes are exercised by unit tests, but with a
little bit of work can inject those instances directly into our tests. As an example, we can start with this simple
activity:

```java
public class InjectedActivity extends GuiceActivity {
    @InjectResource(R.string.injected_activity_caption) String caption;
    @InjectView(R.id.injected_text_view) TextView injectedTextView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.injected);
        injectedTextView.setText(caption);
    }
}
```

To get this to work you need to change your manifest to point to `roboguice.application.GuiceApplication`
(or a subclass) and link with the RoboGuice library. We wrote the following test:

```java
@Test
public void shouldAssignStringToTextView() throws Exception {
    injectedActivity.onCreate(null);
    TextView injectedTextView =
        (TextView) injectedActivity.findViewById(R.id.injected_text_view);
    assertEquals(injectedTextView.getText(), caption);
}
```

This test passes but the set up for it is ugly:

```java
InjectedActivity injectedActivity;
String caption;
@Before
public void setUp() {
    injectedActivity = new InjectedActivity();
    Resources resources = injectedActivity.getResources();
    caption = resources.getString(R.string.injected_activity_caption);
}
```

## Injecting Tests

With some changes to the test runner we can make it look like this:

```java
    @Inject InjectedActivity injectedActivity;
    @InjectResource(R.string.injected_activity_caption) String caption;
```

In `RobolectricSample` we created a subclass of `RobolectricTestRunner` called `InjectedTestRunner` and extended it
to ensure that instances of the test class were injected before the tests were run. `RobolectricTestRunner` has a
method called `prepareTest(Test test)` that exists for the purpose of giving its subclasses access to the `Test`
object just before each test. It can be used for injection as follows:

```java
public class InjectedTestRunner extends RobolectricTestRunner {
    public InjectedTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override public void prepareTest(Object test) {
        GuiceApplication sampleApplication = (GuiceApplication) Robolectric.application;
        Injector injector = sampleApplication.getInjector();
        injector.injectMembers(test);
    }
}
```

It can be used in a `@RunWith` clause instead of `RobolectricTestRunner`:

```java
@RunWith(InjectedTestRunner.class)
public class InjectedActivityTest {
...
}
```

## Injecting Context Objects onto Tests

There are some types, such as `Context`s that RoboGuice can't inject without entering the context scope in the test
runner's `prepareTest()` method:

```java
@Override public void prepareTest(Object test) {
    GuiceApplication sampleApplication = (GuiceApplication) Robolectric.application;
    Injector injector = sampleApplication.getInjector();
    ContextScope scope = injector.getInstance(ContextScope.class);
    scope.enter(sampleApplication);
    injector.injectMembers(test);
}
```

Now it is possible to inject a `Context` object onto the test:

```java
@Inject Context context;
@Test
public void shouldBeAbleToInjectAContext() throws Exception {
    assertNotNull(context);
}
```

## Test-only Bindings

During test it can be useful to use fake objects that produce well-known values in order to simplify assertions. Below
is an example of a fake `Provider&lt;Date&gt;`.

```java
/* bound as singleton so tests will get the same provider as the production code */
@Singleton
public class FakeDateProvider implements Provider<Date> {
    private Date date = new Date();

    @Override public Date get() {
        return date;
    }

    public void setDate(String dateString) {
        ...
    }
}
```

## Defining and Inserting a Test Module

A test module contains the bindings for our fake date provider.

```java
public class RobolectricSampleTestModule extends AbstractAndroidModule {
    @Override protected void configure() {
        bind(Counter.class).in(Scopes.SINGLETON);
        bind(Date.class).toProvider(FakeDateProvider.class);
    }
}
```

Create a new subclass of `GuiceApplication` that contributes your project's modules to the standard
Android modules that will be injected:

```java
public class SampleGuiceApplication extends GuiceApplication {
    private Module module = new RobolectricSampleModule();

    @Override protected void addApplicationModules(List<Module> modules) {
        modules.add(module);
    }
}
```

Add a new setter method so that a test module can be used instead:

```java
public class SampleGuiceApplication extends GuiceApplication {
    ...
    public void setModule(Module module) {
        this.module = module;
    }
}
```

Override the production module with a test module in the test runner, like this:

```java
public class InjectedTestRunner extends RobolectricTestRunner {
   ...
   @Override protected Application createApplication() {
       SampleGuiceApplication application =
           (SampleGuiceApplication) super.createApplication();
       application.setModule(new RobolectricSampleTestModule());

       return application;
   }
   ...
}
```

## Using the New Bindings

To take advantage of the bindings created for test, the production code needs to inject a `Date` instead of
instantiating one itself.

```java
public class InjectedActivity extends GuiceActivity {
    ...
    DateFormat dateFormat = DateFormat.getInstance();

    @Inject Date date;

    @Override protected void onCreate(Bundle savedInstanceState) {
        ...
        injectedTextView.setText(caption + " - " + dateFormat.format(date));
    }
}
```

The FakeDateProvider can then be injected into the test and used to set injected dates to a well-known value
that can be asserted:

```java
public class InjectedActivityTest {
    @Inject FakeDateProvider fakeDateProvider;

    @Before
    public void setUp() {
        fakeDateProvider.setDate("Dec 8, 2010");
    }

    @Test
    public void shouldAssignStringToTextView() throws Exception {
        injectedActivity.onCreate(null);
        TextView injectedTextView = (TextView) injectedActivity.findViewById(R.id.injected_text_view);

        assertThat(injectedTextView.getText().toString(),
                equalTo("Roboguice Activity tested with Robolectric - Dec 8, 2010"));
    }
}
```

