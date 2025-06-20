package org.robolectric.snippets.java;

import static org.junit.Assert.assertEquals;

import android.widget.TextView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;

// --8<-- [start:index_sample_test]
@RunWith(AndroidJUnit4.class)
public class MyActivityTest {
  @Test
  public void clickingButton_shouldChangeMessage() {
    try (ActivityController<MyActivity> controller = Robolectric.buildActivity(MyActivity.class)) {
      controller.setup(); // Moves the Activity to the RESUMED state
      MyActivity activity = controller.get();

      activity.findViewById(R.id.button).performClick();
      assertEquals(((TextView) activity.findViewById(R.id.text)).getText(), "Robolectric Rocks!");
    }
  }
}
// --8<-- [end:index_sample_test]
