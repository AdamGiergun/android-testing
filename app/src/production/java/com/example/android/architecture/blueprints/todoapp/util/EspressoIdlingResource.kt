package com.example.android.architecture.blueprints.todoapp.util

object EspressoIdlingResource {

    inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
        return function()
    }
}