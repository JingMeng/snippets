Jetpack Compose 裁剪 Image（上方圆角，下方直角）

目标
使用 clip() 让 Image 上面两个角是 8dp 圆角，下面是直角。

解决方案
使用 RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 0.dp, bottomEnd = 0.dp)：

```kotlin
Image(
    painter = painterResource(id = R.drawable.sample_image),
    contentDescription = "Rounded Corner Image",
    modifier = Modifier
        .size(100.dp)
        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 0.dp, bottomEnd = 0.dp)) // 仅上方圆角
        .background(Color.Gray) // 防止透明区域
)
```

关键点
- RoundedCornerShape 允许分别控制 4 个角的圆角大小。
- topStart = 8.dp, topEnd = 8.dp 上面两个角是 8dp 圆角。
- bottomStart = 0.dp, bottomEnd = 0.dp 下面两个角保持直角。
- .clip() 必须放在 .background() 之前，否则背景色会溢出原来的直角区域。

结果
该 Image 上方两个角是 8dp 圆角，下方两个角保持直角，可用于 UI 设计中的卡片封面、头像裁剪等场景。
