package com.famessa.deli_antojito.feature.product

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

@Composable
fun ProductEditRoute(
    productId: Long?,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
    viewModel: ProductEditViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) {
            viewModel.dismissImageSourceChooser()
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            val selection = context.readImageSelection(uri)
            if (selection == null) {
                viewModel.dismissImageSourceChooser()
                return@launch
            }
            viewModel.onImageSelected(
                base64 = selection.base64,
                mimeType = selection.mimeType,
                sizeBytes = selection.sizeBytes
            )
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap == null) {
            viewModel.dismissImageSourceChooser()
            return@rememberLauncherForActivityResult
        }
        val selection = bitmap.toImageSelection()
        viewModel.onImageSelected(
            base64 = selection.base64,
            mimeType = selection.mimeType,
            sizeBytes = selection.sizeBytes
        )
    }

    LaunchedEffect(productId) {
        viewModel.start(productId)
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSaved()
    }

    ProductEditScreen(
        state = state,
        isEdit = productId != null && productId > 0L,
        onNameChange = viewModel::onNameChange,
        onPriceChange = viewModel::onPriceChange,
        onActiveChange = viewModel::onActiveChange,
        onImageClick = viewModel::requestImageSourceChooser,
        onSave = viewModel::save,
        onBack = { viewModel.requestNavigateBack(onCancel) },
        onConfirmDiscard = { viewModel.confirmDiscard(onCancel) },
        onCancelDiscard = viewModel::cancelDiscard
    )

    if (state.showImageSourceChooser) {
        ImageSourceChooserDialog(
            hasImage = state.img != null,
            onDismiss = viewModel::dismissImageSourceChooser,
            onGallery = {
                viewModel.dismissImageSourceChooser()
                galleryLauncher.launch("image/*")
            },
            onCamera = {
                viewModel.dismissImageSourceChooser()
                cameraLauncher.launch(null)
            },
            onRemoveImage = if (state.img != null) {
                {
                    viewModel.removeImage()
                    viewModel.dismissImageSourceChooser()
                }
            } else {
                null
            }
        )
    }
}

@Composable
private fun ImageSourceChooserDialog(
    hasImage: Boolean,
    onDismiss: () -> Unit,
    onGallery: () -> Unit,
    onCamera: () -> Unit,
    onRemoveImage: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar imagen") },
        text = {
            Column(
                modifier = Modifier.testTag(ProductTestTags.ImageSourceDialog),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Elige una fuente para la imagen del producto.")
                TextButton(onClick = onGallery, modifier = Modifier.fillMaxWidth()) {
                    Text("Desde dispositivo")
                }
                TextButton(onClick = onCamera, modifier = Modifier.fillMaxWidth()) {
                    Text("Tomar foto")
                }
                if (hasImage && onRemoveImage != null) {
                    TextButton(onClick = onRemoveImage, modifier = Modifier.fillMaxWidth()) {
                        Text("Quitar imagen")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private data class SelectedImage(
    val base64: String,
    val mimeType: String,
    val sizeBytes: Long
)

private suspend fun Context.readImageSelection(uri: Uri): SelectedImage? = withContext(Dispatchers.IO) {
    val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return@withContext null
    if (bytes.isEmpty()) return@withContext null
    val mimeType = contentResolver.getType(uri) ?: guessImageMimeType(bytes) ?: "application/octet-stream"
    SelectedImage(
        base64 = Base64.encodeToString(bytes, Base64.NO_WRAP),
        mimeType = mimeType,
        sizeBytes = bytes.size.toLong()
    )
}

private fun Bitmap.toImageSelection(): SelectedImage {
    val bytes = ByteArrayOutputStream().use { output ->
        compress(Bitmap.CompressFormat.JPEG, 92, output)
        output.toByteArray()
    }
    return SelectedImage(
        base64 = Base64.encodeToString(bytes, Base64.NO_WRAP),
        mimeType = "image/jpeg",
        sizeBytes = bytes.size.toLong()
    )
}

private fun guessImageMimeType(bytes: ByteArray): String? {
    return when {
        bytes.size >= 8 &&
            bytes[0] == 0x89.toByte() &&
            bytes[1] == 0x50.toByte() &&
            bytes[2] == 0x4E.toByte() &&
            bytes[3] == 0x47.toByte() -> "image/png"
        bytes.size >= 2 &&
            bytes[0] == 0xFF.toByte() &&
            bytes[1] == 0xD8.toByte() -> "image/jpeg"
        else -> null
    }
}
