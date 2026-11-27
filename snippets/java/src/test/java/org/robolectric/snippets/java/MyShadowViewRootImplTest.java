package org.robolectric.snippets.java;

import static org.junit.Assert.assertEquals;
import static org.robolectric.util.reflector.Reflector.reflector;

import android.graphics.Insets;
import android.os.Build;
import android.view.WindowInsets;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.shadows.ShadowViewRootImpl;
import org.robolectric.util.reflector.Direct;
import org.robolectric.util.reflector.ForType;

@RunWith(AndroidJUnit4.class)
public class MyShadowViewRootImplTest {

  // --8<-- [start:hidden_shadow_class]
  @Implements(className = "android.view.ViewRootImpl", isInAndroidSdk = false)
  public static class MyShadowViewRootImpl extends ShadowViewRootImpl {
    @RealObject private Object realViewRootImpl;

    @Implementation
    protected WindowInsets getWindowInsets(boolean forceConstruct) {
      WindowInsets insets =
          reflector(ViewRootImplReflector.class, realViewRootImpl).getWindowInsets(forceConstruct);
      return new WindowInsets.Builder(insets)
          .setInsets(WindowInsets.Type.statusBars(), Insets.of(0, 48, 0, 0))
          .build();
    }

    @ForType(className = "android.view.ViewRootImpl")
    interface ViewRootImplReflector {
      @Direct
      WindowInsets getWindowInsets(boolean forceConstruct);
    }
  }

  // --8<-- [end:hidden_shadow_class]

  @Test
  @Config(shadows = MyShadowViewRootImpl.class, sdk = Build.VERSION_CODES.R)
  public void customHiddenShadow_overridesWindowInsets() {
    try (ActivityController<MyActivity> controller = Robolectric.buildActivity(MyActivity.class)) {
      controller.setup();
      WindowInsets insets = controller.get().getWindow().getDecorView().getRootWindowInsets();
      assertEquals(Insets.of(0, 48, 0, 0), insets.getInsets(WindowInsets.Type.statusBars()));
    }
  }
}
