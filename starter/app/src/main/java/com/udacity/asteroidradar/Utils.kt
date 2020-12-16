package com.udacity.asteroidradar

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

fun <A : Activity> Activity.startNewActivity(activity: Class<A>){
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

fun View.visible(isVisible: Boolean){
    visibility = if(isVisible) View.VISIBLE else View.GONE
}

fun View.enable(enabled: Boolean){
    isEnabled = enabled
    alpha = if(enabled) 1f else 0.5f
}

fun View.snackbar(message: String, action: (() -> Unit)? = null){
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    action?.let {
        snackbar.setAction("Retry"){
            it()
        }
    }
    snackbar.show()
}

fun Fragment.handleApiError(
    failure: Resource.Failure,
    retry: (() -> Unit)? = null
){
    when{
        failure.isNetworkError -> requireView().snackbar("Please check your connection!", retry)
        failure.errorCode == 401 -> {
            /**
             * Unauthorized access, typically logout a user
             */
        }
        else -> {
            val error = failure.errorBody?.string().toString()
            requireView().snackbar(error)
        }
    }
}

fun ImageView.setImageFromNetwork(url: String){
    /**
     * Avoiding cleartext issues for Android 9+ where cleartext is disabled by default
     */
    val imageUri = url.toUri().buildUpon().scheme("https").build()
    val picasso = Picasso.get()
    picasso.setIndicatorsEnabled(true)
    picasso.isLoggingEnabled = true
    picasso.load(imageUri)
        .placeholder(R.drawable.placeholder_picture_of_day)
        .error(R.drawable.ic_baseline_broken_image_64)
        .into(this)
}

fun ImageView.setImageFromNetworkVideo(url: String){
    val imageUri = getYoutubeThumbnailUrlFromVideoUrl(url).toUri().buildUpon().scheme("https").build()
    val picasso = Picasso.get()
    picasso.setIndicatorsEnabled(true)
    picasso.isLoggingEnabled = true
    picasso.load(imageUri)
        .placeholder(R.drawable.placeholder_picture_of_day)
        .error(R.drawable.ic_baseline_broken_image_64)
        .into(this)

    /**
     * Creating a new ImageView to display a Play button, directing to the YouTube link
     */
    val playImageView = ImageView(this.context)
    val playResId = R.drawable.ic_baseline_play_circle_outline_64
    playImageView.setImageResource(playResId)

    playImageView.setOnClickListener {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(url)
        startActivity(this.context, openURL, null)
    }

    /**
     * Getting the original ImageView's parent layout (FrameLayout) and adding the view programatically
     */
    (this.parent as FrameLayout).also {
        it.addView(playImageView)

        // A little unorthodox, but it does the job
        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.CENTER
        playImageView.layoutParams = layoutParams
    }

}

/**
 * Return a new URL string, converting our YouTube link to the thumbnail requested as Maximum Resolution
 */
fun getYoutubeThumbnailUrlFromVideoUrl(videoUrl: String): String {
    return "https://img.youtube.com/vi/" + getYoutubeVideoIdFromUrl(videoUrl) + "/maxresdefault.jpg"
}

fun getYoutubeVideoIdFromUrl(url: String): String? {
    var inUrl = url
    inUrl = inUrl.replace("&feature=youtu.be", "")
    if (inUrl.toLowerCase(Locale.ROOT).contains("youtu.be")) {
        return inUrl.substring(inUrl.lastIndexOf("/") + 1)
    }
    val pattern = "(?<=watch\\?v=|/videos/|embed/)[^#&?]*"
    val compiledPattern: Pattern = Pattern.compile(pattern)
    val matcher: Matcher = compiledPattern.matcher(inUrl)
    return if (matcher.find()) {
        matcher.group()
    } else null
}