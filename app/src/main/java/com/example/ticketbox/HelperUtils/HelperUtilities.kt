package com.example.ticketbox.HelperUtils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

object HelperUtilities {
    fun isValidPostalCode(postalCode: String): Boolean {
        val regexPostalCode = "[A-Za-z][0-9][A-Za-z] [0-9][A-Za-z][0-9]"
        return postalCode.matches(regexPostalCode.toRegex())
    }

    fun isValidEmail(email: String): Boolean {
        val regexEmail =
            "([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$"
        return email.matches(regexEmail.toRegex())
    }

    fun isValidPhone(phone: String): Boolean {
        // Indonesian phone number pattern: +62 followed by 8 to 15 digits
        val regexPhone = """\+62\d{8,15}"""
        return phone.matches(regexPhone.toRegex())
    }

    fun isEmptyOrNull(param: String?): Boolean {
        return param == null || param.trim { it <= ' ' }.isEmpty()
    }

    fun isString(data: String): Boolean {
        return !data.matches("""\d+(?:\.\d+)?""".toRegex())
    }


    fun isShortPassword(password: String): Boolean {
        return if (password.length > 5) {
            false
        } else true
    }



    val dateTime: String
        get() = DateFormat.getDateInstance().format(Date())

    fun formatDate(y: Int, m: Int, d: Int): String? {
        try {
            val date = d.toString() + "/" + (m + 1) + "/" + y
            val date1 = SimpleDateFormat("dd/MM/yyyy").parse(date)
            val fullDf = DateFormat.getDateInstance(DateFormat.FULL)
            return fullDf.format(date1)
        } catch (e: Exception) {
        }
        return null
    }

    fun currentYear(): Int {
        val c = Calendar.getInstance()
        return c[Calendar.YEAR]
    }

    fun currentMonth(): Int {
        val c = Calendar.getInstance()
        return c[Calendar.MONTH]
    }

    fun currentDay(): Int {
        val c = Calendar.getInstance()
        return c[Calendar.DAY_OF_MONTH]
    }



    fun capitalize(str: String?): String? {
        return if (str!!.length == 0) str else str.substring(0, 1)
            .uppercase(Locale.getDefault()) + str.substring(1)
    }

    fun filter(input: String): String {
        if (!hasSpecialChars(input)) {
            return input
        }
        val sb = StringBuilder(input.length)
        var c: Char
        for (i in 0 until input.length) {
            c = input[i]
            when (c) {
                '<' -> sb.append("&lt;")
                '>' -> sb.append("&gt;")
                '"' -> sb.append("&quot;")
                '\'' -> sb.append("&apos;")
                '&' -> sb.append("&amp;")
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun hasSpecialChars(input: String): Boolean {
        val regexSpecialChars = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
        val inputStr = regexSpecialChars.matcher(input)
        val hasSpecialChars = inputStr.find()
        return if (!hasSpecialChars) {
            false
        } else true
    }

    //calculates the size of the uploaded image
    fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight
                && halfWidth / inSampleSize >= reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    //reduces the size of the image
    fun decodeSampledBitmapFromByteArray(res: ByteArray, reqWidth: Int, reqHeight: Int): Bitmap {

        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(res, 0, res.size, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeByteArray(res, 0, res.size, options)
    }

    fun calculateTotalFare(outboundFare: Double, returnFare: Double, numTraveller: Int): Double {
        return (outboundFare + returnFare) * numTraveller
    }

    fun calculateTotalFare(fare: Double, numTraveller: Int): Double {
        return fare * numTraveller
    }

    fun compareDate(departureDate: String?, returnDate: String?): Boolean {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val date1 = sdf.parse(departureDate)
            val date2 = sdf.parse(returnDate)
            if (date2.before(date1)) {
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}