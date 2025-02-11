# Jetpack Compose 组件的组织结构

在 Jetpack Compose 里，所有 UI 组件都是 `@Composable` 函数，因此合理地**拆分、分类和组织**这些函数非常重要，否则代码容易变得混乱、难以维护。

## 1. 组织方式
通常，我们可以按照**功能**和**组件类型**来组织 `@Composable` 函数，一般分为以下几类：

| 类型               | 作用 |
|------------------|------------------------------------------------------|
| **Screen 级别**   | 顶级 UI 组件，代表整个页面，例如 `HomeScreen()` |
| **Section 级别** | 页面的一部分，如 `ProfileHeader()`、`ArticleList()` |
| **独立组件级别** | 可复用的小组件，如 `CustomButton()`、`AvatarImage()` |
| **Modifier 扩展** | 复用的修饰符扩展函数，如 `fun Modifier.cardStyle()` |
| **State 处理**   | 处理 UI 逻辑的状态，如 `viewModel`、`remember` |

## 2. 代码拆分示例
### (1) Screen 级别
```kotlin
@Composable
fun HomeScreen() {
    Column {
        ProfileHeader()
        ArticleList()
    }
}
```

### (2) Section 级别
```kotlin
@Composable
fun ProfileHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AvatarImage()
        Text("Welcome, User!", fontSize = 20.sp)
    }
}
```

### (3) 复用组件
```kotlin
@Composable
fun AvatarImage(imageUrl: String = "https://example.com/avatar.jpg") {
    Image(
        painter = rememberAsyncImagePainter(imageUrl),
        contentDescription = "Avatar",
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape)
    )
}
```

### (4) Modifier 扩展
```kotlin
fun Modifier.cardStyle(): Modifier {
    return this
        .padding(8.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(Color.White)
        .shadow(4.dp)
}
```

### (5) 处理状态逻辑
```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val articles by viewModel.articles.collectAsState()

    Column {
        ProfileHeader()
        ArticleList(articles)
    }
}
```

## 3. 目录结构推荐
```
/com/example/app
 ├── ui/
 │   ├── screen/
 │   │   ├── HomeScreen.kt
 │   ├── components/
 │   │   ├── ProfileHeader.kt
 │   │   ├── AvatarImage.kt
 │   ├── modifiers/
 │   │   ├── Modifiers.kt
 ├── viewmodel/
 │   ├── HomeViewModel.kt
 ├── model/
 │   ├── Article.kt
```

## 4. 总结
✅ **Screen 级别**：负责整个页面，直接与 `ViewModel` 交互。  
✅ **Section 级别**：组织 UI 结构，拆分为多个部分。  
✅ **复用组件**：独立小组件，避免重复代码。  
✅ **Modifier 扩展**：提取通用 `Modifier`，保持代码整洁。  
✅ **状态管理**：尽量在 `Screen` 层处理 `ViewModel`，通过参数传递数据。  

这样可以保证**代码结构清晰，组件易于复用，状态管理合理**。 🚀
