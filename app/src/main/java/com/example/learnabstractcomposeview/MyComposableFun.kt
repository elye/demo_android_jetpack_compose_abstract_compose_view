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

    val parentComposition = rememberCompositionContext()
    val myComposeView = remember {
        MyComposeView(view, id).apply {
            setCustomContent(parentComposition) {
                content(::dismiss)
            }
        }
    }

    Text(
        text = "\"${textValue}\" in Container",
        textAlign = TextAlign.Center
    )

    DisposableEffect(key1 = myComposeView) {
        myComposeView.show()
        onDispose {
            myComposeView.dispose()
        }
    }
}
