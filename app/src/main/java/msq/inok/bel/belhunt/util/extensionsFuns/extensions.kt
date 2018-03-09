package msq.inok.bel.belhunt.util.extensionsFuns

import java.text.DateFormat
import java.util.*

/**
 * Created by inoknote on 09/03/18.
 */
//extensinon
fun Long.toDateString(dateFormat: Int = DateFormat.MEDIUM): String {
	val df = DateFormat.getDateInstance(dateFormat, Locale.getDefault())
	return df.format(this)
}