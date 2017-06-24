package com.example.syed.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.syed.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by syed on 2017-06-15.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {


    private static String DATABASE_NAME = "inventory.db";

    private static int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + "( "
                + InventoryEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + InventoryEntry.COLUMN_NAME + " TEXT NOT NULL ,"
                + InventoryEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + InventoryEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + InventoryEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_PICTURE + " BLOB NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
