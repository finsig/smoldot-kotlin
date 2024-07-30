package io.finsig.smoldotkotlinexample.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.core.content.pm.ShortcutInfoCompat
import io.finsig.smoldotkotlin.Chain
import io.finsig.smoldotkotlin.name

@Composable
fun ChainListScreen(
    chains: List<Chain>,
    onChainSelected: (Chain) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        chains.forEach { chain ->
            ListItem(
                headlineContent = {
                    ClickableText(
                        text = AnnotatedString(chain.name()),
                        modifier = modifier,
                        style = TextStyle.Default.copy(color = LocalContentColor.current),
                        onClick = {
                            onChainSelected(chain)
                        })
                }
            )
        }
    }
}
