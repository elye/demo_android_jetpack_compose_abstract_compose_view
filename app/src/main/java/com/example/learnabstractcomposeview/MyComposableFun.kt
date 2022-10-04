package com.example.learnabstractcomposeview

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import java.util.*

@Composable
fun MyComposableFun(textValue: String, content: @Composable (()->Unit) -> Unit) {

    val view = LocalView.current
    val id = rememberSaveable { UUID.randomUUID() }
    var checkedState by rememberSaveable { mutableStateOf(false) }
    var myText by rememberSaveable (textValue){ mutableStateOf(textValue) }

    val parentComposition = rememberCompositionContext()
    val myComposeView = remember {
        MyComposeView(view, id).apply {
            setCustomContent(parentComposition) {
                content(::dismiss)
            }
        }
    }

    Row (verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checkedState,
            onCheckedChange = {
                myText = if (it) "Internal Change" else "Internal Change Again"
                checkedState = it
            }
        )
        Text("Title: $myText")
    }

    DisposableEffect(key1 = myComposeView) {
        myComposeView.show()
        onDispose {
            myComposeView.dispose()
        }
    }
}
