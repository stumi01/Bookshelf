package com.bencestumpf.bookshelf

import android.view.KeyEvent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.bencestumpf.bookshelf.data.model.Book
import com.bencestumpf.bookshelf.domain.repositories.BookRepository
import com.bencestumpf.bookshelf.ui.addbook.AddBookActivity
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class AddBookTest {

    private val bookRepository: BookRepository = mock()

    @get:Rule
    val activityRule = ActivityTestRule(AddBookActivity::class.java, false, false)

    private val dummyBook = Book(
        1, "Title", "http://psypressuk.com/wp-content/uploads/2009/08/hesse-196x300.jpg",
        "Author", 1.99f
    )

    @Before
    fun setUp() {
        StandAloneContext.loadKoinModules(
            module {
                single(override = true) { bookRepository }
            })
    }

    @Test
    fun doesNotSendEmptyFields() {
        //Given

        //When
        activityRule.launchActivity(null)
        onView(withId(R.id.book_add_upload)).perform(click())

        //Then
        verifyNoMoreInteractions(bookRepository)
    }

    @Test
    fun doesNotSendInvalidUrl() {
        //Given

        //When
        activityRule.launchActivity(null)
        onView(withId(R.id.book_add_title)).perform(typeText("my title"))
        onView(withId(R.id.book_add_author)).perform(typeText("my author"))
        onView(withId(R.id.book_add_price)).perform(typeText("1"))
        onView(withId(R.id.book_add_cover_url)).perform(typeText("invalid url"))
            .perform(pressKey(KeyEvent.KEYCODE_ENTER))

        onView(withId(R.id.book_add_upload)).perform(click())

        //Then
        verifyNoMoreInteractions(bookRepository)
    }

    @Test
    fun sendValidData() {
        //Given
        Mockito.`when`(bookRepository.addBook(any(), any(), any(), any())).thenReturn(Single.just(dummyBook))
        val title = "My title"
        val author = "My author"
        val price = 1
        val coverUrl = "http://example.com/whatever.jpg"
        //When
        activityRule.launchActivity(null)
        onView(withId(R.id.book_add_title)).perform(typeText(title))
        onView(withId(R.id.book_add_author)).perform(typeText(author))
        onView(withId(R.id.book_add_price)).perform(typeText(price.toString()))
        onView(withId(R.id.book_add_cover_url)).perform(typeText(coverUrl))
            .perform(pressKey(KeyEvent.KEYCODE_ENTER))

        onView(withId(R.id.book_add_upload)).perform(click())

        //Then
        verify(bookRepository).addBook(eq(title), eq(author), eq(price.toFloat()), eq(coverUrl))
        verifyNoMoreInteractions(bookRepository)
    }
}
