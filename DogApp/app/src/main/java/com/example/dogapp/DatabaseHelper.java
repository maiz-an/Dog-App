package com.example.dogapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    public static final String DATABASE_NAME = "DogApp.db";

    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "ID";
    public static final String COL_USER_NAME = "Name";
    public static final String COL_USER_EMAIL = "Email";
    public static final String COL_USER_PASSWORD = "Password";
    public static final String COL_USER_ADDRESS = "Address";

    public static final String TABLE_PRODUCTS = "products";
    public static final String COL_PRODUCT_ID = "ID";
    public static final String COL_PRODUCT_NAME = "Name";
    public static final String COL_PRODUCT_DESCRIPTION = "Description";
    public static final String COL_PRODUCT_PRICE = "Price";
    public static final String COL_PRODUCT_CATEGORY = "Category";
    public static final String COL_PRODUCT_PICTURE = "Picture";  // Store file path as a string

    public static final String TABLE_CART = "cart";
    public static final String COL_CART_ID = "ID";
    public static final String COL_CART_PRODUCT_ID = "ProductID";
    public static final String COL_CART_QUANTITY = "Quantity";

    public static final String TABLE_ORDERS = "orders";
    public static final String COL_ORDER_ID = "ID";
    public static final String COL_ORDER_USER_ID = "UserID";
    public static final String COL_ORDER_STATUS = "Status";
    public static final String COL_ORDER_TOTAL = "TotalAmount";

    public static final String TABLE_FEEDBACK = "feedback";
    public static final String COL_FEEDBACK_ID = "ID";
    public static final String COL_FEEDBACK_USER_ID = "UserID"; // New column for linking feedback to a user
    public static final String COL_FEEDBACK_TEXT = "Text";

    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, null, null); // Deletes all rows in the cart table
    }

    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2); // Increment this number if you made schema changes
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                    COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_USER_NAME + " TEXT, " +
                    COL_USER_EMAIL + " TEXT, " +
                    COL_USER_PASSWORD + " TEXT, " +
                    COL_USER_ADDRESS + " TEXT)");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCTS + " (" +
                    COL_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_PRODUCT_NAME + " TEXT, " +
                    COL_PRODUCT_DESCRIPTION + " TEXT, " +
                    COL_PRODUCT_PRICE + " DOUBLE, " +
                    COL_PRODUCT_CATEGORY + " TEXT, " +
                    COL_PRODUCT_PICTURE + " TEXT)");  // Store file path

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CART + " (" +
                    COL_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_CART_PRODUCT_ID + " INTEGER, " +
                    COL_CART_QUANTITY + " INTEGER)");

            // Create Orders table
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ORDERS + " (" +
                    COL_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_ORDER_USER_ID + " INTEGER, " +
                    COL_ORDER_STATUS + " TEXT DEFAULT 'pending', " +
                    COL_ORDER_TOTAL + " DOUBLE, " +
                    "FOREIGN KEY(" + COL_ORDER_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ")" +
                    " ON DELETE CASCADE ON UPDATE CASCADE)"); // Optional: Ensure foreign key integrity on delete/update

            // Create feedback table with a foreign key linking to users
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_FEEDBACK + " (" +
                    COL_FEEDBACK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_FEEDBACK_USER_ID + " INTEGER, " + // Add user ID column
                    COL_FEEDBACK_TEXT + " TEXT, " +
                    "FOREIGN KEY(" + COL_FEEDBACK_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ")" +
                    " ON DELETE CASCADE ON UPDATE CASCADE)"); // Ensure foreign key integrity

            Log.d(TAG, "Tables created successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Error creating tables: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            // Drop older tables if they exist
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDBACK);

            // Create tables again
            onCreate(db);

            Log.d(TAG, "Tables upgraded successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading tables: " + e.getMessage());
        }
    }

    // Insert user data into the database
    public long insertUser(String name, String email, String password, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = -1;

        // Use a transaction block to ensure atomicity
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_USER_NAME, name);
            contentValues.put(COL_USER_EMAIL, email);
            contentValues.put(COL_USER_PASSWORD, password);
            contentValues.put(COL_USER_ADDRESS, address);

            result = db.insertOrThrow(TABLE_USERS, null, contentValues);
            Log.d(TAG, "User inserted successfully with ID: " + result);

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, "SQLException inserting user: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error inserting user: " + e.getMessage());
        } finally {
            db.endTransaction();
        }

        return result;
    }

    // Method to check if a user with the given email exists
    public boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_USER_ID};
        String selection = COL_USER_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        Log.d(TAG, "User exists: " + exists);
        return exists;
    }

    // Method to retrieve user by email and password
    public Cursor getUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_USER_NAME, COL_USER_EMAIL};
        String selection = COL_USER_EMAIL + " = ? AND " + COL_USER_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};
        return db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
    }

    // Method to retrieve user details
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, null, null, null, null, COL_USER_NAME + " ASC");
        Log.d(TAG, "getAllUsers query executed, rows fetched: " + cursor.getCount()); // Debug log
        return cursor;
    }

    // Method to search user details
    public Cursor searchUsersByName(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_USER_NAME + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%"};
        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, COL_USER_NAME + " ASC");
        Log.d(TAG, "searchUsersByName query executed, rows fetched: " + cursor.getCount()); // Debug log
        return cursor;
    }

    // Method to get users by ID
    public Cursor getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        return db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);
    }

    // Method to Update users by ID
    public int updateUser(int userId, String name, String email, String password, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USER_NAME, name);
        contentValues.put(COL_USER_EMAIL, email);
        contentValues.put(COL_USER_PASSWORD, password);
        contentValues.put(COL_USER_ADDRESS, address);
        return db.update(TABLE_USERS, contentValues, COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    // Method to Delete users by ID
    public int deleteUserById(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USERS, COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    // Method to get prod by ID
    public Cursor getProductById(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_PRODUCT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(productId)};
        return db.query(TABLE_PRODUCTS, null, selection, selectionArgs, null, null, null);
    }

    // Method to add a product
    public long addProduct(String name, String description, double price, String category, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = -1;

        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_PRODUCT_NAME, name);
            contentValues.put(COL_PRODUCT_DESCRIPTION, description);
            contentValues.put(COL_PRODUCT_PRICE, price);
            contentValues.put(COL_PRODUCT_CATEGORY, category);
            contentValues.put(COL_PRODUCT_PICTURE, imagePath); // Store image path as String

            // Logging the values before attempting to insert
            Log.d(TAG, "Name: " + name);
            Log.d(TAG, "Description: " + description);
            Log.d(TAG, "Price: " + price);
            Log.d(TAG, "Category: " + category);
            Log.d(TAG, "ImagePath: " + imagePath);

            // Attempt to insert the product
            result = db.insertOrThrow(TABLE_PRODUCTS, null, contentValues);

            if (result == -1) {
                Log.e(TAG, "Insert failed. No row added.");
            } else {
                Log.d(TAG, "Product added successfully with ID: " + result);
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, "SQLException inserting product: " + e.getMessage(), e);
            Log.e(TAG, Log.getStackTraceString(e)); // Log the stack trace for more details
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error inserting product: " + e.getMessage(), e);
            Log.e(TAG, Log.getStackTraceString(e)); // Log the stack trace for more details
        } finally {
            db.endTransaction();
        }

        return result;
    }

    // Method to update a product
    public int updateProduct(int productId, String name, String description, double price, String category, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PRODUCT_NAME, name);
        contentValues.put(COL_PRODUCT_DESCRIPTION, description);
        contentValues.put(COL_PRODUCT_PRICE, price);
        contentValues.put(COL_PRODUCT_CATEGORY, category);
        contentValues.put(COL_PRODUCT_PICTURE, imagePath);  // Store image path as String

        return db.update(TABLE_PRODUCTS, contentValues, COL_PRODUCT_ID + " = ?", new String[]{String.valueOf(productId)});
    }

    // Method to delete a product by ID
    public int deleteProductById(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PRODUCTS, COL_PRODUCT_ID + " = ?", new String[]{String.valueOf(productId)});
    }

    // Method to retrieve all products
    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_PRODUCTS, null, null, null, null, null, COL_PRODUCT_NAME + " ASC");
    }

    // Method to search products by name
    public Cursor searchProductsByName(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_PRODUCT_NAME + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%"};
        return db.query(TABLE_PRODUCTS, null, selection, selectionArgs, null, null, COL_PRODUCT_NAME + " ASC");
    }

    // Method to retrieve all items in the cart
    public Cursor getAllCartItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CART, null, null, null, null, null, null);
    }

    // Method to delete a cart item by product ID
    public int deleteCartItemByProductId(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CART, COL_CART_PRODUCT_ID + " = ?", new String[]{String.valueOf(productId)});
    }

    // Insert order
    public long insertOrder(int userId, double totalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = -1;

        try {
            // Log the inputs to ensure they are correct
            Log.d(TAG, "Attempting to insert order. userId: " + userId + ", totalAmount: " + totalAmount);

            // Check for invalid userId
            if (userId == -1) {
                Log.e(TAG, "Invalid userId: " + userId + ". Cannot insert order without a valid user.");
                return result;
            }

            // Verify that the userId exists in the users table
            Cursor cursor = db.query(TABLE_USERS, null, COL_USER_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                cursor.close();
            } else {
                Log.e(TAG, "User ID " + userId + " does not exist in the users table.");
                if (cursor != null) {
                    cursor.close();
                }
                return -1; // Return -1 to indicate failure
            }

            // Prepare the values to insert
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_ORDER_USER_ID, userId);
            contentValues.put(COL_ORDER_TOTAL, totalAmount);

            // Attempt to insert the order
            result = db.insert(TABLE_ORDERS, null, contentValues);

            // Check if the insertion was successful
            if (result == -1) {
                Log.e(TAG, "Order insertion failed. Possible reasons: UserID does not exist, or database constraints are violated.");
            } else {
                Log.d(TAG, "Order inserted successfully with ID: " + result);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception inserting order: " + e.getMessage(), e);
        }

        return result;
    }

    // Update order status
    public int updateOrderStatus(int orderId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ORDER_STATUS, newStatus);

        return db.update(TABLE_ORDERS, contentValues, COL_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)});
    }

    // Retrieve all orders
    public Cursor getAllOrders() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ORDERS, null, null, null, null, null, COL_ORDER_ID + " ASC");
    }

    // Delete order by ID
    public int deleteOrderById(int orderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ORDERS, COL_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)});
    }

    public Cursor getOrdersByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_ORDER_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        return db.query(TABLE_ORDERS, null, selection, selectionArgs, null, null, null);
    }

    // Method to insert feedback into the database
    public long insertFeedback(int userId, String feedbackText) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = -1;
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_FEEDBACK_USER_ID, userId);
            contentValues.put(COL_FEEDBACK_TEXT, feedbackText);

            // Attempt to insert the feedback
            result = db.insert(TABLE_FEEDBACK, null, contentValues);

            if (result == -1) {
                Log.e(TAG, "Feedback insertion failed.");
            } else {
                Log.d(TAG, "Feedback inserted successfully with ID: " + result);
                db.setTransactionSuccessful();
            }
        } catch (SQLException e) {
            Log.e(TAG, "SQLException inserting feedback: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error inserting feedback: " + e.getMessage(), e);
        } finally {
            db.endTransaction();
        }

        return result;
    }

    // Method to retrieve all feedback
    public Cursor getAllFeedback() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_FEEDBACK, null, null, null, null, null, COL_FEEDBACK_ID + " ASC");
    }
}
