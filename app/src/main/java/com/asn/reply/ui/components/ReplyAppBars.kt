/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
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

package com.asn.reply.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.asn.reply.R
import com.asn.reply.data.Email
import com.asn.reply.data.local.LocalEmailsDataProvider

// 搜索欄組件，提供搜索功能，並動態過濾 Email 列表
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplyDockedSearchBar(
    emails: List<Email>, // 傳入 Email 列表
    onSearchItemSelected: (Email) -> Unit, // 點擊某個 Email 項目的回調
    modifier: Modifier = Modifier // 父組件的樣式修飾器
) {
    var query by remember { mutableStateOf("") } // 搜索框輸入內容
    var expanded by remember { mutableStateOf(false) } // 搜索欄是否展開
    val searchResults = remember { mutableStateListOf<Email>() } // 搜索結果列表

    // 根據 query 的變化動態更新 searchResults
    LaunchedEffect(query) {
        searchResults.clear()
        if (query.isNotEmpty()) {
            searchResults.addAll(
                emails.filter {
                    it.subject.startsWith(query, ignoreCase = true) || // 根據主題過濾
                            it.sender.fullName.startsWith(query, ignoreCase = true) // 根據發送者過濾
                }
            )
        }
    }

    // 搜索欄 DockedSearchBar 組件
    DockedSearchBar(
        inputField = {
            // 搜索框的內容
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = { query = it }, // 更新搜索內容
                onSearch = { expanded = false }, // 點擊搜索按鈕時收起
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = stringResource(id = R.string.search_emails)) }, // 提示文字
                leadingIcon = {
                    if (expanded) {
                        // 展開時的返回按鈕
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_button),
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .clickable {
                                    expanded = false // 點擊返回按鈕收起
                                    query = "" // 清空搜索內容
                                },
                        )
                    } else {
                        // 收起時的搜索圖標
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.search),
                            modifier = Modifier.padding(start = 16.dp),
                        )
                    }
                },
                trailingIcon = {
                    // 搜索框右側的使用者頭像
                    ReplyProfileImage(
                        drawableResource = R.drawable.avatar_6,
                        description = stringResource(id = R.string.profile),
                        modifier = Modifier
                            .padding(12.dp)
                            .size(32.dp)
                    )
                },
            )
        },
        expanded = expanded,
        onExpandedChange = { expanded = it }, // 展開狀態切換
        modifier = modifier,
        content = {
            if (searchResults.isNotEmpty()) {
                // 顯示搜索結果列表
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(items = searchResults, key = { it.id }) { email ->
                        // 每個搜索結果的顯示項
                        ListItem(
                            headlineContent = { Text(email.subject) }, // 顯示主題
                            supportingContent = { Text(email.sender.fullName) }, // 顯示發送者名稱
                            leadingContent = {
                                // 顯示發送者頭像
                                ReplyProfileImage(
                                    drawableResource = email.sender.avatar,
                                    description = stringResource(id = R.string.profile),
                                    modifier = Modifier.size(32.dp)
                                )
                            },
                            modifier = Modifier.clickable {
                                onSearchItemSelected.invoke(email) // 點擊回調
                                query = "" // 清空搜索內容
                                expanded = false // 收起搜索欄
                            }
                        )
                    }
                }
            } else if (query.isNotEmpty()) {
                // 當沒有找到結果時顯示提示
                Text(
                    text = stringResource(id = R.string.no_item_found),
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                // 沒有搜索歷史的提示
                Text(
                    text = stringResource(id = R.string.no_search_history),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    )
}

// Email 詳細頁的頂部導航欄
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailDetailAppBar(
    email: Email, // 當前的 Email 資料
    isFullScreen: Boolean, // 是否全屏模式
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit // 返回按鈕的回調
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.inverseOnSurface // 背景顏色
        ),
        title = {
            // 顯示 Email 主題和訊息數量
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = if (isFullScreen) Alignment.CenterHorizontally
                else Alignment.Start
            ) {
                Text(
                    text = email.subject, // Email 主題
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = "${email.threads.size} ${stringResource(id = R.string.messages)}", // 訊息數量
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        navigationIcon = {
            if (isFullScreen) {
                // 全屏模式下顯示返回按鈕
                FilledIconButton(
                    onClick = onBackPressed,
                    modifier = Modifier.padding(8.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_button),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        },
        actions = {
            // 更多選項按鈕
            IconButton(
                onClick = { /*TODO*/ },
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(id = R.string.more_options_button),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

// 預覽 DockedSearchBar
@Preview
@Composable
fun ReplyDockedSearchBarPreview() {
    ReplyDockedSearchBar(emails = LocalEmailsDataProvider.allEmails, onSearchItemSelected = {})
}

// 預覽 Email 詳細頁的頂部導航欄
@Preview
@Composable
fun EmailDetailAppBarPreview() {
    EmailDetailAppBar(
        email = LocalEmailsDataProvider.get(2)!!,
        isFullScreen = true,
        onBackPressed = {})
}
