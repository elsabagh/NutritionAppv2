package com.example.nutritionapp.util

import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.nutritionapp.R
import java.util.Calendar
import java.util.Date


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
    if (!msg.isNullOrEmpty()) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}

fun String.isValidEmail() =
    isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun List<Pair<View, TextView>>.applyClickBehavior(selectedGoal: (String?) -> Unit) {
    // Function to reset the views to their normal state
    fun resetViews() {
        for ((view, textView) in this) {
            view.background = ContextCompat.getDrawable(view.context, R.drawable.strok_view)
            textView.setTextColor(ContextCompat.getColor(view.context, R.color.gray))
            textView.setTypeface(null, Typeface.NORMAL)
        }
    }

    // Set click listeners for each view
    for ((view, textView) in this) {
        view.setOnClickListener {
            resetViews()
            view.background =
                ContextCompat.getDrawable(view.context, R.drawable.selected_stroke_view)
            textView.setTextColor(ContextCompat.getColor(view.context, R.color.blue))
            textView.setTypeface(null, Typeface.BOLD)
            selectedGoal(textView.text.toString()) // Invoke the provided onClick lambda with the text from the clicked TextView
        }
    }
}

fun View.updateCardSelection(selectedCard: View?, allCards: List<View>) {
    // Reset background of previously selected card
//    allCards.forEach { card ->
//        selectedCard?.setBackgroundResource(R.drawable.strok_view)
//    }
    allCards.forEach { card ->
        card.setBackgroundResource(
            if (card == this) R.drawable.selected_stroke_view
            else R.drawable.strok_view
        )
    }

//    // Set stroke color for the newly selected card
//    this.setBackgroundResource(R.drawable.selected_stroke_view)
}

fun Date.startOfDay(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

fun Date.endOfDay(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.time
}