package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Depth
import com.example.model.Subject
import com.example.ui.theme.VintageGold
import com.example.ui.theme.VintageGoldBg
import com.example.ui.theme.VintageGreen
import com.example.ui.theme.VintageInk
import com.example.ui.theme.VintageInkSoft
import com.example.ui.theme.VintageLine
import com.example.ui.theme.VintageNavy
import com.example.ui.theme.VintagePaperBg
import com.example.ui.theme.VintagePaperSheet

@Composable
fun ExamPaperHeader(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 0.dp, color = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CA Foundation — Practice Copy",
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = VintageInk
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Subjective for Law and Accounting. Objective for Economics and Quant Aptitude.",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.5.sp,
                            color = VintageInkSoft,
                            lineHeight = 15.sp
                        )
                    )
                }

                // Rotated Vintage Stamp
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp, start = 8.dp)
                        .rotate(8f)
                        .border(
                            border = BorderStroke(1.5.dp, VintageNavy.copy(alpha = 0.75f)),
                            shape = RoundedCornerShape(2.dp)
                        )
                        .background(VintagePaperSheet.copy(alpha = 0.8f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "PRACTICE COPY",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp,
                            letterSpacing = 0.8.sp,
                            color = VintageNavy.copy(alpha = 0.85f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Thick Ink Line Divider under header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(VintageInk)
            )
        }
    }
}

@Composable
fun FilterBar(
    selectedSubject: Subject,
    onSubjectChange: (Subject) -> Unit,
    availableChapters: List<String>,
    selectedChapter: String,
    onChapterChange: (String) -> Unit,
    selectedDepth: Depth,
    onDepthChange: (Depth) -> Unit,
    questionCount: Int,
    onOpenSubjectDrillDown: (Subject) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var depthMenuExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Section Kicker
        Text(
            text = "SUBJECT SELECTOR",
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = VintageNavy,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // "All Subjects" Full-Width Tile
        val isAllSelected = selectedSubject == Subject.ALL
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onSubjectChange(Subject.ALL)
                    onChapterChange("all")
                    onDepthChange(Depth.ALL)
                }
                .testTag("subject_tile_all"),
            shape = RoundedCornerShape(3.dp),
            color = if (isAllSelected) VintageGoldBg else VintagePaperSheet,
            border = BorderStroke(
                if (isAllSelected) 1.5.dp else 1.dp,
                if (isAllSelected) VintageGold else VintageLine
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = if (isAllSelected) VintageGold else VintageNavy,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "All Subjects (Mixed Exam Mode)",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                            color = VintageInk
                        )
                    )
                }

                if (isAllSelected) {
                    Surface(
                        shape = CircleShape,
                        color = VintageGreen,
                        modifier = Modifier.size(15.dp)
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
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 4 Core Subject Tiles: 2x2 Grid
        val subjects = listOf(
            Triple(Subject.ACC, "Accounting", "Paper 1 • Subj"),
            Triple(Subject.LAW, "Business Law", "Paper 2 • Subj"),
            Triple(Subject.QUANT, "Quant Aptitude", "Paper 3 • MCQ"),
            Triple(Subject.ECO, "Economics", "Paper 4 • MCQ")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SubjectTile(
                subject = subjects[0].first,
                title = subjects[0].second,
                badge = subjects[0].third,
                isSelected = selectedSubject == subjects[0].first,
                onClick = {
                    onSubjectChange(subjects[0].first)
                    onOpenSubjectDrillDown(subjects[0].first)
                },
                modifier = Modifier.weight(1f)
            )
            SubjectTile(
                subject = subjects[1].first,
                title = subjects[1].second,
                badge = subjects[1].third,
                isSelected = selectedSubject == subjects[1].first,
                onClick = {
                    onSubjectChange(subjects[1].first)
                    onOpenSubjectDrillDown(subjects[1].first)
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SubjectTile(
                subject = subjects[2].first,
                title = subjects[2].second,
                badge = subjects[2].third,
                isSelected = selectedSubject == subjects[2].first,
                onClick = {
                    onSubjectChange(subjects[2].first)
                    onOpenSubjectDrillDown(subjects[2].first)
                },
                modifier = Modifier.weight(1f)
            )
            SubjectTile(
                subject = subjects[3].first,
                title = subjects[3].second,
                badge = subjects[3].third,
                isSelected = selectedSubject == subjects[3].first,
                onClick = {
                    onSubjectChange(subjects[3].first)
                    onOpenSubjectDrillDown(subjects[3].first)
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Active Selection & Syllabus Drill-down Trigger
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (selectedSubject != Subject.ALL) {
                        onOpenSubjectDrillDown(selectedSubject)
                    }
                }
                .testTag("active_filter_summary"),
            shape = RoundedCornerShape(3.dp),
            color = VintagePaperBg,
            border = BorderStroke(1.dp, VintageLine)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (selectedSubject == Subject.ALL) {
                            "Scope: All 4 Subjects (Mixed Questions)"
                        } else {
                            val chLabel = if (selectedChapter == "all") "All Chapters" else selectedChapter
                            val depthLabel = when (selectedDepth) {
                                Depth.UNIT -> " • Unit Depth"
                                Depth.CHAPTER -> " • Chapter Depth"
                                Depth.ALL -> ""
                            }
                            "Scope: ${selectedSubject.displayName} › $chLabel$depthLabel"
                        },
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VintageInk
                        ),
                        maxLines = 1
                    )
                }

                if (selectedSubject != Subject.ALL) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Chapters",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = VintageNavy
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Explore Chapters",
                            tint = VintageNavy,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Depth Row & Filter Count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Depth:",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.5.sp,
                        color = VintageInkSoft
                    ),
                    modifier = Modifier.padding(end = 6.dp)
                )

                Box {
                    Surface(
                        modifier = Modifier
                            .clickable { depthMenuExpanded = true }
                            .testTag("depth_filter_dropdown"),
                        shape = RoundedCornerShape(3.dp),
                        color = VintagePaperSheet,
                        border = BorderStroke(1.dp, VintageLine)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedDepth.displayName,
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.5.sp,
                                    color = VintageInk
                                )
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = VintageInkSoft,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = depthMenuExpanded,
                        onDismissRequest = { depthMenuExpanded = false },
                        modifier = Modifier.background(VintagePaperSheet)
                    ) {
                        Depth.values().forEach { d ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        d.displayName,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        color = if (d == selectedDepth) VintageNavy else VintageInk,
                                        fontWeight = if (d == selectedDepth) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    onDepthChange(d)
                                    depthMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Count Indicator
            Text(
                text = "$questionCount ${if (questionCount == 1) "question" else "questions"} in filter",
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = VintageInkSoft
                )
            )
        }
    }
}

@Composable
private fun SubjectTile(
    subject: Subject,
    title: String,
    badge: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("subject_tile_${subject.name.lowercase()}"),
        shape = RoundedCornerShape(3.dp),
        color = if (isSelected) VintageGoldBg else VintagePaperSheet,
        border = BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) VintageNavy else VintageLine
        ),
        shadowElevation = if (isSelected) 1.dp else 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 7.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = VintageInk
                    ),
                    maxLines = 1
                )
                if (isSelected) {
                    Surface(
                        shape = CircleShape,
                        color = VintageGreen,
                        modifier = Modifier.size(13.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(9.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = badge,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.5.sp,
                    color = if (isSelected) VintageNavy else VintageInkSoft
                ),
                maxLines = 1
            )
        }
    }
}
