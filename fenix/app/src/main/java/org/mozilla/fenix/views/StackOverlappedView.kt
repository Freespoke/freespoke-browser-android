package org.mozilla.fenix.views

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import coil.load
import coil.size.Scale
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.ViewStackOverlappedBinding

class StackOverlappedView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    private var binding: ViewStackOverlappedBinding

    init {
        binding = ViewStackOverlappedBinding.inflate(LayoutInflater.from(context), this, false)
        addView(binding.root)
    }

    fun setUpStackIconsView(urls: List<String>, count: Int = IS_DEFAULT_MEMBERS_COUNT) {
        fillIconsHolder(urls, count)
    }

    private fun fillIconsHolder(list: List<String>, count: Int) {
        binding.apply {
            val iconsToDisplay = if (list.size > MAX_MEMBERS_TO_DISPLAY_COUNT)
                MAX_MEMBERS_TO_DISPLAY_COUNT - 1 else list.size
            val paddingSize = 4.px
            val viewSize = 24.px
            val overlapSize = (-4).px
            val textSize = 14f

            stackOverlapped.removeAllViews()

            for (i in 0 until iconsToDisplay) {
                val cardView = CardView(context)
                val imageView = ImageView(context)

                cardView.layoutParams = LayoutParams(viewSize, viewSize).apply {
                    if (i != 0) marginStart = overlapSize
                }
                cardView.setBackgroundResource(R.drawable.circle_border)
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                imageView.load(list[i]) {
                    scale(Scale.FILL)
                }
                cardView.addView(imageView)
                stackOverlapped.addView(cardView)
            }

            if (list.size > MAX_MEMBERS_TO_DISPLAY_COUNT) {
                val textView = TextView(context)
                textView.layoutParams = LayoutParams(viewSize, viewSize).apply {
                    marginStart = overlapSize
                }
                textView.textSize = textSize
                textView.setTextColor(ContextCompat.getColor(context, android.R.color.white))
                textView.gravity = Gravity.CENTER
                textView.setPadding(paddingSize, paddingSize, paddingSize, paddingSize)
                textView.text =
                    context.getString(R.string.icon_count, list.size - count)
                textView.background = AppCompatResources.getDrawable(context, R.drawable.circle_border)
                stackOverlapped.addView(textView)
            }
        }
    }

    companion object {
        const val MAX_MEMBERS_TO_DISPLAY_COUNT = 3
        const val IS_DEFAULT_MEMBERS_COUNT = -1
    }
}

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
