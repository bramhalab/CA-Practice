package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccountingTableType
import com.example.ui.theme.VintageGold
import com.example.ui.theme.VintageGoldBg
import com.example.ui.theme.VintageGreen
import com.example.ui.theme.VintageGreenBg
import com.example.ui.theme.VintageInk
import com.example.ui.theme.VintageInkSoft
import com.example.ui.theme.VintageLine
import com.example.ui.theme.VintageNavy
import com.example.ui.theme.VintageNavyDeep
import com.example.ui.theme.VintagePaperBg
import com.example.ui.theme.VintagePaperSheet
import com.example.ui.theme.VintageRed
import com.example.ui.theme.VintageRedBg

@Composable
fun AccountingFormatSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isExpanded: Boolean,
    onToggleExpanded: (Boolean) -> Unit,
    activeTableType: AccountingTableType,
    onSelectFormat: (AccountingTableType) -> Unit,
    modifier: Modifier = Modifier
) {
    val allFormats = remember { AccountingTableType.ALL_FORMATS }

    val filteredFormats = remember(searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isBlank()) {
            allFormats
        } else {
            allFormats.filter { format ->
                format.title.lowercase().contains(q) ||
                format.command.lowercase().contains(q) ||
                format.description.lowercase().contains(q) ||
                format.category.lowercase().contains(q) ||
                when {
                    q in listOf("pl", "p&l", "profit", "loss", "labh") -> format == AccountingTableType.PROFIT_LOSS_ACCOUNT
                    q in listOf("trading", "vyapar") -> format == AccountingTableType.TRADING_ACCOUNT
                    q in listOf("bs", "balance", "sheet", "chittha") -> format == AccountingTableType.BALANCE_SHEET
                    q in listOf("journal", "roznama", "entry") -> format == AccountingTableType.JOURNAL
                    q in listOf("ledger", "khata", "t-account") -> format == AccountingTableType.LEDGER
                    q in listOf("trial", "balance", "talpat", "tb") -> format == AccountingTableType.TRIAL_BALANCE
                    q in listOf("cash", "rokad", "bank") -> format == AccountingTableType.CASH_BOOK
                    q in listOf("brs", "passbook", "reconciliation") -> format == AccountingTableType.BRS
                    q in listOf("partner", "capital", "punji") -> format == AccountingTableType.PARTNERS_CAPITAL
                    q in listOf("reval", "revaluation") -> format == AccountingTableType.REVALUATION_ACCOUNT
                    q in listOf("realisation", "dissolution") -> format == AccountingTableType.REALISATION_ACCOUNT
                    q in listOf("consignment", "chalani") -> format == AccountingTableType.CONSIGNMENT_ACCOUNT
                    q in listOf("petty", "imprest") -> format == AccountingTableType.PETTY_CASH_BOOK
                    q in listOf("mfg", "manufacturing", "nirman") -> format == AccountingTableType.MANUFACTURING_ACCOUNT
                    else -> false
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("accounting_format_search_container")
    ) {
        // Closed / Compact Search Bar Header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, VintageNavy.copy(alpha = 0.5f), RoundedCornerShape(6.dp)),
            shape = RoundedCornerShape(6.dp),
            color = VintagePaperSheet,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search accounting formats",
                    tint = VintageNavy,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp)
                ) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search /Accounts formats (e.g. Journal, BRS, P&L)...",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.5.sp,
                                color = VintageInkSoft.copy(alpha = 0.7f)
                            )
                        )
                    }

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = {
                            onSearchQueryChange(it)
                            if (!isExpanded) onToggleExpanded(true)
                        },
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = VintageInk
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("format_search_text_input")
                    )
                }

                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            onSearchQueryChange("")
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("clear_format_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = VintageInkSoft,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Dropdown trigger pill badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(VintageNavy.copy(alpha = 0.1f))
                        .clickable { onToggleExpanded(!isExpanded) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("toggle_format_dropdown_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = if (searchQuery.isBlank()) "${allFormats.size} Formats" else "${filteredFormats.size} found",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = VintageNavy
                            )
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Collapse formats" else "Expand formats",
                            tint = VintageNavy,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Expanded Search Dropdown Results List
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .border(1.dp, VintageLine, RoundedCornerShape(6.dp)),
                shape = RoundedCornerShape(6.dp),
                color = VintagePaperBg,
                shadowElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SELECT FORMAT TO INSERT & RENDER",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = VintageNavy,
                                letterSpacing = 0.8.sp
                            )
                        )
                        Text(
                            text = "Tap to load table",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.5.sp,
                                color = VintageInkSoft
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (filteredFormats.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No matching accounting format found for \"$searchQuery\"",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.5.sp,
                                    color = VintageInkSoft
                                )
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp)
                        ) {
                            items(filteredFormats, key = { it.name }) { format ->
                                val isCurrentlyActive = format == activeTableType

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                        .border(
                                            width = if (isCurrentlyActive) 1.5.dp else 1.dp,
                                            color = if (isCurrentlyActive) VintageNavy else VintageLine.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .clickable {
                                            onSelectFormat(format)
                                            onSearchQueryChange("")
                                            onToggleExpanded(false)
                                        }
                                        .testTag("format_item_${format.name.lowercase()}"),
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (isCurrentlyActive) VintageNavy.copy(alpha = 0.08f) else VintagePaperSheet
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = format.title,
                                                    style = TextStyle(
                                                        fontFamily = FontFamily.Serif,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = VintageInk
                                                    )
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(3.dp))
                                                        .background(VintageGold.copy(alpha = 0.18f))
                                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        text = format.category,
                                                        style = TextStyle(
                                                            fontFamily = FontFamily.Monospace,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            color = VintageGold
                                                        )
                                                    )
                                                }
                                            }

                                            if (format.description.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = format.description,
                                                    style = TextStyle(
                                                        fontFamily = FontFamily.Monospace,
                                                        fontSize = 11.sp,
                                                        color = VintageInkSoft
                                                    ),
                                                    maxLines = 1
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Slash command pill
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (isCurrentlyActive) VintageNavy else VintageNavy.copy(alpha = 0.12f))
                                                .padding(horizontal = 7.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = format.command,
                                                style = TextStyle(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isCurrentlyActive) Color.White else VintageNavy
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
