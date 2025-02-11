# Jetpack Compose 集成 IJKPlayer 并管理生命周期

在 Jetpack Compose 中，`@Composable` 函数本身**没有生命周期**，但你仍然可以使用 `AndroidView` 嵌入 Java/Kotlin 版本的 IJKPlayer，并利用 `LifecycleObserver` 来管理播放器的生命周期，例如**在界面可见时开始播放，不可见时暂停或释放资源**。

## 1. 依赖 & 权限
### (1) 添加 IJKPlayer 依赖
在 `build.gradle` 添加：
```gradle
dependencies {
    implementation 'tv.danmaku.ijk.media:ijkplayer-java:0.8.8'
    implementation 'tv.danmaku.ijk.media:ijkplayer-armv7a:0.8.8'
}
```
### (2) AndroidManifest.xml 申请网络权限
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## 2. Jetpack Compose 中使用 IJKPlayer
### (1) 使用 `AndroidView` 组件
```kotlin
@Composable
fun IjkPlayerView(videoUrl: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            IjkVideoView(ctx).apply {
                setVideoPath(videoUrl)
                keepScreenOn = true
                setOnPreparedListener { start() }
            }
        },
        update = { videoView ->
            videoView.setVideoPath(videoUrl)
            videoView.requestFocus()
        },
        modifier = modifier
    )

    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                Log.d("IjkPlayerView", "Resuming video playback")
            }
            override fun onPause(owner: LifecycleOwner) {
                Log.d("IjkPlayerView", "Pausing video playback")
            }
            override fun onDestroy(owner: LifecycleOwner) {
                Log.d("IjkPlayerView", "Releasing player")
            }
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }
}
```

## 3. 处理生命周期：自动暂停 & 释放资源
```kotlin
@Composable
fun IjkPlayerView(videoUrl: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val player = remember { IjkMediaPlayer() } // 记住播放器实例

    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                player.start()
            }
            override fun onPause(owner: LifecycleOwner) {
                player.pause()
            }
            override fun onDestroy(owner: LifecycleOwner) {
                player.release()
            }
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            player.release()
        }
    }
}
```

## 4. 结论
✅ **Compose 如何集成 IJKPlayer**
- 使用 `AndroidView` 让 `IjkVideoView` 适配 Compose。
- 监听 `Lifecycle`，在 `onResume()` 播放、`onPause()` 暂停、`onDestroy()` 释放资源。
- 通过 `DisposableEffect` 确保 `LifecycleObserver` 正确解绑，防止内存泄漏。

✅ **最终效果**
- **用户进入页面时自动播放**。
- **用户切换界面时暂停播放**。
- **用户退出页面时释放播放器，防止资源泄漏**。

这样，在 Compose 里就可以**优雅地**管理 IJKPlayer 了 🚀。
