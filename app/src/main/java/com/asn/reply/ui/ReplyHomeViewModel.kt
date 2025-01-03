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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asn.reply.data.Email
import com.asn.reply.data.EmailsRepository
import com.asn.reply.data.EmailsRepositoryImpl
import com.asn.reply.ui.utils.ReplyContentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * ReplyHomeViewModel 為 Home 屏幕的 ViewModel，用來管理與 UI 相關的狀態和業務邏輯。
 * 它包含了獲取電子郵件、選擇/打開郵件等功能，並將結果傳遞給 UI 層。
 */
class ReplyHomeViewModel(private val emailsRepository: EmailsRepository = EmailsRepositoryImpl()) :
    ViewModel() {

    // UI 狀態，儲存當前屏幕的狀態（如：郵件列表，是否正在加載，錯誤信息等）
    private val _uiState = MutableStateFlow(ReplyHomeUIState(loading = true))
    val uiState: StateFlow<ReplyHomeUIState> = _uiState

    // 初始化時，開始觀察郵件數據
    init {
        observeEmails()
    }

    /**
     * 觀察郵件數據流，並更新 UI 狀態。
     * 如果成功加載郵件，則將第一封郵件設為打開的郵件。
     * 如果發生錯誤，則更新錯誤信息。
     */
    private fun observeEmails() {
        // 在 viewModelScope 中啟動協程來獲取郵件數據
        viewModelScope.launch {
            emailsRepository.getAllEmails()  // 調用 Repository 來獲取所有郵件
                .catch { ex -> // 捕獲錯誤並設置錯誤信息
                    _uiState.value = ReplyHomeUIState(error = ex.message)
                }
                .collect { emails -> // 成功時，將郵件更新到 UI 狀態中
                    /**
                     * 我們在大屏設備上，首次啟動應用時，默認選擇第一封郵件
                     */
                    _uiState.value = ReplyHomeUIState(
                        emails = emails, // 更新郵件列表
                        openedEmail = emails.first() // 默認打開第一封郵件
                    )
                }
        }
    }

    /**
     * 根據傳入的 emailId 和 contentType 更新 UI 狀態。
     * 當 contentType 為 SINGLE_PANE 時，設置 isDetailOnlyOpen 為 true。
     */
    fun setOpenedEmail(emailId: Long, contentType: ReplyContentType) {
        // 根據 emailId 查找郵件
        val email = uiState.value.emails.find { it.id == emailId }
        // 更新 UI 狀態，設置打開的郵件和是否僅顯示細節
        _uiState.value = _uiState.value.copy(
            openedEmail = email,
            isDetailOnlyOpen = contentType == ReplyContentType.SINGLE_PANE
        )
    }

    /**
     * 切換選擇的郵件。如果當前郵件已被選擇，則取消選擇；如果未被選擇，則將其添加到選擇列表中。
     */
    fun toggleSelectedEmail(emailId: Long) {
        // 當前選擇的郵件列表
        val currentSelection = uiState.value.selectedEmails
        // 更新選擇的郵件列表
        _uiState.value = _uiState.value.copy(
            selectedEmails = if (currentSelection.contains(emailId))
                currentSelection.minus(emailId) // 如果已選擇，取消選擇
            else currentSelection.plus(emailId) // 否則添加到選擇列表中
        )
    }

    /**
     * 關閉郵件詳細信息頁面，恢復顯示第一封郵件並隱藏詳細頁。
     */
    fun closeDetailScreen() {
        _uiState.value = _uiState
            .value.copy(
                isDetailOnlyOpen = false, // 設置不僅顯示詳細頁
                openedEmail = _uiState.value.emails.first() // 恢復顯示第一封郵件
            )
    }
}

/**
 * 存儲 Home 屏幕的 UI 狀態，包括郵件列表、選擇的郵件、當前打開的郵件等信息。
 */
data class ReplyHomeUIState(
    val emails: List<Email> = emptyList(), // 郵件列表
    val selectedEmails: Set<Long> = emptySet(), // 選擇的郵件 ID 集合
    val openedEmail: Email? = null, // 當前打開的郵件
    val isDetailOnlyOpen: Boolean = false, // 是否只顯示郵件細節
    val loading: Boolean = false, // 是否正在加載郵件
    val error: String? = null // 錯誤信息
)
