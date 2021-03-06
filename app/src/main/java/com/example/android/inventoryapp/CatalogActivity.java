package com.example.android.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;
import android.content.ContentValues;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.android.inventoryapp.data.InventoryDbHelper;

/**
 *  - name
 *  - quantity
 *  - supplier name
 *  - product condition
 *  - price
 *  - supplier phone number
 *
 * Displays items listed above that the user inputs.
 */
public class CatalogActivity extends AppCompatActivity {

    /** Database helper that will provide access to the database */
    private InventoryDbHelper mDbHelper;

    /**
     * Floating Action button to send user to add item activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When FAB is click, run this intent
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new InventoryDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        displayDatabaseInfo();
    }
    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the inventory database.
     */
    private void displayDatabaseInfo() {

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM inventory"
        // to get a Cursor that contains all rows from the inventory table.

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_CONDITION,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER };

    // Perform a query on the inventory table
        Cursor cursor = db.query(
                InventoryEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order


        TextView displayView = (TextView) findViewById(R.id.text_view_inventory);

        try {

            // Create a header in the Text View that looks like this:
            //
            // _id - name - supplier name - quantity - condition - price - supplier phone number

            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText("The inventory table contains " + cursor.getCount() + " products.\n\n");
            displayView.append(InventoryEntry._ID + " - " +
                    InventoryEntry.COLUMN_PRODUCT_NAME + " - " +
                    InventoryEntry.COLUMN_PRODUCT_SUPPLIER + " - " +
                    InventoryEntry.COLUMN_PRODUCT_QUANTITY + " - " +
                    InventoryEntry.COLUMN_PRODUCT_CONDITION + " - " +
                    InventoryEntry.COLUMN_PRODUCT_PRICE + " - " +
                    InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER);
            int itemQuantityIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int conditionColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_CONDITION);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int supplierPhoneNumber = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentSupplier = cursor.getString(supplierColumnIndex);
                int currentQuantity = cursor.getInt(itemQuantityIndex);
                int currentCondition = cursor.getInt(conditionColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentSupplierPhoneNumber = cursor.getInt(supplierPhoneNumber);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        currentSupplier + " - " +
                        currentQuantity + " - " +
                        currentCondition + " - " +
                        currentPrice + " - " +
                        currentSupplierPhoneNumber));

            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }


    /**
     * Helper method to insert hardcoded data into the database. For debugging purposes only.
     */
    private void insertProduct() {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, "Laptop 5439");
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER, "Elita Bay");
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, "7");
        values.put(InventoryEntry.COLUMN_PRODUCT_CONDITION, InventoryEntry.CONDITION_NEW);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, 2000);
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER, 1234567890);

        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the inventory table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.
        long newRowId = db.insert(InventoryEntry.TABLE_NAME, null, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertProduct();
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

