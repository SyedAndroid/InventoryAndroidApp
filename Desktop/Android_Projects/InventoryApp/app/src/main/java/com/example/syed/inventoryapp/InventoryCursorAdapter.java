package com.example.syed.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.syed.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by syed on 2017-06-15.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    private final MainActivity activity;

    public InventoryCursorAdapter(MainActivity context, Cursor c) {
        super(context, c, 0);
        this.activity = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        ImageView imageView = (ImageView) view.findViewById(R.id.picture);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        Button sell = (Button) view.findViewById(R.id.sell);
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_NAME);
        int imageIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PICTURE);
        int quantityIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
        int priceIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
        final int quantity = cursor.getInt(quantityIndex);
        byte[] image = cursor.getBlob(imageIndex);


        nameTextView.setText(cursor.getString(nameColumnIndex));
        imageView.setImageBitmap(BitmapUtil.getImage(image));
        quantityTextView.setText(cursor.getString(quantityIndex));
        priceTextView.setText(cursor.getString(priceIndex));

        final long id = cursor.getLong(cursor.getColumnIndex(InventoryEntry.COLUMN_ID));


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.clickOnViewItem(id);
            }
        });

        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.sellOneItem(id, quantity);
            }
        });

    }

    @Override
    public Cursor getCursor() {
        return super.getCursor();
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }


}
