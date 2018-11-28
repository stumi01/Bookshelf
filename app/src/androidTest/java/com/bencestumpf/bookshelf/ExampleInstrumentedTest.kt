package com.bencestumpf.bookshelf

import com.bencestumpf.bookshelf.domain.usecases.GetBooks
import com.bencestumpf.bookshelf.ui.booklist.BookListActivity
import com.nhaarman.mockito_kotlin.mock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.mockito.Mockito.`when`

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private val getBooks: GetBooks = mock()

    @get:Rule
    val activityRule = ActivityTestRule(BookListActivity::class.java, false, false)


    @Before
    fun setUp() {
        loadKoinModules(
            module {
                single(override = true) { getBooks }
            })
    }

    @Test
    fun useAppContext() {
        //Given
        val message = "My new message"
        `when`(getBooks.getData()).thenReturn(message)

        //When
        activityRule.launchActivity(null)

        //Then
        onView(withId(R.id.message)).check(matches(withText(message)))

    }
}
