package course.examples.firebase.myhomelibrary

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class AuthorList(private val context: Activity, private var authors: List<Author>) :
    ArrayAdapter<Author>(context, R.layout.layout_author_list, authors) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem =
            inflater.inflate(R.layout.layout_author_list, parent, false)

        val textViewName = listViewItem.findViewById<TextView>(R.id.textViewName)
        val textViewCountry = listViewItem.findViewById<TextView>(R.id.textViewCountry)

        val author = authors[position]
        textViewName.text = author.authorName
        textViewCountry.text = author.authorCountry

        return listViewItem
    }
}