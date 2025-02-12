# Jetpack Compose 权限请求并跳转

在 Jetpack Compose 中，权限请求成功后可以使用 `NavController` 进行页面跳转。以下是完整的实现示例。

---

## **1. 添加依赖**
在 `build.gradle` 中添加：
```gradle
dependencies {
    implementation "com.google.accompanist:accompanist-permissions:0.30.1"
    implementation "androidx.navigation:navigation-compose:2.7.5"
}
```

---

## **2. 设置 `NavController`**
在 `MainActivity` 中创建 `NavHost`，用于管理页面导航：
```kotlin
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "request_permission") {
        composable("request_permission") { RequestCameraPermissionScreen(navController) }
        composable("camera_screen") { CameraScreen() }
    }
}
```

---

## **3. 创建权限请求界面**
如果用户**同意权限**，就**跳转到相机界面**：
```kotlin
@Composable
fun RequestCameraPermissionScreen(navController: NavController) {
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Camera permission is ${if (permissionState.status.isGranted) "granted" else "denied"}")

        Button(onClick = { permissionState.launchPermissionRequest() }) {
            Text("Request Camera Permission")
        }

        // 监听权限状态，成功后自动跳转
        LaunchedEffect(permissionState.status) {
            if (permissionState.status.isGranted) {
                navController.navigate("camera_screen")
            }
        }
    }
}
```

---

## **4. 创建相机界面**
当权限被授予后，用户将被导航到 `CameraScreen`：
```kotlin
@Composable
fun CameraScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text("Camera is ready!", color = Color.White, fontSize = 24.sp)
    }
}
```

---

## **5. 运行逻辑**
1. 进入 `RequestCameraPermissionScreen`，如果**没有权限**，用户需要手动点击按钮请求权限。  
2. **如果用户授予权限**，`LaunchedEffect` 监听状态变化，调用 `navController.navigate("camera_screen")` 进入 `CameraScreen`。  
3. **如果用户拒绝权限**，则停留在当前页面，用户可以再次请求。  

---

## **6. 结论**
✅ **请求权限后自动跳转**到相机界面。  
✅ **使用 `NavController` 进行页面导航**，结构清晰。  
✅ **实时监听权限状态**，用户不需要手动点击进入下一个页面。  

这个方案适用于所有需要**权限后自动跳转**的场景，比如相机、存储、位置信息等 🚀。
