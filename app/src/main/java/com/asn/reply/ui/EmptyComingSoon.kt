package com.asn.reply.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.asn.reply.R

@Composable
fun EmptyComingSoon(
    // Modifier 是標準的 Kotlin Object，使用時直接以 Object 操作即可。
    // 更改元件的外觀，比方說尺寸、排版，甚至是行為。
    // 為元件增加額外資訊，比方說 Accessibility 標記。
    // 處理使用者輸入的訊息。
    // 增加高階互動，例如把元件變成可被點擊、可被滾動、可被拖曳或是可被放大縮小。
    modifier: Modifier = Modifier
) {
    Column(
        // fillMaxSize: 使 Column 佔據整個可用空間
        // Arrangement.Center: 使內容在垂直方向居中排列。
        // Alignment.CenterHorizontally: 使內容在水平方向居中對齊
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = stringResource(id = R.string.empty_screen_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = stringResource(id = R.string.empty_screen_subtitle),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.outline
        )
    }

}

// @Preview: 告訴編譯器將這個函數作為可預覽的組件來渲染
@Preview
@Composable
fun ComingSoonPreview() {
    EmptyComingSoon()
}