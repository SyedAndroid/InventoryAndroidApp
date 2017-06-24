package com.example.syed.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.syed.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by syed on 2017-06-15.
 */

public class DetailsActivity extends AppCompatActivity {


    TextView mTitle;
    TextView mPrice;
    TextView mQuantity;
    ImageView mPicture;
    TextView mEmail;
    Button mAddQuantity;
    Button mDeleteQuantity;
    Button mOrder;
    Uri uri;
    long key;
    int quantity;
    String supplierEmail;
    String productName;
    Boolean quantityChanged = false;
    Button saveQuantity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        mTitle = (TextView) findViewById(R.id.detail_prod_name);
        mPrice = (TextView) findViewById(R.id.detail_price1);
        mQuantity = (EditText) findViewById(R.id.detail_quantity);
        mPicture = (ImageView) findViewById(R.id.detail_image);
        mEmail = (TextView) findViewById(R.id.supplier);
        mAddQuantity = (Button) findViewById(R.id.add_quantity);
        mDeleteQuantity = (Button) findViewById(R.id.delete_quantity);
        mOrder = (Button) findViewById(R.id.order);
        saveQuantity = (Button) findViewById(R.id.save_quantity);


        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                newString = null;
            } else {
                newString = extras.getString("itemId");
            }
        } else {
            newString = (String) savedInstanceState.getSerializable("itemId");
        }
        key = Long.parseLong(newString);
        uri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, key);
        updateDetails(key);


        mAddQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseQuantity();
                quantityChanged = true;
                // return;

            }
        });

        mDeleteQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                decreaseQuantity();
                quantityChanged = true;

            }
        });


        mOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:" + supplierEmail));
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New order from Inventory App company!");
                String bodyMessage = "We are putting a order of " +
                        productName +
                        ". Thank you";
                intent.putExtra(android.content.Intent.EXTRA_TEXT, bodyMessage);
                startActivity(intent);

            }
        });

        saveQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityValueString = mQuantity.getText().toString();
                int QuantityValue = Integer.parseInt(quantityValueString);
                uri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, key);
                updateProductQuantity(QuantityValue);
                quantityChanged = false;
                finish();
            }
        });

    }


    public void updateDetails(long id) {
        uri = InventoryEntry.CONTENT_URI;
        String[] projection = {
                InventoryEntry.COLUMN_NAME,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_PICTURE,
                InventoryEntry.COLUMN_SUPPLIER_EMAIL
        };

        String selection = InventoryEntry.COLUMN_ID + " = ?";
        String[] args = {String.valueOf(id)};
        Cursor c = getContentResolver().query(uri, projection, selection, args, null);

        if (c.moveToFirst()) {

            int noOfRows = c.getCount();
            int titleIndex = c.getColumnIndex(InventoryEntry.COLUMN_NAME);
            int priceIndex = c.getColumnIndex(InventoryEntry.COLUMN_PRICE);
            int quantityIndex = c.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
            int emailIndex = c.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_EMAIL);
            int pictureIndex = c.getColumnIndex(InventoryEntry.COLUMN_PICTURE);
            quantity = c.getInt(quantityIndex);
            supplierEmail = c.getString(emailIndex);
            productName = c.getString(titleIndex);
            mTitle.setText(productName);
            mPrice.setText(String.valueOf(c.getInt(priceIndex)));
            mQuantity.setText(String.valueOf(quantity));
            mEmail.setText(supplierEmail);
            mPicture.setImageBitmap(BitmapUtil.getImage(c.getBlob(pictureIndex)));
        }
        c.close();
    }

    public void deleteProduct() {

        String selection = InventoryEntry.COLUMN_ID + " = ?";
        String[] args = {String.valueOf(key)};

        int RowsDeleted = 0;
        RowsDeleted = getContentResolver().delete(uri, selection, args);

        if (RowsDeleted == 0) {
            Toast.makeText(this, "Product not deleted",
                    Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "Product successfully deleted from database.",
                    Toast.LENGTH_SHORT).show();
        finish();

    }

    public void updateProductQuantity(int quant) {
        ContentValues values = new ContentValues();

        values.put(InventoryEntry.COLUMN_QUANTITY, quant);
        int rowsUpdated = 0;
        String selection = InventoryEntry.COLUMN_ID + " = ?";
        String[] args = {String.valueOf(key)};
        rowsUpdated = getContentResolver().update(uri, values, selection, args);
        if (rowsUpdated == 0) {
            Toast.makeText(this, "Product Quantity not updated",
                    Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "Product successfully updated  database.",
                    Toast.LENGTH_SHORT).show();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.delete_menu:
                uri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, key);
                DeleteDialog();


                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    private void DeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this product?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteProduct();


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void onBackPressed() {
        if (!quantityChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showQuantityNotSavedDialog(discardButtonClickListener);
    }

    public void showQuantityNotSavedDialog(DialogInterface.OnClickListener discardButtonClickListener) {


        AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
        builder.setMessage("Quantity has not been saved?");
        builder.setPositiveButton("Discard Changes", discardButtonClickListener);
        builder.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void decreaseQuantity() {
        String QuantityValueString = mQuantity.getText().toString();
        int QuantityValue;
        if (QuantityValueString.isEmpty()) {
            return;
        } else if (QuantityValueString.equals("0")) {
            return;
        } else {
            QuantityValue = Integer.parseInt(QuantityValueString);
            mQuantity.setText(String.valueOf(QuantityValue - 1));
        }
    }

    private void increaseQuantity() {
        String previousValueString = mQuantity.getText().toString();
        int QuantityValue;
        if (previousValueString.isEmpty()) {
            QuantityValue = 0;
        } else {
            QuantityValue = Integer.parseInt(previousValueString);
        }
        mQuantity.setText(String.valueOf(QuantityValue + 1));
    }

}
