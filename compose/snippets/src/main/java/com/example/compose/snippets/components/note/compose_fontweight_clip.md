# Jetpack Compose `FontWeight` & `.clip(CircleShape)` 详解

---

## **1. FontWeight 是什么？**
在 Jetpack Compose 中，`FontWeight` 代表**字体的粗细**，对应 **View 体系中的 `android:textStyle` 和 `android:fontWeight`**。  

### **FontWeight 常见值**
```kotlin
FontWeight.Thin       // 100
FontWeight.ExtraLight // 200
FontWeight.Light      // 300
FontWeight.Normal     // 400（默认）
FontWeight.Medium     // 500
FontWeight.SemiBold   // 600
FontWeight.Bold       // 700
FontWeight.ExtraBold  // 800
FontWeight.Black      // 900
```

### **📌 在 Compose 中使用 `FontWeight`**
```kotlin
Text(
    text = "Hello Compose",
    fontWeight = FontWeight.Bold
)
```

### **FontWeight 对应 XML 配置**
| **Compose (FontWeight)** | **View XML (textStyle/fontWeight)** |
|----------------|------------------------|
| `FontWeight.Normal` (400) | `android:textStyle="normal"` |
| `FontWeight.Bold` (700) | `android:textStyle="bold"` |
| `FontWeight.Thin` (100) | `android:fontWeight="100"` |
| `FontWeight.Light` (300) | `android:fontWeight="300"` |
| `FontWeight.Medium` (500) | `android:fontWeight="500"` |
| `FontWeight.SemiBold` (600) | `android:fontWeight="600"` |
| `FontWeight.ExtraBold` (800) | `android:fontWeight="800"` |

**⚠️ 注意**： 在 XML 里，`textStyle="bold"` 只支持 **Normal 和 Bold**，要使用更细/更粗的字体需要 `fontWeight`（API 26+）。

### **💡 结论**
- **FontWeight = 字体粗细**
- **默认 `FontWeight.Normal` (400)**
- **可选范围 `100`-`900`，类似 `fontWeight`**
- **在 XML 里用 `android:fontWeight` 控制**
- **比 `textStyle="bold"` 更灵活！** 🚀

---

## **2. `.clip(CircleShape)` 作用解析**
在 Jetpack Compose 中，`.clip(CircleShape)` **用于裁剪组件的形状**。  
在以下代码中，它将 `Box` **裁剪成圆形**。

```kotlin
Box(
    modifier = Modifier
        .size(50.dp)
        .clip(CircleShape) // 裁剪成圆形
        .background(Color.Blue) // 设置背景色
)
```

### **📌 逐步解析这段代码**
1. `size(50.dp)` - 设置 `Box` 为 **50x50** 的正方形。
2. `.clip(CircleShape)` - **将 `Box` 裁剪成圆形**。
3. `.background(Color.Blue)` - **填充蓝色背景**。

### **🎯 `.clip(CircleShape)` 的关键点**
| **特点** | **解释** |
|---------|---------|
| 作用 | **裁剪组件形状**，让 `Box` 变成 **圆形** |
| 配合 `.background()` | **必须先 `.clip()` 再 `.background()`**，保证背景不会超出圆形区域 |
| 适用组件 | **`Box`、`Image`、`Surface` 等任意 `Composable`** |

### **🌟 示例：对图片裁剪成圆形**
```kotlin
Image(
    painter = painterResource(id = R.drawable.profile_picture),
    contentDescription = "Profile Picture",
    modifier = Modifier
        .size(100.dp)
        .clip(CircleShape) // 让图片变成圆形
)
```
**效果**：  
✅ 让 `Image` 变成 **圆形头像**，而不会显示为默认的方形。

### **🚀 结论**
- **`.clip(CircleShape)` = 把组件裁剪成圆形** 🎯
- **常用于 `Box`、`Image` 做圆角或圆形 UI**
- **必须 `.clip()` 后再 `.background()`，否则背景会溢出**

💡 **总结**：`FontWeight` 控制文本粗细，`.clip(CircleShape)` 让组件变成圆形，在 Compose 中都非常实用！ 🚀
