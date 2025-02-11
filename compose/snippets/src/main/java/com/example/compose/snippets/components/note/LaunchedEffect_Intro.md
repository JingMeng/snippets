# LaunchedEffect 介绍

`LaunchedEffect` 是 Jetpack Compose 中的一个 **可组合（Composable）副作用 API**，用于在 **Composable 进入 Composition** 时运行某些代码，并在 **键值（key）发生变化** 或 **Composable 退出 Composition** 时自动取消或重新执行代码。

---

## **📌 作用**
1. **执行一次性任务**（如网络请求、数据加载、动画启动等）。
2. **监听状态变化并触发操作**（如用户输入、导航变化等）。
3. **自动管理协程**，避免手动处理生命周期。

---

## **🔹 基本用法**
```kotlin
@Composable
fun MyComposable(name: String) {
    LaunchedEffect(name) {
        // 这里的代码会在 `name` 变化时重新执行
        println("LaunchedEffect 执行: $name")
    }
}
```
✅ **特点：**
- `LaunchedEffect(name)` 会在 `name` **发生变化时重新执行**。
- 如果 `name` 没有变化，`LaunchedEffect` **不会重新执行**。
- `LaunchedEffect` 内部使用 **`rememberCoroutineScope().launch {}`**，自动取消旧协程，防止重复执行。

---

## **🔹 典型使用场景**
### **1️⃣ 组件初始化时执行一次**
```kotlin
@Composable
fun MyComposable() {
    LaunchedEffect(Unit) {  // Unit 作为 key，表示仅执行一次
        println("只执行一次，例如初始化数据")
    }
}
```
📌 **适用于：**
- 组件首次渲染时执行 **初始化逻辑**，如加载数据、注册监听器等。

---

### **2️⃣ 监听某个状态变化**
```kotlin
@Composable
fun MyComposable(counter: Int) {
    LaunchedEffect(counter) {
        println("counter 变了: $counter")
    }
}
```
📌 **适用于：**
- 当 `counter` **变化** 时，触发特定逻辑（比如请求数据、动画）。

---

### **3️⃣ 处理协程任务**
```kotlin
@Composable
fun MyComposable() {
    LaunchedEffect(Unit) {
        delay(2000)  // 2 秒后执行
        println("2 秒后执行的任务")
    }
}
```
📌 **适用于：**
- **定时任务**（比如延迟 2 秒后执行某个操作）。
- 轻量级的异步任务（比如网络请求）。

---

### **4️⃣ 监听 `ViewModel` 的事件**
```kotlin
@Composable
fun MyComposable(viewModel: MyViewModel) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state) {
        println("ViewModel 的 state 变了: $state")
    }
}
```
📌 **适用于：**
- 当 `ViewModel` 中的 `state` **变化时执行操作**。

---

## **🔹 `LaunchedEffect` VS `rememberCoroutineScope`**
| API | 作用 | 何时使用 |
|------|------|----------|
| **`LaunchedEffect`** | 绑定生命周期，自动取消 | **适用于** 监听 `state` 变化、初始化任务 |
| **`rememberCoroutineScope`** | 需要手动管理协程 | **适用于** 事件触发（如 `Button` 点击后启动协程） |

**示例对比：**
```kotlin
@Composable
fun MyComposable() {
    val scope = rememberCoroutineScope()

    Button(onClick = {
        scope.launch { // 需要手动管理
            println("按钮点击后执行的任务")
        }
    }) {
        Text("点击")
    }
}
```
📌 **区别：**
- `LaunchedEffect` 适用于 **自动执行任务**（例如监听 `state` 变化）。
- `rememberCoroutineScope` 适用于 **用户交互触发的任务**（例如 `Button` 点击）。

---

## **🔹 `LaunchedEffect` VS `SideEffect` VS `DisposableEffect`**
| API | 用途 | 何时执行 | 何时取消 |
|------|------|--------|--------|
| **LaunchedEffect** | 执行副作用任务 | `key` 变化时重新执行 | 组件销毁或 `key` 变化时 |
| **SideEffect** | UI 线程中执行轻量级任务 | 任何重组时 | 立即执行，无需取消 |
| **DisposableEffect** | 注册 & 取消监听器 | `key` 变化时重新执行 | 组件销毁时取消资源 |

---

## **🚀 总结**
- `LaunchedEffect` **适合执行异步任务**（如网络请求、动画、监听 `state`）。
- **会在 `key` 变化时重新执行**，并在 **Composable 销毁时自动取消**。
- **比 `rememberCoroutineScope().launch {}` 更安全**，不需要手动管理协程。

**💡 适用场景：**
✅ **初始化数据**
✅ **监听状态变化**
✅ **执行异步任务**
✅ **防止协程泄漏**

如果你的任务**需要手动控制**，可以用 `rememberCoroutineScope`，否则 `LaunchedEffect` 是更安全的选择！ 🚀
