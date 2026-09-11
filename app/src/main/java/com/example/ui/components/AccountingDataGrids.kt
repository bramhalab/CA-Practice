package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccountRow
import com.example.model.AccountingTableType
import com.example.model.BrsRow
import com.example.model.CashBookRow
import com.example.model.JournalRow
import com.example.model.LedgerRow
import com.example.model.PartnersCapitalRow
import com.example.model.PettyCashRow
import com.example.model.TrialBalanceRow
import com.example.ui.theme.VintageGreen
import com.example.ui.theme.VintageGreenBg
import com.example.ui.theme.VintageInk
import com.example.ui.theme.VintageInkSoft
import com.example.ui.theme.VintageLine
import com.example.ui.theme.VintageNavy
import com.example.ui.theme.VintagePaperBg
import com.example.ui.theme.VintagePaperSheet
import com.example.ui.theme.VintageRed
import com.example.ui.theme.VintageRedBg
import java.text.DecimalFormat

private val currencyFormatter = DecimalFormat("#,##,##0.00")

@Composable
fun AccountingTableContainer(
    tableType: AccountingTableType,
    tradingDrRows: List<AccountRow> = emptyList(),
    tradingCrRows: List<AccountRow> = emptyList(),
    onUpdateTradingDrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onUpdateTradingCrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onAddTradingDrRow: () -> Unit = {},
    onAddTradingCrRow: () -> Unit = {},
    onDeleteTradingDrRow: (Int) -> Unit = {},
    onDeleteTradingCrRow: (Int) -> Unit = {},
    // Profit & Loss
    plDrRows: List<AccountRow> = emptyList(),
    plCrRows: List<AccountRow> = emptyList(),
    onUpdatePlDrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onUpdatePlCrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onAddPlDrRow: () -> Unit = {},
    onAddPlCrRow: () -> Unit = {},
    onDeletePlDrRow: (Int) -> Unit = {},
    onDeletePlCrRow: (Int) -> Unit = {},
    // Manufacturing
    manufacturingDrRows: List<AccountRow> = emptyList(),
    manufacturingCrRows: List<AccountRow> = emptyList(),
    onUpdateManufacturingDrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onUpdateManufacturingCrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onAddManufacturingDrRow: () -> Unit = {},
    onAddManufacturingCrRow: () -> Unit = {},
    onDeleteManufacturingDrRow: (Int) -> Unit = {},
    onDeleteManufacturingCrRow: (Int) -> Unit = {},
    // Revaluation
    revaluationDrRows: List<AccountRow> = emptyList(),
    revaluationCrRows: List<AccountRow> = emptyList(),
    onUpdateRevaluationDrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onUpdateRevaluationCrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onAddRevaluationDrRow: () -> Unit = {},
    onAddRevaluationCrRow: () -> Unit = {},
    onDeleteRevaluationDrRow: (Int) -> Unit = {},
    onDeleteRevaluationCrRow: (Int) -> Unit = {},
    // Realisation
    realisationDrRows: List<AccountRow> = emptyList(),
    realisationCrRows: List<AccountRow> = emptyList(),
    onUpdateRealisationDrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onUpdateRealisationCrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onAddRealisationDrRow: () -> Unit = {},
    onAddRealisationCrRow: () -> Unit = {},
    onDeleteRealisationDrRow: (Int) -> Unit = {},
    onDeleteRealisationCrRow: (Int) -> Unit = {},
    // Consignment
    consignmentDrRows: List<AccountRow> = emptyList(),
    consignmentCrRows: List<AccountRow> = emptyList(),
    onUpdateConsignmentDrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onUpdateConsignmentCrRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onAddConsignmentDrRow: () -> Unit = {},
    onAddConsignmentCrRow: () -> Unit = {},
    onDeleteConsignmentDrRow: (Int) -> Unit = {},
    onDeleteConsignmentCrRow: (Int) -> Unit = {},
    bsLiabilityRows: List<AccountRow> = emptyList(),
    bsAssetRows: List<AccountRow> = emptyList(),
    onUpdateBsLiabilityRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onUpdateBsAssetRow: (Int, String, String) -> Unit = { _, _, _ -> },
    onAddBsLiabilityRow: () -> Unit = {},
    onAddBsAssetRow: () -> Unit = {},
    onDeleteBsLiabilityRow: (Int) -> Unit = {},
    onDeleteBsAssetRow: (Int) -> Unit = {},
    ledgerAccountName: String = "Cash Account",
    onUpdateLedgerAccountName: (String) -> Unit = {},
    ledgerDrRows: List<LedgerRow> = emptyList(),
    ledgerCrRows: List<LedgerRow> = emptyList(),
    onUpdateLedgerDrRow: (Int, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onUpdateLedgerCrRow: (Int, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onAddLedgerDrRow: () -> Unit = {},
    onAddLedgerCrRow: () -> Unit = {},
    onDeleteLedgerDrRow: (Int) -> Unit = {},
    onDeleteLedgerCrRow: (Int) -> Unit = {},
    // Specialized format parameters
    journalRows: List<JournalRow> = emptyList(),
    onUpdateJournalRow: (Int, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _ -> },
    onAddJournalRow: () -> Unit = {},
    onDeleteJournalRow: (Int) -> Unit = {},
    trialBalanceRows: List<TrialBalanceRow> = emptyList(),
    onUpdateTrialBalanceRow: (Int, String, String, String, String, String) -> Unit = { _, _, _, _, _, _ -> },
    onAddTrialBalanceRow: () -> Unit = {},
    onDeleteTrialBalanceRow: (Int) -> Unit = {},
    brsStartingBalance: String = "50000",
    onUpdateBrsStartingBalance: (String) -> Unit = {},
    brsRows: List<BrsRow> = emptyList(),
    onUpdateBrsRow: (Int, String, Boolean, String) -> Unit = { _, _, _, _ -> },
    onAddBrsRow: (Boolean) -> Unit = {},
    onDeleteBrsRow: (Int) -> Unit = {},
    cashBookDrRows: List<CashBookRow> = emptyList(),
    cashBookCrRows: List<CashBookRow> = emptyList(),
    onUpdateCashBookDr: (Int, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _ -> },
    onUpdateCashBookCr: (Int, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _ -> },
    onAddCashBookDr: () -> Unit = {},
    onAddCashBookCr: () -> Unit = {},
    onDeleteCashBookDr: (Int) -> Unit = {},
    onDeleteCashBookCr: (Int) -> Unit = {},
    partnersCapitalDrRows: List<PartnersCapitalRow> = emptyList(),
    partnersCapitalCrRows: List<PartnersCapitalRow> = emptyList(),
    onUpdatePartnersCapitalDr: (Int, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onUpdatePartnersCapitalCr: (Int, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onAddPartnersCapitalDr: () -> Unit = {},
    onAddPartnersCapitalCr: () -> Unit = {},
    onDeletePartnersCapitalDr: (Int) -> Unit = {},
    onDeletePartnersCapitalCr: (Int) -> Unit = {},
    pettyCashRows: List<PettyCashRow> = emptyList(),
    onUpdatePettyCashRow: (Int, String, String, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onAddPettyCashRow: () -> Unit = {},
    onDeletePettyCashRow: (Int) -> Unit = {},
    onChangeFormat: () -> Unit = {},
    onCloseTable: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (tableType == AccountingTableType.NONE) return

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .testTag("accounting_table_container"),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, VintageNavy),
        color = VintagePaperSheet,
        shadowElevation = 3.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tableType.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = VintageNavy
                    )
                    Text(
                        text = "Command: ${tableType.command} • ${tableType.category}",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = VintageInkSoft
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onChangeFormat,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("change_format_button"),
                        shape = RoundedCornerShape(3.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VintageNavy)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Formats",
                            style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        )
                    }

                    IconButton(
                        onClick = onCloseTable,
                        modifier = Modifier.testTag("close_table_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Table",
                            tint = VintageInkSoft
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (tableType) {
                AccountingTableType.TRADING_ACCOUNT -> {
                    TradingAccountGrid(
                        drRows = tradingDrRows,
                        crRows = tradingCrRows,
                        onUpdateDr = onUpdateTradingDrRow,
                        onUpdateCr = onUpdateTradingCrRow,
                        onAddDr = onAddTradingDrRow,
                        onAddCr = onAddTradingCrRow,
                        onDeleteDr = onDeleteTradingDrRow,
                        onDeleteCr = onDeleteTradingCrRow
                    )
                }
                AccountingTableType.PROFIT_LOSS_ACCOUNT -> {
                    TwoSidedAccountGrid(
                        tableType = tableType,
                        drRows = plDrRows,
                        crRows = plCrRows,
                        onUpdateDr = onUpdatePlDrRow,
                        onUpdateCr = onUpdatePlCrRow,
                        onAddDr = onAddPlDrRow,
                        onAddCr = onAddPlCrRow,
                        onDeleteDr = onDeletePlDrRow,
                        onDeleteCr = onDeletePlCrRow
                    )
                }
                AccountingTableType.MANUFACTURING_ACCOUNT -> {
                    TwoSidedAccountGrid(
                        tableType = tableType,
                        drRows = manufacturingDrRows,
                        crRows = manufacturingCrRows,
                        onUpdateDr = onUpdateManufacturingDrRow,
                        onUpdateCr = onUpdateManufacturingCrRow,
                        onAddDr = onAddManufacturingDrRow,
                        onAddCr = onAddManufacturingCrRow,
                        onDeleteDr = onDeleteManufacturingDrRow,
                        onDeleteCr = onDeleteManufacturingCrRow
                    )
                }
                AccountingTableType.REVALUATION_ACCOUNT -> {
                    TwoSidedAccountGrid(
                        tableType = tableType,
                        drRows = revaluationDrRows,
                        crRows = revaluationCrRows,
                        onUpdateDr = onUpdateRevaluationDrRow,
                        onUpdateCr = onUpdateRevaluationCrRow,
                        onAddDr = onAddRevaluationDrRow,
                        onAddCr = onAddRevaluationCrRow,
                        onDeleteDr = onDeleteRevaluationDrRow,
                        onDeleteCr = onDeleteRevaluationCrRow
                    )
                }
                AccountingTableType.REALISATION_ACCOUNT -> {
                    TwoSidedAccountGrid(
                        tableType = tableType,
                        drRows = realisationDrRows,
                        crRows = realisationCrRows,
                        onUpdateDr = onUpdateRealisationDrRow,
                        onUpdateCr = onUpdateRealisationCrRow,
                        onAddDr = onAddRealisationDrRow,
                        onAddCr = onAddRealisationCrRow,
                        onDeleteDr = onDeleteRealisationDrRow,
                        onDeleteCr = onDeleteRealisationCrRow
                    )
                }
                AccountingTableType.CONSIGNMENT_ACCOUNT -> {
                    TwoSidedAccountGrid(
                        tableType = tableType,
                        drRows = consignmentDrRows,
                        crRows = consignmentCrRows,
                        onUpdateDr = onUpdateConsignmentDrRow,
                        onUpdateCr = onUpdateConsignmentCrRow,
                        onAddDr = onAddConsignmentDrRow,
                        onAddCr = onAddConsignmentCrRow,
                        onDeleteDr = onDeleteConsignmentDrRow,
                        onDeleteCr = onDeleteConsignmentCrRow
                    )
                }
                AccountingTableType.BALANCE_SHEET -> {
                    BalanceSheetGrid(
                        liabilities = bsLiabilityRows,
                        assets = bsAssetRows,
                        onUpdateLiability = onUpdateBsLiabilityRow,
                        onUpdateAsset = onUpdateBsAssetRow,
                        onAddLiability = onAddBsLiabilityRow,
                        onAddAsset = onAddBsAssetRow,
                        onDeleteLiability = onDeleteBsLiabilityRow,
                        onDeleteAsset = onDeleteBsAssetRow
                    )
                }
                AccountingTableType.LEDGER -> {
                    LedgerAccountGrid(
                        accountName = ledgerAccountName,
                        onUpdateAccountName = onUpdateLedgerAccountName,
                        drRows = ledgerDrRows,
                        crRows = ledgerCrRows,
                        onUpdateDr = onUpdateLedgerDrRow,
                        onUpdateCr = onUpdateLedgerCrRow,
                        onAddDr = onAddLedgerDrRow,
                        onAddCr = onAddLedgerCrRow,
                        onDeleteDr = onDeleteLedgerDrRow,
                        onDeleteCr = onDeleteLedgerCrRow
                    )
                }
                AccountingTableType.JOURNAL -> {
                    JournalEntryGrid(
                        journalRows = journalRows,
                        onUpdateRow = onUpdateJournalRow,
                        onAddRow = onAddJournalRow,
                        onDeleteRow = onDeleteJournalRow
                    )
                }
                AccountingTableType.TRIAL_BALANCE -> {
                    TrialBalanceGrid(
                        trialBalanceRows = trialBalanceRows,
                        onUpdateRow = onUpdateTrialBalanceRow,
                        onAddRow = onAddTrialBalanceRow,
                        onDeleteRow = onDeleteTrialBalanceRow
                    )
                }
                AccountingTableType.BRS -> {
                    BrsGrid(
                        startingBalance = brsStartingBalance,
                        onUpdateStartingBalance = onUpdateBrsStartingBalance,
                        brsRows = brsRows,
                        onUpdateRow = onUpdateBrsRow,
                        onAddRow = onAddBrsRow,
                        onDeleteRow = onDeleteBrsRow
                    )
                }
                AccountingTableType.CASH_BOOK -> {
                    CashBookGrid(
                        cashBookDrRows = cashBookDrRows,
                        cashBookCrRows = cashBookCrRows,
                        onUpdateDr = onUpdateCashBookDr,
                        onUpdateCr = onUpdateCashBookCr,
                        onAddDr = onAddCashBookDr,
                        onAddCr = onAddCashBookCr,
                        onDeleteDr = onDeleteCashBookDr,
                        onDeleteCr = onDeleteCashBookCr
                    )
                }
                AccountingTableType.PARTNERS_CAPITAL -> {
                    PartnersCapitalGrid(
                        drRows = partnersCapitalDrRows,
                        crRows = partnersCapitalCrRows,
                        onUpdateDr = onUpdatePartnersCapitalDr,
                        onUpdateCr = onUpdatePartnersCapitalCr,
                        onAddDr = onAddPartnersCapitalDr,
                        onAddCr = onAddPartnersCapitalCr,
                        onDeleteDr = onDeletePartnersCapitalDr,
                        onDeleteCr = onDeletePartnersCapitalCr
                    )
                }
                AccountingTableType.PETTY_CASH_BOOK -> {
                    PettyCashGrid(
                        pettyCashRows = pettyCashRows,
                        onUpdateRow = onUpdatePettyCashRow,
                        onAddRow = onAddPettyCashRow,
                        onDeleteRow = onDeletePettyCashRow
                    )
                }
                AccountingTableType.NONE -> {}
            }
        }
    }
}

@Composable
fun TradingAccountGrid(
    drRows: List<AccountRow>,
    crRows: List<AccountRow>,
    onUpdateDr: (Int, String, String) -> Unit,
    onUpdateCr: (Int, String, String) -> Unit,
    onAddDr: () -> Unit,
    onAddCr: () -> Unit,
    onDeleteDr: (Int) -> Unit,
    onDeleteCr: (Int) -> Unit
) {
    val scrollState = rememberScrollState()
    val totalDr = drRows.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val totalCr = crRows.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val grossProfit = totalCr - totalDr

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .width(720.dp)
                    .border(1.dp, VintageLine)
            ) {
                // Table Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VintagePaperBg)
                        .border(1.dp, VintageLine)
                ) {
                    TableCell(text = "Dr. Particulars", weight = 0.32f, isHeader = true)
                    TableCell(text = "Amount (₹)", weight = 0.18f, isHeader = true, alignRight = true)
                    TableCell(text = "Cr. Particulars", weight = 0.32f, isHeader = true)
                    TableCell(text = "Amount (₹)", weight = 0.18f, isHeader = true, alignRight = true)
                }

                // Table Body Rows
                val rowCount = maxOf(drRows.size, crRows.size)
                for (i in 0 until rowCount) {
                    val drItem = drRows.getOrNull(i)
                    val crItem = crRows.getOrNull(i)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.5.dp, VintageLine.copy(alpha = 0.5f))
                    ) {
                        // Debit Side
                        if (drItem != null) {
                            EditableParticularsCell(
                                value = drItem.particulars,
                                onValueChange = { onUpdateDr(i, it, drItem.amount) },
                                weight = 0.32f,
                                onDelete = if (drRows.size > 1) { { onDeleteDr(i) } } else null
                            )
                            EditableAmountCell(
                                value = drItem.amount,
                                onValueChange = { onUpdateDr(i, drItem.particulars, it) },
                                weight = 0.18f
                            )
                        } else {
                            TableCell(text = "", weight = 0.32f)
                            TableCell(text = "", weight = 0.18f)
                        }

                        // Credit Side
                        if (crItem != null) {
                            EditableParticularsCell(
                                value = crItem.particulars,
                                onValueChange = { onUpdateCr(i, it, crItem.amount) },
                                weight = 0.32f,
                                onDelete = if (crRows.size > 1) { { onDeleteCr(i) } } else null
                            )
                            EditableAmountCell(
                                value = crItem.amount,
                                onValueChange = { onUpdateCr(i, crItem.particulars, it) },
                                weight = 0.18f
                            )
                        } else {
                            TableCell(text = "", weight = 0.32f)
                            TableCell(text = "", weight = 0.18f)
                        }
                    }
                }

                // Totals Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VintagePaperBg.copy(alpha = 0.7f))
                        .border(1.dp, VintageLine)
                ) {
                    TableCell(text = "Total Debit (Dr)", weight = 0.32f, isHeader = true)
                    TableCell(text = "₹${currencyFormatter.format(totalDr)}", weight = 0.18f, isHeader = true, alignRight = true)
                    TableCell(text = "Total Credit (Cr)", weight = 0.32f, isHeader = true)
                    TableCell(text = "₹${currencyFormatter.format(totalCr)}", weight = 0.18f, isHeader = true, alignRight = true)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onAddDr,
                    modifier = Modifier.testTag("add_debit_row_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.height(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Dr Row", style = MaterialTheme.typography.labelSmall)
                }
                OutlinedButton(
                    onClick = onAddCr,
                    modifier = Modifier.testTag("add_credit_row_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.height(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Cr Row", style = MaterialTheme.typography.labelSmall)
                }
            }

            // Gross Profit Indicator
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (grossProfit >= 0) VintageGreenBg else VintageRedBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (grossProfit >= 0) VintageGreen else VintageRed)
            ) {
                Text(
                    text = if (grossProfit >= 0) "Gross Profit c/d: ₹${currencyFormatter.format(grossProfit)}"
                    else "Gross Loss c/d: ₹${currencyFormatter.format(-grossProfit)}",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (grossProfit >= 0) VintageGreen else VintageRed
                    )
                )
            }
        }
    }
}

@Composable
fun BalanceSheetGrid(
    liabilities: List<AccountRow>,
    assets: List<AccountRow>,
    onUpdateLiability: (Int, String, String) -> Unit,
    onUpdateAsset: (Int, String, String) -> Unit,
    onAddLiability: () -> Unit,
    onAddAsset: () -> Unit,
    onDeleteLiability: (Int) -> Unit,
    onDeleteAsset: (Int) -> Unit
) {
    val scrollState = rememberScrollState()
    val totalLiabilities = liabilities.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val totalAssets = assets.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val difference = totalAssets - totalLiabilities

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .width(720.dp)
                    .border(1.dp, VintageLine)
            ) {
                // Table Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VintagePaperBg)
                        .border(1.dp, VintageLine)
                ) {
                    TableCell(text = "Liabilities & Capital", weight = 0.32f, isHeader = true)
                    TableCell(text = "Amount (₹)", weight = 0.18f, isHeader = true, alignRight = true)
                    TableCell(text = "Assets & Properties", weight = 0.32f, isHeader = true)
                    TableCell(text = "Amount (₹)", weight = 0.18f, isHeader = true, alignRight = true)
                }

                // Table Body Rows
                val rowCount = maxOf(liabilities.size, assets.size)
                for (i in 0 until rowCount) {
                    val liabItem = liabilities.getOrNull(i)
                    val assetItem = assets.getOrNull(i)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.5.dp, VintageLine.copy(alpha = 0.5f))
                    ) {
                        // Liabilities Side
                        if (liabItem != null) {
                            EditableParticularsCell(
                                value = liabItem.particulars,
                                onValueChange = { onUpdateLiability(i, it, liabItem.amount) },
                                weight = 0.32f,
                                onDelete = if (liabilities.size > 1) { { onDeleteLiability(i) } } else null
                            )
                            EditableAmountCell(
                                value = liabItem.amount,
                                onValueChange = { onUpdateLiability(i, liabItem.particulars, it) },
                                weight = 0.18f
                            )
                        } else {
                            TableCell(text = "", weight = 0.32f)
                            TableCell(text = "", weight = 0.18f)
                        }

                        // Assets Side
                        if (assetItem != null) {
                            EditableParticularsCell(
                                value = assetItem.particulars,
                                onValueChange = { onUpdateAsset(i, it, assetItem.amount) },
                                weight = 0.32f,
                                onDelete = if (assets.size > 1) { { onDeleteAsset(i) } } else null
                            )
                            EditableAmountCell(
                                value = assetItem.amount,
                                onValueChange = { onUpdateAsset(i, assetItem.particulars, it) },
                                weight = 0.18f
                            )
                        } else {
                            TableCell(text = "", weight = 0.32f)
                            TableCell(text = "", weight = 0.18f)
                        }
                    }
                }

                // Totals Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VintagePaperBg.copy(alpha = 0.7f))
                        .border(1.dp, VintageLine)
                ) {
                    TableCell(text = "Total Liabilities", weight = 0.32f, isHeader = true)
                    TableCell(text = "₹${currencyFormatter.format(totalLiabilities)}", weight = 0.18f, isHeader = true, alignRight = true)
                    TableCell(text = "Total Assets", weight = 0.32f, isHeader = true)
                    TableCell(text = "₹${currencyFormatter.format(totalAssets)}", weight = 0.18f, isHeader = true, alignRight = true)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onAddLiability,
                    modifier = Modifier.testTag("add_liability_row_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.height(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Liability", style = MaterialTheme.typography.labelSmall)
                }
                OutlinedButton(
                    onClick = onAddAsset,
                    modifier = Modifier.testTag("add_asset_row_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.height(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Asset", style = MaterialTheme.typography.labelSmall)
                }
            }

            // Tally Status Indicator
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (difference == 0.0) VintageGreenBg else VintageRedBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (difference == 0.0) VintageGreen else VintageRed)
            ) {
                Text(
                    text = if (difference == 0.0) "✓ Balance Sheet Tallied"
                    else "Diff: ₹${currencyFormatter.format(difference)}",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (difference == 0.0) VintageGreen else VintageRed
                    )
                )
            }
        }
    }
}

@Composable
fun LedgerAccountGrid(
    accountName: String,
    onUpdateAccountName: (String) -> Unit,
    drRows: List<LedgerRow>,
    crRows: List<LedgerRow>,
    onUpdateDr: (Int, String, String, String, String) -> Unit,
    onUpdateCr: (Int, String, String, String, String) -> Unit,
    onAddDr: () -> Unit,
    onAddCr: () -> Unit,
    onDeleteDr: (Int) -> Unit,
    onDeleteCr: (Int) -> Unit
) {
    val scrollState = rememberScrollState()
    val totalDr = drRows.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val totalCr = crRows.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val balance = totalDr - totalCr

    Column(modifier = Modifier.fillMaxWidth()) {
        // Account Name Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Account Title:",
                style = MaterialTheme.typography.labelMedium,
                color = VintageNavy,
                modifier = Modifier.padding(end = 8.dp)
            )
            BasicTextField(
                value = accountName,
                onValueChange = onUpdateAccountName,
                textStyle = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = VintageInk
                ),
                modifier = Modifier
                    .weight(1f)
                    .background(VintagePaperBg, RoundedCornerShape(3.dp))
                    .border(1.dp, VintageLine, RoundedCornerShape(3.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .width(880.dp)
                    .border(1.dp, VintageLine)
            ) {
                // Table Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VintagePaperBg)
                        .border(1.dp, VintageLine)
                ) {
                    TableCell(text = "Dr Date", weight = 0.10f, isHeader = true)
                    TableCell(text = "Dr Particulars", weight = 0.26f, isHeader = true)
                    TableCell(text = "J.F.", weight = 0.06f, isHeader = true)
                    TableCell(text = "Amount (₹)", weight = 0.12f, isHeader = true, alignRight = true)
                    TableCell(text = "Cr Date", weight = 0.10f, isHeader = true)
                    TableCell(text = "Cr Particulars", weight = 0.26f, isHeader = true)
                    TableCell(text = "J.F.", weight = 0.06f, isHeader = true)
                    TableCell(text = "Amount (₹)", weight = 0.12f, isHeader = true, alignRight = true)
                }

                // Table Body Rows
                val rowCount = maxOf(drRows.size, crRows.size)
                for (i in 0 until rowCount) {
                    val drItem = drRows.getOrNull(i)
                    val crItem = crRows.getOrNull(i)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.5.dp, VintageLine.copy(alpha = 0.5f))
                    ) {
                        // Debit Side
                        if (drItem != null) {
                            EditableLedgerCell(
                                value = drItem.date,
                                onValueChange = { onUpdateDr(i, it, drItem.particulars, drItem.jf, drItem.amount) },
                                weight = 0.10f
                            )
                            EditableParticularsCell(
                                value = drItem.particulars,
                                onValueChange = { onUpdateDr(i, drItem.date, it, drItem.jf, drItem.amount) },
                                weight = 0.26f,
                                onDelete = if (drRows.size > 1) { { onDeleteDr(i) } } else null
                            )
                            EditableLedgerCell(
                                value = drItem.jf,
                                onValueChange = { onUpdateDr(i, drItem.date, drItem.particulars, it, drItem.amount) },
                                weight = 0.06f
                            )
                            EditableAmountCell(
                                value = drItem.amount,
                                onValueChange = { onUpdateDr(i, drItem.date, drItem.particulars, drItem.jf, it) },
                                weight = 0.12f
                            )
                        } else {
                            TableCell(text = "", weight = 0.10f)
                            TableCell(text = "", weight = 0.26f)
                            TableCell(text = "", weight = 0.06f)
                            TableCell(text = "", weight = 0.12f)
                        }

                        // Credit Side
                        if (crItem != null) {
                            EditableLedgerCell(
                                value = crItem.date,
                                onValueChange = { onUpdateCr(i, it, crItem.particulars, crItem.jf, crItem.amount) },
                                weight = 0.10f
                            )
                            EditableParticularsCell(
                                value = crItem.particulars,
                                onValueChange = { onUpdateCr(i, crItem.date, it, crItem.jf, crItem.amount) },
                                weight = 0.26f,
                                onDelete = if (crRows.size > 1) { { onDeleteCr(i) } } else null
                            )
                            EditableLedgerCell(
                                value = crItem.jf,
                                onValueChange = { onUpdateCr(i, crItem.date, crItem.particulars, it, crItem.amount) },
                                weight = 0.06f
                            )
                            EditableAmountCell(
                                value = crItem.amount,
                                onValueChange = { onUpdateCr(i, crItem.date, crItem.particulars, crItem.jf, it) },
                                weight = 0.12f
                            )
                        } else {
                            TableCell(text = "", weight = 0.10f)
                            TableCell(text = "", weight = 0.26f)
                            TableCell(text = "", weight = 0.06f)
                            TableCell(text = "", weight = 0.12f)
                        }
                    }
                }

                // Totals Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VintagePaperBg.copy(alpha = 0.7f))
                        .border(1.dp, VintageLine)
                ) {
                    TableCell(text = "Total Dr", weight = 0.42f, isHeader = true)
                    TableCell(text = "₹${currencyFormatter.format(totalDr)}", weight = 0.12f, isHeader = true, alignRight = true)
                    TableCell(text = "Total Cr", weight = 0.42f, isHeader = true)
                    TableCell(text = "₹${currencyFormatter.format(totalCr)}", weight = 0.12f, isHeader = true, alignRight = true)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onAddDr,
                    modifier = Modifier.testTag("add_ledger_dr_row_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.height(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Dr Entry", style = MaterialTheme.typography.labelSmall)
                }
                OutlinedButton(
                    onClick = onAddCr,
                    modifier = Modifier.testTag("add_ledger_cr_row_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.height(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Cr Entry", style = MaterialTheme.typography.labelSmall)
                }
            }

            // Running Balance Indicator
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = VintagePaperBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, VintageNavy)
            ) {
                Text(
                    text = if (balance >= 0) "Dr Balance c/d: ₹${currencyFormatter.format(balance)}"
                    else "Cr Balance c/d: ₹${currencyFormatter.format(-balance)}",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = VintageNavy
                    )
                )
            }
        }
    }
}

// ---------------- Helper Components for Cells ----------------

@Composable
fun TableCell(
    text: String,
    weight: Float,
    isHeader: Boolean = false,
    alignRight: Boolean = false
) {
    Box(
        modifier = Modifier
            .width((weight * 720).dp)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = if (alignRight) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Text(
            text = text,
            style = if (isHeader) MaterialTheme.typography.labelMedium else MaterialTheme.typography.bodySmall,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            color = if (isHeader) VintageNavy else VintageInk,
            textAlign = if (alignRight) TextAlign.End else TextAlign.Start
        )
    }
}

@Composable
fun EditableParticularsCell(
    value: String,
    onValueChange: (String) -> Unit,
    weight: Float,
    onDelete: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .width((weight * 720).dp)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontFamily = FontFamily.Serif,
                fontSize = 13.sp,
                color = VintageInk
            ),
            modifier = Modifier.weight(1f)
        )
        if (onDelete != null) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Row",
                tint = VintageInkSoft.copy(alpha = 0.5f),
                modifier = Modifier
                    .height(14.dp)
                    .width(14.dp)
                    .clickable { onDelete() }
            )
        }
    }
}

@Composable
fun EditableAmountCell(
    value: String,
    onValueChange: (String) -> Unit,
    weight: Float
) {
    Box(
        modifier = Modifier
            .width((weight * 720).dp)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        BasicTextField(
            value = value,
            onValueChange = { newVal ->
                // Allow digits and decimal point
                if (newVal.isEmpty() || newVal.matches(Regex("""^\d*\.?\d*$"""))) {
                    onValueChange(newVal)
                }
            },
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                color = VintageInk,
                textAlign = TextAlign.End
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EditableLedgerCell(
    value: String,
    onValueChange: (String) -> Unit,
    weight: Float
) {
    Box(
        modifier = Modifier
            .width((weight * 720).dp)
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = VintageInk
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
