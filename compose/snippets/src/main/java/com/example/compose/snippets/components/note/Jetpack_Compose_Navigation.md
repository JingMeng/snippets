
# Jetpack Compose 路由和页面跳转

在 Jetpack Compose 中，页面跳转和路由管理可以通过 Navigation Compose 来实现。Navigation Compose 是专门为 Jetpack Compose 提供的导航库，它简化了页面跳转的操作，并且与传统的 Fragment 或 Activity 跳转有一些不同的方式。

## Navigation Compose 概念

导航图（NavHost） 是一个存储不同页面（或 Composables）和页面间跳转逻辑的容器。通过 NavController 来控制页面跳转和传递参数。

## 1. 设置 Navigation Compose

### 添加依赖

首先，你需要在 `build.gradle` 文件中添加导航的依赖：

```
dependencies {
    implementation "androidx.navigation:navigation-compose:2.6.0"  // 适配 Compose 的导航库
}
```

### 基本导航结构

1. **创建 NavController 和 NavHost**
   - `NavController`：管理导航的核心对象。
   - `NavHost`：用于声明不同页面的跳转关系。

2. **定义页面 Composable**  
   每个页面都是一个 Composable 函数，并在 `NavHost` 中注册。

### 简单示例

```
@Composable
fun AppNavHost() {
    // 创建 NavController 用于管理导航
    val navController = rememberNavController()

    // 设置 NavHost 并定义导航规则
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController)  // 第一个页面
        }
        composable("details/{itemId}") { backStackEntry ->
            // 获取传递的参数
            val itemId = backStackEntry.arguments?.getString("itemId")
            DetailsScreen(itemId)  // 第二个页面
        }
    }
}
```

## 2. 页面跳转

在 Compose 中，页面跳转可以通过 `NavController` 来完成。

### 使用 NavController.navigate() 跳转

```
@Composable
fun HomeScreen(navController: NavController) {
    Column {
        Button(onClick = {
            // 跳转到另一个页面，并传递参数
            navController.navigate("details/123")
        }) {
            Text("Go to Details")
        }
    }
}

@Composable
fun DetailsScreen(itemId: String?) {
    Text("Item ID: $itemId")
}
```

在上面的代码中，点击按钮会跳转到 `details/{itemId}` 页面，`itemId` 是通过路由参数传递的。

### 命名参数传递

如果需要在跳转时传递参数，可以在路由路径中定义参数：

```
composable("details/{itemId}") { backStackEntry ->
    val itemId = backStackEntry.arguments?.getString("itemId")
    DetailsScreen(itemId)
}
```

## 3. 返回操作（Back Stack）

返回操作通过 `NavController.popBackStack()` 实现。当用户按下返回按钮或调用该方法时，Compose 会将当前页面从导航堆栈中移除，并显示上一个页面。

### 手动控制返回

```
navController.popBackStack()  // 返回到上一个页面
```

### 默认的返回按钮

在默认情况下，Android 系统的返回按钮会调用 NavController 来处理返回操作，无需手动处理。你可以通过 BackHandler 自定义返回按钮的行为。

```
BackHandler {
    navController.popBackStack()
}
```

## 4. 路由参数和类型转换

如果需要传递更复杂的参数（例如整数、布尔值等），可以使用类型安全的方式进行转换。

### 传递基本类型参数

```
composable("details/{itemId}") { backStackEntry ->
    val itemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull()
    DetailsScreen(itemId)
}
```

### 使用 NavType 处理类型转换

```
import androidx.navigation.navArgument
import androidx.navigation.NavType

NavHost(navController = navController, startDestination = "home") {
    composable(
        "details/{itemId}",
        arguments = listOf(navArgument("itemId") { type = NavType.IntType })
    ) { backStackEntry ->
        val itemId = backStackEntry.arguments?.getInt("itemId")
        DetailsScreen(itemId)
    }
}
```

在这个例子中，`itemId` 被强制转换为 `Int` 类型，并且你可以通过 `NavType.IntType` 明确指定该参数的类型。

## 5. 可选的高级特性

### 嵌套路由（Nested Navigation）

如果有嵌套的界面结构，你可以在 `NavHost` 中嵌套 `NavHost` 来管理子页面的导航。

```
NavHost(navController = navController, startDestination = "home") {
    composable("home") {
        HomeScreen(navController)
    }
    navigation(startDestination = "nestedStart", route = "nested") {
        composable("nestedStart") {
            NestedStartScreen(navController)
        }
        composable("nestedEnd") {
            NestedEndScreen(navController)
        }
    }
}
```

### 完整示例

```
@Composable
fun MyNavGraph(navController: NavController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController)
        }
        // 嵌套路由
        navigation(startDestination = "nestedStart", route = "nested") {
            composable("nestedStart") {
                NestedStartScreen(navController)
            }
            composable("nestedEnd") {
                NestedEndScreen(navController)
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Column {
        Text("This is the Home Screen")
        Button(onClick = { navController.navigate("nested/nestedStart") }) {
            Text("Go to Nested Navigation")
        }
    }
}

@Composable
fun NestedStartScreen(navController: NavController) {
    Column {
        Text("This is the Nested Start Screen")
        Button(onClick = { navController.navigate("nested/nestedEnd") }) {
            Text("Go to Nested End")
        }
    }
}

@Composable
fun NestedEndScreen(navController: NavController) {
    Text("This is the Nested End Screen")
}
```

### 重点：
1. **主导航**（`home`）和**嵌套导航**（`nested`）之间的跳转是通过 `navController.navigate("nested/nestedStart")` 来触发的。
2. 嵌套的页面 `"nestedStart"` 和 `"nestedEnd"` 是在 `navigation` 内部定义的。
3. 这样做之后，嵌套的导航才会生效，用户从 `HomeScreen` 可以通过点击按钮进入嵌套的页面。



### 条件导航（Conditional Navigation）

你可以在某些情况下，根据条件来决定跳转路径：

```
if (isLoggedIn) {
    navController.navigate("home")
} else {
    navController.navigate("login")
}
```

## 总结：

- NavHost 用于设置导航图，管理页面跳转。
- NavController 是控制页面跳转和堆栈操作的核心。
- 通过 navigate() 方法进行页面跳转，传递参数。
- 页面之间可以通过路由参数传递数据，并且可以使用 NavType 安全地转换参数类型。
- 返回操作 会自动通过系统的返回按钮处理，也可以手动控制。
- 支持嵌套路由和条件导航。
