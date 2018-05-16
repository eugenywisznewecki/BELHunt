package bel.ink.bel.belhunt.ui.interpolators

import android.view.animation.BounceInterpolator

class HeartInterpolator : BounceInterpolator() {

	override fun getInterpolation(t: Float): Float {
		var t1 = t
		t1 *= 1.1226f
		return if (t1 < 0.7408f)
			bounce(t)
		else if (t < 0.8644f)
			bounce(t1 - 0.8526f) + 0.4f
		else
			bounce(t1 - 1.0435f) + 0.3f
	}

	private fun bounce(t: Float): Float {
		return t * t
	}
}