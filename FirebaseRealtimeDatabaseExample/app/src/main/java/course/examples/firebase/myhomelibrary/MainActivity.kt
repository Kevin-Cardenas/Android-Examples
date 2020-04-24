package course.examples.firebase.myhomelibrary

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val AUTHOR_NAME = "course.examples.firebase.myhomelibrary.authorname"
        const val AUTHOR_ID = "course.examples.firebase.myhomelibrary.authorid"
    }

    private lateinit var editTextName: EditText
    private lateinit var spinnerCountry: Spinner
    private lateinit var buttonAddAuthor: Button
    private lateinit var listViewAuthors: ListView

    //a list to store all the artist from firebase database
    private lateinit var authors: MutableList<Author>

    //our database reference object
    private lateinit var databaseAuthors: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //getting the reference of artists node
        databaseAuthors = FirebaseDatabase.getInstance().getReference("authors")

        //getting views
        editTextName = findViewById(R.id.editTextName)
        spinnerCountry = findViewById(R.id.spinnerCountry)
        listViewAuthors = findViewById(R.id.listViewAuthors)
        buttonAddAuthor = findViewById(R.id.buttonAddAuthor)

        //list to store authors
        authors = ArrayList()

        //adding an onclicklistener to button
        buttonAddAuthor.setOnClickListener {
            //calling the method addArtist()
            //the method is defined below
            //this method is actually performing the write operation
            addAuthor()
        }

        //attaching listener to listview
        listViewAuthors.onItemClickListener = AdapterView.OnItemClickListener { _, _, item, _ ->
            //getting the selected artist
            val author = authors[item]

            //creating an intent
            val intent = Intent(applicationContext, AuthorActivity::class.java)

            //putting artist name and id to intent
            intent.putExtra(AUTHOR_ID, author.authorId)
            intent.putExtra(AUTHOR_NAME, author.authorName)

            //starting the activity with intent
            startActivity(intent)
        }

        listViewAuthors.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { _, _, item, _ ->
                val author = authors[item]
                showUpdateDeleteDialog(author.authorId, author.authorName)
                true
            }
    }

    @SuppressLint("InflateParams")
    private fun showUpdateDeleteDialog(authorId: String, authorName: String) {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.update_dialog, null)
        dialogBuilder.setView(dialogView)

        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
        val spinnerCountry = dialogView.findViewById<Spinner>(R.id.spinnerCountry)
        val buttonUpdate = dialogView.findViewById<Button>(R.id.buttonUpdateAuthor)
        val buttonDelete = dialogView.findViewById<Button>(R.id.buttonDeleteAuthor)

        dialogBuilder.setTitle(authorName)
        val b = dialogBuilder.create()
        b.show()

        buttonUpdate.setOnClickListener {
            val name = editTextName.text.toString().trim { it <= ' ' }
            val country = spinnerCountry.selectedItem.toString()
            if (!TextUtils.isEmpty(name)) {
                updateAuthor(authorId, name, country)
                b.dismiss()
            }
        }


        buttonDelete.setOnClickListener {
            deleteAuthor(authorId)
            b.dismiss()
        }
    }


    private fun updateAuthor(id: String, name: String, country: String): Boolean {
        //getting the specified author reference
        val dR = FirebaseDatabase.getInstance().getReference("authors").child(id)

        //updating author
        val author = Author(id, name, country)
        dR.setValue(author)
        Toast.makeText(applicationContext, "Author Updated", Toast.LENGTH_LONG).show()
        return true
    }

    private fun deleteAuthor(id: String): Boolean {
        //getting the specified author reference
        val dR = FirebaseDatabase.getInstance().getReference("authors").child(id)

        //removing author
        dR.removeValue()

        //getting the titles reference for the specified author
        val drTitles = FirebaseDatabase.getInstance().getReference("titles").child(id)

        //removing all titles
        drTitles.removeValue()
        Toast.makeText(applicationContext, "Author Deleted", Toast.LENGTH_LONG).show()

        return true
    }

    override fun onStart() {
        super.onStart()
        //attaching value event listener
        databaseAuthors.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                //clearing the previous artist list
                authors.clear()

                //iterating through all the nodes
                for (postSnapshot in dataSnapshot.children) {
                    //getting artist
                    val author = postSnapshot.getValue<Author>(Author::class.java)
                    //adding author to the list
                    authors.add(author!!)
                }

                //creating adapter
                val authorAdapter = AuthorList(this@MainActivity, authors)
                //attaching adapter to the listview
                listViewAuthors.adapter = authorAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }


    /*
     * This method is saving a new author to the
     * Firebase Realtime Database
     * */
    private fun addAuthor() {
        //getting the values to save
        val name = editTextName.text.toString().trim { it <= ' ' }
        val country = spinnerCountry.selectedItem.toString()

        //checking if the value is provided
        if (!TextUtils.isEmpty(name)) {

            //getting a unique id using push().getKey() method
            //it will create a unique id and we will use it as the Primary Key for our Author
            val id = databaseAuthors.push().key

            //creating an Author Object
            val author = Author(id!!, name, country)

            //Saving the Author
            databaseAuthors.child(id).setValue(author)

            //setting edittext to blank again
            editTextName.setText("")

            //displaying a success toast
            Toast.makeText(this, "Author added", Toast.LENGTH_LONG).show()
        } else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show()
        }
    }
}

