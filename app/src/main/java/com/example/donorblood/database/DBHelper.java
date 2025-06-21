package com.example.donorblood.database;  // Change to your package

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.donorblood.User;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BloodDonor.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_USERS = "users";

    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_CITIZENSHIP = "citizenship";
    private static final String COL_PHONE = "phone";
    private static final String COL_ADDRESS = "address";
    private static final String COL_EMAIL = "email";
    private static final String COL_DOB = "dob";
    private static final String COL_GENDER = "gender";
    private static final String COL_PASSWORD = "password";
    private static final String COL_BLOODGROUP = "bloodgroup";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String createTable = "CREATE TABLE " + TABLE_USERS + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_CITIZENSHIP + " TEXT NOT NULL, "
                + COL_PHONE + " TEXT NOT NULL, "
                + COL_ADDRESS + " TEXT NOT NULL, "
                + COL_EMAIL + " TEXT UNIQUE NOT NULL, "
                + COL_DOB + " TEXT NOT NULL, "
                + COL_GENDER + " TEXT NOT NULL, "
                + COL_PASSWORD + " TEXT NOT NULL, "
                + COL_BLOODGROUP + " TEXT NOT NULL"
                + ")";

        db.execSQL(createTable);

        String createUserProfileTable = "CREATE TABLE user_profile ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "email TEXT UNIQUE, " // Foreign key to users.email
                + "photo BLOB, "
                + "location TEXT, "
                + "donationCount INTEGER DEFAULT 0, "
                + "requestCount INTEGER DEFAULT 0, "
                + "FOREIGN KEY (email) REFERENCES users(email))";

        db.execSQL(createUserProfileTable);
    }







    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table and recreate if DB version changed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS user_profile");

        onCreate(db);
    }

    public boolean saveOrUpdateProfile(String email, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("photo", imageUri);

        // Try update first
        int rowsAffected = db.update("user_profile", values, "email = ?", new String[]{email});
        if (rowsAffected == 0) {
            // If update failed (new entry), insert
            long result = db.insert("user_profile", null, values);
            db.close();
            return result != -1;
        } else {
            db.close();
            return true;
        }
    }

    // Insert a new user
    public boolean insertUser(String name, String citizenship, String phone, String address,
                              String email, String dob, String gender, String hashedPassword, String bloodGroup) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_NAME, name);
        values.put(COL_CITIZENSHIP, citizenship);
        values.put(COL_PHONE, phone);
        values.put(COL_ADDRESS, address);
        values.put(COL_EMAIL, email);
        values.put(COL_DOB, dob);
        values.put(COL_GENDER, gender);
        values.put(COL_PASSWORD, hashedPassword);
        values.put(COL_BLOODGROUP, bloodGroup);


        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;  // Return true if insert success
    }


    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, email, address FROM users WHERE email = ?", new String[]{email});

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(0);
            String emailStr = cursor.getString(1);
            String address = cursor.getString(2);
            cursor.close();
            return new User(name,emailStr,address);
        }

        return null;
    }






    // Check if user exists with email and password
    public boolean checkUser(String email, String inputPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT password FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + " = ?", new String[]{email});

        if (cursor != null && cursor.moveToFirst()) {
            String storedHashedPassword = cursor.getString(0);
            cursor.close();

            try {
                return HashUtil.verifyPassword(inputPassword, storedHashedPassword);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return false; // User not found
    }
    public void insertOrUpdateProfile(String email, byte[] photo, String location) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("photo", photo);
        values.put("location", location);
        Log.d("DB_DEBUG", "Inserting: " + email + ", location: " + location + ", image bytes: " + photo.length);

        db.insertWithOnConflict("user_profile", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }


    public Cursor getUserProfile(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT u.name, u.email, p.location, p.photo " +
                "FROM users u " +
                "LEFT JOIN user_profile p ON u.email = p.email " +
                "WHERE u.email = ?";

        return db.rawQuery(query, new String[]{email});
    }

    public List<User> getUsersByBloodType(String bloodType) {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_USERS+" WHERE bloodgroup = ?", new String[]{bloodType});

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                user.setBloodType(cursor.getString(cursor.getColumnIndexOrThrow("bloodgroup")));
                users.add(user);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return users;
    }

}

