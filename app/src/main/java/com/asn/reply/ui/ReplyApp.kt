/*
 * 這個檔案定義了 Reply 應用的主要組件函數 `ReplyApp`，
 * 負責根據設備的視窗大小、摺疊狀態和其他顯示特性來管理 UI 佈局和導航。
 */

package com.asn.reply.ui

// 引入 UI、導航和設備處理所需的相關庫。
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import com.asn.reply.ui.navigation.ReplyNavigationActions
import com.asn.reply.ui.navigation.ReplyNavigationWrapper
import com.asn.reply.ui.navigation.Route
import com.asn.reply.ui.utils.DevicePosture
import com.asn.reply.ui.utils.ReplyContentType
import com.asn.reply.ui.utils.ReplyNavigationType
import com.asn.reply.ui.utils.isBookPosture
import com.asn.reply.ui.utils.isSeparating


// 將 `NavigationSuiteType` 映射為 `ReplyNavigationType` 的輔助函數。
private fun NavigationSuiteType.toReplyNavType() = when (this) {
    NavigationSuiteType.NavigationBar -> ReplyNavigationType.BOTTOM_NAVIGATION
    NavigationSuiteType.NavigationRail -> ReplyNavigationType.NAVIGATION_RAIL
    NavigationSuiteType.NavigationDrawer -> ReplyNavigationType.PERMANENT_NAVIGATION_DRAWER
    else -> ReplyNavigationType.BOTTOM_NAVIGATION
}

@Composable
fun ReplyApp(
    windowSize: WindowSizeClass, // 提供視窗大小的分類資訊。
    displayFeatures: List<DisplayFeature>, // 顯示特性列表，如摺疊功能。
    replyHomeUIState: ReplyHomeUIState, // 表示主畫面的 UI 狀態。
    closeDetailScreen: () -> Unit = {}, // 用於關閉詳細頁面的回呼函數。
    navigateToDetail: (Long, ReplyContentType) -> Unit = { _, _ -> }, // 用於導航到詳細頁面的回呼函數。
    toggleSelectedEmail: (Long) -> Unit = { } // 用於切換選擇郵件的回呼函數。
) {
    /*
     * 根據摺疊功能來確定摺疊設備的姿態。
     * 確認設備是否處於書本模式、分離模式或普通模式。
     */
    val foldingFeature = displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()

    val foldingDevicePosture = when {
        isBookPosture(foldingFeature) ->
            DevicePosture.BookPosture(foldingFeature.bounds)

        isSeparating(foldingFeature) ->
            DevicePosture.Separating(foldingFeature.bounds, foldingFeature.orientation)

        else -> DevicePosture.NormalPosture
    }

    /*
     * 根據視窗大小和摺疊姿態來決定內容顯示類型（單頁面或雙頁面）。
     */
    val contentType = when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> ReplyContentType.SINGLE_PANE
        WindowWidthSizeClass.Medium -> if (foldingDevicePosture != DevicePosture.NormalPosture) {
            ReplyContentType.DUAL_PANE
        } else {
            ReplyContentType.SINGLE_PANE
        }
        WindowWidthSizeClass.Expanded -> ReplyContentType.DUAL_PANE
        else -> ReplyContentType.SINGLE_PANE
    }

    /*
     * 設置應用的導航控制器和導航操作。
     */
    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        ReplyNavigationActions(navController)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // 使用 Surface 組件作為應用的根佈局。
    Surface {
        ReplyNavigationWrapper(
            currentDestination = currentDestination, // 當前導航的目的地。
            navigateToTopLevelDestination = navigationActions::navigateTo // 執行頂層導航的操作。
        ) {
            // 配置應用的導航主機。
            ReplyNavHost(
                navController = navController,
                contentType = contentType,
                displayFeatures = displayFeatures,
                replyHomeUIState = replyHomeUIState,
                navigationType = navSuiteType.toReplyNavType(),
                closeDetailScreen = closeDetailScreen,
                navigateToDetail = navigateToDetail,
                toggleSelectedEmail = toggleSelectedEmail,
            )
        }
    }
}

/*
 * 定義應用的導航主機，用於設置不同路由及其對應的 UI 組件。
 */
@Composable
private fun ReplyNavHost(
    navController: NavHostController, // 導航控制器。
    contentType: ReplyContentType, // 內容顯示類型。
    displayFeatures: List<DisplayFeature>, // 顯示特性列表。
    replyHomeUIState: ReplyHomeUIState, // 主畫面的 UI 狀態。
    navigationType: ReplyNavigationType, // 導航類型。
    closeDetailScreen: () -> Unit, // 關閉詳細頁面的回呼函數。
    navigateToDetail: (Long, ReplyContentType) -> Unit, // 導航到詳細頁面的回呼函數。
    toggleSelectedEmail: (Long) -> Unit, // 切換選擇郵件的回呼函數。
    modifier: Modifier = Modifier, // 組件修飾符。
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Route.Inbox, // 設置導航的起始路由。
    ) {
        composable<Route.Inbox> {
            ReplyInboxScreen(
                contentType = contentType,
                replyHomeUIState = replyHomeUIState,
                navigationType = navigationType,
                displayFeatures = displayFeatures,
                closeDetailScreen = closeDetailScreen,
                navigateToDetail = navigateToDetail,
                toggleSelectedEmail = toggleSelectedEmail
            )
        }
        composable<Route.DirectMessages> {
            EmptyComingSoon() // 顯示“即將推出”的佔位頁面。
        }
        composable<Route.Articles> {
            EmptyComingSoon() // 顯示“即將推出”的佔位頁面。
        }
        composable<Route.Groups> {
            EmptyComingSoon() // 顯示“即將推出”的佔位頁面。
        }
    }
}
