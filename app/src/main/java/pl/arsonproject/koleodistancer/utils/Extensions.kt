package pl.arsonproject.koleodistancer.utils

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout


fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

@BindingAdapter("app:errorText")
fun setErrorMessage(view: TextInputLayout, errorMessage: String?) {
    view.error = if (errorMessage.isNullOrBlank()) null else errorMessage
}