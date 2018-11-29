package com.bencestumpf.bookshelf

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.bencestumpf.bookshelf.data.model.Book
import com.bencestumpf.bookshelf.domain.repositories.BookRepository
import com.bencestumpf.bookshelf.helper.ChildMatcher.hasChildren
import com.bencestumpf.bookshelf.ui.booklist.BookListActivity
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import org.hamcrest.CoreMatchers.`is`
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
class BookListTest {

    private val bookRepository: BookRepository = mock()

    @get:Rule
    val activityRule = ActivityTestRule(BookListActivity::class.java, false, false)

    private val dummyBook = Book(
        1, "Title", "http://psypressuk.com/wp-content/uploads/2009/08/hesse-196x300.jpg",
        "Author", 1.99f
    )

    private val dummyBook2 = Book(
        2, "Title2", "http://psypressuk.com/wp-content/uploads/2009/08/hesse-196x300.jpg",
        "Author2", 1.99f
    )

    @Before
    fun setUp() {
        StandAloneContext.loadKoinModules(
            module {
                single(override = true) { bookRepository }
            })
    }

    @Test
    fun errorViewShows() {
        //Given
        Mockito.`when`(bookRepository.getBooks(any())).thenReturn(Single.error(Exception("Test Exception")))

        //When
        activityRule.launchActivity(null)

        //Then
        verify(bookRepository).getBooks(any())
        verifyNoMoreInteractions(bookRepository)

        onView(withId(R.id.error_view)).check(matches(isDisplayed()))
        onView(withId(R.id.content_view)).check(matches(not(isDisplayed())))
    }

    @Test
    fun listItemShowsCorrectly() {
        //Given
        Mockito.`when`(bookRepository.getBooks(any())).thenReturn(Single.just(listOf(dummyBook)))
        //When
        activityRule.launchActivity(null)

        //Then
        verify(
            bookRepository,
            times(2)//2 times because of the load more when reaching the end of the infinite scroll
        ).getBooks(any())
        verifyNoMoreInteractions(bookRepository)

        onView(withId(R.id.error_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.content_view)).check(matches(isDisplayed()))
        onView(withId(R.id.book_title)).check(matches(withText(dummyBook.title)))
    }

    @Test
    fun allListItemShows() {
        //Given
        Mockito.`when`(bookRepository.getBooks(any())).thenReturn(Single.just(listOf(dummyBook, dummyBook2)))
        //When
        activityRule.launchActivity(null)

        //Then
        verify(bookRepository, times(2)).getBooks(any())
        verifyNoMoreInteractions(bookRepository)

        onView(withId(R.id.error_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.content_view)).check(matches(isDisplayed()))
        onView(withId(R.id.recycler_view)).check(matches(hasChildren(`is`(2))))
    }

    @Test
    fun floatingButtonNavigatesToTheAddBookScreen() {
        //Given
        `when`(bookRepository.getBooks(any())).thenReturn(Single.just(listOf(dummyBook)))

        //When
        activityRule.launchActivity(null)
        onView(withId(R.id.add_book_floating)).perform(click())

        //Then
        verify(bookRepository, times(2)).getBooks(any())
        verifyNoMoreInteractions(bookRepository)

        onView(withId(R.id.add_book_view)).check(matches(isDisplayed()))

    }

    @Test
    fun listItemNavigatesToTheDetailsView() {
        //Given
        `when`(bookRepository.getBooks(any())).thenReturn(Single.just(listOf(dummyBook)))
        `when`(bookRepository.getBook(any())).thenReturn(Single.just(dummyBook))

        //When
        activityRule.launchActivity(null)
        onView(withId(R.id.book_title)).perform(click())

        //Then
        verify(bookRepository, times(2)).getBooks(any())
        verify(bookRepository).getBook(any())
        verifyNoMoreInteractions(bookRepository)

        onView(withId(R.id.book_details_view)).check(matches(isDisplayed()))
    }
}