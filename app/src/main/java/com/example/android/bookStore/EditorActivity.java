package com.example.android.bookStore;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bookStore.data.BookContract;
import com.example.android.bookStore.data.BookDbHelper;
import com.example.android.bookStore.pojo.CatalogItemDetail;
import com.example.android.bookStore.util.AppUtil;
import com.example.android.bookStore.util.IntentUtil;

/**
 * Allows user to create a new product or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {

    /**
     * EditText field to enter the product name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the product price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the product Quanitity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the Supplier name
     */
    private EditText mSupplierNameSpinner;

    /**
     * EditText field to enter the Supplier phone
     */
    private EditText mSupplierPhoneSpinner;

    private CatalogItemDetail mCatalogItemDetail;
    private boolean mIsProductAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        initBundleVariables();
        setUpToolbar();
        init();
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mIsProductAdd)
            getSupportActionBar().setTitle(getString(R.string.title_activity_add));
        else
            getSupportActionBar().setTitle(getString(R.string.title_activity_edit));
    }

    private void initBundleVariables() {
        Intent intent = getIntent();
        mCatalogItemDetail = intent.getParcelableExtra(IntentUtil.ITEM_DETAIL);
        mIsProductAdd = intent.getBooleanExtra(IntentUtil.IS_ADD_PRODUCT, false);
    }

    private void init() {
        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_productName);
        mPriceEditText = (EditText) findViewById(R.id.edit_productPrice);
        mQuantityEditText = (EditText) findViewById(R.id.edit_productQuantity);
        mSupplierNameSpinner = (EditText) findViewById(R.id.edit_supplierName);
        mSupplierPhoneSpinner = (EditText) findViewById(R.id.edit_supplierPhoneNumber);

        if (mCatalogItemDetail != null) {
            mNameEditText.setText(mCatalogItemDetail.getItemName());
            mPriceEditText.setText(String.valueOf(mCatalogItemDetail.getItemPrice()));
            mQuantityEditText.setText(String.valueOf(mCatalogItemDetail.getItemQuantity()));
            mSupplierNameSpinner.setText(mCatalogItemDetail.getSupplierName());
            mSupplierPhoneSpinner.setText(mCatalogItemDetail.getPhoneNumber());
        }
    }

    /**
     * Get user input from editor and save new product into database.
     */
    private void insertOrUpdateProduct() {

        boolean isEntryValid = true;
        boolean isNameValid = true;
        boolean isPriceValid = true;
        boolean isQuantityValid = true;
        boolean isSNameValid = true;
        boolean isSPhoneValid = true;

        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String sNameString = mSupplierNameSpinner.getText().toString().trim();
        String sPhoneString = mSupplierPhoneSpinner.getText().toString().trim();

        if (!AppUtil.getNullCheck(nameString))
            isNameValid = false;

        if (!AppUtil.getNullCheck(priceString))
            isPriceValid = false;

        if (!AppUtil.getNullCheck(quantityString))
            isQuantityValid = false;

        if (!AppUtil.getNullCheck(sNameString))
            isSNameValid = false;

        if (!AppUtil.getNullCheck(sPhoneString))
            isSPhoneValid = false;

        isEntryValid = isNameValid
                || isPriceValid
                || isQuantityValid
                || isSNameValid
                || isSPhoneValid;


        if (!isEntryValid) {
            Toast.makeText(this, "Kindly enter all the fields", Toast.LENGTH_SHORT).show();
        } else if (!isNameValid) {
            Toast.makeText(this, "Name can't be blank", Toast.LENGTH_SHORT).show();
        } else if (!isPriceValid) {
            Toast.makeText(this, "Price can't be blank", Toast.LENGTH_SHORT).show();
        } else if (!isQuantityValid) {
            Toast.makeText(this, "Quantity can't be blank", Toast.LENGTH_SHORT).show();
        } else if (!isSNameValid) {
            Toast.makeText(this, "Supplier name can't be blank", Toast.LENGTH_SHORT).show();
        } else if (!isSPhoneValid) {
            Toast.makeText(this, "Supplier phone can't be blank", Toast.LENGTH_SHORT).show();
        } else {
            int price = Integer.parseInt(priceString);
            int quantity = Integer.parseInt(quantityString);
            long sPhone = Long.parseLong(sPhoneString);

            // Create database helper
            BookDbHelper mDbHelper = new BookDbHelper(this);

            // Gets the database in write mode
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            // Create a ContentValues object where column names are the keys,
            // and pet attributes from the editor are the values.
            ContentValues values = new ContentValues();
            values.put(BookContract.BookEntry.COLUMN_PRODUCT_NAME, nameString);
            values.put(BookContract.BookEntry.COLUMN_PRODUCT_PRICE, price);
            values.put(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY, quantity);
            values.put(BookContract.BookEntry.COLUMN_SUPPLIER_NAME, sNameString);
            values.put(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE, sPhone);

            long newRowId;
            // here we have to decide whether we need to create a new record or update a record
            if (mIsProductAdd) {
                // Insert a new row for book store in the database, returning the ID of that new row.
                newRowId = db.insert(BookContract.BookEntry.TABLE_NAME, null, values);
            } else {
                // Update a new row for book store in the database, returning the ID of that new row.
                newRowId = db.update(
                        BookContract.BookEntry.TABLE_NAME,
                        values,
                        "_id=" + mCatalogItemDetail.getId(),
                        null);
            }

            // Show a toast message depending on whether or not the insertion was successful
            if (newRowId == -1) {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, "Error with saving product", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent();
                intent.putExtra(IntentUtil.IS_ITEM_DELETED, false);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                insertOrUpdateProduct();
                return true;
            case R.id.action_delete:
                deleteItemFromCatalog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteItemFromCatalog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Create database helper
                BookDbHelper mDbHelper = new BookDbHelper(EditorActivity.this);
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
}
