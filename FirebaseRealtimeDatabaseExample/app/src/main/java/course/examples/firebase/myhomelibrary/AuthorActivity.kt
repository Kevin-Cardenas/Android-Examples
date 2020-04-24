package course.examples.firebase.myhomelibrary

import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.util.*

class AuthorActivity : AppCompatActivity() {
    private lateinit var buttonAddTitle: Button
    private lateinit var editTextTitleName: EditText
    private lateinit var seekBarRating: SeekBar
    private lateinit var textViewRating: TextView
    private lateinit var textViewAuthor: TextView
    private lateinit var listViewTitles: ListView
    private lateinit var databaseTitles: DatabaseReference
    private lateinit var titles: MutableList<Title>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_author)

        val intent = intent

        /*
         * this line is important
         * this time we are not getting the reference of a direct node
         * but inside the node track we are creating a new child with the author id
         * and inside that node we will store all the titles with unique ids
         * */
        databaseTitles = FirebaseDatabase.getInstance().getReference("titles")
            .child(intent.getStringExtra(MainActivity.AUTHOR_ID)!!)

        buttonAddTitle = findViewById(R.id.buttonAddTitle)
        editTextTitleName = findViewById(R.id.editTextName)
        seekBarRating = findViewById(R.id.seekBarRating)
        textViewRating = findViewById(R.id.textViewRating)
        textViewAuthor = findViewById(R.id.textViewAuthor)
        listViewTitles = findViewById(R.id.listViewTitles)

        titles = ArrayList()

        textViewAuthor.text = intent.getStringExtra(MainActivity.AUTHOR_NAME)

        seekBarRating.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                textViewRating.text = i.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) { /* NA */ }
            override fun onStopTrackingTouch(seekBar: SeekBar) { /* NA */ }
        })
        buttonAddTitle.setOnClickListener { saveTitle() }
    }

    override fun onStart() {
        super.onStart()

        databaseTitles.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                titles.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val title = postSnapshot.getValue<Title>(Title::class.java)
                    titles.add(title!!)
                }
                val titleListAdapter = TitleList(this@AuthorActivity, titles)
                listViewTitles.adapter = titleListAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) { /* NA */ }
        })
    }

    private fun saveTitle() {
        val titleName = editTextTitleName.text.toString().trim { it <= ' ' }
        val rating = seekBarRating.progress
        if (!TextUtils.isEmpty(titleName)) {
            val id = (databaseTitles.push()).key.toString()
            val title = Title(id, titleName, rating)
            databaseTitles.child(id).setValue(title)
            Toast.makeText(this, "Title saved", Toast.LENGTH_LONG).show()
            editTextTitleName.setText("")
        } else {
            Toast.makeText(this, "Please enter title name", Toast.LENGTH_LONG).show()
        }
    }
}
