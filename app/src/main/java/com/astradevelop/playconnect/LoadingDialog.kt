package com.astradevelop.playconnect

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater

class LoadingDialog {
    fun showLoadingDialog(context: Context): AlertDialog {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false) // No se puede cerrar al tocar fuera
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        return dialog
    }

}