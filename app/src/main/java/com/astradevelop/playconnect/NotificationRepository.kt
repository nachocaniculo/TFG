package com.astradevelop.playconnect

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class NotificationRepository(context: Context) {

    private val dbHelper = NotificationDBHelper(context)

    fun insertNotification(
        requestCode: Int,
        title: String,
        body: String,
        match: String?,
        type: String?
    ) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(NotificationDBHelper.COLUMN_REQUEST_CODE, requestCode)
            put(NotificationDBHelper.COLUMN_TITLE, title)
            put(NotificationDBHelper.COLUMN_BODY, body)
            put(NotificationDBHelper.COLUMN_MATCH, match)
            put(NotificationDBHelper.COLUMN_TYPE, type)
        }
        db.insertWithOnConflict(NotificationDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    fun getAllNotifications(): List<Map<String, Any?>> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            NotificationDBHelper.TABLE_NAME,
            null, null, null, null, null, null
        )

        val notifications = mutableListOf<Map<String, Any?>>()

        if (cursor.moveToFirst()) {
            do {
                val notification = mapOf(
                    "requestCode" to cursor.getInt(cursor.getColumnIndexOrThrow(NotificationDBHelper.COLUMN_REQUEST_CODE)),
                    "title" to cursor.getString(cursor.getColumnIndexOrThrow(NotificationDBHelper.COLUMN_TITLE)),
                    "body" to cursor.getString(cursor.getColumnIndexOrThrow(NotificationDBHelper.COLUMN_BODY)),
                    "match" to cursor.getString(cursor.getColumnIndexOrThrow(NotificationDBHelper.COLUMN_MATCH)),
                    "type" to cursor.getString(cursor.getColumnIndexOrThrow(NotificationDBHelper.COLUMN_TYPE))
                )
                notifications.add(notification)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return notifications
    }

    fun getNotificationByMatchAndType(match: String, type: String): Map<String, Any?>? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            NotificationDBHelper.TABLE_NAME,
            null,
            "${NotificationDBHelper.COLUMN_MATCH}=? AND ${NotificationDBHelper.COLUMN_TYPE}=?",
            arrayOf(match, type),
            null, null, null
        )

        var result: Map<String, Any?>? = null
        if (cursor.moveToFirst()) {
            result = mapOf(
                "requestCode" to cursor.getString(cursor.getColumnIndexOrThrow(NotificationDBHelper.COLUMN_REQUEST_CODE)),
                "title" to cursor.getString(cursor.getColumnIndexOrThrow(NotificationDBHelper.COLUMN_TITLE)),
                "body" to cursor.getString(cursor.getColumnIndexOrThrow(NotificationDBHelper.COLUMN_BODY)),
                "match" to cursor.getString(cursor.getColumnIndexOrThrow(NotificationDBHelper.COLUMN_MATCH)),
                "type" to cursor.getString(cursor.getColumnIndexOrThrow(NotificationDBHelper.COLUMN_TYPE))
            )
        }

        cursor.close()
        db.close()
        return result
    }

    fun deleteNotification(requestCode: Int) {
        val db = dbHelper.writableDatabase
        db.delete(
            NotificationDBHelper.TABLE_NAME,
            "${NotificationDBHelper.COLUMN_REQUEST_CODE}=?",
            arrayOf(requestCode.toString())
        )
        db.close()
    }

    fun updateNotificationTime(
        requestCode: Int,
        newTitle: String,
        newBody: String,
        newMatch: String?,
        newType: String?
    ) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(NotificationDBHelper.COLUMN_TITLE, newTitle)
            put(NotificationDBHelper.COLUMN_BODY, newBody)
            put(NotificationDBHelper.COLUMN_MATCH, newMatch)
            put(NotificationDBHelper.COLUMN_TYPE, newType)
        }

        db.update(
            NotificationDBHelper.TABLE_NAME,
            values,
            "${NotificationDBHelper.COLUMN_REQUEST_CODE}=?",
            arrayOf(requestCode.toString())
        )
        db.close()
    }
}
