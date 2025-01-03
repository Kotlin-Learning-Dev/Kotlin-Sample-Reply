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

import androidx.compose.foundation.Image // 用於顯示圖片的 Compose 基本組件
import androidx.compose.foundation.layout.size // 控制組件尺寸的 Modifier 函數
import androidx.compose.foundation.shape.CircleShape // 定義圓形形狀
import androidx.compose.runtime.Composable // 用於創建 Compose 函數的注解
import androidx.compose.ui.Modifier // 用於設置 UI 屬性的修飾器
import androidx.compose.ui.draw.clip // 用於裁剪組件形狀的 Modifier 函數
import androidx.compose.ui.res.painterResource // 從資源加載圖片的函數
import androidx.compose.ui.tooling.preview.Preview // 預覽 Compose 函數的注解
import androidx.compose.ui.unit.dp // 定義尺寸的單位
import com.asn.reply.R // 引入 R 文件以訪問資源

/**
 * 一個組件，用於顯示用戶的圓形頭像圖片。
 *
 * @param drawableResource 圖片資源 ID，指定要顯示的頭像。
 * @param description 圖片的內容描述，用於無障礙支持。
 * @param modifier 可以額外配置的修飾器，默認值為空。
 */
@Composable
fun ReplyProfileImage(
    drawableResource: Int, // 頭像圖片資源
    description: String, // 圖片內容描述
    modifier: Modifier = Modifier // 修飾器默認值為空
) {
    Image(
        modifier = modifier
            .size(40.dp) // 設置圖片的大小為 40 dp
            .clip(CircleShape), // 裁剪圖片為圓形
        painter = painterResource(id = drawableResource), // 加載圖片資源
        contentDescription = description, // 提供圖片描述
    )
}

/**
 * 測試 ReplyProfileImage 組件。
 * 顯示預覽時將會使用一個假設的資源和描述。
 */
@Preview
@Composable
fun TestReplyProfileImage() {
    ReplyProfileImage(
        drawableResource = R.drawable.avatar_10, // 測試圖片資源
        description = "test" // 測試描述文字
    )
}
