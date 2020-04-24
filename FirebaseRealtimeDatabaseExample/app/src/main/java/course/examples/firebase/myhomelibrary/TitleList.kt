package course.examples.firebase.myhomelibrary

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class TitleList(private val context: Activity, private var titles: List<Title>) :
    ArrayAdapter<Title>(context, R.layout.layout_author_list, titles) {


    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem =
            inflater.inflate(R.layout.layout_author_list, parent, false)

        val textViewName = listViewItem.findViewById<TextView>(R.id.textViewName)
        val textViewRating = listViewItem.findViewById<TextView>(R.id.textViewCountry)

        val title = titles[position]
        textViewName.text = title.titleName
        textViewRating.text = title.rating.toString()

        return listViewItem
    }
}
