package com.example.android.architecture.blueprints.todoapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext

@ExperimentalCoroutinesApi
class MainCoroutineExtension(private val dispatcher: TestDispatcher = StandardTestDispatcher()) :
    AfterTestExecutionCallback, BeforeTestExecutionCallback {

    override fun afterTestExecution(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }

    override fun beforeTestExecution(context: ExtensionContext?) {
        Dispatchers.setMain(dispatcher)
    }
}