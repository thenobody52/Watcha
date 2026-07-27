package com.fraziym.soft.watcha.domain.model

import java.io.File

data class SubtitleItem(
    val id: String,
    val name: String,
    val language: String,
    val file: File? = null,
    val url: String? = null,
    val isEmbedded: Boolean = false
)
