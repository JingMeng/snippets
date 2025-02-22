/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.snippets

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.snippets.ui.theme.SnippetsTheme
import kotlinx.coroutines.launch

class MyActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            Text(text = stringResource(id = R.string.greeting))
//            test1()

//            test2()

//            SnippetsTheme {
//                ScaffoldExample()
//            }

            SnippetsTheme {
                SampleScreen()
            }


        }
    }

    @Preview
    @Composable
    fun SampleScreen() {
        ExpandableText("这是一个很长的文本示例，需要支持展开和折叠的功能。超过三行后，应该显示省略号，并提供‘展开’按钮，点击后应该显示完整文本，并提供‘关闭’按钮。这是一个很长的文本示例，需要支持展开和折叠的功能。超过三行后，应该显示省略号，并提供‘展开’按钮，点击后应该显示完整文本，并提供‘关闭’按钮。")
    }

    @Composable
    fun ExpandableText(text: String, maxLines: Int = 3) {
        var expanded by remember { mutableStateOf(false) }
        var isOverflowed by remember { mutableStateOf(false) }
        var collapsedText by remember { mutableStateOf<AnnotatedString>(AnnotatedString(text)) }

        val interactionSource = remember { MutableInteractionSource() }

        val textMeasurer = rememberTextMeasurer()
        val textStyle = MaterialTheme.typography.body1


        // 动画: 控制文本的透明度
        val alphaAnim by animateFloatAsState(
            targetValue = if (expanded) 1f else 0.7f,
            animationSpec = tween(durationMillis = 300)
        )

        val textLayoutResult = textMeasurer.measure(text, style = textStyle)

        // 动态计算文本高度
        val heightAnim by animateDpAsState(
            targetValue = if (expanded) textLayoutResult.size.height.dp else calculateCollapsedHeight(
                maxLines,
                text,
                textStyle,
                textMeasurer
            ),
            animationSpec = tween(durationMillis = 300)
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (expanded) {
                    buildAnnotatedString {
                        append(text)
                        append(" ")
                        withStyle(
                            SpanStyle(
                                color = Color.Blue,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("关闭")
                        }
                    }
                } else {
                    collapsedText
                },
                maxLines = if (expanded) Int.MAX_VALUE else maxLines,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { result ->
                    // 判断是否溢出并需要折叠
                    if (!expanded && result.lineCount >= maxLines) {
                        val lastLineStart = result.getLineStart(maxLines - 1)
                        val lastLineEnd = result.getLineEnd(maxLines - 1)
                        var lastLineText = text.substring(lastLineStart, lastLineEnd).trimEnd()
                        var remainingText = lastLineText

                        while (textMeasurer.measure(
                                remainingText,
                                style = textStyle
                            ).size.width > result.size.width
                        ) {
                            if (remainingText.isEmpty()) break
                            remainingText = remainingText.dropLast(1)
                        }

                        if (lastLineText != remainingText) {
                            isOverflowed = true
                            val expandText = AnnotatedString(
                                " 展开",
                                spanStyle = SpanStyle(
                                    color = Color.Blue,
                                    textDecoration = TextDecoration.Underline
                                )
                            )

                            val ellipsisText = "…"

                            while (textMeasurer.measure(
                                    lastLineText + ellipsisText + expandText.text,
                                    style = textStyle
                                )
                                    .size.width > result.size.width
                            ) {
                                if (lastLineText.isEmpty()) break
                                lastLineText = lastLineText.dropLast(1)
                            }

                            collapsedText = buildAnnotatedString {
                                append(text.substring(0, lastLineStart) + lastLineText)
                                append(ellipsisText) // 保持原样式的省略号
                                append(expandText) // "展开" 应用蓝色和下划线
                            }
                        }
                    } else {
                        isOverflowed = false
                    }
                },
                modifier = Modifier
                    .animateContentSize()
                    .clickable(
                        onClick = { expanded = !expanded },
                        interactionSource = interactionSource,  // 设置 interactionSource
                        indication = null // 禁用水波纹效果
                    )
                    .alpha(alphaAnim) // 使用 alphaAnim 控制透明度
//                    .height(heightAnim) // 通过动态高度动画控制折叠展开的过渡
            )
        }
    }


    // 计算折叠状态下的高度（基于 maxLines 行数）
    fun calculateCollapsedHeight(
        maxLines: Int,
        text: String,
        textStyle: TextStyle,
        textMeasurer: TextMeasurer
    ): Dp {
        // 测量折叠后的最大行数所需的高度
        val textLayoutResult = textMeasurer.measure(text, style = textStyle)
        val lineHeight = textLayoutResult.size.height / textLayoutResult.lineCount // 获取每行高度
        val collapsedHeight = lineHeight * maxLines // 计算折叠后的总高度
        return collapsedHeight.dp // 返回折叠时需要的高度
    }

    @Composable
    fun ExpandableText1(text: String, maxLines: Int = 3) {
        var expanded by remember { mutableStateOf(false) }
        var isOverflowed by remember { mutableStateOf(false) }
        var collapsedText by remember { mutableStateOf<AnnotatedString>(AnnotatedString(text)) }

        val textMeasurer = rememberTextMeasurer()
        val textStyle = MaterialTheme.typography.body1

        Column {
            Text(
                text = if (expanded) {
                    buildAnnotatedString {
                        append(text)
                        append(" ")
                        withStyle(
                            SpanStyle(
                                color = Color.Blue,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("关闭")
                        }
                    }
                } else {
                    collapsedText
                },
                maxLines = if (expanded) Int.MAX_VALUE else maxLines,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { result ->
                    Log.d(
                        "ExpandableText",
                        "expanded: $expanded, 行数: ${result.lineCount}，isOverflowed: $isOverflowed"
                    )

                    // 判断是否溢出并需要折叠
                    if (!expanded && result.lineCount >= maxLines) {
                        // 获取最后一行结束位置的文本
                        val lastLineStart = result.getLineStart(maxLines - 1)
                        val lastLineEnd = result.getLineEnd(maxLines - 1)
                        var lastLineText = text.substring(lastLineStart, lastLineEnd).trimEnd()

                        // 获取剩余的文本（被截断部分）
                        var remainingText = lastLineText

                        //处理可以展示的文本大小
                        while (textMeasurer.measure(
                                remainingText,
                                style = textStyle
                            ).size.width > result.size.width
                        ) {
                            if (remainingText.isEmpty()) break
                            remainingText = remainingText.dropLast(1)
                        }
                        Log.d(
                            "ExpandableText123",
                            "expanded: ${lastLineText != remainingText}"
                        )

                        // 判断最后一行的文本和剩余文本是否一致
                        if (lastLineText != remainingText) {
                            isOverflowed = true
                            val expandText = AnnotatedString(
                                " 展开",
                                spanStyle = SpanStyle(
                                    color = Color.Blue,
                                    textDecoration = TextDecoration.Underline
                                )
                            )

                            val ellipsisText = "…" // 省略号保持原样式

                            // 确保 "… 展开" 完整显示
                            while (textMeasurer.measure(
                                    lastLineText + ellipsisText + expandText.text,
                                    style = textStyle
                                ).size.width > result.size.width
                            ) {
                                if (lastLineText.isEmpty()) break
                                lastLineText = lastLineText.dropLast(1)
                            }

                            collapsedText = buildAnnotatedString {
                                append(text.substring(0, lastLineStart) + lastLineText)
                                append(ellipsisText) // 保持原样式的省略号
                                append(expandText) // "展开" 应用蓝色和下划线
                            }

                            Log.d("ExpandableText", "计算后的 collapsedText: $collapsedText")
                        }
                    } else {
                        isOverflowed = false
                    }
                },
                modifier = Modifier.clickable { expanded = !expanded }
            )
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ScaffoldExample() {
        val scaffoldState = rememberScaffoldState() // 记住 Scaffold 的状态
        val coroutineScope = rememberCoroutineScope() // 记住协程作用域

        Scaffold(
            scaffoldState = scaffoldState, // 传递 Scaffold 的状态
            topBar = {
                TopAppBar(
                    title = { Text("Scaffold 示例") }, // 顶部应用栏标题
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch { scaffoldState.drawerState.open() } // 打开抽屉
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "菜单") // 菜单图标
                        }
                    }
                )
            },
            bottomBar = {
                BottomAppBar {
                    Text("底部栏") // 底部栏文本
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    coroutineScope.launch {
                        scaffoldState.snackbarHostState.showSnackbar("FAB 点击！") // 点击 FAB 显示 Snackbar
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "添加") // FAB 按钮图标
                }
            },
            floatingActionButtonPosition = FabPosition.Center, // FAB 按钮位置设为右下角----后期改成中间
            isFloatingActionButtonDocked = true, // FAB 按钮是否停靠在 BottomAppBar 上，如果是false 就不会产生上下叠层的效果
            drawerContent = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text("抽屉菜单项 1", modifier = Modifier.clickable {
                        coroutineScope.launch { scaffoldState.drawerState.close() } // 点击后关闭抽屉
                    }, color = Color.Yellow)
                    Spacer(modifier = Modifier.height(8.dp)) // 增加间距
                    Text("抽屉菜单项 2", modifier = Modifier.clickable {
                        coroutineScope.launch { scaffoldState.drawerState.close() } // 点击后关闭抽屉
                    })
                }
            },
            drawerGesturesEnabled = true, // 启用抽屉手势
            drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp), // 设置抽屉形状
            drawerElevation = 8.dp, // 设置抽屉的阴影高度
            drawerBackgroundColor = Color.Gray, // 设置抽屉的背景颜色
            //--------------这个目前不止到影响哪里
            drawerContentColor = Color.Blue, // 设置抽屉内内容的颜色
            drawerScrimColor = Color.Black.copy(alpha = 0.32f), // 抽屉背景遮罩颜色
            backgroundColor = Color.LightGray, // 设置 Scaffold 背景颜色
            contentColor = Color.Black, // 设置 Scaffold 内容颜色
            snackbarHost = {
                SnackbarHost(it) // 设置 Snackbar 宿主
            },
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues), // 填充整个屏幕并应用内边距
                    contentAlignment = Alignment.Center // 内容居中
                ) {
                    Text("主内容区域") // 显示主内容文本
                }
            }
        )
    }


    /**
     * 但是这个是没有问题的
     */
    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun test2() {
        Scaffold(
            topBar = { TopAppBar(title = { Text("主页") }) },
            bottomBar = {
                BottomNavigation {
                    Text("底部导航", modifier = Modifier.padding(16.dp))
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)  // ✅ 自动避开 TopAppBar 和 BottomNavigation
            ) {
                items(50) { index ->
                    Text("列表项 $index", fontSize = 20.sp, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }

    /**
     * 放在非文字滑不动，不然 LazyColumn(modifier = Modifier.fillMaxSize()) { // 假设内容很多
     * 就会存在其他的问题
     */
    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun test1() {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = { Text("主页") }) // 这里没有自动处理 padding
            //                LazyColumn(modifier = Modifier.fillMaxSize()) { // 假设内容很多
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ) { // 假设内容很多
                items(50) { index ->
                    Text("列表项 $index", fontSize = 20.sp, modifier = Modifier.padding(16.dp))
                }
            }
            BottomNavigation {
                Text(
                    "底部导航",
                    modifier = Modifier.padding(16.dp)
                )/* 底部导航 */
            }
        }
    }
}
