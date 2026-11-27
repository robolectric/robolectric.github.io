package org.robolectric.snippets.kotlin

import android.graphics.Insets
import android.os.Build
import android.view.WindowInsets
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.annotation.RealObject
import org.robolectric.shadows.ShadowViewRootImpl
import org.robolectric.util.reflector.Direct
import org.robolectric.util.reflector.ForType
import org.robolectric.util.reflector.Reflector.reflector

@RunWith(AndroidJUnit4::class)
class MyShadowViewRootImplTest {

  // --8<-- [start:hidden_shadow_class]
  @Implements(className = "android.view.ViewRootImpl", isInAndroidSdk = false)
  class MyShadowViewRootImpl : ShadowViewRootImpl() {
    @RealObject private lateinit var realViewRootImpl: Any

    @Implementation
    protected fun getWindowInsets(forceConstruct: Boolean): WindowInsets {
      val insets =
        reflector(ViewRootImplReflector::class.java, realViewRootImpl)
          .getWindowInsets(forceConstruct)
      return WindowInsets.Builder(insets)
        .setInsets(WindowInsets.Type.statusBars(), Insets.of(0, 48, 0, 0))
        .build()
    }

    @ForType(className = "android.view.ViewRootImpl")
    internal interface ViewRootImplReflector {
      @Direct fun getWindowInsets(forceConstruct: Boolean): WindowInsets
    }
  }

  // --8<-- [end:hidden_shadow_class]

  @Test
  @Config(shadows = [MyShadowViewRootImpl::class], sdk = [Build.VERSION_CODES.R])
  fun customHiddenShadow_overridesWindowInsets() {
    Robolectric.buildActivity(MyActivity::class.java).use { controller ->
      controller.setup()
      val insets = controller.get().window.decorView.rootWindowInsets
      assertEquals(Insets.of(0, 48, 0, 0), insets.getInsets(WindowInsets.Type.statusBars()))
    }
  }
}
