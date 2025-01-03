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

package com.asn.reply.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.layout.DisplayFeature
import com.asn.reply.R
import com.asn.reply.data.Email
import com.asn.reply.data.local.LocalEmailsDataProvider
import com.asn.reply.ui.components.EmailDetailAppBar
import com.asn.reply.ui.components.ReplyDockedSearchBar
import com.asn.reply.ui.components.ReplyEmailListItem
import com.asn.reply.ui.components.ReplyEmailThreadItem
import com.asn.reply.ui.utils.ReplyContentType
import com.asn.reply.ui.utils.ReplyNavigationType
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy
import com.google.accompanist.adaptive.TwoPane

@Composable
fun ReplyInboxScreen(
    contentType: ReplyContentType, // 顯示類型，單面板或雙面板
    replyHomeUIState: ReplyHomeUIState, // 應用的當前狀態，包括郵件列表和選中郵件
    navigationType: ReplyNavigationType, // 導航類型，例如底部導航
    displayFeatures: List<DisplayFeature>, // 用於支持多窗口的顯示特性
    closeDetailScreen: () -> Unit, // 關閉詳情頁的回調函數
    navigateToDetail: (Long, ReplyContentType) -> Unit, // 導航至郵件詳情頁的回調函數
    toggleSelectedEmail: (Long) -> Unit, // 切換郵件選中狀態的回調函數
    modifier: Modifier = Modifier // 修飾符，用於設置布局樣式
) {
    /**
     * 當從 "列表與詳情" 視圖切換到僅顯示 "列表" 視圖時，
     * 清除選擇的郵件，確保使用者回到僅顯示郵件列表的頁面。
     */
    LaunchedEffect(key1 = contentType) {
        if (contentType == ReplyContentType.SINGLE_PANE && !replyHomeUIState.isDetailOnlyOpen) {
            closeDetailScreen()
        }
    }

    // 記住郵件列表的滾動狀態，用於保持滾動位置一致
    val emailLazyListState = rememberLazyListState()

    // TODO: 在多選模式下，讓頂部的 AppBar 覆蓋應用的整個寬度

    // 如果當前顯示模式為雙面板
    if (contentType == ReplyContentType.DUAL_PANE) {
        TwoPane(
            first = {
                // 第一個面板：郵件列表
                ReplyEmailList(
                    emails = replyHomeUIState.emails, // 所有郵件
                    openedEmail = replyHomeUIState.openedEmail, // 當前打開的郵件
                    selectedEmailIds = replyHomeUIState.selectedEmails, // 已選中的郵件 ID 列表
                    toggleEmailSelection = toggleSelectedEmail, // 切換郵件選中狀態
                    emailLazyListState = emailLazyListState, // 滾動狀態
                    navigateToDetail = navigateToDetail // 點擊郵件導航至詳情頁
                )
            },
            second = {
                // 第二個面板：郵件詳情
                ReplyEmailDetail(
                    email = replyHomeUIState.openedEmail
                        ?: replyHomeUIState.emails.first(), // 顯示當前選中的郵件，若無則顯示第一封
                    isFullScreen = false // 非全屏模式
                )
            },
            strategy = HorizontalTwoPaneStrategy(splitFraction = 0.5f, gapWidth = 16.dp), // 水平分屏策略
            displayFeatures = displayFeatures // 多窗口顯示特性
        )
    } else {
        // 單面板顯示模式
        Box(modifier = modifier.fillMaxSize()) {
            // 顯示單面板的內容
            ReplySinglePaneContent(
                replyHomeUIState = replyHomeUIState, // 當前應用狀態
                toggleEmailSelection = toggleSelectedEmail, // 切換選中狀態
                emailLazyListState = emailLazyListState, // 滾動狀態
                modifier = Modifier.fillMaxSize(), // 佔滿整個屏幕
                closeDetailScreen = closeDetailScreen, // 關閉詳情頁
                navigateToDetail = navigateToDetail // 導航至詳情頁
            )
            // 如果使用底部導航，顯示浮動操作按鈕（FAB）
            if (navigationType == ReplyNavigationType.BOTTOM_NAVIGATION) {
                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(id = R.string.compose)) }, // 顯示 "撰寫" 按鈕文字
                    icon = {
                        Icon(
                            Icons.Default.Edit,
                            stringResource(id = R.string.compose)
                        )
                    }, // 顯示 "編輯" 圖標
                    onClick = { /*TODO*/ }, // 點擊事件尚未實現
                    modifier = Modifier
                        .align(Alignment.BottomEnd) // 將按鈕對齊到右下角
                        .padding(16.dp), // 設置內邊距
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer, // 按鈕背景色
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer, // 按鈕內容顏色
                    expanded = emailLazyListState.lastScrolledBackward || // 控制按鈕展開狀態
                            !emailLazyListState.canScrollBackward
                )
            }
        }
    }
}

// 顯示單一或列表電子郵件內容的函數
@Composable
fun ReplySinglePaneContent(
    replyHomeUIState: ReplyHomeUIState,  // 儲存目前 UI 狀態的資料模型
    toggleEmailSelection: (Long) -> Unit, // 用來切換電子郵件選擇狀態的回呼函數
    emailLazyListState: LazyListState,   // 用於 LazyColumn 列表滾動狀態的參數
    modifier: Modifier = Modifier,       // 可選的修飾符，控制外觀或行為
    closeDetailScreen: () -> Unit,       // 用來關閉電子郵件詳細畫面的回呼函數
    navigateToDetail: (Long, ReplyContentType) -> Unit // 用來導航至詳細電子郵件畫面的回呼函數
) {
    // 如果已經開啟詳細電子郵件且只顯示詳細畫面
    if (replyHomeUIState.openedEmail != null && replyHomeUIState.isDetailOnlyOpen) {
        // 處理返回事件，執行關閉畫面的邏輯
        BackHandler {
            closeDetailScreen()
        }
        // 顯示單封電子郵件詳細內容
        ReplyEmailDetail(email = replyHomeUIState.openedEmail) {
            closeDetailScreen()
        }
    } else {
        // 顯示電子郵件列表
        ReplyEmailList(
            emails = replyHomeUIState.emails,
            openedEmail = replyHomeUIState.openedEmail,
            selectedEmailIds = replyHomeUIState.selectedEmails,
            toggleEmailSelection = toggleEmailSelection,
            emailLazyListState = emailLazyListState,
            modifier = modifier,
            navigateToDetail = navigateToDetail
        )
    }
}

// 顯示電子郵件列表的 Composable 函數
@Composable
fun ReplyEmailList(
    emails: List<Email>,                 // 所有顯示的電子郵件
    openedEmail: Email?,                 // 當前開啟的電子郵件（如果有）
    selectedEmailIds: Set<Long>,         // 被選擇的電子郵件 ID 集合
    toggleEmailSelection: (Long) -> Unit, // 用來切換電子郵件選擇狀態的回呼函數
    emailLazyListState: LazyListState,   // 列表的滾動狀態
    modifier: Modifier = Modifier,       // 可選的修飾符，控制外觀或行為
    navigateToDetail: (Long, ReplyContentType) -> Unit // 用來導航至詳細電子郵件畫面的回呼函數
) {
    // 設置頂部的回應式佈局，支持狀態欄的留白
    Box(modifier = modifier.windowInsetsPadding(WindowInsets.statusBars)) {
        // 顯示停駐式搜尋條，並在選擇搜索項目時導航到詳細畫面
        ReplyDockedSearchBar(
            emails = emails,
            onSearchItemSelected = { searchedEmail ->
                navigateToDetail(searchedEmail.id, ReplyContentType.SINGLE_PANE)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )

        // 顯示電子郵件列表
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 80.dp), // 上方空間避免與搜尋條重疊
            state = emailLazyListState
        ) {
            // 為每封電子郵件創建一個條目
            items(items = emails, key = { it.id }) { email ->
                // 顯示電子郵件列表項
                ReplyEmailListItem(
                    email = email,
                    navigateToDetail = { emailId ->
                        navigateToDetail(emailId, ReplyContentType.SINGLE_PANE)
                    },
                    toggleSelection = toggleEmailSelection,
                    isOpened = openedEmail?.id == email.id,  // 判斷是否當前已打開
                    isSelected = selectedEmailIds.contains(email.id)  // 判斷是否被選中
                )
            }
            // 列表底部添加空白來避免內容被系統欄遮擋
            item {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
            }
        }
    }
}


// 顯示電子郵件詳細內容的 Composable 函數
@Composable
fun ReplyEmailDetail(
    email: Email,  // 要顯示的電子郵件
    modifier: Modifier = Modifier,  // 用於調整佈局的修飾符
    isFullScreen: Boolean = true,  // 是否全螢幕顯示
    onBackPressed: () -> Unit = {}  // 當用戶按下返回鍵時執行的回調函數
) {
    // 顯示一個列表，顯示電子郵件的詳細內容
    LazyColumn(
        modifier = modifier
            .background(MaterialTheme.colorScheme.inverseOnSurface)  // 設置背景顏色
    ) {
        // 顯示電子郵件的 app bar，包括返回按鈕
        item {
            EmailDetailAppBar(email, isFullScreen) {
                onBackPressed()  // 返回時執行的回調
            }
        }
        // 顯示該電子郵件的所有會話線程
        items(items = email.threads, key = { it.id }) { email ->
            ReplyEmailThreadItem(email = email)  // 顯示每封會話中的電子郵件
        }
        // 顯示底部空間以避免被系統狀態欄遮擋
        item {
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
        }
    }
}

// 顯示 ReplyEmailDetail 的預覽
@Preview
@Composable
fun ReplyEmailDetailPreview() {
    // 使用模擬的數據來展示電子郵件詳細畫面
    ReplyEmailDetail(email = LocalEmailsDataProvider.get(2)!!)
}

// 顯示 ReplyEmailList 的預覽
@Preview
@Composable
fun ReplyEmailListPreview() {
    var selectedEmailIds by remember { mutableStateOf(setOf<Long>()) }
    val navigateToDetail: (Long, ReplyContentType) -> Unit = { emailId, contentType ->
        println("Navigating to email with ID $emailId in $contentType mode")
    }
    // 顯示電子郵件列表的預覽
    ReplyEmailList(
        emails = LocalEmailsDataProvider.allEmails,
        openedEmail = LocalEmailsDataProvider.get(2),
        selectedEmailIds = selectedEmailIds,
        toggleEmailSelection = {},
        emailLazyListState = rememberLazyListState(),
        navigateToDetail = navigateToDetail
    )
}

// 顯示 ReplySinglePaneContent 的預覽
@Preview
@Composable
fun PreviewReplySinglePaneContent() {
    // 模擬數據
    val testEmails = listOf(
        LocalEmailsDataProvider.get(1),
        LocalEmailsDataProvider.get(2)
    )

    val replyHomeUIState = ReplyHomeUIState(
        emails = testEmails as List<Email>,  // 設置顯示的電子郵件
        openedEmail = testEmails[1],  // 設置已開啟的電子郵件
        selectedEmails = setOf(2L),  // 設置選擇的電子郵件 ID
        isDetailOnlyOpen = true  // 指定是否只顯示詳細畫面
    )

    val toggleEmailSelection: (Long) -> Unit = { emailId ->
        println("Toggling selection for emailId=$emailId")
    }

    val navigateToDetail: (Long, ReplyContentType) -> Unit = { emailId, contentType ->
        println("Navigating to detail for emailId=$emailId, contentType=$contentType")
    }

    val closeDetailScreen: () -> Unit = {
        println("Closing detail screen")
    }

    // 渲染 ReplySinglePaneContent 組件
    ReplySinglePaneContent(
        replyHomeUIState = replyHomeUIState,
        toggleEmailSelection = toggleEmailSelection,
        emailLazyListState = rememberLazyListState(),
        modifier = Modifier.fillMaxSize(),
        closeDetailScreen = closeDetailScreen,
        navigateToDetail = navigateToDetail
    )
}

// 顯示 ReplyInboxScreen 的預覽
@Preview
@Composable
fun PreviewReplyInboxScreen() {
    // 模擬數據
    val testEmails = listOf(
        LocalEmailsDataProvider.get(1),
        LocalEmailsDataProvider.get(2),
        LocalEmailsDataProvider.get(3)
    )

    val replyHomeUIState = ReplyHomeUIState(
        emails = testEmails as List<Email>,
        openedEmail = testEmails[0],
        selectedEmails = setOf(1L),
        isDetailOnlyOpen = false  // 顯示電子郵件列表而非詳細內容
    )

    val navigateToDetail: (Long, ReplyContentType) -> Unit = { emailId, contentType ->
        println("Navigating to detail for emailId=$emailId, contentType=$contentType")
    }

    val toggleSelectedEmail: (Long) -> Unit = { emailId ->
        println("Toggling selection for emailId=$emailId")
    }

    val closeDetailScreen: () -> Unit = {
        println("Closing detail screen")
    }

    // 渲染 ReplyInboxScreen 組件
    ReplyInboxScreen(
        contentType = ReplyContentType.SINGLE_PANE,
        replyHomeUIState = replyHomeUIState,
        navigationType = ReplyNavigationType.BOTTOM_NAVIGATION,
        displayFeatures = emptyList(),
        closeDetailScreen = closeDetailScreen,
        navigateToDetail = navigateToDetail,
        toggleSelectedEmail = toggleSelectedEmail,
        modifier = Modifier.fillMaxSize()
    )
}



