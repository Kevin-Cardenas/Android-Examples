package course.examples.networking.xml

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(v: View) {
        startActivity(
            Intent(
                this@MainActivity,
                NetworkingXMLResponseActivity::class.java
            )
        )
    }
}
