package it.mike5v.example

import android.graphics.Color
import android.os.Bundle
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.core.text.set
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.toSpannable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewMore
            .setAnimationDuration(400)
            .setEllipsizedText("See More")
            .setVisibleLines(3)
            .setIsExpanded(false)
            .setEllipsizedTextColor(ContextCompat.getColor(this, R.color.colorAccent))

//        val text = ("Quisquam beatae possimus sed porro quasi repudiandae magnam rerum et numquam libero quasi non fuga quae in corporis cupiditate tenetur qui occaecati dicta vel.")
//            .toSpannable()
//
//        // Set clickable span
//        // Another clickable span
//        text[9 until 15] = object: ClickableSpan(){
//            override fun updateDrawState(ds: TextPaint) {
//                super.updateDrawState(ds)
//                ds.color = Color.RED;
//                ds.isUnderlineText = true;
//            }
//            override fun onClick(view: View) {
//                Toast.makeText(this@MainActivity, "Clicked: book", Toast.LENGTH_LONG).show()
//            }
//        }
//
//        // Make the text view text clickable
//        viewMore.movementMethod = LinkMovementMethod()
//
//        viewMore.text = text

//        viewMore.text =
//            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua." +
//                    "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat." +
//                    "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur." +
//                    "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."

        viewMore.text =
            "Quisquam beatae possimus sed porro quasi repudiandae magnam rerum et numquam libero quasi non fuga quae in corporis cupiditate tenetur qui occaecati dicta vel."

//        viewMore.text = "Q\nQ\nQ\nQ\n"

        /*viewMore.text = "Jdhdhdhd\n" +
                "Dddf 0199 Jack ehjjeu hurur \$;&: Hdjd juue djjjed\n" +
                "Djjdjrd\n" +
                "Djdjjrhrr\n" +
                "Djdjjr\n" +
                "Djdjrjjrrjed\n" +
                "D\n" +
                "D\n" +
                "D\n" +
                "R\n" +
                "R\n" +
                "R\n" +
                "R\n" +
                "F\n" +
                "F\n" +
                "\n" +
                "F\n" +
                "F 1124 B"*/

        viewMore.setOnClickListener {
            viewMore.toggle()
        }
    }
}
