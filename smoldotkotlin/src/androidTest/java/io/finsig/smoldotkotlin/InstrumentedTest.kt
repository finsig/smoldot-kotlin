package io.finsig.smoldotkotlin

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedTest {

    private lateinit var chain: Chain

    @Before
    fun createChain() {
        val context = InstrumentationRegistry.getInstrumentation().context

        /**
         * Chain specification file to use for testing.
         */
        val specification = ChainSpecification().readFromAsset("polkadot.json", context)
        //val specification = ChainSpecification().readFromAsset("kusama.json", context)
        //val specification = ChainSpecification().readFromAsset("rococo.json", context)
        //val specification = ChainSpecification().readFromAsset("westend.json", context)
        chain = Chain(specification = specification)

        // Chain is not valid until it is added to the client.
        assertFalse(
            Client.instance().isValid(chain)
        )
    }

    @Test
    fun client_AddChain() {
        // Add the chain to the client
        Client.instance().add(chain)
        assertTrue(
            Client.instance().isValid(chain)
        )
    }

    @Test
    fun client_AddChainAlreadyAdded() {
        // Add the chain to the client
        Client.instance().add(chain)
        assertTrue(
            Client.instance().isValid(chain)
        )

        // Add the chain to the client again
        val exception = assertThrows(Exception::class.java) {
            Client.instance().add(chain)
        }
        assertEquals("Chain has already been added.", exception.message)
    }

    @Test
    fun client_RemoveChain() {
        // Add the chain to the client
        Client.instance().add(chain)
        assertTrue(
            Client.instance().isValid(chain)
        )

        // Remove the chain from the client
        Client.instance().remove(chain)
        assertFalse(
            Client.instance().isValid(chain)
        )
    }

    @Test
    fun client_RemoveChainNotAdded() {
        // Try to remove the chain when it has not been added to the client.
        val exception = assertThrows(Exception::class.java) {
            Client.instance().remove(chain)
        }
        assertEquals("Chain not found in client.", exception.message)
    }

    @Test
    fun client_JSONRPC2RequestChainNotAdded() {
        val jsonString = "{\"id\":1,\"jsonrpc\":\"2.0\",\"method\":\"system_chain\",\"params\":[]}"
        val request = JSONRPC2Request.parse(jsonString)

        /// Try to send a request to a chain without first adding it to the client.
        val exception = assertThrows(Exception::class.java) {
            Client.instance().send(request, chain)
        }
        assertEquals("Chain not found in client.", exception.message)
    }
}