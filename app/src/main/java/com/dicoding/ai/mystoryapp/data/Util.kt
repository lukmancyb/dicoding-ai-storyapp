package com.dicoding.ai.mystoryapp.data

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.dicoding.ai.mystoryapp.databinding.CustomLoadingDialogBinding

fun Context.loadingDialog(layoutInflater: LayoutInflater): Dialog {
    val dialog = Dialog(this)
    val binding : CustomLoadingDialogBinding = CustomLoadingDialogBinding.inflate(layoutInflater)
    dialog.setContentView(binding.root)
    dialog.setCancelable(true)

    return dialog
}