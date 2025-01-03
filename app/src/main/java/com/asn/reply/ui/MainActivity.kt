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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asn.reply.data.local.LocalEmailsDataProvider
import com.asn.reply.ui.theme.ContrastAwareReplyTheme
import com.google.accompanist.adaptive.calculateDisplayFeatures

/**
 * MainActivity 為應用的入口點，負責設置內容並觀察 UI 狀態。
 */
class MainActivity : ComponentActivity() {

    // ViewModel 用於管理 UI 狀態
    private val viewModel: ReplyHomeViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()  // 启用屏幕边缘到边缘的显示方式
        super.onCreate(savedInstanceState)

        // 设置 Composable 內容
        setContent {
            ContrastAwareReplyTheme {  // 使用對比主題來適應不同的顯示
                val windowSize = calculateWindowSizeClass(this)  // 計算視窗大小類型
                val displayFeatures = calculateDisplayFeatures(this)  // 計算顯示特徵（例如折疊屏）
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()  // 觀察 UI 狀態

                // 傳遞必要的參數並顯示 ReplyApp
                ReplyApp(
                    windowSize = windowSize,
                    displayFeatures = displayFeatures,
                    replyHomeUIState = uiState,
                    closeDetailScreen = {
                        viewModel.closeDetailScreen()  // 關閉詳情頁
                    },
                    navigateToDetail = { emailId, pane ->
                        viewModel.setOpenedEmail(emailId, pane)  // 打開特定的郵件
                    },
                    toggleSelectedEmail = { emailId ->
                        viewModel.toggleSelectedEmail(emailId)  // 切換選中郵件
                    }
                )
            }
        }
    }
}

/**
 * ReplyAppPreview 是預覽界面，顯示應用的不同屏幕大小和佈局。
 * 包含了不同設備的預覽，如手機、平板和桌面模式。
 */

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true)
@Composable
fun ReplyAppPreview() {
    ContrastAwareReplyTheme {  // 應用主題
        ReplyApp(
            replyHomeUIState = ReplyHomeUIState(emails = LocalEmailsDataProvider.allEmails),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),  // 計算小型手機屏幕大小
            displayFeatures = emptyList(),  // 假設無顯示特徵
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true, widthDp = 700, heightDp = 500)
@Composable
fun ReplyAppPreviewTablet() {
    ContrastAwareReplyTheme {  // 應用主題
        ReplyApp(
            replyHomeUIState = ReplyHomeUIState(emails = LocalEmailsDataProvider.allEmails),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(700.dp, 500.dp)),  // 計算平板模式屏幕大小
            displayFeatures = emptyList(),  // 假設無顯示特徵
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true, widthDp = 500, heightDp = 700)
@Composable
fun ReplyAppPreviewTabletPortrait() {
    ContrastAwareReplyTheme {  // 應用主題
        ReplyApp(
            replyHomeUIState = ReplyHomeUIState(emails = LocalEmailsDataProvider.allEmails),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(500.dp, 700.dp)),  // 計算平板竪屏模式大小
            displayFeatures = emptyList(),  // 假設無顯示特徵
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true, widthDp = 1100, heightDp = 600)
@Composable
fun ReplyAppPreviewDesktop() {
    ContrastAwareReplyTheme {  // 應用主題
        ReplyApp(
            replyHomeUIState = ReplyHomeUIState(emails = LocalEmailsDataProvider.allEmails),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(1100.dp, 600.dp)),  // 計算桌面模式屏幕大小
            displayFeatures = emptyList(),  // 假設無顯示特徵
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true, widthDp = 600, heightDp = 1100)
@Composable
fun ReplyAppPreviewDesktopPortrait() {
    ContrastAwareReplyTheme {  // 應用主題
        ReplyApp(
            replyHomeUIState = ReplyHomeUIState(emails = LocalEmailsDataProvider.allEmails),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(600.dp, 1100.dp)),  // 計算桌面竪屏模式大小
            displayFeatures = emptyList(),  // 假設無顯示特徵
        )
    }
}
