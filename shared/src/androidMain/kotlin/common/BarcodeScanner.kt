package common

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.TextView




class BarcodeScanner : Activity(){
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        // Create a new TextView programmatically
        // Create a new TextView programmatically
        val textView = TextView(this)

        // Set the text to "Hello World"

        // Set the text to "Hello World"
        textView.text = "Hello World"

        // Set the TextView as the content view of the activity

        // Set the TextView as the content view of the activity
        setContentView(textView)
    }
}