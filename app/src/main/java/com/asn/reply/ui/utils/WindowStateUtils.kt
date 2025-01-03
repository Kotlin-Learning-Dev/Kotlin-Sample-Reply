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

package com.asn.reply.ui.utils

import android.graphics.Rect
import androidx.window.layout.FoldingFeature
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * 代表設備的姿勢（Posture）狀態的封閉接口。根據設備的摺疊狀態，這個接口提供了多種具體的姿勢。
 */
sealed interface DevicePosture {
    // 正常姿勢（未摺疊）
    object NormalPosture : DevicePosture

    // 書本模式（摺疊設備，展示中間縫隙）
    data class BookPosture(
        val hingePosition: Rect // 這裡保存摺疊裝置的縫隙位置
    ) : DevicePosture

    // 裝置摺疊處於分離狀態（設備摺疊但仍可分開）
    data class Separating(
        val hingePosition: Rect, // 縫隙位置
        var orientation: FoldingFeature.Orientation // 裝置摺疊的方向
    ) : DevicePosture
}

@OptIn(ExperimentalContracts::class) // 啟用實驗性功能：Contracts
fun isBookPosture(foldFeature: FoldingFeature?): Boolean {
    // 使用契約確保若返回true，foldFeature 一定不為 null
    contract { returns(true) implies (foldFeature != null) }

    // 檢查摺疊設備是否處於「書本模式」：半開並且摺疊方向垂直
    return foldFeature?.state == FoldingFeature.State.HALF_OPENED &&
            foldFeature.orientation == FoldingFeature.Orientation.VERTICAL
}

@OptIn(ExperimentalContracts::class) // 啟用實驗性功能：Contracts
fun isSeparating(foldFeature: FoldingFeature?): Boolean {
    // 使用契約確保若返回true，foldFeature 一定不為 null
    contract { returns(true) implies (foldFeature != null) }

    // 檢查設備是否處於分離模式：展開並且是分隔模式
    return foldFeature?.state == FoldingFeature.State.FLAT && foldFeature.isSeparating
}

/**
 * 根據設備的尺寸和狀態，應用程式支持的不同導航類型。
 */
enum class ReplyNavigationType {
    BOTTOM_NAVIGATION, // 底部導航
    NAVIGATION_RAIL,   // 側邊導航欄（用於大螢幕設備）
    PERMANENT_NAVIGATION_DRAWER // 永久性側邊抽屜導航
}

/**
 * 根據設備的尺寸和狀態，導航內容在導航欄、導航抽屜中的不同位置。
 */
enum class ReplyNavigationContentPosition {
    TOP, // 內容位於頂部
    CENTER // 內容位於中間
}

/**
 * 根據設備的尺寸和狀態，應用程式顯示的內容類型。
 */
enum class ReplyContentType {
    SINGLE_PANE, // 單頁顯示（通常用於小屏設備）
    DUAL_PANE // 雙頁顯示（通常用於大屏設備或摺疊設備）
}
