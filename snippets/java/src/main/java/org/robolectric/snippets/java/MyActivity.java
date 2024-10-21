package org.robolectric.snippets.java;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MyActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_my);

    findViewById(R.id.button)
            .setOnClickListener((view) -> ((TextView) findViewById(R.id.text)).setText("Robolectric Rocks!"));
  }
}
