package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
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
import com.example.data.QuestionRepository
import com.example.model.Depth
import com.example.model.Subject
import com.example.ui.theme.VintageGold
import com.example.ui.theme.VintageGoldBg
import com.example.ui.theme.VintageGreen
import com.example.ui.theme.VintageGreenBg
import com.example.ui.theme.VintageInk
import com.example.ui.theme.VintageInkSoft
import com.example.ui.theme.VintageLine
import com.example.ui.theme.VintageNavy
import com.example.ui.theme.VintagePaperBg
import com.example.ui.theme.VintagePaperSheet

@Composable
fun SubjectDrillDownScreen(
    subject: Subject,
    selectedChapter: String,
    selectedDepth: Depth,
    onChapterSelected: (String) -> Unit,
    onDepthSelected: (Depth) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onDismiss()
    }

    val chapters = remember(subject) {
        QuestionRepository.getChaptersForSubject(subject)
    }

    // Keep track of which chapters are expanded
    val expandedChapters = remember {
        mutableStateMapOf<String, Boolean>().apply {
            chapters.forEach { ch ->
                val units = QuestionRepository.getUnitsForChapter(subject, ch)
                if (units.isNotEmpty() && ch == selectedChapter) {
                    this[ch] = true
                }
            }
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = VintagePaperBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .drawBehind {
                    val step = 34.dp.toPx()
                    var y = step
                    while (y < size.height) {
                        drawLine(
                            color = VintageLine.copy(alpha = 0.35f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1f
                        )
                        y += step
                    }
                },
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                // Top Header Bar with Back Button
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    color = VintagePaperSheet,
                    border = BorderStroke(1.dp, VintageLine),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("drilldown_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Return to Practice",
                                tint = VintageNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = subject.displayName,
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VintageInk
                                )
                            )
                            Text(
                                text = when (subject) {
                                    Subject.LAW -> "Paper 2 • 100 Marks (ICAI Subjective Format)"
                                    Subject.ACC -> "Paper 1 • 100 Marks (Accounting Data Grids)"
                                    Subject.QUANT -> "Paper 3 • 100 Marks (Objective MCQs)"
                                    Subject.ECO -> "Paper 4 • 100 Marks (Objective MCQs)"
                                    else -> "CA Foundation Syllabus Practice"
                                },
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = VintageInkSoft
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = VintageNavy
                        ) {
                            Text(
                                text = subject.name,
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Option 1: Practice Entire Subject (Mixed Chapters)
                    val isAllMixedSelected = selectedChapter == "all"
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onChapterSelected("all")
                                onDepthSelected(Depth.ALL)
                                onDismiss()
                            }
                            .testTag("drilldown_all_chapters"),
                        shape = RoundedCornerShape(4.dp),
                        color = if (isAllMixedSelected) VintageGoldBg else VintagePaperSheet,
                        border = BorderStroke(
                            if (isAllMixedSelected) 1.5.dp else 1.dp,
                            if (isAllMixedSelected) VintageGold else VintageLine
                        ),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = null,
                                    tint = if (isAllMixedSelected) VintageGold else VintageNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "All Chapters (Full Subject Mixed)",
                                        style = TextStyle(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = VintageInk
                                        )
                                    )
                                    Text(
                                        text = "Random practice questions across the entire ${subject.displayName} syllabus",
                                        style = TextStyle(
                                            fontFamily = FontFamily.Serif,
                                            fontSize = 11.5.sp,
                                            color = VintageInkSoft
                                        )
                                    )
                                }
                            }

                            if (isAllMixedSelected) {
                                Surface(
                                    shape = CircleShape,
                                    color = VintageGreen,
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Section Kicker
                    Text(
                        text = "CHAPTERS & UNITS SYLLABUS",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VintageNavy,
                            letterSpacing = 0.5.sp
                        ),
                        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                    )

                    // Chapters and Expandable Units List
                    chapters.forEach { chapter ->
                        val units = QuestionRepository.getUnitsForChapter(subject, chapter)
                        val hasUnits = units.isNotEmpty()
                        val isChapterSelected = selectedChapter == chapter
                        val isExpanded = expandedChapters[chapter] == true

                        if (hasUnits) {
                            // Expandable Chapter Card
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("chapter_card_${chapter.hashCode()}"),
                                shape = RoundedCornerShape(4.dp),
                                color = VintagePaperSheet,
                                border = BorderStroke(
                                    if (isChapterSelected) 1.5.dp else 1.dp,
                                    if (isChapterSelected) VintageNavy else VintageLine
                                ),
                                shadowElevation = 1.dp
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Header Row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                expandedChapters[chapter] = !isExpanded
                                            }
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = chapter,
                                                style = TextStyle(
                                                    fontFamily = FontFamily.Serif,
                                                    fontSize = 13.5.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = VintageInk
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "${units.size} Units available • Tap to explore units",
                                                style = TextStyle(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 11.sp,
                                                    color = VintageNavy
                                                )
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (isChapterSelected) {
                                                Surface(
                                                    shape = RoundedCornerShape(3.dp),
                                                    color = VintageGreenBg,
                                                    border = BorderStroke(1.dp, VintageGreen),
                                                    modifier = Modifier.padding(end = 6.dp)
                                                ) {
                                                    Text(
                                                        text = if (selectedDepth == Depth.UNIT) "Unit Active" else "Active",
                                                        style = TextStyle(
                                                            fontFamily = FontFamily.Monospace,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = VintageGreen
                                                        ),
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            Icon(
                                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                contentDescription = if (isExpanded) "Collapse" else "Expand",
                                                tint = VintageNavy,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    // Expandable Units Drill-Down
                                    AnimatedVisibility(
                                        visible = isExpanded,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(VintagePaperBg.copy(alpha = 0.5f))
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            HorizontalDivider(color = VintageLine.copy(alpha = 0.6f))
                                            Spacer(modifier = Modifier.height(6.dp))

                                            // Practice Whole Chapter option
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        onChapterSelected(chapter)
                                                        onDepthSelected(Depth.CHAPTER)
                                                        onDismiss()
                                                    }
                                                    .padding(vertical = 7.dp, horizontal = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "• Practice Entire Chapter (Mixed Units)",
                                                    style = TextStyle(
                                                        fontFamily = FontFamily.Monospace,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = VintageNavy
                                                    )
                                                )

                                                if (isChapterSelected && selectedDepth == Depth.CHAPTER) {
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = VintageGreen,
                                                        modifier = Modifier.size(16.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = "Selected",
                                                            tint = Color.White,
                                                            modifier = Modifier.size(10.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            HorizontalDivider(color = VintageLine.copy(alpha = 0.4f))

                                            // Nested Units
                                            units.forEachIndexed { uIdx, unitName ->
                                                val isThisUnitSelected = isChapterSelected && selectedDepth == Depth.UNIT
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            onChapterSelected(chapter)
                                                            onDepthSelected(Depth.UNIT)
                                                            onDismiss()
                                                        }
                                                        .padding(vertical = 7.dp, horizontal = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = "  $unitName",
                                                        style = TextStyle(
                                                            fontFamily = FontFamily.Serif,
                                                            fontSize = 12.5.sp,
                                                            fontWeight = FontWeight.Normal,
                                                            color = VintageInk
                                                        ),
                                                        modifier = Modifier.weight(1f)
                                                    )

                                                    Surface(
                                                        shape = RoundedCornerShape(2.dp),
                                                        color = VintagePaperSheet,
                                                        border = BorderStroke(0.8.dp, VintageLine)
                                                    ) {
                                                        Text(
                                                            text = "Unit",
                                                            style = TextStyle(
                                                                fontFamily = FontFamily.Monospace,
                                                                fontSize = 9.5.sp,
                                                                color = VintageInkSoft
                                                            ),
                                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                        )
                                                    }
                                                }
                                                if (uIdx < units.lastIndex) {
                                                    HorizontalDivider(color = VintageLine.copy(alpha = 0.25f))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // Flat Selectable Chapter Card (No unit drill-down available)
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onChapterSelected(chapter)
                                        onDepthSelected(Depth.CHAPTER)
                                        onDismiss()
                                    }
                                    .testTag("chapter_flat_${chapter.hashCode()}"),
                                shape = RoundedCornerShape(4.dp),
                                color = if (isChapterSelected) VintageGoldBg else VintagePaperSheet,
                                border = BorderStroke(
                                    if (isChapterSelected) 1.5.dp else 1.dp,
                                    if (isChapterSelected) VintageNavy else VintageLine
                                ),
                                shadowElevation = 1.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = chapter,
                                            style = TextStyle(
                                                fontFamily = FontFamily.Serif,
                                                fontSize = 13.5.sp,
                                                fontWeight = if (isChapterSelected) FontWeight.Bold else FontWeight.SemiBold,
                                                color = VintageInk
                                            )
                                        )
                                        Text(
                                            text = "Chapter Level Practice",
                                            style = TextStyle(
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp,
                                                color = VintageInkSoft
                                            )
                                        )
                                    }

                                    if (isChapterSelected) {
                                        Surface(
                                            shape = CircleShape,
                                            color = VintageGreen,
                                            modifier = Modifier.size(18.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color.White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
