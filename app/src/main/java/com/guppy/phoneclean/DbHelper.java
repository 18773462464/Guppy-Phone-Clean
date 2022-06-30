package com.guppy.phoneclean;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public final class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = IConstant.DB_NAME;
    private static int DB_VERSION = 1;
    public static final String DATAS_TABLE = IConstant.DATAS_TABLE;

    public DbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + DATAS_TABLE + " (" + IConstant.DB_COLUMN_ID + " text," + IConstant.DB_COLUMN_CONTENT + " text)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
