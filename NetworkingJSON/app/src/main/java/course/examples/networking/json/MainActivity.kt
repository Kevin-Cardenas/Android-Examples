package course.examples.networking.json

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(v: View) {
        startActivity(
            Intent(
                this@MainActivity,
                NetworkingJSONResponseActivity::class.java
            )
        )
    }
}