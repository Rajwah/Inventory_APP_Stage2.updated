
package com.example.android.bookStore;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookStore.adapters.CatalogAdapter;
import com.example.android.bookStore.data.BookContract;
import com.example.android.bookStore.data.BookDbHelper;
import com.example.android.bookStore.pojo.CatalogItemDetail;
import com.example.android.bookStore.util.IntentUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays list of BOOK STORE ITEMS that were entered and stored in the app.
 */
public class CatalogActivity extends BaseNavigationActivity implements View.OnClickListener,
        CatalogAdapter.ICatalogListContract {

    private static final int REQ_CODE_ADD_PRODUCT = 1000;
    private static final int REQ_CODE_EDIT_PRODUCT = 1001;
    private static final int REQ_CODE_PRODUCT_DETAILS = 1002;
    /**
     * Database helper that will provide us access to the database
     */
    private BookDbHelper mDbHelper;
    private RecyclerView mRVListItems;
    private List<CatalogItemDetail> mListCatalogItems;
    private CatalogAdapter mCatalogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        init();
    }

    private void init() {
        mListCatalogItems = new ArrayList<>();
        TextView textView = (TextView) findViewById(R.id.textToolbar);
        textView.setText(getString(R.string.title_activity_main));
        setUpNavigationView((DrawerLayout) findViewById(R.id.drawer_layout),
                (NavigationView) findViewById(R.id.nav_view));
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new BookDbHelper(this);
        // recycler view for the list of items displayed in catalog
        mRVListItems = (RecyclerView) findViewById(R.id.rv_listItems);
        final LinearLayoutManager productsLayoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false);
        mRVListItems.setLayoutManager(productsLayoutManager);
        mCatalogAdapter = new CatalogAdapter(this, this, mListCatalogItems);
        mRVListItems.setAdapter(mCatalogAdapter);
        displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        final String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_PRODUCT_NAME,
                BookContract.BookEntry.COLUMN_PRODUCT_PRICE,
                BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookContract.BookEntry.COLUMN_SUPPLIER_NAME,
                BookContract.BookEntry.COLUMN_SUPPLIER_PHONE
        };
        // Perform a query on the BOOK STORE table
        final Cursor cursor = db.query(
                BookContract.BookEntry.TABLE_NAME,   // The table to query
                projection,                          // The columns to return
                null,                       // The columns for the WHERE clause
                null,                   // The values for the WHERE clause
                null,                       // Don't group the rows
                null,                        // Don't filter by row groups
                null);                      // The sort order

        TextView displayView = (TextView) findViewById(R.id.text_view_book);
        try {
            int dbItemCount = cursor.getCount();
            // Show 0 count else show the products
            if (dbItemCount == 0) {
                mRVListItems.setVisibility(View.GONE);
                displayView.setVisibility(View.VISIBLE);
                displayView.setText("The book store table contains " + dbItemCount + " products.\n\n Kindly add the products");
            } else {
                displayView.setVisibility(View.GONE);
                mRVListItems.setVisibility(View.VISIBLE);
                loadCoursesFromDatabase(cursor);
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                intent.putExtra(IntentUtil.IS_ADD_PRODUCT, true);
                startActivityForResult(intent, REQ_CODE_ADD_PRODUCT);
                break;
        }
    }

    @Override
    public void onProductSale(int position) {
        // Check if the product count is greater than 0, update the value till than
        CatalogItemDetail catalogItemDetail = mListCatalogItems.get(position);
        int quantity = catalogItemDetail.getItemQuantity();
        if (quantity > 0 && mDbHelper != null) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY, quantity - 1);
            long newRowId = db.update(
                    BookContract.BookEntry.TABLE_NAME,
                    values,
                    "_id=" + catalogItemDetail.getId(),
                    null);
            // Check if the row is properly updated
            if (newRowId != -1) {
                displayDatabaseInfo();
            }
        }
    }

    @Override
    public void onProductEdit(int position) {
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra(IntentUtil.ITEM_DETAIL, mListCatalogItems.get(position));
        startActivityForResult(intent, REQ_CODE_EDIT_PRODUCT);
    }

    @Override
    public void onProductSelected(int position) {
        Intent intent = new Intent(this, ProductActivity.class);
        intent.putExtra(IntentUtil.ITEM_DETAIL, mListCatalogItems.get(position));
        startActivityForResult(intent, REQ_CODE_PRODUCT_DETAILS);
    }

    private void launchActivityToEditProduct(int position) {
    }

    @Override
    protected void onDestroy() {
        // Close any open database object.
        mDbHelper.close();
        super.onDestroy();
    }

    private void loadCoursesFromDatabase(Cursor cursor) {
        // Figure out the index of each column
        int idColumnIndex = cursor.getColumnIndex(BookContract.BookEntry._ID);
        int productNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
        int productPriceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_PRICE);
        int productQuantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY);
        int supplierNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_NAME);
        int supplierPhoneColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE);

        // Clearing the list for old existing items that isn't required any more
        this.mListCatalogItems.clear();

        // Iterate through all the returned rows in the cursor
        while (cursor.moveToNext()) {
            // Use that index to extract the String or Int value of the word
            // at the current row the cursor is on.
            int currentID = cursor.getInt(idColumnIndex);
            String currentProductName = cursor.getString(productNameColumnIndex);
            int currentProductPrice = cursor.getInt(productPriceColumnIndex);
            int currentProductQuantity = cursor.getInt(productQuantityColumnIndex);
            String currentSupplierName = cursor.getString(supplierNameColumnIndex);
            int currentSupplierPhone = cursor.getInt(supplierPhoneColumnIndex);

            CatalogItemDetail catalogItemDetail =
                    new CatalogItemDetail(
                            currentID,
                            currentProductName,
                            currentProductPrice,
                            currentProductQuantity,
                            currentSupplierName,
                            currentSupplierPhone + ""
                    );
            this.mListCatalogItems.add(catalogItemDetail);
        }
        mCatalogAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_ADD_PRODUCT:
            case REQ_CODE_EDIT_PRODUCT:
                if (resultCode == RESULT_OK && data != null) {
                    boolean isDeleted =
                            data.getBooleanExtra(IntentUtil.IS_ITEM_DELETED, false);
                    if (isDeleted) {
                        Toast.makeText(this,
                                getString(R.string.label_product_deleted_success), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this,
                                getString(R.string.label_product_saved_success), Toast.LENGTH_SHORT).show();
                    }
                    displayDatabaseInfo();
                }
                break;
            case REQ_CODE_PRODUCT_DETAILS:
                if (resultCode == RESULT_OK && data != null) {
                    boolean isDeleted =
                            data.getBooleanExtra(IntentUtil.IS_ITEM_DELETED, false);
                    if (isDeleted) {
                        Toast.makeText(this,
                                getString(R.string.label_product_deleted_success), Toast.LENGTH_SHORT).show();
                    }
                    displayDatabaseInfo();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
