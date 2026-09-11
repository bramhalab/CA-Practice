package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import com.example.model.PracticeQuestion
import com.example.model.Subject
import com.example.model.TrialBalanceRow
import com.example.ui.theme.VintageGreen
import com.example.ui.theme.VintageGreenBg
import com.example.ui.theme.VintageInk
import com.example.ui.theme.VintageInkSoft
import com.example.ui.theme.VintageLine
import com.example.ui.theme.VintageNavy
import com.example.ui.theme.VintagePaperBg
import com.example.ui.theme.VintagePaperSheet

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubjectiveAnswerView(
    question: PracticeQuestion,
    userAnswer: String,
    onAnswerChange: (String) -> Unit,
    showModelAnswer: Boolean,
    onToggleModelAnswer: () -> Unit,
    activeTableType: AccountingTableType,
    onSelectTableType: (AccountingTableType) -> Unit,
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
    // Format Search State
    formatSearchQuery: String = "",
    onFormatSearchQueryChange: (String) -> Unit = {},
    isFormatSearchExpanded: Boolean = false,
    onToggleFormatSearchExpanded: (Boolean) -> Unit = {},
    // All Accounting Format Models
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
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Module specific helper toolbars
        if (question.subject == Subject.LAW) {
            LawStructuringToolbar(
                currentText = userAnswer,
                onApplyStructure = {
                    val structuredTemplate = buildString {
                        append("I) Provision:\n")
                        append("Under the relevant provisions of the statute...\n\n")
                        append("II) Analysis and Conclusion:\n")
                        append("Applying the statutory provisions to the facts given...\n")
                    }
                    onAnswerChange(structuredTemplate)
                }
            )
        } else if (question.subject == Subject.ACC) {
            AccountingFormatSearchBar(
                searchQuery = formatSearchQuery,
                onSearchQueryChange = onFormatSearchQueryChange,
                isExpanded = isFormatSearchExpanded,
                onToggleExpanded = onToggleFormatSearchExpanded,
                activeTableType = activeTableType,
                onSelectFormat = { format ->
                    val cmd = format.command
                    val newText = if (userAnswer.isBlank()) cmd else "$userAnswer\n$cmd"
                    onAnswerChange(newText)
                    onSelectTableType(format)
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Ruled Notebook Answer Script Area
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, VintageLine, RoundedCornerShape(4.dp)),
            shape = RoundedCornerShape(4.dp),
            color = VintagePaperBg
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        // Notebook ruling lines (subtle horizontal ruled lines every 28dp)
                        val lineSpacing = 28.dp.toPx()
                        var y = lineSpacing
                        while (y < size.height) {
                            drawLine(
                                color = VintageLine.copy(alpha = 0.45f),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1f
                            )
                            y += lineSpacing
                        }
                    }
                    .padding(12.dp)
            ) {
                if (userAnswer.isEmpty()) {
                    Text(
                        text = if (question.subject == Subject.ACC) {
                            "Type your answer here or enter /Trading-Account, /Balance-Sheet, /Ledger to render interactive tables..."
                        } else if (question.subject == Subject.LAW) {
                            "Draft your answer following ICAI standard: I) Provision, II) Analysis & Conclusion..."
                        } else {
                            "Type your answer here in your own words..."
                        },
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = VintageInkSoft.copy(alpha = 0.7f),
                            lineHeight = 28.sp
                        )
                    )
                }

                BasicTextField(
                    value = userAnswer,
                    onValueChange = { newText ->
                        onAnswerChange(newText)
                        // Slash command detector
                        val matchingFormat = AccountingTableType.findByCommandOrQuery(newText)
                        if (matchingFormat != null && activeTableType != matchingFormat) {
                            onSelectTableType(matchingFormat)
                        }
                    },
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 15.sp,
                        color = VintageInk,
                        lineHeight = 28.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("subjective_answer_input")
                )
            }
        }

        // Render Interactive Accounting Table if triggered
        if (activeTableType != AccountingTableType.NONE) {
            AccountingTableContainer(
                tableType = activeTableType,
                tradingDrRows = tradingDrRows,
                tradingCrRows = tradingCrRows,
                onUpdateTradingDrRow = onUpdateTradingDrRow,
                onUpdateTradingCrRow = onUpdateTradingCrRow,
                onAddTradingDrRow = onAddTradingDrRow,
                onAddTradingCrRow = onAddTradingCrRow,
                onDeleteTradingDrRow = onDeleteTradingDrRow,
                onDeleteTradingCrRow = onDeleteTradingCrRow,
                plDrRows = plDrRows,
                plCrRows = plCrRows,
                onUpdatePlDrRow = onUpdatePlDrRow,
                onUpdatePlCrRow = onUpdatePlCrRow,
                onAddPlDrRow = onAddPlDrRow,
                onAddPlCrRow = onAddPlCrRow,
                onDeletePlDrRow = onDeletePlDrRow,
                onDeletePlCrRow = onDeletePlCrRow,
                manufacturingDrRows = manufacturingDrRows,
                manufacturingCrRows = manufacturingCrRows,
                onUpdateManufacturingDrRow = onUpdateManufacturingDrRow,
                onUpdateManufacturingCrRow = onUpdateManufacturingCrRow,
                onAddManufacturingDrRow = onAddManufacturingDrRow,
                onAddManufacturingCrRow = onAddManufacturingCrRow,
                onDeleteManufacturingDrRow = onDeleteManufacturingDrRow,
                onDeleteManufacturingCrRow = onDeleteManufacturingCrRow,
                revaluationDrRows = revaluationDrRows,
                revaluationCrRows = revaluationCrRows,
                onUpdateRevaluationDrRow = onUpdateRevaluationDrRow,
                onUpdateRevaluationCrRow = onUpdateRevaluationCrRow,
                onAddRevaluationDrRow = onAddRevaluationDrRow,
                onAddRevaluationCrRow = onAddRevaluationCrRow,
                onDeleteRevaluationDrRow = onDeleteRevaluationDrRow,
                onDeleteRevaluationCrRow = onDeleteRevaluationCrRow,
                realisationDrRows = realisationDrRows,
                realisationCrRows = realisationCrRows,
                onUpdateRealisationDrRow = onUpdateRealisationDrRow,
                onUpdateRealisationCrRow = onUpdateRealisationCrRow,
                onAddRealisationDrRow = onAddRealisationDrRow,
                onAddRealisationCrRow = onAddRealisationCrRow,
                onDeleteRealisationDrRow = onDeleteRealisationDrRow,
                onDeleteRealisationCrRow = onDeleteRealisationCrRow,
                consignmentDrRows = consignmentDrRows,
                consignmentCrRows = consignmentCrRows,
                onUpdateConsignmentDrRow = onUpdateConsignmentDrRow,
                onUpdateConsignmentCrRow = onUpdateConsignmentCrRow,
                onAddConsignmentDrRow = onAddConsignmentDrRow,
                onAddConsignmentCrRow = onAddConsignmentCrRow,
                onDeleteConsignmentDrRow = onDeleteConsignmentDrRow,
                onDeleteConsignmentCrRow = onDeleteConsignmentCrRow,
                bsLiabilityRows = bsLiabilityRows,
                bsAssetRows = bsAssetRows,
                onUpdateBsLiabilityRow = onUpdateBsLiabilityRow,
                onUpdateBsAssetRow = onUpdateBsAssetRow,
                onAddBsLiabilityRow = onAddBsLiabilityRow,
                onAddBsAssetRow = onAddBsAssetRow,
                onDeleteBsLiabilityRow = onDeleteBsLiabilityRow,
                onDeleteBsAssetRow = onDeleteBsAssetRow,
                ledgerAccountName = ledgerAccountName,
                onUpdateLedgerAccountName = onUpdateLedgerAccountName,
                ledgerDrRows = ledgerDrRows,
                ledgerCrRows = ledgerCrRows,
                onUpdateLedgerDrRow = onUpdateLedgerDrRow,
                onUpdateLedgerCrRow = onUpdateLedgerCrRow,
                onAddLedgerDrRow = onAddLedgerDrRow,
                onAddLedgerCrRow = onAddLedgerCrRow,
                onDeleteLedgerDrRow = onDeleteLedgerDrRow,
                onDeleteLedgerCrRow = onDeleteLedgerCrRow,
                journalRows = journalRows,
                onUpdateJournalRow = onUpdateJournalRow,
                onAddJournalRow = onAddJournalRow,
                onDeleteJournalRow = onDeleteJournalRow,
                trialBalanceRows = trialBalanceRows,
                onUpdateTrialBalanceRow = onUpdateTrialBalanceRow,
                onAddTrialBalanceRow = onAddTrialBalanceRow,
                onDeleteTrialBalanceRow = onDeleteTrialBalanceRow,
                brsStartingBalance = brsStartingBalance,
                onUpdateBrsStartingBalance = onUpdateBrsStartingBalance,
                brsRows = brsRows,
                onUpdateBrsRow = onUpdateBrsRow,
                onAddBrsRow = onAddBrsRow,
                onDeleteBrsRow = onDeleteBrsRow,
                cashBookDrRows = cashBookDrRows,
                cashBookCrRows = cashBookCrRows,
                onUpdateCashBookDr = onUpdateCashBookDr,
                onUpdateCashBookCr = onUpdateCashBookCr,
                onAddCashBookDr = onAddCashBookDr,
                onAddCashBookCr = onAddCashBookCr,
                onDeleteCashBookDr = onDeleteCashBookDr,
                onDeleteCashBookCr = onDeleteCashBookCr,
                partnersCapitalDrRows = partnersCapitalDrRows,
                partnersCapitalCrRows = partnersCapitalCrRows,
                onUpdatePartnersCapitalDr = onUpdatePartnersCapitalDr,
                onUpdatePartnersCapitalCr = onUpdatePartnersCapitalCr,
                onAddPartnersCapitalDr = onAddPartnersCapitalDr,
                onAddPartnersCapitalCr = onAddPartnersCapitalCr,
                onDeletePartnersCapitalDr = onDeletePartnersCapitalDr,
                onDeletePartnersCapitalCr = onDeletePartnersCapitalCr,
                pettyCashRows = pettyCashRows,
                onUpdatePettyCashRow = onUpdatePettyCashRow,
                onAddPettyCashRow = onAddPettyCashRow,
                onDeletePettyCashRow = onDeletePettyCashRow,
                onChangeFormat = { onToggleFormatSearchExpanded(true) },
                onCloseTable = { onSelectTableType(AccountingTableType.NONE) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Compare with ICAI Answer Button
        OutlinedButton(
            onClick = onToggleModelAnswer,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("toggle_icai_answer_btn"),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (showModelAnswer) VintageNavy else VintageInk
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (showModelAnswer) VintageNavy else VintageLine
            )
        ) {
            Icon(
                imageVector = if (showModelAnswer) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (showModelAnswer) "Hide ICAI Model Answer" else "Compare with ICAI Model Answer",
                style = MaterialTheme.typography.labelMedium
            )
        }

        // Model Answer Box
        AnimatedVisibility(
            visible = showModelAnswer,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .testTag("icai_model_answer_box"),
                shape = RoundedCornerShape(4.dp),
                color = VintageGreenBg,
                border = androidx.compose.foundation.BorderStroke(1.2.dp, VintageGreen)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ICAI-STYLE MODEL ANSWER",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = VintageGreen,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = VintageGreen
                        ) {
                            Text(
                                text = "OFFICIAL SYLLABUS",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = question.modelAnswer,
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontSize = 14.5.sp,
                            lineHeight = 22.sp,
                            color = VintageInk
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LawStructuringToolbar(
    currentText: String,
    onApplyStructure: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = VintagePaperBg.copy(alpha = 0.6f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FormatListNumbered,
                    contentDescription = null,
                    tint = VintageNavy,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ICAI Law Structure:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = VintageNavy
                )
            }

            AssistChip(
                onClick = onApplyStructure,
                label = { Text("Pre-fill Headings (I & II)", style = MaterialTheme.typography.labelSmall) },
                leadingIcon = {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = VintageNavy
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    labelColor = VintageNavy
                ),
                border = AssistChipDefaults.assistChipBorder(true, borderColor = VintageLine),
                modifier = Modifier.testTag("prefill_law_structure_btn")
            )
        }
    }
}
