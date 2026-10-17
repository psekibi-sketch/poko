package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.audio.AudioService
import com.example.data.model.GradeLevel
import com.example.ui.components.Kid3DButton
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.SunGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OfficialCertificateDialog(
    studentName: String = "Genç İngilizce Şampiyonu",
    gradeLevel: GradeLevel,
    audioService: AudioService,
    onDismiss: () -> Unit
) {
    val currentDate = remember {
        SimpleDateFormat("dd MMMM yyyy", Locale("tr")).format(Date())
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A).copy(alpha = 0.95f))
                .padding(14.dp)
                .testTag("official_certificate_dialog"),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top close action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White)
                    }
                }

                // Official Golden Certificate Canvas Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF5)),
                    border = BorderStroke(4.dp, SunGold),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .border(1.5.dp, Color(0xFFD97706).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Certificate Top Header
                        Text(text = "👑 ⭐ 👑", fontSize = 24.sp)
                        Text(
                            text = "POKO İNGİLİZCE AKADEMİSİ",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF78350F),
                            fontSize = 13.sp,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "RESMİ BAŞARI DİPLOMASI",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFB45309),
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Certificate of English Excellence",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E),
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Bu başarı sertifikası,",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                        )

                        Text(
                            text = studentName,
                            fontWeight = FontWeight.Black,
                            color = BrandBlue,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "MEB ${gradeLevel.title} İngilizce Müfredatı derslerini, sesli konuşma çalışmalarını ve kelime şampiyonası görevlerini üstün başarıyla tamamladığı için verilmiştir.",
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bottom Signatures & Seal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Date
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Tarih", fontWeight = FontWeight.Bold, color = Color(0xFF64748B), fontSize = 10.sp)
                                Text(text = currentDate, fontWeight = FontWeight.Black, color = Color(0xFF0F172A), fontSize = 11.sp)
                            }

                            // Wax Seal
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFDC2626),
                                border = BorderStroke(2.dp, SunGold),
                                shadowElevation = 4.dp,
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "🦊👑", fontSize = 20.sp)
                                }
                            }

                            // Signature
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Maskot Eğitmen", fontWeight = FontWeight.Bold, color = Color(0xFF64748B), fontSize = 10.sp)
                                Text(text = "Lego Poko 🐾", fontWeight = FontWeight.Black, color = Color(0xFF0F172A), fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions (Print & Audio Fanfare)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Kid3DButton(
                        text = "Alkış & Kutla 🎉",
                        onClick = {
                            audioService.playFanfareSound()
                            audioService.speakPraise("Tebrikler! Sen resmi bir İngilizce şampiyonusun!")
                        },
                        color = SunGold,
                        shadowColor = Color(0xFFB45309),
                        modifier = Modifier.weight(1f)
                    )

                    Kid3DButton(
                        text = "Tamam 🌟",
                        onClick = onDismiss,
                        color = LeafGreen,
                        shadowColor = Color(0xFF15803D),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
