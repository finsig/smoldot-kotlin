package io.finsig.smoldotkotlinexample

import androidx.lifecycle.ViewModel
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request
import io.finsig.smoldotkotlin.Chain
import io.finsig.smoldotkotlin.ChainSpecification
import io.finsig.smoldotkotlin.Client
import io.finsig.smoldotkotlin.readFromAsset
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

import java.time.LocalDateTime

data class UiState(
    val chain: Chain? = null,
    val requestCount: Int = 0
)
class MainViewModel(
    private val client: Client = Client.instance(),
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    context: android.content.Context
): ViewModel() {

    val chains = ChainSpecificationAsset.entries.map { asset ->
        Chain(specification = ChainSpecification().readFromAsset(asset.fileName, context))
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun setChain(chain: Chain) {
        _uiState.update {
            it.copy(
                chain = chain
            )
        }
    }

    fun connect(chain: Chain) {
        val updatedChain = client.add(chain)
        _uiState.update { currentState ->
            currentState.copy(
                chain = updatedChain,
            )
        }
    }

    fun parseRequest(string: String): Result<JSONRPC2Request> {
        try {
            val request = JSONRPC2Request.parse(string)
            return Result.success(request)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    fun sendRequest(request: JSONRPC2Request) {
        _uiState.value.chain?.let {
            client.send(request, it)
            _uiState.update {
                it.copy(
                    requestCount = _uiState.value.requestCount + 1
                )
            }
        }
    }

    fun responses(chain: Chain): Flow<Response> {
        return client.responses(chain).map { Response(it) }
    }

    fun disconnect() {
        _uiState.value.chain?.let {
            client.remove(it)
        }
        _uiState.update {
            it.copy(
                chain = null,
                requestCount = 0
            )
        }
    }
}

interface Message {
    val body: String
    val timeStamp: LocalDateTime
}
data class Request(override val body: String, override val timeStamp: LocalDateTime = LocalDateTime.now()) :
    Message
data class Response(override val body: String, override val timeStamp: LocalDateTime = LocalDateTime.now()) :
    Message

