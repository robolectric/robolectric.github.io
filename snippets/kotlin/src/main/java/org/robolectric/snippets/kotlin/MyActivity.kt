package org.robolectric.snippets.kotlin

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MyActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_my)

    findViewById<Button>(R.id.button).setOnClickListener {
      findViewById<TextView>(R.id.text).text = "Robolectric Rocks!"
    }
  }
}
