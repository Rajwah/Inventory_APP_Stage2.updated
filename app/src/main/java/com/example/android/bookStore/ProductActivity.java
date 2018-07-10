package com.example.android.bookStore;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookStore.data.BookContract;
import com.example.android.bookStore.data.BookDbHelper;
import com.example.android.bookStore.pojo.CatalogItemDetail;
import com.example.android.bookStore.util.IntentUtil;

public class ProductActivity extends AppCompatActivity implements View.OnClickListener {

    private CatalogItemDetail mCatalogItemDetail;
    private TextView mTextProductQty;
    private TextView mTextSupplierPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        setUpToolbar();
        initBundleVariables();
        init();
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_product_view));
    }

    private void initBundleVariables() {
        mCatalogItemDetail = getIntent().getParcelableExtra(IntentUtil.ITEM_DETAIL);
    }

    private void init() {
        TextView textProductName = (TextView) findViewById(R.id.text_productNameValue);
        TextView textProductPrice = (TextView) findViewById(R.id.text_productPriceValue);
        mTextProductQty = (TextView) findViewById(R.id.text_productQuantityValue);
        TextView textSupplierName = (TextView) findViewById(R.id.text_supplierNameValue);
        mTextSupplierPhone = (TextView) findViewById(R.id.text_supplierPhoneNumberValue);
        findViewById(R.id.button_plus).setOnClickListener(this);
        findViewById(R.id.button_minus).setOnClickListener(this);
        findViewById(R.id.button_call).setOnClickListener(this);
        findViewById(R.id.text_delete).setOnClickListener(this);


        if (mCatalogItemDetail != null) {
            textProductName.setText(mCatalogItemDetail.getItemName());
            textProductPrice.setText(String.valueOf(mCatalogItemDetail.getItemPrice()));
            mTextProductQty.setText(String.valueOf(mCatalogItemDetail.getItemQuantity()));
            textSupplierName.setText(mCatalogItemDetail.getSupplierName());
            mTextSupplierPhone.setText(mCatalogItemDetail.getPhoneNumber());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_plus:
                mTextProductQty.setText(String.valueOf(getProductQuantity() + 1));
                updateItemQuantity();
                break;
            case R.id.button_minus:
                if (getProductQuantity() > 0) {
                    mTextProductQty.setText(String.valueOf(getProductQuantity() - 1));
                    updateItemQuantity();
                }
                break;
            case R.id.button_call:
                String customerCareNumber = mTextSupplierPhone.getText().toString();
                if (customerCareNumber.length() > 0) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + customerCareNumber));
                    startActivity(callIntent);
                }
                break;
            case R.id.text_delete:
                handleDeleteItem();
                break;
        }
    }

    private void handleDeleteItem() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Create database helper
                BookDbHelper mDbHelper = new BookDbHelper(ProductActivity.this);
                // Gets the database in write mode
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                db.delete(
                        BookContract.BookEntry.TABLE_NAME,
                        "_id=" + mCatalogItemDetail.getId(),
                        null
                );
                db.close();
                Intent intent = new Intent();
                intent.putExtra(IntentUtil.IS_ITEM_DELETED, true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do Nothing
            }
        });
        alertDialogBuilder.setTitle(getString(R.string.alert_delete_product));
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private int getProductQuantity() {
        return Integer.parseInt(mTextProductQty.getText().toString());
    }

    private void updateItemQuantity() {
        String quantityString = mTextProductQty.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);

        // Create database helper
        BookDbHelper mDbHelper = new BookDbHelper(this);

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY, quantity);

        long newRowId;
        // Update a new row for book store in the database, returning the ID of that new row.
        newRowId = db.update(
                BookContract.BookEntry.TABLE_NAME,
                values,
                "_id=" + mCatalogItemDetail.getId(),
                null);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving product", Toast.LENGTH_SHORT).show();
        } else {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.label_product_saved_success), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        handleBackPress();
    }

    private void handleBackPress() {
        Intent intent = new Intent();
        intent.putExtra(IntentUtil.IS_ITEM_DELETED, false);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            handleBackPress();
        return super.onOptionsItemSelected(item);
    }
}
