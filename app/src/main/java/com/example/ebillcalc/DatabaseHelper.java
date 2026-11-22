
package com.example.ebillcalc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "EBillCalc.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_BILLS = "bills";

    public static final String COL_ID = "id";
    public static final String COL_MONTH = "month";
    public static final String COL_UNITS = "units";
    public static final String COL_TOTAL = "total";
    public static final String COL_REBATE = "rebate";
    public static final String COL_FINAL = "final_cost";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_BILLS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MONTH + " TEXT, " +
                COL_UNITS + " INTEGER, " +
                COL_TOTAL + " REAL, " +
                COL_REBATE + " INTEGER, " +
                COL_FINAL + " REAL)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILLS);
        onCreate(db);
    }

    // Save bill
    public boolean insertBill(String month, int units,
                              double total, int rebate,
                              double finalCost) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_MONTH, month);
        values.put(COL_UNITS, units);
        values.put(COL_TOTAL, total);
        values.put(COL_REBATE, rebate);
        values.put(COL_FINAL, finalCost);

        long result = db.insert(TABLE_BILLS, null, values);

        return result != -1;
    }

    // Get all bills
    public Cursor getAllBills() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                TABLE_BILLS,
                null,
                null,
                null,
                null,
                null,
                COL_ID + " DESC"
        );
    }

    // Get one bill
    public Cursor getBillById(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                TABLE_BILLS,
                null,
                COL_ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
    }

    // Update bill
    public boolean updateBill(int id, String month, int units,
                              double total, int rebate,
                              double finalCost) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_MONTH, month);
        values.put(COL_UNITS, units);
        values.put(COL_TOTAL, total);
        values.put(COL_REBATE, rebate);
        values.put(COL_FINAL, finalCost);

        int result = db.update(
                TABLE_BILLS,
                values,
                COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    // Delete bill
    public boolean deleteBill(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_BILLS,
                COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }
}
