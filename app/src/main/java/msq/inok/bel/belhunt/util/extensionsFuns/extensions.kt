package msq.inok.bel.belhunt.util.extensionsFuns

import java.text.DateFormat
import java.util.*

fun Long.toDateString(dateFormat: Int = DateFormat.MEDIUM): String {
	val df = DateFormat.getDateInstance(dateFormat, Locale.getDefault())
	return df.format(this)
}