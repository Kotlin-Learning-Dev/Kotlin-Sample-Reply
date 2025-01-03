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

package com.asn.reply.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.asn.reply.R
import com.asn.reply.data.Email
import com.asn.reply.data.MailboxType
import com.asn.reply.data.local.LocalAccountsDataProvider

/**
 * 顯示單個郵件的詳細信息，包括發件人、主題、內容等。
 * @param email 郵件數據對象，包含郵件的所有必要信息。
 * @param modifier 可選的修飾符，用於自定義組件外觀和佈局。
 */
@Composable
fun ReplyEmailThreadItem(
    email: Email,
    modifier: Modifier = Modifier
) {
    // 使用 Card 容器包裹郵件內容，並設置容器顏色和邊距
    Card(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        // 外部的垂直佈局容器，用於排列郵件內容的各個部分
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // 第一行包含發件人信息和收藏按鈕
            Row(modifier = Modifier.fillMaxWidth()) {
                // 發件人頭像
                ReplyProfileImage(
                    drawableResource = email.sender.avatar,
                    description = email.sender.fullName,
                )
                // 發件人名稱和時間信息
                Column(
                    modifier = Modifier
                        .weight(1f) // 佔滿剩餘空間
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.Center // 垂直居中
                ) {
                    // 發件人名稱
                    Text(
                        text = email.sender.firstName,
                        style = MaterialTheme.typography.labelMedium
                    )
                    // 發送時間（此處為靜態文本，應改為動態數據）
                    Text(
                        text = "20 mins ago",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                // 收藏按鈕
                IconButton(
                    onClick = { /*TODO*/ }, // 收藏按鈕點擊事件待實現
                    modifier = Modifier
                        .clip(CircleShape) // 設置按鈕為圓形
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.StarBorder, // 使用星形圖標
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // 顯示郵件主題
            Text(
                text = email.subject,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )

            // 顯示郵件內容
            Text(
                text = email.body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // 底部包含回覆按鈕
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp), // 按鈕之間的間距
            ) {
                // "Reply" 按鈕
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(1f), // 平分空間
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceBright
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.reply),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                // "Reply All" 按鈕
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceBright
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.reply_all),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * 預覽功能，用於在編輯器中顯示組件的效果。
 */
@Preview(showBackground = true)
@Composable
fun ReplyEmailThreadItemPreview() {
    // 創建一個範例郵件數據
    val sampleEmail = Email(
        id = 8L, // 郵件 ID
        sender = LocalAccountsDataProvider.getContactAccountByUid(13L), // 發件人信息
        recipients = listOf(LocalAccountsDataProvider.getDefaultUserAccount()), // 收件人信息
        subject = "Your update on Google Play Store is live!", // 郵件主題
        body = """
              Your update, 0.1.1, is now live on the Play Store and available for your alpha users to start testing.
              
              Your alpha testers will be automatically notified. If you'd rather send them a link directly, go to your Google Play Console and follow the instructions for obtaining an open alpha testing link.
            """.trimIndent(), // 郵件內容
        mailbox = MailboxType.TRASH, // 郵件所在的郵箱類型
        createdAt = "3 hours ago", // 郵件創建時間
    )

    // 使用範例郵件數據來顯示 ReplyEmailListItem
    ReplyEmailThreadItem(
        email = sampleEmail // 傳遞範例郵件數據
    )
}
