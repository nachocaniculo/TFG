package com.astradevelop.playconnect

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotificationDBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "notifications.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "notifications"
        const val COLUMN_REQUEST_CODE = "request_code"
        const val COLUMN_TITLE = "title"
        const val COLUMN_BODY = "body"
        const val COLUMN_MATCH = "match"
        const val COLUMN_TYPE = "type"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_REQUEST_CODE INTEGER PRIMARY KEY,
                $COLUMN_TITLE TEXT,
                $COLUMN_BODY TEXT,
                $COLUMN_MATCH TEXT,
                $COLUMN_TYPE TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}
