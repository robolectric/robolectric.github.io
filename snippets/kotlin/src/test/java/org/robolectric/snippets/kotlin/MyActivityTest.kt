package org.robolectric.snippets.kotlin

import android.widget.Button
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlin.jvm.java
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric

// --8<-- [start:index_sample_test]
@RunWith(AndroidJUnit4::class)
class MyActivityTest {
  @Test
  fun clickingButton_shouldChangeMessage() {
    Robolectric.buildActivity(MyActivity::class.java).use { controller ->
      controller.setup() // Moves the Activity to the RESUMED state
      val activity: MyActivity = controller.get()

      activity.findViewById<Button>(R.id.button).performClick()
      assertEquals(activity.findViewById<TextView>(R.id.text).text, "Robolectric Rocks!")
    }
  }
}
// --8<-- [end:index_sample_test]
