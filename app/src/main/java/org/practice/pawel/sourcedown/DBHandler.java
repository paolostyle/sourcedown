package org.practice.pawel.sourcedown;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

class DBHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sourcedown.db";
    private static final String SOURCES_TABLE_NAME = "sources";
    private static final String SOURCES_COLUMN_ID = "id";
    private static final String SOURCES_COLUMN_URL = "url";
    private static final String SOURCES_COLUMN_CONTENT = "content";

    DBHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String table = "CREATE TABLE " + SOURCES_TABLE_NAME + " (" +
                SOURCES_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                SOURCES_COLUMN_URL + " TEXT, " +
                SOURCES_COLUMN_CONTENT + " TEXT); ";
        String uk = "CREATE UNIQUE INDEX url_i ON " + SOURCES_TABLE_NAME + " (" +
                SOURCES_COLUMN_URL +");";
        SQLiteStatement stmt = db.compileStatement(table + uk);
        stmt.execute();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SOURCES_TABLE_NAME);
        onCreate(db);
    }

    void insertSource(String url, String content) {
        String sql = "INSERT INTO " + SOURCES_TABLE_NAME + " (" + SOURCES_COLUMN_ID + ", "
                + SOURCES_COLUMN_URL + ", " +  SOURCES_COLUMN_CONTENT + ") " +
                "VALUES (NULL, ?, ?)";
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(sql);

        statement.bindString(1, url);
        statement.bindString(2, content);

        statement.executeInsert();
    }

    int updateSource(String url, String content) {
        String sql = "UPDATE " + SOURCES_TABLE_NAME + " SET " + SOURCES_COLUMN_URL + " = ?, " +
                SOURCES_COLUMN_CONTENT + " = ? WHERE " + SOURCES_COLUMN_URL + "= ?";
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(sql);

        statement.bindString(1, url);
        statement.bindString(2, content);
        statement.bindString(3, url);

        return statement.executeUpdateDelete();
    }

    Cursor getSource(String url) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + SOURCES_TABLE_NAME + " WHERE "
                + SOURCES_COLUMN_URL + " = ?", new String[] {url});
    }

    void dropDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + SOURCES_TABLE_NAME);
        String table = "CREATE TABLE " + SOURCES_TABLE_NAME + " (" +
                SOURCES_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                SOURCES_COLUMN_URL + " TEXT, " +
                SOURCES_COLUMN_CONTENT + " TEXT); ";
        String uk = "CREATE UNIQUE INDEX url_i ON " + SOURCES_TABLE_NAME + " (" +
                SOURCES_COLUMN_URL +");";
        SQLiteStatement stmt = db.compileStatement(table + uk);
        stmt.execute();
    }
}