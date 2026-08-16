package com.example.ui.feature.vault

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.VaultItemModel
import com.example.ui.theme.BentoAlertBg
import com.example.ui.theme.BentoAlertBorder
import com.example.ui.theme.BentoAlertText
import com.example.ui.theme.BentoBadgeBlue
import com.example.ui.theme.BentoBadgeBlueIcon
import com.example.ui.theme.BentoBadgeGreen
import com.example.ui.theme.BentoBadgeGreenIcon
import com.example.ui.theme.BentoBadgePeach
import com.example.ui.theme.BentoBadgePurple
import com.example.ui.theme.BentoBadgePurpleIcon
import com.example.ui.theme.BentoBadgeSlate
import com.example.ui.theme.BentoBadgeSlateIcon
import com.example.ui.theme.BentoCanvasBg
import com.example.ui.theme.BentoCardBg
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoHeroAccent
import com.example.ui.theme.BentoHeroBg
import com.example.ui.theme.BentoHeroBorder
import com.example.ui.theme.BentoHeroText
import com.example.ui.theme.BentoTextMuted
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.RiskCritical
import com.example.ui.theme.RiskHigh
import com.example.ui.viewmodel.VaultViewModel

@Composable
fun VaultScreen(
    viewModel: VaultViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val vaultItems by viewModel.vaultItems.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoCanvasBg)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(BentoCardBg)
                        .border(1.dp, BentoCardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = BentoTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Encrypted Vault",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Text(
                        text = if (uiState.isUnlocked) "AES-256 Unlocked" else "Secured with Master Key",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (uiState.isUnlocked) BentoBadgeGreenIcon else BentoHeroAccent
                    )
                }
            }

            if (uiState.isUnlocked) {
                IconButton(
                    onClick = { viewModel.lockVault() },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(BentoCardBg)
                        .border(1.dp, BentoCardBorder, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock", tint = BentoHeroAccent, modifier = Modifier.size(18.dp))
                }
            }
        }

        if (!uiState.isUnlocked) {
            VaultPinPadLockView(
                enteredLength = uiState.enteredPin.length,
                isError = uiState.pinError,
                onDigit = { viewModel.enterPinDigit(it) },
                onDelete = { viewModel.deletePinDigit() },
                onBiometricUnlock = { viewModel.unlockWithBiometrics() }
            )
        } else {
            VaultUnlockedView(
                items = vaultItems,
                isDecoyMode = uiState.isDecoyMode,
                decryptedItemId = uiState.decryptedItemId,
                decryptedText = uiState.decryptedText,
                onViewDecrypted = { viewModel.viewDecrypted(it) },
                onHideDecrypted = { viewModel.hideDecrypted() },
                onDeleteItem = { viewModel.deleteItem(it) },
                onAddItemClick = { viewModel.setCreatingItem(true) }
            )
        }
    }

    if (uiState.isCreatingItem) {
        AddVaultItemDialog(
            onDismiss = { viewModel.setCreatingItem(false) },
            onSave = { title, cat, content, note ->
                viewModel.addItem(title, cat, content, note)
            }
        )
    }
}

@Composable
fun VaultPinPadLockView(
    enteredLength: Int,
    isError: Boolean,
    onDigit: (String) -> Unit,
    onDelete: () -> Unit,
    onBiometricUnlock: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(BentoBadgePurple),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = BentoBadgePurpleIcon, modifier = Modifier.size(28.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Enter Vault Master PIN",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = BentoTextPrimary
        )
        Text(
            text = "Master PIN: 1234  •  Decoy PIN: 0000",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = BentoHeroAccent
        )

        Spacer(modifier = Modifier.height(16.dp))

        // PIN indicator dots
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            for (i in 0 until 4) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isError -> Color(0xFFB3261E)
                                i < enteredLength -> BentoHeroAccent
                                else -> BentoCardBorder
                            }
                        )
                )
            }
        }

        if (isError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Incorrect PIN. Please try again.", fontSize = 12.sp, color = Color(0xFFB3261E))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Numeric Keypad
        val digits = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("BIO", "0", "DEL")
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (row in digits) {
                Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    for (digit in row) {
                        if (digit == "BIO") {
                            Surface(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(CircleShape)
                                    .clickable { onBiometricUnlock() }
                                    .testTag("btn_biometric_unlock"),
                                color = BentoHeroBg,
                                shape = CircleShape,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BentoHeroBorder)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Default.Key, contentDescription = "Biometric Fingerprint", tint = BentoHeroAccent)
                                }
                            }
                        } else if (digit == "DEL") {
                            Surface(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(CircleShape)
                                    .clickable { onDelete() },
                                color = BentoCardBg,
                                shape = CircleShape,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Default.Backspace, contentDescription = "Delete", tint = BentoTextSecondary)
                                }
                            }
                        } else {
                            Surface(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(CircleShape)
                                    .clickable { onDigit(digit) }
                                    .testTag("pin_key_$digit"),
                                color = BentoCardBg,
                                shape = CircleShape,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = digit, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Anti-coercion note card
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            colors = CardDefaults.cardColors(containerColor = BentoCardBg),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.LockOpen, contentDescription = null, tint = BentoBadgePurpleIcon, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Anti-Coercion Defense: Entering Decoy PIN '0000' opens safe decoy dummy notes without exposing real vault records.",
                    fontSize = 10.sp,
                    color = BentoTextSecondary,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun VaultUnlockedView(
    items: List<VaultItemModel>,
    isDecoyMode: Boolean = false,
    decryptedItemId: Long?,
    decryptedText: String,
    onViewDecrypted: (VaultItemModel) -> Unit,
    onHideDecrypted: () -> Unit,
    onDeleteItem: (Long) -> Unit,
    onAddItemClick: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isDecoyMode) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BentoAlertBg),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoAlertBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.LockOpen, contentDescription = null, tint = RiskCritical, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("DECOY VAULT ACTIVE", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BentoAlertText)
                                Text("Dummy safe records shown. Real AES-256 vault items remain hidden.", fontSize = 11.sp, color = BentoAlertText)
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isDecoyMode) "DECOY DUMMY RECORDS (${items.size})" else "ENCRYPTED RECORDS (${items.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = BentoTextSecondary
                    )
                }
            }

            if (items.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(BentoBadgePurple),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = BentoBadgePurpleIcon, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "Your Vault is Empty", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Store recovery phrases, confidential notes, 2FA backup codes, and passwords here with AES-256 encryption.",
                                fontSize = 12.sp,
                                color = BentoTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(items, key = { it.id }) { item ->
                    val isRevealed = decryptedItemId == item.id
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = BentoBadgePurple,
                                        shape = RoundedCornerShape(100.dp)
                                    ) {
                                        Text(
                                            text = item.category.uppercase(),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BentoBadgePurpleIcon,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = item.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoTextPrimary
                                    )
                                }
                                Row {
                                    IconButton(
                                        onClick = {
                                            if (isRevealed) onHideDecrypted() else onViewDecrypted(item)
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle Reveal",
                                            tint = BentoHeroAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { onDeleteItem(item.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFB3261E), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            if (item.note.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = item.note, fontSize = 11.sp, color = BentoTextSecondary)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isRevealed) decryptedText else "••••••••••••••••••••",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isRevealed) BentoBadgeGreenIcon else BentoTextSecondary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isRevealed) {
                                        IconButton(
                                            onClick = {
                                                clipboard.setText(AnnotatedString(decryptedText))
                                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = BentoHeroAccent, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddItemClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 90.dp, end = 20.dp)
                .testTag("add_vault_item_fab"),
            containerColor = BentoHeroText,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Item")
        }
    }
}

@Composable
fun AddVaultItemDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, category: String, content: String, note: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Password") }
    var content by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BentoCardBg,
        title = { Text("Add Encrypted Record", fontWeight = FontWeight.Bold, color = BentoTextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title / Service Name", color = BentoTextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoHeroAccent,
                        unfocusedBorderColor = BentoCardBorder,
                        focusedTextColor = BentoTextPrimary,
                        unfocusedTextColor = BentoTextPrimary
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (e.g., 2FA Key, PIN, Note)", color = BentoTextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoHeroAccent,
                        unfocusedBorderColor = BentoCardBorder,
                        focusedTextColor = BentoTextPrimary,
                        unfocusedTextColor = BentoTextPrimary
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Confidential Secret / Key", color = BentoTextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoHeroAccent,
                        unfocusedBorderColor = BentoCardBorder,
                        focusedTextColor = BentoTextPrimary,
                        unfocusedTextColor = BentoTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Optional Note", color = BentoTextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoHeroAccent,
                        unfocusedBorderColor = BentoCardBorder,
                        focusedTextColor = BentoTextPrimary,
                        unfocusedTextColor = BentoTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onSave(title, category, content, note)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BentoHeroText, contentColor = Color.White)
            ) {
                Text("Encrypt & Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = BentoTextSecondary)
            }
        }
    )
}

