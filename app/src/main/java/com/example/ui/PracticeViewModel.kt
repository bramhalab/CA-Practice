package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.QuestionRepository
import com.example.data.local.AppDatabase
import com.example.data.local.AttemptedQuestionEntity
import com.example.data.local.ProgressStatsEntity
import com.example.data.local.SyncedQuestionEntity
import com.example.model.AccountRow
import com.example.model.AccountingTableType
import com.example.model.BrsRow
import com.example.model.CashBookRow
import com.example.model.Depth
import com.example.model.JournalRow
import com.example.model.LedgerRow
import com.example.model.PartnersCapitalRow
import com.example.model.PettyCashRow
import com.example.model.PracticeQuestion
import com.example.model.QuestionMode
import com.example.model.Subject
import com.example.model.TrialBalanceRow
import com.example.network.GeminiQuestionExtractor
import com.example.network.HuggingFaceService
import com.example.util.AudioHapticManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

data class PracticeUiState(
    val selectedSubject: Subject = Subject.ALL,
    val selectedChapter: String = "all",
    val selectedDepth: Depth = Depth.ALL,
    val availableChapters: List<String> = emptyList(),
    val questionCount: Int = 0,
    val currentQuestion: PracticeQuestion? = null,
    val isFetchingQuestion: Boolean = false,
    val userAnswerText: String = "",
    val showModelAnswer: Boolean = false,
    val activeTableType: AccountingTableType = AccountingTableType.NONE,
    val tradingDrRows: List<AccountRow> = emptyList(),
    val tradingCrRows: List<AccountRow> = emptyList(),
    val plDrRows: List<AccountRow> = emptyList(),
    val plCrRows: List<AccountRow> = emptyList(),
    val manufacturingDrRows: List<AccountRow> = emptyList(),
    val manufacturingCrRows: List<AccountRow> = emptyList(),
    val revaluationDrRows: List<AccountRow> = emptyList(),
    val revaluationCrRows: List<AccountRow> = emptyList(),
    val realisationDrRows: List<AccountRow> = emptyList(),
    val realisationCrRows: List<AccountRow> = emptyList(),
    val consignmentDrRows: List<AccountRow> = emptyList(),
    val consignmentCrRows: List<AccountRow> = emptyList(),
    val bsLiabilityRows: List<AccountRow> = emptyList(),
    val bsAssetRows: List<AccountRow> = emptyList(),
    val ledgerAccountName: String = "Cash Account",
    val ledgerDrRows: List<LedgerRow> = emptyList(),
    val ledgerCrRows: List<LedgerRow> = emptyList(),
    val journalRows: List<JournalRow> = emptyList(),
    val trialBalanceRows: List<TrialBalanceRow> = emptyList(),
    val brsStartingBalance: String = "50000",
    val brsRows: List<BrsRow> = emptyList(),
    val cashBookDrRows: List<CashBookRow> = emptyList(),
    val cashBookCrRows: List<CashBookRow> = emptyList(),
    val partnersCapitalDrRows: List<PartnersCapitalRow> = emptyList(),
    val partnersCapitalCrRows: List<PartnersCapitalRow> = emptyList(),
    val pettyCashRows: List<PettyCashRow> = emptyList(),
    val formatSearchQuery: String = "",
    val isFormatSearchExpanded: Boolean = false,
    val selectedOptionIndex: Int? = null,
    val isHintModalVisible: Boolean = false,
    val isHintUnlocked: Boolean = false,
    val questionsAttemptedCount: Int = 0,
    val questionsAnsweredCount: Int = 0,
    val questionsViewedCount: Int = 0,
    val hasSubmittedCurrentSubjective: Boolean = false,
    val mcqCorrectCount: Int = 0,
    val mcqAttemptedCount: Int = 0,
    val isWritingViewOpen: Boolean = false,
    val isQuestionSheetOpenInWritingView: Boolean = false,
    val isGrading: Boolean = false,
    val gradingResult: GeminiQuestionExtractor.GradingResult? = null
)

class PracticeViewModel(application: Application) : AndroidViewModel(application) {

    private val audioHapticManager = AudioHapticManager(application.applicationContext)
    private val appDao = AppDatabase.getInstance(application).appDao()
    private val _uiState = MutableStateFlow(PracticeUiState())
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()

    private var loadQuestionJob: Job? = null
    private var lastQuestionId: Int = -1

    init {
        // Observe Room persistence as the source of truth for stats
        viewModelScope.launch {
            appDao.observeAllProgressStats().collect { statsList ->
                val totalAnswered = statsList.sumOf { it.questionsAnsweredCount }
                val totalAttempted = statsList.sumOf { it.questionsAttemptedCount }
                val finalAnswered = if (totalAnswered > 0) totalAnswered else totalAttempted
                val totalViewed = statsList.sumOf { it.questionsViewedCount }
                val totalCorrect = statsList.sumOf { it.mcqCorrectCount }
                val totalMcq = statsList.sumOf { it.mcqAttemptedCount }
                _uiState.update {
                    it.copy(
                        questionsAnsweredCount = finalAnswered,
                        questionsAttemptedCount = finalAnswered,
                        questionsViewedCount = maxOf(it.questionsViewedCount, totalViewed),
                        mcqCorrectCount = totalCorrect,
                        mcqAttemptedCount = totalMcq
                    )
                }
            }
        }

        updateAvailableChaptersAndPool()
        loadNextQuestion(initial = true)

        // Preload dataset file metadata tree in background
        viewModelScope.launch(Dispatchers.IO) {
            try {
                HuggingFaceService.getCachedFiles()
                val count = QuestionRepository.getAvailableFileCount(
                    _uiState.value.selectedSubject,
                    _uiState.value.selectedChapter,
                    _uiState.value.selectedDepth
                )
                _uiState.update { it.copy(questionCount = count) }
            } catch (_: Exception) {
                // Ignore background pre-fetch failures
            }
        }
    }

    companion object {
        fun getDefaultTradingDr(): List<AccountRow> = listOf(
            AccountRow(particulars = "To Opening Stock", amount = ""),
            AccountRow(particulars = "To Purchases (less returns)", amount = ""),
            AccountRow(particulars = "To Direct Wages & Freight", amount = "")
        )

        fun getDefaultTradingCr(): List<AccountRow> = listOf(
            AccountRow(particulars = "By Sales (less returns)", amount = ""),
            AccountRow(particulars = "By Closing Stock", amount = "")
        )

        fun getDefaultBsLiabilities(): List<AccountRow> = listOf(
            AccountRow(particulars = "Capital (Opening)", amount = "250000"),
            AccountRow(particulars = "Add: Net Profit for Year", amount = "65000"),
            AccountRow(particulars = "Less: Drawings", amount = "15000"),
            AccountRow(particulars = "Bank Loan (Secured)", amount = "100000"),
            AccountRow(particulars = "Sundry Creditors", amount = "45000")
        )

        fun getDefaultBsAssets(): List<AccountRow> = listOf(
            AccountRow(particulars = "Plant & Machinery", amount = "180000"),
            AccountRow(particulars = "Furniture & Fixtures", amount = "40000"),
            AccountRow(particulars = "Closing Stock", amount = "40000"),
            AccountRow(particulars = "Sundry Debtors", amount = "120000"),
            AccountRow(particulars = "Cash & Bank Balances", amount = "65000")
        )

        fun getDefaultLedgerDr(): List<LedgerRow> = listOf(
            LedgerRow(date = "01/04", particulars = "To Capital A/c", jf = "1", amount = "50000"),
            LedgerRow(date = "10/04", particulars = "To Sales A/c", jf = "4", amount = "25000")
        )

        fun getDefaultLedgerCr(): List<LedgerRow> = listOf(
            LedgerRow(date = "05/04", particulars = "By Purchases A/c", jf = "2", amount = "18000"),
            LedgerRow(date = "15/04", particulars = "By Rent A/c", jf = "3", amount = "7000"),
            LedgerRow(date = "30/04", particulars = "By Balance c/d", jf = "", amount = "50000")
        )

        fun getDefaultPlDr(): List<AccountRow> = listOf(
            AccountRow(particulars = "To Salaries & Wages", amount = ""),
            AccountRow(particulars = "To Rent, Rates & Taxes", amount = ""),
            AccountRow(particulars = "To Depreciation on Plant", amount = ""),
            AccountRow(particulars = "To Discount Allowed", amount = "")
        )

        fun getDefaultPlCr(): List<AccountRow> = listOf(
            AccountRow(particulars = "By Gross Profit b/d", amount = ""),
            AccountRow(particulars = "By Discount Received", amount = ""),
            AccountRow(particulars = "By Commission Received", amount = "")
        )

        fun getDefaultManufacturingDr(): List<AccountRow> = listOf(
            AccountRow(particulars = "To Opening Raw Materials", amount = ""),
            AccountRow(particulars = "To Purchases of Raw Materials", amount = ""),
            AccountRow(particulars = "To Direct Factory Wages", amount = ""),
            AccountRow(particulars = "To Factory Power & Fuel", amount = "")
        )

        fun getDefaultManufacturingCr(): List<AccountRow> = listOf(
            AccountRow(particulars = "By Sale of Factory Scrap", amount = ""),
            AccountRow(particulars = "By Closing Raw Materials", amount = "")
        )

        fun getDefaultRevaluationDr(): List<AccountRow> = listOf(
            AccountRow(particulars = "To Provision for Doubtful Debts", amount = ""),
            AccountRow(particulars = "To Plant & Machinery (Depreciation)", amount = "")
        )

        fun getDefaultRevaluationCr(): List<AccountRow> = listOf(
            AccountRow(particulars = "By Land & Building (Appreciation)", amount = ""),
            AccountRow(particulars = "By Sundry Creditors (Written back)", amount = "")
        )

        fun getDefaultRealisationDr(): List<AccountRow> = listOf(
            AccountRow(particulars = "To Sundry Assets transferred", amount = ""),
            AccountRow(particulars = "To Bank A/c (Creditors settled)", amount = ""),
            AccountRow(particulars = "To Realisation Expenses", amount = "")
        )

        fun getDefaultRealisationCr(): List<AccountRow> = listOf(
            AccountRow(particulars = "By Sundry Creditors transferred", amount = ""),
            AccountRow(particulars = "By Bank A/c (Assets realised)", amount = "")
        )

        fun getDefaultConsignmentDr(): List<AccountRow> = listOf(
            AccountRow(particulars = "To Goods Sent on Consignment", amount = ""),
            AccountRow(particulars = "To Bank A/c (Freight & Insurance)", amount = ""),
            AccountRow(particulars = "To Consignee A/c (Godown rent & selling exp)", amount = ""),
            AccountRow(particulars = "To Consignee A/c (Commission)", amount = "")
        )

        fun getDefaultConsignmentCr(): List<AccountRow> = listOf(
            AccountRow(particulars = "By Consignee A/c (Gross Sales)", amount = ""),
            AccountRow(particulars = "By Consignment Stock c/d", amount = "")
        )

        fun getDefaultJournal(): List<JournalRow> = listOf(
            JournalRow(date = "01/04", particulars = "Bank A/c   ...Dr.", lf = "12", debit = "100000", credit = "", narration = "(Being capital introduced in bank)"),
            JournalRow(date = "01/04", particulars = "    To Capital A/c", lf = "1", debit = "", credit = "100000", narration = ""),
            JournalRow(date = "05/04", particulars = "Purchases A/c   ...Dr.", lf = "24", debit = "30000", credit = "", narration = "(Being goods purchased for cash)"),
            JournalRow(date = "05/04", particulars = "    To Cash A/c", lf = "2", debit = "", credit = "30000", narration = ""),
            JournalRow(date = "12/04", particulars = "Sundry Debtors A/c   ...Dr.", lf = "35", debit = "45000", credit = "", narration = "(Being credit sales made)"),
            JournalRow(date = "12/04", particulars = "    To Sales A/c", lf = "15", debit = "", credit = "45000", narration = "")
        )

        fun getDefaultTrialBalance(): List<TrialBalanceRow> = listOf(
            TrialBalanceRow(sNo = "1", headOfAccount = "Capital Account", lf = "1", debit = "", credit = "250000"),
            TrialBalanceRow(sNo = "2", headOfAccount = "Plant & Machinery", lf = "4", debit = "150000", credit = ""),
            TrialBalanceRow(sNo = "3", headOfAccount = "Purchases Account", lf = "8", debit = "180000", credit = ""),
            TrialBalanceRow(sNo = "4", headOfAccount = "Sales Account", lf = "12", debit = "", credit = "240000"),
            TrialBalanceRow(sNo = "5", headOfAccount = "Sundry Debtors", lf = "16", debit = "95000", credit = ""),
            TrialBalanceRow(sNo = "6", headOfAccount = "Sundry Creditors", lf = "20", debit = "", credit = "60000"),
            TrialBalanceRow(sNo = "7", headOfAccount = "Cash & Bank Balances", lf = "24", debit = "80000", credit = ""),
            TrialBalanceRow(sNo = "8", headOfAccount = "Salaries & Rent Expenses", lf = "28", debit = "45000", credit = "")
        )
    }

    private fun getDefaultBrs(): List<BrsRow> = listOf(
        BrsRow(particulars = "Cheques issued to suppliers but not yet presented for payment", isAdd = true, amount = "24000"),
        BrsRow(particulars = "Interest directly credited by Bank in Passbook", isAdd = true, amount = "3500"),
        BrsRow(particulars = "Cheques paid into Bank but not yet cleared/collected", isAdd = false, amount = "18000"),
        BrsRow(particulars = "Bank charges directly debited by Bank", isAdd = false, amount = "650")
    )

    private fun getDefaultCashBookDr(): List<CashBookRow> = listOf(
        CashBookRow(date = "01/04", particulars = "To Balance b/d", vn = "", discount = "", cash = "12000", bank = "45000"),
        CashBookRow(date = "08/04", particulars = "To Sales A/c", vn = "14", discount = "", cash = "8500", bank = ""),
        CashBookRow(date = "15/04", particulars = "To Sharma & Co.", vn = "19", discount = "500", cash = "", bank = "24500")
    )

    private fun getDefaultCashBookCr(): List<CashBookRow> = listOf(
        CashBookRow(date = "05/04", particulars = "By Purchases A/c", vn = "21", discount = "", cash = "4000", bank = ""),
        CashBookRow(date = "12/04", particulars = "By Rent A/c", vn = "25", discount = "", cash = "", bank = "7000"),
        CashBookRow(date = "28/04", particulars = "By Verma Ltd.", vn = "30", discount = "300", cash = "", bank = "15700")
    )

    private fun getDefaultPartnersCapitalDr(): List<PartnersCapitalRow> = listOf(
        PartnersCapitalRow(date = "15/09", particulars = "To Drawings A/c", partnerA = "12000", partnerB = "8000"),
        PartnersCapitalRow(date = "31/03", particulars = "To Balance c/d", partnerA = "185000", partnerB = "124000")
    )

    private fun getDefaultPartnersCapitalCr(): List<PartnersCapitalRow> = listOf(
        PartnersCapitalRow(date = "01/04", particulars = "By Balance b/d", partnerA = "150000", partnerB = "100000"),
        PartnersCapitalRow(date = "31/03", particulars = "By Interest on Capital (6%)", partnerA = "9000", partnerB = "6000"),
        PartnersCapitalRow(date = "31/03", particulars = "By Share of Profit (P&L Approp.)", partnerA = "38000", partnerB = "26000")
    )

    private fun getDefaultPettyCash(): List<PettyCashRow> = listOf(
        PettyCashRow(receipts = "5000", date = "01/04", particulars = "To Cash A/c (Imprest received)", totalPayment = "", conveyance = "", cartage = "", stationery = "", misc = ""),
        PettyCashRow(receipts = "", date = "04/04", particulars = "By Taxi fare", totalPayment = "350", conveyance = "350", cartage = "", stationery = "", misc = ""),
        PettyCashRow(receipts = "", date = "08/04", particulars = "By Courier charges", totalPayment = "180", conveyance = "", cartage = "", stationery = "", misc = "180"),
        PettyCashRow(receipts = "", date = "12/04", particulars = "By Printing paper & pens", totalPayment = "620", conveyance = "", cartage = "", stationery = "620", misc = ""),
        PettyCashRow(receipts = "", date = "20/04", particulars = "By Cartage on office goods", totalPayment = "400", conveyance = "", cartage = "400", stationery = "", misc = "")
    )

    private fun updateAvailableChaptersAndPool() {
        val chapters = QuestionRepository.getChaptersForSubject(_uiState.value.selectedSubject)
        val count = QuestionRepository.getAvailableFileCount(
            _uiState.value.selectedSubject,
            _uiState.value.selectedChapter,
            _uiState.value.selectedDepth
        )
        _uiState.update {
            it.copy(
                availableChapters = chapters,
                questionCount = count
            )
        }
    }

    fun onSubjectChanged(subject: Subject) {
        if (_uiState.value.selectedSubject == subject) return
        _uiState.update {
            it.copy(
                selectedSubject = subject,
                selectedChapter = "all"
            )
        }
        updateAvailableChaptersAndPool()
        loadNextQuestion()
    }

    fun onChapterChanged(chapter: String) {
        if (_uiState.value.selectedChapter == chapter) return
        _uiState.update { it.copy(selectedChapter = chapter) }
        updateAvailableChaptersAndPool()
        loadNextQuestion()
    }

    fun onDepthChanged(depth: Depth) {
        if (_uiState.value.selectedDepth == depth) return
        _uiState.update { it.copy(selectedDepth = depth) }
        updateAvailableChaptersAndPool()
        loadNextQuestion()
    }

    fun loadNextQuestion(initial: Boolean = false) {
        loadQuestionJob?.cancel()
        _uiState.update { it.copy(isFetchingQuestion = true) }

        loadQuestionJob = viewModelScope.launch {
            val nextQuestion = withContext(Dispatchers.IO) {
                QuestionRepository.fetchLiveQuestion(
                    _uiState.value.selectedSubject,
                    _uiState.value.selectedChapter,
                    _uiState.value.selectedDepth
                )
            }

            lastQuestionId = nextQuestion.id

            if (!initial) {
                audioHapticManager.playFlip()
            }

            // Automatic Law Structuring pre-fill if subject is Business Law and mode is SUBJECTIVE
            val initialAnswerText = if (nextQuestion.subject == Subject.LAW && nextQuestion.mode == QuestionMode.SUBJECTIVE) {
                "I) Provision:\nUnder the relevant provisions of the statute...\n\nII) Analysis and Conclusion:\nApplying the statutory principles to the facts given..."
            } else {
                ""
            }

            _uiState.update { state ->
                state.copy(
                    currentQuestion = nextQuestion,
                    questionsViewedCount = state.questionsViewedCount + 1,
                    hasSubmittedCurrentSubjective = false,
                    isFetchingQuestion = false,
                    userAnswerText = initialAnswerText,
                    showModelAnswer = false,
                    activeTableType = AccountingTableType.NONE,
                    tradingDrRows = emptyList(),
                    tradingCrRows = emptyList(),
                    plDrRows = emptyList(),
                    plCrRows = emptyList(),
                    manufacturingDrRows = emptyList(),
                    manufacturingCrRows = emptyList(),
                    revaluationDrRows = emptyList(),
                    revaluationCrRows = emptyList(),
                    realisationDrRows = emptyList(),
                    realisationCrRows = emptyList(),
                    consignmentDrRows = emptyList(),
                    consignmentCrRows = emptyList(),
                    bsLiabilityRows = getDefaultBsLiabilities(),
                    bsAssetRows = getDefaultBsAssets(),
                    ledgerAccountName = "Cash Account",
                    ledgerDrRows = getDefaultLedgerDr(),
                    ledgerCrRows = getDefaultLedgerCr(),
                    journalRows = getDefaultJournal(),
                    trialBalanceRows = getDefaultTrialBalance(),
                    brsStartingBalance = "50000",
                    brsRows = getDefaultBrs(),
                    cashBookDrRows = getDefaultCashBookDr(),
                    cashBookCrRows = getDefaultCashBookCr(),
                    partnersCapitalDrRows = getDefaultPartnersCapitalDr(),
                    partnersCapitalCrRows = getDefaultPartnersCapitalCr(),
                    pettyCashRows = getDefaultPettyCash(),
                    formatSearchQuery = "",
                    isFormatSearchExpanded = false,
                    selectedOptionIndex = null,
                    isHintUnlocked = false,
                    isHintModalVisible = false,
                    isWritingViewOpen = false,
                    isQuestionSheetOpenInWritingView = false,
                    isGrading = false,
                    gradingResult = null
                )
            }

            // Record viewed question stat in Room and sync to HF Question Bank
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val subjectKey = nextQuestion.subject.name
                    val existing = appDao.getProgressStats(subjectKey) ?: ProgressStatsEntity(subject = subjectKey)
                    val updated = existing.copy(
                        questionsViewedCount = existing.questionsViewedCount + 1
                    )
                    appDao.insertOrUpdateProgressStats(updated)

                    val alreadySynced = appDao.isQuestionSynced(nextQuestion.topic, nextQuestion.chapter) > 0
                    if (!alreadySynced) {
                        val committed = HuggingFaceService.commitQuestionToBank(nextQuestion)
                        if (committed) {
                            appDao.recordSyncedQuestion(
                                SyncedQuestionEntity(
                                    topic = nextQuestion.topic,
                                    chapter = nextQuestion.chapter,
                                    syncedAt = System.currentTimeMillis()
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.w("PracticeViewModel", "Background stats or HF Question Bank sync error", e)
                }
            }
        }
    }

    fun openWritingView() {
        _uiState.update { it.copy(isWritingViewOpen = true, isQuestionSheetOpenInWritingView = false) }
    }

    fun closeWritingView() {
        _uiState.update { it.copy(isWritingViewOpen = false, isQuestionSheetOpenInWritingView = false) }
    }

    fun toggleQuestionSheetInWritingView(open: Boolean) {
        _uiState.update { it.copy(isQuestionSheetOpenInWritingView = open) }
    }

    fun onAnswerTextChanged(text: String) {
        _uiState.update { it.copy(userAnswerText = text) }
    }

    fun toggleModelAnswer() {
        _uiState.update { it.copy(showModelAnswer = !it.showModelAnswer) }
    }

    fun selectTableType(tableType: AccountingTableType) {
        _uiState.update { state ->
            val updated = state.copy(activeTableType = tableType)
            when (tableType) {
                AccountingTableType.TRADING_ACCOUNT -> {
                    if (state.tradingDrRows.isEmpty() && state.tradingCrRows.isEmpty()) {
                        updated.copy(tradingDrRows = getDefaultTradingDr(), tradingCrRows = getDefaultTradingCr())
                    } else updated
                }
                AccountingTableType.PROFIT_LOSS_ACCOUNT -> {
                    if (state.plDrRows.isEmpty() && state.plCrRows.isEmpty()) {
                        updated.copy(plDrRows = getDefaultPlDr(), plCrRows = getDefaultPlCr())
                    } else updated
                }
                AccountingTableType.MANUFACTURING_ACCOUNT -> {
                    if (state.manufacturingDrRows.isEmpty() && state.manufacturingCrRows.isEmpty()) {
                        updated.copy(manufacturingDrRows = getDefaultManufacturingDr(), manufacturingCrRows = getDefaultManufacturingCr())
                    } else updated
                }
                AccountingTableType.REVALUATION_ACCOUNT -> {
                    if (state.revaluationDrRows.isEmpty() && state.revaluationCrRows.isEmpty()) {
                        updated.copy(revaluationDrRows = getDefaultRevaluationDr(), revaluationCrRows = getDefaultRevaluationCr())
                    } else updated
                }
                AccountingTableType.REALISATION_ACCOUNT -> {
                    if (state.realisationDrRows.isEmpty() && state.realisationCrRows.isEmpty()) {
                        updated.copy(realisationDrRows = getDefaultRealisationDr(), realisationCrRows = getDefaultRealisationCr())
                    } else updated
                }
                AccountingTableType.CONSIGNMENT_ACCOUNT -> {
                    if (state.consignmentDrRows.isEmpty() && state.consignmentCrRows.isEmpty()) {
                        updated.copy(consignmentDrRows = getDefaultConsignmentDr(), consignmentCrRows = getDefaultConsignmentCr())
                    } else updated
                }
                AccountingTableType.BALANCE_SHEET -> {
                    if (state.bsLiabilityRows.isEmpty()) updated.copy(bsLiabilityRows = getDefaultBsLiabilities(), bsAssetRows = getDefaultBsAssets()) else updated
                }
                AccountingTableType.LEDGER -> {
                    if (state.ledgerDrRows.isEmpty()) updated.copy(ledgerDrRows = getDefaultLedgerDr(), ledgerCrRows = getDefaultLedgerCr()) else updated
                }
                AccountingTableType.JOURNAL -> {
                    if (state.journalRows.isEmpty()) updated.copy(journalRows = getDefaultJournal()) else updated
                }
                AccountingTableType.TRIAL_BALANCE -> {
                    if (state.trialBalanceRows.isEmpty()) updated.copy(trialBalanceRows = getDefaultTrialBalance()) else updated
                }
                AccountingTableType.CASH_BOOK -> {
                    if (state.cashBookDrRows.isEmpty()) updated.copy(cashBookDrRows = getDefaultCashBookDr(), cashBookCrRows = getDefaultCashBookCr()) else updated
                }
                AccountingTableType.BRS -> {
                    if (state.brsRows.isEmpty()) updated.copy(brsRows = getDefaultBrs(), brsStartingBalance = "50000") else updated
                }
                AccountingTableType.PARTNERS_CAPITAL -> {
                    if (state.partnersCapitalDrRows.isEmpty()) updated.copy(partnersCapitalDrRows = getDefaultPartnersCapitalDr(), partnersCapitalCrRows = getDefaultPartnersCapitalCr()) else updated
                }
                AccountingTableType.PETTY_CASH_BOOK -> {
                    if (state.pettyCashRows.isEmpty()) updated.copy(pettyCashRows = getDefaultPettyCash()) else updated
                }
                AccountingTableType.NONE -> updated
            }
        }
    }

    // MCQ Selection with Room persistence
    fun onSelectMcqOption(optionIndex: Int) {
        val currentQ = _uiState.value.currentQuestion ?: return
        if (_uiState.value.selectedOptionIndex != null) return

        val isCorrect = optionIndex == currentQ.correctIndex
        if (isCorrect) {
            audioHapticManager.playCorrect()
        } else {
            audioHapticManager.playWrong()
        }

        val studentAns = currentQ.options.getOrNull(optionIndex) ?: ""

        _uiState.update {
            it.copy(
                selectedOptionIndex = optionIndex,
                mcqAttemptedCount = it.mcqAttemptedCount + 1,
                mcqCorrectCount = if (isCorrect) it.mcqCorrectCount + 1 else it.mcqCorrectCount,
                questionsAnsweredCount = it.questionsAnsweredCount + 1,
                questionsAttemptedCount = it.questionsAttemptedCount + 1
            )
        }

        // Persist attempt and update stats in Room (Room is source of truth underneath)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val entity = AttemptedQuestionEntity(
                    questionId = currentQ.id,
                    topic = currentQ.topic,
                    questionText = currentQ.questionText,
                    userAnswerText = studentAns,
                    subject = currentQ.subject.name,
                    chapter = currentQ.chapter,
                    mode = currentQ.mode.name,
                    isCorrect = isCorrect,
                    aiMarksAwarded = null,
                    aiFeedback = null,
                    timestamp = System.currentTimeMillis()
                )
                appDao.insertAttempt(entity)

                val subjectKey = currentQ.subject.name
                val existing = appDao.getProgressStats(subjectKey)
                    ?: ProgressStatsEntity(subject = subjectKey)
                val updated = existing.copy(
                    questionsAnsweredCount = existing.questionsAnsweredCount + 1,
                    questionsAttemptedCount = existing.questionsAttemptedCount + 1,
                    mcqAttemptedCount = existing.mcqAttemptedCount + 1,
                    mcqCorrectCount = if (isCorrect) existing.mcqCorrectCount + 1 else existing.mcqCorrectCount
                )
                appDao.insertOrUpdateProgressStats(updated)
            } catch (e: Exception) {
                Log.e("PracticeViewModel", "Error persisting MCQ attempt to Room", e)
            }
        }
    }

    // Subjective Answer Grading via Gemini against Syllabus / Model answer
    fun submitAnswerForGrading() {
        val currentQ = _uiState.value.currentQuestion ?: return
        val studentAnswer = _uiState.value.userAnswerText.trim()
        if (studentAnswer.isBlank()) return

        val isFirstSubmission = !_uiState.value.hasSubmittedCurrentSubjective

        _uiState.update {
            it.copy(
                isGrading = true,
                hasSubmittedCurrentSubjective = true,
                questionsAnsweredCount = if (isFirstSubmission) it.questionsAnsweredCount + 1 else it.questionsAnsweredCount,
                questionsAttemptedCount = if (isFirstSubmission) it.questionsAttemptedCount + 1 else it.questionsAttemptedCount
            )
        }

        viewModelScope.launch {
            val hasOfficialKey = !currentQ.rawSyllabusAnswer.isNullOrBlank()
            val reference = if (hasOfficialKey) currentQ.rawSyllabusAnswer!! else currentQ.modelAnswer

            val result = GeminiQuestionExtractor.gradeSubjectiveAnswer(
                questionText = currentQ.questionText,
                studentAnswer = studentAnswer,
                referenceAnswer = reference,
                isOfficialKey = hasOfficialKey
            )

            _uiState.update {
                it.copy(
                    isGrading = false,
                    gradingResult = result
                )
            }

            if (result != null) {
                audioHapticManager.playCorrect()
                withContext(Dispatchers.IO) {
                    try {
                        val entity = AttemptedQuestionEntity(
                            questionId = currentQ.id,
                            topic = currentQ.topic,
                            questionText = currentQ.questionText,
                            userAnswerText = studentAnswer,
                            subject = currentQ.subject.name,
                            chapter = currentQ.chapter,
                            mode = currentQ.mode.name,
                            isCorrect = null,
                            aiMarksAwarded = result.marksAwarded,
                            aiFeedback = result.feedback,
                            timestamp = System.currentTimeMillis()
                        )
                        appDao.insertAttempt(entity)

                        if (isFirstSubmission) {
                            val subjectKey = currentQ.subject.name
                            val existing = appDao.getProgressStats(subjectKey)
                                ?: ProgressStatsEntity(subject = subjectKey)
                            val updated = existing.copy(
                                questionsAnsweredCount = existing.questionsAnsweredCount + 1,
                                questionsAttemptedCount = existing.questionsAttemptedCount + 1
                            )
                            appDao.insertOrUpdateProgressStats(updated)
                        }
                    } catch (e: Exception) {
                        Log.e("PracticeViewModel", "Error persisting subjective grading to Room", e)
                    }
                }
            }
        }
    }

    // Hint Rewarded Modal
    fun openHintModal() {
        _uiState.update { it.copy(isHintModalVisible = true) }
    }

    fun closeHintModal() {
        _uiState.update { it.copy(isHintModalVisible = false) }
    }

    fun onHintRewardEarned() {
        audioHapticManager.playHintUnlocked()
        _uiState.update { it.copy(isHintUnlocked = true) }
    }

    // Accounting Table Mutations
    fun updateTradingDrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.tradingDrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(tradingDrRows = list)
        }
    }

    fun updateTradingCrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.tradingCrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(tradingCrRows = list)
        }
    }

    fun addTradingDrRow() {
        _uiState.update {
            it.copy(tradingDrRows = it.tradingDrRows + AccountRow(particulars = "To Other Expense", amount = ""))
        }
    }

    fun addTradingCrRow() {
        _uiState.update {
            it.copy(tradingCrRows = it.tradingCrRows + AccountRow(particulars = "By Other Income", amount = ""))
        }
    }

    fun deleteTradingDrRow(index: Int) {
        _uiState.update {
            val list = it.tradingDrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(tradingDrRows = list)
        }
    }

    fun deleteTradingCrRow(index: Int) {
        _uiState.update {
            val list = it.tradingCrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(tradingCrRows = list)
        }
    }

    // Profit & Loss Account Mutations
    fun updatePlDrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.plDrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(plDrRows = list)
        }
    }

    fun updatePlCrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.plCrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(plCrRows = list)
        }
    }

    fun addPlDrRow() {
        _uiState.update {
            it.copy(plDrRows = it.plDrRows + AccountRow(particulars = "To Other Expense", amount = ""))
        }
    }

    fun addPlCrRow() {
        _uiState.update {
            it.copy(plCrRows = it.plCrRows + AccountRow(particulars = "By Other Income", amount = ""))
        }
    }

    fun deletePlDrRow(index: Int) {
        _uiState.update {
            val list = it.plDrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(plDrRows = list)
        }
    }

    fun deletePlCrRow(index: Int) {
        _uiState.update {
            val list = it.plCrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(plCrRows = list)
        }
    }

    // Manufacturing Account Mutations
    fun updateManufacturingDrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.manufacturingDrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(manufacturingDrRows = list)
        }
    }

    fun updateManufacturingCrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.manufacturingCrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(manufacturingCrRows = list)
        }
    }

    fun addManufacturingDrRow() {
        _uiState.update {
            it.copy(manufacturingDrRows = it.manufacturingDrRows + AccountRow(particulars = "To Factory Overhead", amount = ""))
        }
    }

    fun addManufacturingCrRow() {
        _uiState.update {
            it.copy(manufacturingCrRows = it.manufacturingCrRows + AccountRow(particulars = "By Other Item", amount = ""))
        }
    }

    fun deleteManufacturingDrRow(index: Int) {
        _uiState.update {
            val list = it.manufacturingDrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(manufacturingDrRows = list)
        }
    }

    fun deleteManufacturingCrRow(index: Int) {
        _uiState.update {
            val list = it.manufacturingCrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(manufacturingCrRows = list)
        }
    }

    // Revaluation Account Mutations
    fun updateRevaluationDrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.revaluationDrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(revaluationDrRows = list)
        }
    }

    fun updateRevaluationCrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.revaluationCrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(revaluationCrRows = list)
        }
    }

    fun addRevaluationDrRow() {
        _uiState.update {
            it.copy(revaluationDrRows = it.revaluationDrRows + AccountRow(particulars = "To Loss on Asset", amount = ""))
        }
    }

    fun addRevaluationCrRow() {
        _uiState.update {
            it.copy(revaluationCrRows = it.revaluationCrRows + AccountRow(particulars = "By Gain on Asset", amount = ""))
        }
    }

    fun deleteRevaluationDrRow(index: Int) {
        _uiState.update {
            val list = it.revaluationDrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(revaluationDrRows = list)
        }
    }

    fun deleteRevaluationCrRow(index: Int) {
        _uiState.update {
            val list = it.revaluationCrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(revaluationCrRows = list)
        }
    }

    // Realisation Account Mutations
    fun updateRealisationDrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.realisationDrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(realisationDrRows = list)
        }
    }

    fun updateRealisationCrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.realisationCrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(realisationCrRows = list)
        }
    }

    fun addRealisationDrRow() {
        _uiState.update {
            it.copy(realisationDrRows = it.realisationDrRows + AccountRow(particulars = "To Payment / Discharge", amount = ""))
        }
    }

    fun addRealisationCrRow() {
        _uiState.update {
            it.copy(realisationCrRows = it.realisationCrRows + AccountRow(particulars = "By Realisation", amount = ""))
        }
    }

    fun deleteRealisationDrRow(index: Int) {
        _uiState.update {
            val list = it.realisationDrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(realisationDrRows = list)
        }
    }

    fun deleteRealisationCrRow(index: Int) {
        _uiState.update {
            val list = it.realisationCrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(realisationCrRows = list)
        }
    }

    // Consignment Account Mutations
    fun updateConsignmentDrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.consignmentDrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(consignmentDrRows = list)
        }
    }

    fun updateConsignmentCrRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.consignmentCrRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(consignmentCrRows = list)
        }
    }

    fun addConsignmentDrRow() {
        _uiState.update {
            it.copy(consignmentDrRows = it.consignmentDrRows + AccountRow(particulars = "To Consignment Exp", amount = ""))
        }
    }

    fun addConsignmentCrRow() {
        _uiState.update {
            it.copy(consignmentCrRows = it.consignmentCrRows + AccountRow(particulars = "By Consignment Sales", amount = ""))
        }
    }

    fun deleteConsignmentDrRow(index: Int) {
        _uiState.update {
            val list = it.consignmentDrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(consignmentDrRows = list)
        }
    }

    fun deleteConsignmentCrRow(index: Int) {
        _uiState.update {
            val list = it.consignmentCrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(consignmentCrRows = list)
        }
    }

    fun updateBsLiabilityRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.bsLiabilityRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(bsLiabilityRows = list)
        }
    }

    fun updateBsAssetRow(index: Int, particulars: String, amount: String) {
        _uiState.update {
            val list = it.bsAssetRows.toMutableList()
            if (index in list.indices) list[index] = list[index].copy(particulars = particulars, amount = amount)
            it.copy(bsAssetRows = list)
        }
    }

    fun addBsLiabilityRow() {
        _uiState.update {
            it.copy(bsLiabilityRows = it.bsLiabilityRows + AccountRow(particulars = "Other Liability", amount = "0"))
        }
    }

    fun addBsAssetRow() {
        _uiState.update {
            it.copy(bsAssetRows = it.bsAssetRows + AccountRow(particulars = "Other Asset", amount = "0"))
        }
    }

    fun deleteBsLiabilityRow(index: Int) {
        _uiState.update {
            val list = it.bsLiabilityRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(bsLiabilityRows = list)
        }
    }

    fun deleteBsAssetRow(index: Int) {
        _uiState.update {
            val list = it.bsAssetRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(bsAssetRows = list)
        }
    }

    fun updateLedgerAccountName(name: String) {
        _uiState.update { it.copy(ledgerAccountName = name) }
    }

    fun updateLedgerDrRow(index: Int, date: String, particulars: String, jf: String, amount: String) {
        _uiState.update {
            val list = it.ledgerDrRows.toMutableList()
            if (index in list.indices) list[index] = LedgerRow(date = date, particulars = particulars, jf = jf, amount = amount)
            it.copy(ledgerDrRows = list)
        }
    }

    fun updateLedgerCrRow(index: Int, date: String, particulars: String, jf: String, amount: String) {
        _uiState.update {
            val list = it.ledgerCrRows.toMutableList()
            if (index in list.indices) list[index] = LedgerRow(date = date, particulars = particulars, jf = jf, amount = amount)
            it.copy(ledgerCrRows = list)
        }
    }

    fun addLedgerDrRow() {
        _uiState.update {
            it.copy(ledgerDrRows = it.ledgerDrRows + LedgerRow(date = "01/05", particulars = "To Entry", jf = "", amount = "0"))
        }
    }

    fun addLedgerCrRow() {
        _uiState.update {
            it.copy(ledgerCrRows = it.ledgerCrRows + LedgerRow(date = "01/05", particulars = "By Entry", jf = "", amount = "0"))
        }
    }

    fun deleteLedgerDrRow(index: Int) {
        _uiState.update {
            val list = it.ledgerDrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(ledgerDrRows = list)
        }
    }

    fun deleteLedgerCrRow(index: Int) {
        _uiState.update {
            val list = it.ledgerCrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(ledgerCrRows = list)
        }
    }

    // Journal Entry Mutations
    fun updateJournalRow(index: Int, date: String, particulars: String, lf: String, debit: String, credit: String, narration: String) {
        _uiState.update {
            val list = it.journalRows.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(
                    date = date,
                    particulars = particulars,
                    lf = lf,
                    debit = debit,
                    credit = credit,
                    narration = narration
                )
            }
            it.copy(journalRows = list)
        }
    }

    fun addJournalRow() {
        _uiState.update {
            it.copy(journalRows = it.journalRows + JournalRow(date = "15/04", particulars = "Expense A/c ...Dr.", lf = "", debit = "5000", credit = "", narration = "(Being expenses paid)"))
        }
    }

    fun deleteJournalRow(index: Int) {
        _uiState.update {
            val list = it.journalRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(journalRows = list)
        }
    }

    // Trial Balance Mutations
    fun updateTrialBalanceRow(index: Int, sNo: String, head: String, lf: String, debit: String, credit: String) {
        _uiState.update {
            val list = it.trialBalanceRows.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(sNo = sNo, headOfAccount = head, lf = lf, debit = debit, credit = credit)
            }
            it.copy(trialBalanceRows = list)
        }
    }

    fun addTrialBalanceRow() {
        _uiState.update {
            val nextSno = (it.trialBalanceRows.size + 1).toString()
            it.copy(trialBalanceRows = it.trialBalanceRows + TrialBalanceRow(sNo = nextSno, headOfAccount = "Rent & Rates", lf = "", debit = "12000", credit = ""))
        }
    }

    fun deleteTrialBalanceRow(index: Int) {
        _uiState.update {
            val list = it.trialBalanceRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(trialBalanceRows = list)
        }
    }

    // BRS Mutations
    fun updateBrsStartingBalance(amount: String) {
        _uiState.update { it.copy(brsStartingBalance = amount) }
    }

    fun updateBrsRow(index: Int, particulars: String, isAdd: Boolean, amount: String) {
        _uiState.update {
            val list = it.brsRows.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(particulars = particulars, isAdd = isAdd, amount = amount)
            }
            it.copy(brsRows = list)
        }
    }

    fun addBrsRow(isAdd: Boolean = true) {
        _uiState.update {
            it.copy(brsRows = it.brsRows + BrsRow(
                particulars = if (isAdd) "Direct deposit into bank by debtor" else "Bank charges & commission debited",
                isAdd = isAdd,
                amount = "2500"
            ))
        }
    }

    fun deleteBrsRow(index: Int) {
        _uiState.update {
            val list = it.brsRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(brsRows = list)
        }
    }

    // Cash Book Mutations
    fun updateCashBookDrRow(index: Int, date: String, particulars: String, vn: String, discount: String, cash: String, bank: String) {
        _uiState.update {
            val list = it.cashBookDrRows.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(date = date, particulars = particulars, vn = vn, discount = discount, cash = cash, bank = bank)
            }
            it.copy(cashBookDrRows = list)
        }
    }

    fun updateCashBookCrRow(index: Int, date: String, particulars: String, vn: String, discount: String, cash: String, bank: String) {
        _uiState.update {
            val list = it.cashBookCrRows.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(date = date, particulars = particulars, vn = vn, discount = discount, cash = cash, bank = bank)
            }
            it.copy(cashBookCrRows = list)
        }
    }

    fun addCashBookDrRow() {
        _uiState.update {
            it.copy(cashBookDrRows = it.cashBookDrRows + CashBookRow(date = "20/04", particulars = "To Commission", vn = "", discount = "", cash = "3000", bank = ""))
        }
    }

    fun addCashBookCrRow() {
        _uiState.update {
            it.copy(cashBookCrRows = it.cashBookCrRows + CashBookRow(date = "22/04", particulars = "By Stationery Exp", vn = "", discount = "", cash = "1200", bank = ""))
        }
    }

    fun deleteCashBookDrRow(index: Int) {
        _uiState.update {
            val list = it.cashBookDrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(cashBookDrRows = list)
        }
    }

    fun deleteCashBookCrRow(index: Int) {
        _uiState.update {
            val list = it.cashBookCrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(cashBookCrRows = list)
        }
    }

    // Partners Capital Mutations
    fun updatePartnersCapitalDrRow(index: Int, date: String, particulars: String, pA: String, pB: String) {
        _uiState.update {
            val list = it.partnersCapitalDrRows.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(date = date, particulars = particulars, partnerA = pA, partnerB = pB)
            }
            it.copy(partnersCapitalDrRows = list)
        }
    }

    fun updatePartnersCapitalCrRow(index: Int, date: String, particulars: String, pA: String, pB: String) {
        _uiState.update {
            val list = it.partnersCapitalCrRows.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(date = date, particulars = particulars, partnerA = pA, partnerB = pB)
            }
            it.copy(partnersCapitalCrRows = list)
        }
    }

    fun addPartnersCapitalDrRow() {
        _uiState.update {
            it.copy(partnersCapitalDrRows = it.partnersCapitalDrRows + PartnersCapitalRow(date = "31/03", particulars = "To Interest on Drawings", partnerA = "1500", partnerB = "1000"))
        }
    }

    fun addPartnersCapitalCrRow() {
        _uiState.update {
            it.copy(partnersCapitalCrRows = it.partnersCapitalCrRows + PartnersCapitalRow(date = "31/03", particulars = "By Salary to Partner", partnerA = "24000", partnerB = ""))
        }
    }

    fun deletePartnersCapitalDrRow(index: Int) {
        _uiState.update {
            val list = it.partnersCapitalDrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(partnersCapitalDrRows = list)
        }
    }

    fun deletePartnersCapitalCrRow(index: Int) {
        _uiState.update {
            val list = it.partnersCapitalCrRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(partnersCapitalCrRows = list)
        }
    }

    // Petty Cash Mutations
    fun updatePettyCashRow(index: Int, receipts: String, date: String, particulars: String, total: String, conveyance: String, cartage: String, stationery: String, misc: String) {
        _uiState.update {
            val list = it.pettyCashRows.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(receipts = receipts, date = date, particulars = particulars, totalPayment = total, conveyance = conveyance, cartage = cartage, stationery = stationery, misc = misc)
            }
            it.copy(pettyCashRows = list)
        }
    }

    fun addPettyCashRow() {
        _uiState.update {
            it.copy(pettyCashRows = it.pettyCashRows + PettyCashRow(receipts = "", date = "25/04", particulars = "By Office supplies", totalPayment = "350", conveyance = "", cartage = "", stationery = "350", misc = ""))
        }
    }

    fun deletePettyCashRow(index: Int) {
        _uiState.update {
            val list = it.pettyCashRows.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(pettyCashRows = list)
        }
    }

    // Format Search Handlers
    fun onFormatSearchQueryChanged(query: String) {
        _uiState.update { it.copy(formatSearchQuery = query) }
    }

    fun toggleFormatSearch(expanded: Boolean) {
        _uiState.update { it.copy(isFormatSearchExpanded = expanded) }
    }
}
