package io.finsig.smoldotkotlinexample.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request
import io.finsig.smoldotkotlin.Chain
import io.finsig.smoldotkotlin.name
import io.finsig.smoldotkotlinexample.Message
import io.finsig.smoldotkotlinexample.R
import io.finsig.smoldotkotlinexample.Request
import io.finsig.smoldotkotlinexample.Response
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun RPCScreen(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    chain: Chain,
    onStart: (Chain) -> Unit,
    onStop: () -> Unit,
    parseRequest: (String) -> Result<JSONRPC2Request>,
    sendRequest: (JSONRPC2Request) -> Unit,
    requestCount: Int,
    responses: (Chain) -> Flow<Response>,
    modifier: Modifier = Modifier,
) {

    LaunchedEffect(Unit) {
        onStart(chain)
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            onStop()
        }
    }

    val messages = remember { mutableStateListOf<Message>() }

    Column(
        Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ConnectionBanner(
            chain = chain,
            messages = messages)

        Session(
            chain = chain,
            responses = responses,
            messages = messages,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
                .weight(1f))

        Row (
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            val requestIdentifier = requestCount + 1
            var text by rememberSaveable { mutableStateOf("") }
            val showRequestBuilderDialog = remember { mutableStateOf(false) }
            var exceptionMessage by remember { mutableStateOf("") }
            val showExceptionAlertDialog = remember { mutableStateOf(false) }

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("JSON-RPC2 Request String") },
                singleLine = true,
                leadingIcon = {
                    var expanded by remember { mutableStateOf(false) }

                    Button(onClick = {
                        expanded = true
                    },
                        shape = CircleShape,
                        contentPadding = PaddingValues(2.dp),
                        colors = ButtonDefaults.buttonColors(colorResource(R.color.polkadot_pink)),
                        modifier = modifier
                            .padding(4.dp)
                            .size(32.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Build request...") },
                            onClick = {
                                showRequestBuilderDialog.value = true
                                expanded = false
                            },
                            leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("system_chain") },
                            onClick = {
                                text = "{\"id\":${requestIdentifier},\"jsonrpc\":\"2.0\",\"method\":\"system_chain\",\"params\":[]}"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("chain_get_header") },
                            onClick = {
                                text = "{\"id\":${requestIdentifier},\"jsonrpc\":\"2.0\",\"method\":\"chain_getHeader\",\"params\":[]}"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("chain_subscribe_new_heads") },
                            onClick = {
                                text = "{\"id\":${requestIdentifier},\"jsonrpc\":\"2.0\",\"method\":\"chain_subscribeNewHeads\",\"params\":[]}"
                                expanded = false
                            }
                        )
                    }
                },
                trailingIcon = {
                    if (text.isNotEmpty()) {
                        Button(onClick = {
                            val result = parseRequest(text)
                            result.onSuccess {
                                messages.add(Request(text))
                                sendRequest(it)
                            }
                            result.onFailure {
                                it.message?.let {message ->
                                    exceptionMessage = message
                                    showExceptionAlertDialog.value = true
                                }
                            }
                            text = ""
                        },
                            shape = CircleShape,
                            contentPadding = PaddingValues(2.dp),
                            colors = ButtonDefaults.buttonColors(colorResource(R.color.polkadot_green)),
                            modifier = modifier
                                .padding(4.dp)
                                .size(32.dp)
                        ) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = "Submit")
                        }
                    }
                },
                shape = CircleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )

            RequestBuilderDialog(
                showDialog = showRequestBuilderDialog.value,
                onDismissRequest = { showRequestBuilderDialog.value = false },
                onConfirmation = { method, parameters ->
                    val params = if (parameters.isEmpty()) "[]" else parameters
                    text = "{\"id\":${requestIdentifier},\"jsonrpc\":\"2.0\",\"method\":\"${method}\",\"params\":${params}}"
                    showRequestBuilderDialog.value = false
                }
            )

            ExceptionAlertDialog(
                showDialog = showExceptionAlertDialog.value,
                onDismissRequest = { showExceptionAlertDialog.value = false },
                dialogTitle = "Error",
                dialogText = exceptionMessage
            )
        }
    }

}

@Composable
fun ConnectionBanner(
    chain: Chain,
    messages: MutableList<Message>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(
                colorResource(R.color.polkadot_cyan)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Connected to " + chain.specification.name())
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                messages.clear()
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(Icons.Outlined.Refresh, contentDescription = "Clear Messages")
            }
        }
    }
}

@Composable
fun Session(
    dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    chain: Chain,
    responses: (Chain) -> Flow<Response>,
    messages: SnapshotStateList<Message>,
    modifier: Modifier
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        scope.launch(dispatcherIO) {
            responses(chain).collect {
                messages.add(it)
            }
        }
    }

    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        itemsIndexed(messages) {_,row ->
            Text((if (row is Request) "Request" else "Response") + " - " + row.timeStamp.toString(),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Text(
                JSONObject(row.body).toString(2),
                modifier = Modifier.padding(12.dp)
            )
            HorizontalDivider(Modifier.padding(12.dp))
        }
    }

    LaunchedEffect(messages.count()) {
        scope.launch {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.lastIndex)
            }
        }
    }
}
