package com.example.nutritionapp.util

import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.nutritionapp.R


fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.disable() {
    isEnabled = false
}

fun View.enabled() {
    isEnabled = true
}

fun Fragment.toast(msg: String?) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
}


fun String.isValidEmail() =
    isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun List<Pair<View, TextView>>.applyClickBehavior(selectedGoal: (String?) -> Unit) {
    // Function to reset the views to their normal state
    fun resetViews() {
        for ((view, textView) in this) {
            view.background = ContextCompat.getDrawable(view.context, R.drawable.strok_view)
            textView.setTextColor(ContextCompat.getColor(view.context, R.color.black))
            textView.setTypeface(null, Typeface.NORMAL)
        }
    }

    // Set click listeners for each view
    for ((view, textView) in this) {
        view.setOnClickListener {
            resetViews()
            view.background = ContextCompat.getDrawable(view.context, R.drawable.selected_stroke_view)
            textView.setTextColor(ContextCompat.getColor(view.context, R.color.blue))
            textView.setTypeface(null, Typeface.BOLD)
            selectedGoal(textView.text.toString()) // Invoke the provided onClick lambda with the text from the clicked TextView
        }
    }
}
