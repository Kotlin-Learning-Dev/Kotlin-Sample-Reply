package com.asn.reply.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.asn.reply.data.Email
import com.asn.reply.data.MailboxType
import com.asn.reply.data.local.LocalAccountsDataProvider

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReplyEmailListItem(
    // 電子郵件的詳細數據
    email: Email,
    // 點擊時進行導航的 lambda 函數，接受電子郵件的 ID
    navigateToDetail: (Long) -> Unit,
    // 切換選中狀態的 lambda 函數，接受電子郵件的 ID
    toggleSelection: (Long) -> Unit,
    // 用於修飾組件的修飾符
    modifier: Modifier = Modifier,
    // 表示該電子郵件是否已讀
    isOpened: Boolean = false,
    // 表示該電子郵件是否被選中
    isSelected: Boolean = false,
) {
    // 使用 Card 容器包裹整個電子郵件項目
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp) // 設置項目邊距
            .semantics { selected = isSelected } // 語意信息，表示是否被選中
            .clip(CardDefaults.shape) // 剪裁為默認形狀
            .combinedClickable( // 支持單擊和長按行為
                onClick = { navigateToDetail(email.id) }, // 單擊導航到詳細頁
                onLongClick = { toggleSelection(email.id) } // 長按切換選中狀態
            )
            .clip(CardDefaults.shape),
        colors = CardDefaults.cardColors(
            // 根據選中或已讀狀態設置卡片顏色
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else if (isOpened) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        // 使用 Column 垂直排列卡片內容
        Column(
            modifier = Modifier
                .fillMaxWidth() // 卡片寬度填滿父容器
                .padding(20.dp) // 設置內部間距
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                // 點擊切換選中狀態的修飾符
                val clickModifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() }, // 記錄點擊交互狀態
                    indication = null // 移除點擊效果
                ) { toggleSelection(email.id) }

                // 動畫顯示不同的頭像（選中或未選中）
                AnimatedContent(targetState = isSelected, label = "avatar") { selected ->
                    if (selected) {
                        SelectedProfileImage(clickModifier) // 顯示選中狀態的頭像
                    } else {
                        ReplyProfileImage(
                            email.sender.avatar, // 發件人頭像
                            email.sender.fullName, // 發件人全名
                            clickModifier
                        )
                    }
                }

                // 發件人信息及郵件日期
                Column(
                    modifier = Modifier
                        .weight(1f) // 使用剩餘空間
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.Center // 垂直居中
                ) {
                    Text(
                        text = email.sender.firstName, // 發件人名字
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = email.createdAt, // 郵件日期
                        style = MaterialTheme.typography.labelMedium,
                    )
                }

                // 收藏按鈕（目前未實現具體功能）
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .clip(CircleShape) // 圓形背景
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Icon(
                        imageVector = Icons.Default.StarBorder, // 星形圖標
                        contentDescription = "Favorite", // 輔助描述
                        tint = MaterialTheme.colorScheme.outline // 圖標顏色
                    )
                }
            }

            // 顯示郵件主題
            Text(
                text = email.subject,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )

            // 顯示郵件內容（最多兩行）
            Text(
                text = email.body,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2, // 最多顯示兩行
                overflow = TextOverflow.Ellipsis // 超出部分顯示省略號
            )
        }
    }
}


@Composable
fun SelectedProfileImage(modifier: Modifier = Modifier) {
    // 使用 Box 包裹圖標，實現圓形背景
    Box(
        modifier
            .size(40.dp) // 設置圓形大小
            .clip(CircleShape) // 剪裁為圓形
            .background(MaterialTheme.colorScheme.primary) // 設置圓形背景顏色
    ) {
        // 在圓形內部顯示一個勾選圖標，居中顯示
        Icon(
            Icons.Default.Check, // 使用默認的勾選圖標
            contentDescription = null, // 不提供內容描述
            modifier = Modifier
                .size(24.dp) // 設置圖標大小
                .align(Alignment.Center), // 居中顯示
            tint = MaterialTheme.colorScheme.onPrimary // 設置圖標顏色為背景色的對比色
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReplyEmailListItemPreview() {
    // 創建一個範例郵件數據
    val sampleEmail = Email(
        id = 8L,
        sender = LocalAccountsDataProvider.getContactAccountByUid(13L), // 使用提供的 ID 獲取發件人
        recipients = listOf(LocalAccountsDataProvider.getDefaultUserAccount()), // 設置收件人為默認帳號
        subject = "Your update on Google Play Store is live!", // 郵件主題
        body = """
              Your update, 0.1.1, is now live on the Play Store and available for your alpha users to start testing.
              
              Your alpha testers will be automatically notified. If you'd rather send them a link directly, go to your Google Play Console and follow the instructions for obtaining an open alpha testing link.
            """.trimIndent(), // 郵件內容
        mailbox = MailboxType.TRASH, // 設置郵件所在的郵箱類型
        createdAt = "3 hours ago", // 郵件創建時間
    )

    // 使用範例郵件數據來顯示 ReplyEmailListItem
    ReplyEmailListItem(
        email = sampleEmail,
        navigateToDetail = {}, // 導航動作留空
        toggleSelection = {} // 切換選擇動作留空
    )
}

