package com.example.syed.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.syed.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by syed on 2017-06-15.
 */

public class InventoryProvider extends ContentProvider {
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();


    private static final int INVENTORY = 100;

    private static final int INVENTORY_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionARG, @Nullable String sort) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor cursor = null;
        switch (match) {

            case INVENTORY:

                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionARG, null, null, sort);
                break;
            case INVENTORY_ID:

                selection = InventoryEntry.COLUMN_ID + " = ?";
                selectionARG = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionARG, null, null, sort);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertInventory(uri, contentValues);

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }


    }

    private Uri insertInventory(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(InventoryEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        // Check that the quantity is valid
        Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Inventory requires valid Quantity");
        }

        // If the price is provided, check that it's greater than or equal to 0 kg
        Integer price = values.getAsInteger(InventoryEntry.COLUMN_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Inventory requires valid price");
        }

        String supplier = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_EMAIL);
        if (supplier == null) {
            throw new IllegalArgumentException("Product requires a SUPPLIER_EMAIL");
        }
        String picture = values.getAsString(InventoryEntry.COLUMN_PICTURE);
        if (picture == null) {
            throw new IllegalArgumentException("Product requires a picture");
        }


        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(InventoryEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        if (id != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }


        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] args) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;


        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY_ID:
                selection = InventoryEntry.COLUMN_ID + "= ?";
                args = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, args);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] args) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated = 0;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY_ID:

                Integer quantity = contentValues.getAsInteger(InventoryEntry.COLUMN_QUANTITY);

                if (quantity == null || quantity < 0) {
                    throw new IllegalArgumentException("Inventory requires valid Quantity");
                }
                selection = InventoryEntry.COLUMN_ID + " = ?";
                args = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = db.update(InventoryEntry.TABLE_NAME, contentValues, selection, args);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}

