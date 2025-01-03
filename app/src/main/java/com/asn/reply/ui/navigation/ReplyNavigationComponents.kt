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

package com.asn.reply.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.asn.reply.R
import com.asn.reply.ui.utils.ReplyNavigationContentPosition
import kotlinx.coroutines.launch

// 擴展函數，檢查螢幕大小是否為 Compact
private fun WindowSizeClass.isCompact() =
    windowWidthSizeClass == WindowWidthSizeClass.COMPACT ||
            windowHeightSizeClass == WindowHeightSizeClass.COMPACT

// 定義導航套件範圍，包含導航類型
class ReplyNavSuiteScope(
    val navSuiteType: NavigationSuiteType
)

// 回應型導覽包裹組件，根據當前的視窗大小和方向來決定導航佈局
@Composable
fun ReplyNavigationWrapper(
    currentDestination: NavDestination?,  // 當前目的地
    navigateToTopLevelDestination: (ReplyTopLevelDestination) -> Unit,  // 導航至頂層目的地的函數
    content: @Composable ReplyNavSuiteScope.() -> Unit  // 要顯示的內容
) {
    // 取得當前的視窗適應性資訊
    val adaptiveInfo = currentWindowAdaptiveInfo()
    // 取得當前視窗大小，轉換為 Dp 單位
    val windowSize = with(LocalDensity.current) {
        currentWindowSize().toSize().toDpSize()
    }

    // 根據視窗大小和適應性決定導航佈局類型
    val navLayoutType = when {
        adaptiveInfo.windowPosture.isTabletop -> NavigationSuiteType.NavigationBar // 如果是桌面姿勢，使用導航列
        adaptiveInfo.windowSizeClass.isCompact() -> NavigationSuiteType.NavigationBar // 如果是緊湊型，使用導航列
        adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED &&
                windowSize.width >= 1200.dp -> NavigationSuiteType.NavigationDrawer // 如果寬度大於1200dp，使用導航抽屜
        else -> NavigationSuiteType.NavigationRail // 否則使用導航軌
    }

    // 根據視窗高度決定導航內容顯示位置
    val navContentPosition = when (adaptiveInfo.windowSizeClass.windowHeightSizeClass) {
        WindowHeightSizeClass.COMPACT -> ReplyNavigationContentPosition.TOP // 如果是緊湊型，導航在頂部
        WindowHeightSizeClass.MEDIUM,
        WindowHeightSizeClass.EXPANDED -> ReplyNavigationContentPosition.CENTER // 如果是中型或擴展型，導航居中
        else -> ReplyNavigationContentPosition.TOP // 預設導航在頂部
    }

    // 記住抽屜的狀態
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // 記住協程範圍
    val coroutineScope = rememberCoroutineScope()

    // 設置是否啟用手勢，當抽屜開啟或使用導航軌時，手勢可用
    val gesturesEnabled =
        drawerState.isOpen || navLayoutType == NavigationSuiteType.NavigationRail

    // 配置返回按鈕處理，當抽屜開啟時，返回按鈕關閉抽屜
    BackHandler(enabled = drawerState.isOpen) {
        coroutineScope.launch {
            drawerState.close()
        }
    }

    // 構建Modal導航抽屜，根據條件顯示不同的導航內容
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        drawerContent = {
            ModalNavigationDrawerContent(
                currentDestination = currentDestination,
                navigationContentPosition = navContentPosition,
                navigateToTopLevelDestination = navigateToTopLevelDestination,
                onDrawerClicked = {
                    coroutineScope.launch {
                        drawerState.close() // 點擊抽屜時關閉
                    }
                }
            )
        },
    ) {
        // 根據導航佈局類型設置不同的導航組件
        NavigationSuiteScaffoldLayout(
            layoutType = navLayoutType,
            navigationSuite = {
                when (navLayoutType) {
                    NavigationSuiteType.NavigationBar -> ReplyBottomNavigationBar(
                        currentDestination = currentDestination,
                        navigateToTopLevelDestination = navigateToTopLevelDestination
                    )

                    NavigationSuiteType.NavigationRail -> ReplyNavigationRail(
                        currentDestination = currentDestination,
                        navigationContentPosition = navContentPosition,
                        navigateToTopLevelDestination = navigateToTopLevelDestination,
                        onDrawerClicked = {
                            coroutineScope.launch {
                                drawerState.open() // 點擊導航欄時開啟抽屜
                            }
                        }
                    )

                    NavigationSuiteType.NavigationDrawer -> PermanentNavigationDrawerContent(
                        currentDestination = currentDestination,
                        navigationContentPosition = navContentPosition,
                        navigateToTopLevelDestination = navigateToTopLevelDestination
                    )
                }
            }
        ) {
            // 在導航佈局內顯示內容
            ReplyNavSuiteScope(navLayoutType).content()
        }
    }
}

// 回應型導航軌組件，顯示導航項目和選擇的圖標
@Composable
fun ReplyNavigationRail(
    currentDestination: NavDestination?,
    navigationContentPosition: ReplyNavigationContentPosition,
    navigateToTopLevelDestination: (ReplyTopLevelDestination) -> Unit,
    onDrawerClicked: () -> Unit = {}, // 點擊抽屜的動作
) {
    NavigationRail(
        modifier = Modifier.fillMaxHeight(), // 填滿垂直高度
        containerColor = MaterialTheme.colorScheme.inverseOnSurface // 設置容器顏色
    ) {
        // 顯示導航軌的標題區域
        Column(
            modifier = Modifier.layoutId(LayoutType.HEADER),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp) // 設置間距
        ) {
            // 導航欄菜單
            NavigationRailItem(
                selected = false,
                onClick = onDrawerClicked, // 點擊時開啟抽屜
                icon = {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = stringResource(id = R.string.navigation_drawer) // 設置圖標描述
                    )
                }
            )
            // 浮動操作按鈕
            FloatingActionButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp), // 設置按鈕的內外邊距
                containerColor = MaterialTheme.colorScheme.tertiaryContainer, // 設置顏色
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer // 設置圖標顏色
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(id = R.string.compose),
                    modifier = Modifier.size(18.dp) // 設置圖標大小
                )
            }
            Spacer(Modifier.height(8.dp)) // 填充間隔
            Spacer(Modifier.height(4.dp)) // 填充間隔
        }

        // 顯示導航項目
        Column(
            modifier = Modifier.layoutId(LayoutType.CONTENT),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp) // 設置項目間距
        ) {
            // 顯示頂層導航項目
            TOP_LEVEL_DESTINATIONS.forEach { replyDestination ->
                NavigationRailItem(
                    selected = currentDestination.hasRoute(replyDestination), // 根據是否選擇該目的地來設置選中狀態
                    onClick = { navigateToTopLevelDestination(replyDestination) }, // 點擊導航項目
                    icon = {
                        Icon(
                            imageVector = replyDestination.selectedIcon,
                            contentDescription = stringResource(id = replyDestination.iconTextId) // 設置圖標描述
                        )
                    }
                )
            }
        }
    }
}


@Composable
fun ReplyBottomNavigationBar(
    currentDestination: NavDestination?,  // 當前導航目的地
    navigateToTopLevelDestination: (ReplyTopLevelDestination) -> Unit  // 導航到頂級目的地的函式
) {
    // 使用 NavigationBar 元件來顯示底部導航條，並且設定它的寬度為最大值
    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        // 遍歷 TOP_LEVEL_DESTINATIONS 列表並顯示每個導航項目
        TOP_LEVEL_DESTINATIONS.forEach { replyDestination ->
            // 根據當前的目的地決定該導航項目是否被選中
            NavigationBarItem(
                selected = currentDestination.hasRoute(replyDestination),  // 檢查當前目的地是否匹配導航目的地
                onClick = { navigateToTopLevelDestination(replyDestination) },  // 點擊後導航到對應目的地
                icon = {
                    // 顯示每個導航項目的圖標
                    Icon(
                        imageVector = replyDestination.selectedIcon,
                        contentDescription = stringResource(id = replyDestination.iconTextId)  // 提供圖標的文字描述
                    )
                }
            )
        }
    }
}


@Composable
fun PermanentNavigationDrawerContent(
    currentDestination: NavDestination?,  // 當前導航目的地
    navigationContentPosition: ReplyNavigationContentPosition,  // 導航內容位置
    navigateToTopLevelDestination: (ReplyTopLevelDestination) -> Unit,  // 導航到頂級目的地的函式
) {
    // 創建永久導航抽屜，並設置最大和最小寬度範圍
    PermanentDrawerSheet(
        modifier = Modifier.sizeIn(minWidth = 200.dp, maxWidth = 300.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,  // 設置抽屜的背景顏色
    ) {
        // TODO: 移除自定義的導航抽屜內容定位，當 NavDrawer 元件支持該功能時
        Layout(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)  // 設置背景顏色
                .padding(16.dp),
            content = {
                // 導航抽屜的標頭部分
                Column(
                    modifier = Modifier.layoutId(LayoutType.HEADER),  // 使用佈局ID來定位元素
                    horizontalAlignment = Alignment.Start,  // 項目對齊方式
                    verticalArrangement = Arrangement.spacedBy(4.dp)  // 項目之間的間距
                ) {
                    // 顯示應用名稱
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = stringResource(id = R.string.app_name).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // 顯示一個擴展的浮動操作按鈕
                    ExtendedFloatingActionButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .fillMaxWidth()  // 按鈕填滿寬度
                            .padding(top = 8.dp, bottom = 40.dp),
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ) {
                        // 按鈕的圖標
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(id = R.string.compose),
                            modifier = Modifier.size(24.dp)
                        )
                        // 按鈕的文字
                        Text(
                            text = stringResource(id = R.string.compose),
                            modifier = Modifier.weight(1f),  // 使文字在按鈕中間顯示
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // 顯示導航項目列表
                Column(
                    modifier = Modifier
                        .layoutId(LayoutType.CONTENT)  // 使用佈局ID來定位內容
                        .verticalScroll(rememberScrollState()),  // 使內容可滾動
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // 遍歷 TOP_LEVEL_DESTINATIONS 列表顯示導航項目
                    TOP_LEVEL_DESTINATIONS.forEach { replyDestination ->
                        // 顯示每個導航項目
                        NavigationDrawerItem(
                            selected = currentDestination.hasRoute(replyDestination),  // 根據當前目的地檢查是否選中
                            label = {
                                Text(
                                    text = stringResource(id = replyDestination.iconTextId),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = replyDestination.selectedIcon,
                                    contentDescription = stringResource(
                                        id = replyDestination.iconTextId
                                    )
                                )
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = Color.Transparent  // 當項目未選中時設置背景為透明
                            ),
                            onClick = { navigateToTopLevelDestination(replyDestination) }  // 點擊後導航到對應目的地
                        )
                    }
                }
            },
            measurePolicy = navigationMeasurePolicy(navigationContentPosition)  // 設置佈局測量策略
        )
    }
}


@Composable
fun ModalNavigationDrawerContent(
    currentDestination: NavDestination?,  // 當前導航目的地
    navigationContentPosition: ReplyNavigationContentPosition,  // 導航內容位置（例如居中或頂部）
    navigateToTopLevelDestination: (ReplyTopLevelDestination) -> Unit,  // 用於導航到頂級目的地的函式
    onDrawerClicked: () -> Unit = {}  // 處理抽屜關閉事件的函式
) {
    // 使用 ModalDrawerSheet 創建一個模態導航抽屜
    ModalDrawerSheet {
        // TODO: 移除自定義的導航抽屜內容定位，當 NavDrawer 元件支持此功能時
        Layout(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.inverseOnSurface)  // 設置抽屜背景顏色
                .padding(16.dp),
            content = {
                // 標頭部分的內容，顯示應用名稱和抽屜關閉按鈕
                Column(
                    modifier = Modifier.layoutId(LayoutType.HEADER),  // 使用佈局ID定位標頭
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()  // 使行佔滿寬度
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,  // 應用間距
                        verticalAlignment = Alignment.CenterVertically  // 垂直對齊
                    ) {
                        // 顯示應用名稱
                        Text(
                            text = stringResource(id = R.string.app_name).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        // 顯示關閉抽屜的按鈕
                        IconButton(onClick = onDrawerClicked) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuOpen,
                                contentDescription = stringResource(id = R.string.close_drawer)  // 按鈕文字描述
                            )
                        }
                    }

                    // 擴展浮動操作按鈕，這裡的按鈕執行自定義操作
                    ExtendedFloatingActionButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .fillMaxWidth()  // 按鈕佔滿寬度
                            .padding(top = 8.dp, bottom = 40.dp),
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ) {
                        // 按鈕的圖標
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(id = R.string.compose),
                            modifier = Modifier.size(18.dp)
                        )
                        // 按鈕中的文字
                        Text(
                            text = stringResource(id = R.string.compose),
                            modifier = Modifier.weight(1f),  // 使文字在按鈕中間顯示
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // 顯示導航項目列表，並根據當前目的地顯示是否選中
                Column(
                    modifier = Modifier
                        .layoutId(LayoutType.CONTENT)  // 使用佈局ID定位內容區域
                        .verticalScroll(rememberScrollState()),  // 使內容可滾動
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // 遍歷 `TOP_LEVEL_DESTINATIONS` 顯示每個導航項目
                    TOP_LEVEL_DESTINATIONS.forEach { replyDestination ->
                        // 顯示每個導航項目
                        NavigationDrawerItem(
                            selected = currentDestination.hasRoute(replyDestination),  // 檢查是否選中
                            label = {
                                Text(
                                    text = stringResource(id = replyDestination.iconTextId),
                                    modifier = Modifier.padding(horizontal = 16.dp)  // 項目文字的間距
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = replyDestination.selectedIcon,
                                    contentDescription = stringResource(id = replyDestination.iconTextId)
                                )
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = Color.Transparent  // 當項目未選中時設置背景為透明
                            ),
                            onClick = { navigateToTopLevelDestination(replyDestination) }  // 點擊後導航到對應目的地
                        )
                    }
                }
            },
            measurePolicy = navigationMeasurePolicy(navigationContentPosition)  // 使用自定義的佈局測量策略
        )
    }
}


fun navigationMeasurePolicy(
    navigationContentPosition: ReplyNavigationContentPosition,  // 定義導航內容的顯示位置
): MeasurePolicy {
    return MeasurePolicy { measurables, constraints ->
        // 初始化標頭和內容的可測量元素
        lateinit var headerMeasurable: Measurable
        lateinit var contentMeasurable: Measurable
        // 遍歷可測量元素並根據佈局ID找到標頭和內容
        measurables.forEach {
            when (it.layoutId) {
                LayoutType.HEADER -> headerMeasurable = it  // 標頭
                LayoutType.CONTENT -> contentMeasurable = it  // 內容
                else -> error("Unknown layoutId encountered!")  // 錯誤處理
            }
        }

        // 測量標頭和內容的大小
        val headerPlaceable = headerMeasurable.measure(constraints)
        val contentPlaceable = contentMeasurable.measure(
            constraints.offset(vertical = -headerPlaceable.height)  // 對內容進行垂直位移以避免與標頭重疊
        )
        // 返回最終的佈局
        layout(constraints.maxWidth, constraints.maxHeight) {
            // 放置標頭部分
            headerPlaceable.placeRelative(0, 0)

            // 計算剩餘空間並根據位置決定內容的位置
            val nonContentVerticalSpace = constraints.maxHeight - contentPlaceable.height

            val contentPlaceableY = when (navigationContentPosition) {
                // 根據導航內容位置放置內容
                ReplyNavigationContentPosition.TOP -> 0
                ReplyNavigationContentPosition.CENTER -> nonContentVerticalSpace / 2
            }
                .coerceAtLeast(headerPlaceable.height)  // 保證內容不會與標頭重疊

            // 放置內容部分
            contentPlaceable.placeRelative(0, contentPlaceableY)
        }
    }
}


enum class LayoutType {
    HEADER, CONTENT
}

fun NavDestination?.hasRoute(destination: ReplyTopLevelDestination): Boolean =
    this?.hasRoute(destination.route::class) ?: false

@Preview(showBackground = true)
@Composable
fun PreviewReplyNavigationWrapper() {
    val currentDestination = null // 假設沒有當前選中的目的地
    val navigateToTopLevelDestination: (ReplyTopLevelDestination) -> Unit = {}

    ReplyNavigationWrapper(
        currentDestination = currentDestination,
        navigateToTopLevelDestination = navigateToTopLevelDestination
    ) {
        // 這裡是您想要展示的內容
        // 示例：可以放置您想要顯示在導航區域的組件
        Text(text = "Hello, Navigation!")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReplyNavigationRail() {
    val currentDestination = null // 假設沒有當前選中的目的地
    val navigationContentPosition = ReplyNavigationContentPosition.TOP // 假設位置為 TOP
    val navigateToTopLevelDestination: (ReplyTopLevelDestination) -> Unit = {}
    val onDrawerClicked: () -> Unit = {}

    ReplyNavigationRail(
        currentDestination = currentDestination,
        navigationContentPosition = navigationContentPosition,
        navigateToTopLevelDestination = navigateToTopLevelDestination,
        onDrawerClicked = onDrawerClicked
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewReplyBottomNavigationBar() {
    val currentDestination = null // 假設沒有當前選中的目的地
    // 假設的 navigateToTopLevelDestination 函數
    val navigateToTopLevelDestination: (ReplyTopLevelDestination) -> Unit = {}

    ReplyBottomNavigationBar(
        currentDestination = currentDestination,
        navigateToTopLevelDestination = navigateToTopLevelDestination
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewPermanentNavigationDrawerContent() {
    val currentDestination = null // 假設沒有當前選中的目的地
    val navigationContentPosition = ReplyNavigationContentPosition.CENTER

    // 假設的 navigateToTopLevelDestination 函數
    val navigateToTopLevelDestination: (ReplyTopLevelDestination) -> Unit = {}

    PermanentNavigationDrawerContent(
        currentDestination = currentDestination,
        navigationContentPosition = navigationContentPosition,
        navigateToTopLevelDestination = navigateToTopLevelDestination
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewModalNavigationDrawerContent() {
    val currentDestination = null // 假設沒有當前選中的目的地
    val navigationContentPosition = ReplyNavigationContentPosition.CENTER

    // 假設的 navigateToTopLevelDestination 函數
    val navigateToTopLevelDestination: (ReplyTopLevelDestination) -> Unit = {}

    // 假設的 onDrawerClicked 函數
    val onDrawerClicked: () -> Unit = {}

    ModalNavigationDrawerContent(
        currentDestination = currentDestination,
        navigationContentPosition = navigationContentPosition,
        navigateToTopLevelDestination = navigateToTopLevelDestination,
        onDrawerClicked = onDrawerClicked
    )
}
