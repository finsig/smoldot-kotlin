package io.finsig.smoldotkotlinexample

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.finsig.smoldotkotlinexample.ui.RPCScreen
import io.finsig.smoldotkotlinexample.ui.theme.ChainListScreen

enum class AppScreen(@StringRes val title: Int) {
    ChainList(title = R.string.chain_list),
    RemoteProcedureCall(title = R.string.remote_procedure_call)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    currentScreen: AppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun App(
    viewModel: MainViewModel = MainViewModel(context = LocalContext.current),
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.ChainList.name
    )

    Scaffold(
        topBar = {
            AppBar(
                currentScreen = currentScreen,
                canNavigateBack =  navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.ChainList.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = AppScreen.ChainList.name) {
                ChainListScreen(
                    chains = viewModel.chains,
                    onChainSelected = {chain ->
                        viewModel.setChain(chain)
                        navController.navigate(AppScreen.RemoteProcedureCall.name)
                    }
                )
            }
            composable(route = AppScreen.RemoteProcedureCall.name) {
                uiState.chain?.let {chain ->
                    RPCScreen(
                        chain = chain,
                        onStart = { viewModel.connect(chain) },
                        onStop = { viewModel.disconnect() },
                        parseRequest = { viewModel.parseRequest(it) },
                        sendRequest = { viewModel.sendRequest(it) },
                        requestCount = uiState.requestCount,
                        responses = { viewModel.responses(chain) }
                    )
                }
            }
        }
    }
}
