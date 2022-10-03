package com.example.learnabstractcomposeview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.example.learnabstractcomposeview.ui.theme.LearnAbstractComposeViewTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val originTitle = "Custom Compose Title"
        private const val changedTitle = "Changed Compose Title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LearnAbstractComposeViewTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    color = MaterialTheme.colors.background
                ) {
                    val textValue = rememberSaveable { mutableStateOf(originTitle) }
                    val context = LocalContext.current
                    val view = LocalView.current
                    val parentComposition = rememberCompositionContext()
                    val myComposeView = remember {
                        MyComposeView(context, view).apply {
                            setCustomContent(parentComposition) {
                                MyComposeViewContent(textValue.value, ::dismiss)
                            }
                        }
                    }

                    var checkedState by rememberSaveable { mutableStateOf(false) }
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row (verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = checkedState,
                                onCheckedChange = {
                                    textValue.value = if (it)  changedTitle else originTitle
                                    checkedState = it
                                }
                            )
                        }
                        Text("Title: ${textValue.value}")
                        Button(onClick = { myComposeView.show() }) {
                            Text(text = "Open")
                        }

                        MyComposableFun(textValue)
                    }

                    DisposableEffect(myComposeView) {
                        onDispose {
                            myComposeView.dispose()
                        }
                    }
                }
            }
        }
    }
}
