package com.navyblue.navyweather.core.mvi

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlin.test.Test
import kotlin.test.assertEquals

/** One-shot effects are collected only while the screen is at least started (specs/presentation-architecture). */
@OptIn(ExperimentalTestApi::class)
class CollectEffectsTest {

    private data class Shown(val message: String) : UiEffect

    private class TestLifecycleOwner : LifecycleOwner {
        // createUnsafe: the Compose test thread is not the platform main thread.
        val registry = LifecycleRegistry.createUnsafe(this)
        override val lifecycle: Lifecycle get() = registry
    }

    @Test
    fun effectSentWhileNotStartedIsDeliveredOnceWhenTheScreenStarts() = runComposeUiTest {
        val owner = TestLifecycleOwner().apply { registry.currentState = Lifecycle.State.CREATED }
        val effects = Channel<Shown>(Channel.BUFFERED)
        val received = mutableListOf<Shown>()
        setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                CollectEffects(effects.receiveAsFlow()) { received += it }
            }
        }

        effects.trySend(Shown("saved"))
        waitForIdle()
        assertEquals(emptyList(), received, "not collected while only CREATED")

        runOnIdle { owner.registry.currentState = Lifecycle.State.STARTED }
        waitForIdle()
        assertEquals(listOf(Shown("saved")), received)

        // Going to the background and back does not deliver it again.
        runOnIdle { owner.registry.currentState = Lifecycle.State.CREATED }
        runOnIdle { owner.registry.currentState = Lifecycle.State.RESUMED }
        waitForIdle()
        assertEquals(listOf(Shown("saved")), received)
    }
}
