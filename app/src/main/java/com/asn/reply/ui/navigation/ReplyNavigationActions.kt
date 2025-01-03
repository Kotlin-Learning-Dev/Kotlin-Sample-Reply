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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.asn.reply.R
import kotlinx.serialization.Serializable

// 定義 Route 介面，並指定不同的路由類型
sealed interface Route {
    @Serializable data object Inbox : Route // 收件匣頁面
    @Serializable data object Articles : Route // 文章頁面
    @Serializable data object DirectMessages : Route // 直接消息頁面
    @Serializable data object Groups : Route // 群組頁面
}

// 設定頂層目的地，包括路由、選中/未選中的圖標和圖標的文本 ID
data class ReplyTopLevelDestination(
    val route: Route, // 路由名稱
    val selectedIcon: ImageVector, // 選中狀態圖標
    val unselectedIcon: ImageVector, // 未選中狀態圖標
    val iconTextId: Int // 用來顯示的文本 ID（例如 tab 名稱）
)

// 定義處理導航的操作
class ReplyNavigationActions(private val navController: NavHostController) {

    // 用於導航到指定的目的地，並處理堆棧管理
    fun navigateTo(destination: ReplyTopLevelDestination) {
        navController.navigate(destination.route) {
            // 彈出到圖形的開始目的地，避免堆積過多的目的地
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true // 儲存當前狀態
            }
            // 當重新選擇相同的項目時，避免創建新的副本
            launchSingleTop = true
            // 重新選擇之前的項目時，恢復狀態
            restoreState = true
        }
    }
}

// 定義四個頂層導航目的地，並為每個目的地提供對應的圖標和文本
val TOP_LEVEL_DESTINATIONS = listOf(
    ReplyTopLevelDestination(
        route = Route.Inbox,
        selectedIcon = Icons.Default.Inbox, // 選中時顯示的圖標
        unselectedIcon = Icons.Default.Inbox, // 未選中時顯示的圖標
        iconTextId = R.string.tab_inbox // 顯示的文本 ID
    ),
    ReplyTopLevelDestination(
        route = Route.Articles,
        selectedIcon = Icons.AutoMirrored.Filled.Article, // 選中時顯示的圖標
        unselectedIcon = Icons.AutoMirrored.Filled.Article, // 未選中時顯示的圖標
        iconTextId = R.string.tab_article // 顯示的文本 ID
    ),
    ReplyTopLevelDestination(
        route = Route.DirectMessages,
        selectedIcon = Icons.Outlined.ChatBubbleOutline, // 選中時顯示的圖標
        unselectedIcon = Icons.Outlined.ChatBubbleOutline, // 未選中時顯示的圖標
        iconTextId = R.string.tab_inbox // 顯示的文本 ID
    ),
    ReplyTopLevelDestination(
        route = Route.Groups,
        selectedIcon = Icons.Default.People, // 選中時顯示的圖標
        unselectedIcon = Icons.Default.People, // 未選中時顯示的圖標
        iconTextId = R.string.tab_article // 顯示的文本 ID
    )
)
