package com.example.learnabstractcomposeview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MyComposeViewContent(textValue: String, buttonClicked: (() -> Unit)? = null) {
    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.padding(16.dp)) {
        Surface(
            border = BorderStroke(1.dp, MaterialTheme.colors.secondary),
            shape = RoundedCornerShape(8.dp),
            elevation = 8.dp
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                var checkedState by rememberSaveable { mutableStateOf(false) }
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Checkbox(
                        checked = checkedState,
                        onCheckedChange = { checkedState = it }
                    )
                }

                Text(textValue)

                TextButton(
                    onClick = { buttonClicked?.invoke() },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Remove")
                }
            }
        }
    }
}
