package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.curriculum.CurriculumData
import com.example.data.model.GradeLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Poko English Kids", appName)
    }

    @Test
    fun `verify curriculum contains units for Grade 1 and Grade 2`() {
        val grade1Units = CurriculumData.getUnitsForGrade(GradeLevel.GRADE_1)
        val grade2Units = CurriculumData.getUnitsForGrade(GradeLevel.GRADE_2)

        assertTrue(grade1Units.isNotEmpty())
        assertTrue(grade2Units.isNotEmpty())

        val allWords = CurriculumData.getAllWords()
        assertTrue(allWords.size >= 30)
    }
}
