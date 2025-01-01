package com.example.green

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "carbon_footprint.db"
        const val DATABASE_VERSION = 1

        // Table for calculations
        const val TABLE_CALCULATIONS = "Calculations"
        const val COLUMN_ID = "id"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_VALUE = "value"

        // Table for users
        const val TABLE_USERS = "Users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USER_LOGIN = "login"
        const val COLUMN_USER_PASSWORD = "password"
        const val COLUMN_USER_NAME = "name"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create Calculations table
        val createCalculationsTableQuery = """
            CREATE TABLE $TABLE_CALCULATIONS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_VALUE REAL
            )
        """.trimIndent()
        db.execSQL(createCalculationsTableQuery)

        // Create Users table
        val createUsersTableQuery = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_LOGIN TEXT UNIQUE,
                $COLUMN_USER_PASSWORD TEXT,
                $COLUMN_USER_NAME TEXT
            )
        """.trimIndent()
        db.execSQL(createUsersTableQuery)

        // Insert first user
        val insertFirstUserQuery = """
            INSERT INTO $TABLE_USERS ($COLUMN_USER_LOGIN, $COLUMN_USER_PASSWORD, $COLUMN_USER_NAME)
            VALUES ('e24a578k', '00000000', 'Farah SAFSAFI')
        """.trimIndent()
        db.execSQL(insertFirstUserQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CALCULATIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }
}
