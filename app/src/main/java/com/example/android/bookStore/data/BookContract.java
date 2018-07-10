
package com.example.android.bookStore.data;

import android.provider.BaseColumns;

/**
 * API Contract for the BOOK STORE app.
 */
public final class BookContract {

    private BookContract() {
    }

    public static final class BookEntry implements BaseColumns {
        /**
         * Name of database table for BOOK STORE
         */
        public final static String TABLE_NAME = "bookStore";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "Product_name";
        public final static String COLUMN_PRODUCT_PRICE = "Price";
        public final static String COLUMN_PRODUCT_QUANTITY = "Quantity";
        public final static String COLUMN_SUPPLIER_NAME = "Supplier_Name";
        public final static String COLUMN_SUPPLIER_PHONE = "Supplier_Phone_Number";
    }
}