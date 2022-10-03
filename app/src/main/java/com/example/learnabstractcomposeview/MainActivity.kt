package com.example.learnabstractcomposeview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.R
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import com.example.learnabstractcomposeview.ui.theme.LearnAbstractComposeViewTheme
import java.util.*

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
                                MyCustomComposeView(textValue.value, ::dismiss)
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

                        MyComposeViewComposable(textValue)
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

    @Composable
    private fun MyComposeViewComposable(textValue: MutableState<String>) {
        val context = LocalContext.current
        val view = LocalView.current
        val id = rememberSaveable { UUID.randomUUID() }
        val parentComposition = rememberCompositionContext()
        val myComposeView = remember {
            MyComposeView(context, view, id).apply {
                setCustomContent(parentComposition) {
                    MyCustomComposeView(textValue.value, ::dismiss)
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
}

@SuppressLint("ViewConstructor")
class MyComposeView(
    context: Context,
    composeView: View,
    saveID: UUID? = null,

    ) : AbstractComposeView(context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var params = createLayoutParams()

    private var viewShowing = false

    init {
        ViewTreeLifecycleOwner.set(this, ViewTreeLifecycleOwner.get(composeView))
        ViewTreeViewModelStoreOwner.set(this, ViewTreeViewModelStoreOwner.get(composeView))
        ViewTreeSavedStateRegistryOwner.set(this, ViewTreeSavedStateRegistryOwner.get(composeView))
        saveID?.let {
            setTag(R.id.compose_view_saveable_id_tag, "MyComposeView:$saveID")
        }
    }

    private var content: @Composable () -> Unit by mutableStateOf({})
    override var shouldCreateCompositionOnAttachedToWindow: Boolean = false
        private set

    @Composable
    override fun Content() {
        content()
    }

    fun setCustomContent(parent: CompositionContext? = null, content: @Composable () -> Unit) {
        parent?.let {
            setParentCompositionContext(it)
        }
        this.content = content
        shouldCreateCompositionOnAttachedToWindow = true
    }

    fun show() {
        if (viewShowing) dismiss()
        windowManager.addView(this, params)
        params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        windowManager.updateViewLayout(this, params)
        viewShowing = true
    }

    fun dismiss() {
        if (!viewShowing) return
        disposeComposition()
        windowManager.removeViewImmediate(this)
        viewShowing = false
    }

    fun dispose() {
        dismiss()
        ViewTreeSavedStateRegistryOwner.set(this, null)
        ViewTreeLifecycleOwner.set(this, null)
        ViewTreeViewModelStoreOwner.set(this, null)
    }

    private fun createLayoutParams(): WindowManager.LayoutParams =
        WindowManager.LayoutParams().apply {
            gravity = Gravity.CENTER
//            gravity = when (position) {
//                ConstructKitToastPlacement.TOP -> Gravity.TOP
//                ConstructKitToastPlacement.BOTTOM -> Gravity.BOTTOM
//                else -> Gravity.CENTER
//            }
//            type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
//            token = composeView.applicationWindowToken
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            format = PixelFormat.TRANSLUCENT
//            flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        }
}


@Composable
fun MyCustomComposeView(textValue: String, buttonClicked: (() -> Unit)? = null) {
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

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LearnAbstractComposeViewTheme {
        Greeting("Android")
    }
}