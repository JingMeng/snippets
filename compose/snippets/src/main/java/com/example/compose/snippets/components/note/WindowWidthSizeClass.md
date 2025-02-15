
# 使用 `WindowWidthSizeClass` 判断屏幕宽度类别

在 Jetpack Compose 中，`WindowWidthSizeClass` 是一个枚举类型，用于表示当前屏幕的宽度类别，通常用于根据屏幕尺寸动态调整布局。

## `WindowWidthSizeClass` 枚举

`WindowWidthSizeClass` 包含以下三种值：

- **`Compact`**：适用于较小屏幕设备，例如手机。
- **`Medium`**：适用于中等宽度的屏幕，通常是较大的手机或小型平板。
- **`Expanded`**：适用于宽屏设备，例如平板、桌面或大屏设备。

## 示例代码：判断当前屏幕宽度

```kotlin
val isExpandedScreen = widthSizeClass == WindowWidthSizeClass.Expanded
```

这行代码用于判断当前屏幕的宽度是否为 **Expanded** 类型。如果是宽屏设备（如平板、桌面），`isExpandedScreen` 的值将为 `true`，否则为 `false`。

## 使用 `isExpandedScreen` 来调整布局

可以通过判断 `isExpandedScreen` 来动态调整界面布局。比如，在宽屏设备上显示侧边栏，而在小屏设备上隐藏它。

### 示例：根据屏幕宽度显示或隐藏侧边栏

```kotlin
val isExpandedScreen = widthSizeClass == WindowWidthSizeClass.Expanded

if (isExpandedScreen) {
    // 显示侧边栏
    SideBar()
} else {
    // 隐藏侧边栏或显示一个按钮来控制侧边栏
    // 例如：可以显示一个 Drawer 或者按钮来打开侧边栏
}
```

### 使用场景
- **手机屏幕**：单栏布局，简单的垂直布局。
- **平板或宽屏设备**：双栏布局或复杂的界面，可以容纳更多内容。

## 总结

通过 `WindowWidthSizeClass` 和 `isExpandedScreen` 的判断，可以根据屏幕宽度动态调整应用的界面布局，提升用户体验，确保在不同屏幕大小的设备上都能获得合适的展示效果。
