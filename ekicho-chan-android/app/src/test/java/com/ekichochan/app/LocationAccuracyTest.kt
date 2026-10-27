package com.ekichochan.app.ui.location

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationAccuracyTest {
    @Test
    fun acceptsAccuracyUpToFiftyMeters() {
        assertTrue(isLocationAccuracyAcceptable(true, 0f))
        assertTrue(isLocationAccuracyAcceptable(true, 50f))
    }

    @Test
    fun rejectsMissingOrInsufficientAccuracy() {
        assertFalse(isLocationAccuracyAcceptable(false, 5f))
        assertFalse(isLocationAccuracyAcceptable(true, 50.1f))
        assertFalse(isLocationAccuracyAcceptable(true, Float.NaN))
    }
}
