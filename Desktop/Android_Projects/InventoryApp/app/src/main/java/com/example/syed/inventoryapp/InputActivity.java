package com.example.syed.inventoryapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.syed.inventoryapp.data.InventoryContract.InventoryEntry;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by syed on 2017-06-16.
 */

public class InputActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;

    private ImageView imageView;
    private EditText titleView;
    private EditText quantityView;
    private EditText priceView;
    private EditText supplierView;
    Bitmap bitmap;
    private static final int SELECT_PICTURE = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product);
        imageView = (ImageView) findViewById(android.R.id.icon);
        titleView = (EditText) findViewById(R.id.edit_prod_name);
        priceView = (EditText) findViewById(R.id.price);
        quantityView = (EditText) findViewById(R.id.quantity);
        supplierView = (EditText) findViewById(R.id.supplier);
        Button save = (Button) findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                saveProduct();
            }
        });
    }

    private void saveProduct() {
        String nameString = titleView.getText().toString().trim();
        String priceString = priceView.getText().toString().trim();
        String quantityString = quantityView.getText().toString().trim();
        String supplierString = supplierView.getText().toString().trim();

        if(bitmap == null)
        {
            Toast.makeText(this, "Please select a product Image.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(supplierString)) {
            Toast.makeText(this, "Please fill all the fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] ImageInByte = BitmapUtil.getBytes(bitmap);
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_NAME, nameString);
        values.put(InventoryEntry.COLUMN_PRICE, priceString);
        values.put(InventoryEntry.COLUMN_QUANTITY, quantityString);
        values.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL, supplierString);
        values.put(InventoryEntry.COLUMN_PICTURE, ImageInByte);

        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, "Insert Product failed.",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, "Product successfully added.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

    }


    public void pickPhoto(View view) {
        //TODO: launch the photo picker
        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        Uri uri = null;
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
           // Log.i(TAG, "Uri: " + uri.toString());
          //  showImage(uri);

            uri = resultData.getData();
            try {
                bitmap=getBitmapFromUri(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmap);
        }
    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}
