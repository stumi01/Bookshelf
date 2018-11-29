package com.bencestumpf.bookshelf.helper

import android.view.View
import android.view.ViewGroup
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

object ChildMatcher {
    fun hasChildren(numChildrenMatcher: Matcher<Int>): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun matchesSafely(view: View): Boolean {
                return view is ViewGroup && numChildrenMatcher.matches((view as ViewGroup).childCount)
            }

            override fun describeTo(description: Description) {
                description.appendText("a view with # children is ")
                numChildrenMatcher.describeTo(description)
            }
        }
    }
}