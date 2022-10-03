package com.example.learnabstractcomposeview

import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import java.util.*

@Composable
fun MyComposableFun(textValue: MutableState<String>) {
    val view = LocalView.current
    val id = rememberSaveable { UUID.randomUUID() }
    val parentComposition = rememberCompositionContext()
    val myComposeView = remember {
        MyComposeView(view, id).apply {
            setCustomContent(parentComposition) {
                MyComposeViewContent(textValue.value, ::dismiss)
            }
        }
    }

    Text(
        text = "\"${textValue.value}\" in Container",
        textAlign = TextAlign.Center
    )

    DisposableEffect(key1 = myComposeView) {
        myComposeView.show()
        onDispose {
            myComposeView.dispose()
        }
    }
}
