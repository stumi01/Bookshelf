package com.bencestumpf.bookshelf

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.bencestumpf.bookshelf.data.model.Book
import com.bencestumpf.bookshelf.domain.repositories.BookRepository
import com.bencestumpf.bookshelf.ui.bookdetails.DetailsActivity
import com.bencestumpf.bookshelf.ui.bookdetails.DetailsActivity.Companion.EXTRA_BOOK_ID
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class BookDetailsTest {

    private val bookRepository: BookRepository = mock()

    @get:Rule
    val activityRule = ActivityTestRule(DetailsActivity::class.java, false, false)

    private lateinit var intent: Intent

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

        intent = Intent().apply {
            putExtra(EXTRA_BOOK_ID, dummyBook.id)
        }
    }

    @Test
    fun errorViewShows() {
        //Given
        `when`(bookRepository.getBook(any())).thenReturn(Single.error(Exception("Test Exception")))

        //When
        activityRule.launchActivity(intent)

        //Then
        verify(bookRepository).getBook(any())
        verifyNoMoreInteractions(bookRepository)

        onView(withId(R.id.error_view)).check(matches(isDisplayed()))
        onView(withId(R.id.content_view)).check(matches(not(isDisplayed())))
    }

    @Test
    fun retryButtonWorks() {
        //Given
        `when`(bookRepository.getBook(any())).thenReturn(Single.error(Exception("Test Exception")))

        //When
        activityRule.launchActivity(intent)

        `when`(bookRepository.getBook(any())).thenReturn(Single.just(dummyBook))

        onView(withId(R.id.retry)).perform(ViewActions.click())

        //Then
        verify(bookRepository, times(2)).getBook(any())
        verifyNoMoreInteractions(bookRepository)

        onView(withId(R.id.error_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.content_view)).check(matches(isDisplayed()))
    }

    @Test
    fun bookDetailsShowsCorrectly() {
        //Given
        Mockito.`when`(bookRepository.getBook(any())).thenReturn(Single.just(dummyBook))
        //When
        activityRule.launchActivity(intent)

        //Then
        verify(bookRepository).getBook(any())
        verifyNoMoreInteractions(bookRepository)

        onView(withId(R.id.book_title)).check(matches(withText(dummyBook.title)))
        onView(withId(R.id.book_author)).check(matches(withText(dummyBook.author)))
        onView(withId(R.id.book_price))
            .check(matches(withText(activityRule.activity.getString(R.string.price_placeholder, dummyBook.price))))
    }
}