package com.peterparthimos.personalfinancetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBConnector extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Finance.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_1_NAME = "users";
    public static final String TABLE_2_NAME = "purchases";
    public static final String TABLE_3_NAME = "subscriptions";

    public DBConnector(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_1_NAME + "(username text PRIMARY KEY UNIQUE, password text NOT NULL, fname text NOT NULL, lname text NOT NULL, budget double NOT NULL, spent double NOT NULL, average double NOT NULL)");
        db.execSQL("CREATE TABLE " + TABLE_2_NAME + "(purchaseId integer PRIMARY KEY AUTOINCREMENT, username text NOT NULL, amount double NOT NULL, name text NOT NULL, purchaseDate date NOT NULL, FOREIGN KEY(username) REFERENCES users(username))");
        db.execSQL("CREATE TABLE " + TABLE_3_NAME + "(subscriptionId integer PRIMARY KEY AUTOINCREMENT, username text NOT NULL, amount double NOT NULL, name double NOT NULL, renewDate integer NOT NULL, FOREIGN KEY(username) REFERENCES users(username))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_1_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_2_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_3_NAME);
        onCreate(db);
    }

    public boolean register(String username, String password, String fname, String lname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("fname", fname);
        values.put("lname", lname);
        values.put("budget", 1000);
        values.put("spent", 0);
        values.put("average", 0);
        long result = db.insert(TABLE_1_NAME, null, values);
        db.close();
        return result != -1;
    }

    public Cursor login(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_1_NAME + " WHERE username = ? AND password = ?",
                new String[] {username, password});
        return result;
    }

    public void editUser(String username, double newAverage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("spent", 0.0);
        values.put("average", newAverage);
        db.update(TABLE_1_NAME, values, "username = ?", new String[] {username});
    }

    public boolean editBudget(String username, double newBudget) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("budget", newBudget);
        db.update(TABLE_1_NAME, values, "username = ?", new String[] {username});
        return true;
    }

    public boolean addPurchase(String username, String name, double amount, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("name", name);
        values.put("amount", amount);
        values.put("purchaseDate", date);
        long result = db.insert(TABLE_2_NAME, null, values);
        db.close();
        return result != -1;
    }

    public Cursor getAllPurchases(String username, ArrayList<String> prices, ArrayList<String> dates, ArrayList<String> names) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT amount, name, purchaseDate FROM " + TABLE_2_NAME
                + " WHERE username = ? ORDER BY purchaseId DESC", new String[] {username});
        if(c.moveToFirst()) {
            while(!c.isAfterLast()) {
                prices.add(c.getString(c.getColumnIndex("amount")));
                dates.add(c.getString(c.getColumnIndex("purchaseDate")));
                names.add(c.getString(c.getColumnIndex("name")));
                c.moveToNext();
            }
        }
        return c;
    }

    public boolean addSubscription(String username, String name, double amount, int renewDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("name", name);
        values.put("amount", amount);
        values.put("renewDate", renewDate);
        long result = db.insert(TABLE_3_NAME, null, values);
        db.close();
        return result != -1;
    }

    public Cursor getAllSubscriptions(String username, ArrayList<String> prices, ArrayList<String> dates, ArrayList<String> names) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT amount, name, renewDate FROM " + TABLE_3_NAME
                + " WHERE username = ? ORDER BY subscriptionId DESC", new String[] {username});
        if(c.moveToFirst()) {
            while(!c.isAfterLast()) {
                prices.add(c.getString(c.getColumnIndex("amount")));
                dates.add(c.getString(c.getColumnIndex("renewDate")));
                names.add(c.getString(c.getColumnIndex("name")));
                c.moveToNext();
            }
        }
        return c;
    }

    public boolean updateSpent(String username, double spent, double purchase) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        double finalSpent = spent + purchase;
        values.put("spent", finalSpent);
        db.update(TABLE_1_NAME, values, "username = ?", new String[] {username});
        return true;
    }
}