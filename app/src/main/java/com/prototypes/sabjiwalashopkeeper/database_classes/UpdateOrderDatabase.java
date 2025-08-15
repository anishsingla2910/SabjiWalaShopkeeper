package com.prototypes.sabjiwalashopkeeper.database_classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class UpdateOrderDatabase extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "VEGETABLES_DATA";
    public String Col1 = "VEGETABLE_ID";
    public String Col2 = "VEGETABLE_NAME";
    public String Col3 = "VEGETABLE_CATEGORY";
    public String Col5 = "VEGETABLE_PRICE";
    public String Col6 = "VEGETABLE_PRICE_PER_UNIT";
    public String Col7 = "VEGETABLE_QUANTITY";
    public String Col8 = "VEGETABLE_TYPE";
    public String Col9 = "VEGETABLE_STATUS";
    public String Col10 = "VEGETABLE_ACTUAL_QUANTITY";
    public String Col11 = "VEGETABLE_NAME_HINDI";
    public String Col12 = "VEGETABLE_TYPE_HINDI";
    public String Col13 = "VEGETABLE_UNIT_HINDI";

    public UpdateOrderDatabase(@Nullable Context context, @Nullable String database_name) {
        super(context, database_name + ".db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + Col1 + " TEXT PRIMARY KEY, " + Col2 + " TEXT, " + Col3 + " TEXT, " + Col5 + " TEXT, " + Col6 + " TEXT, " + Col7 + " TEXT, " + Col8 + " TEXT, " + Col9 +" TEXT, "
                + Col10 + " TEXT, " + Col11 + " TEXT, " + Col12 + " TEXT, " + Col13 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String ID, String NAME, String CATEGORY, String PRICE, String PRICE_PER_UNIT,
                           String QUANTITY, String TYPE, String STATUS, String ACTUAL_QUANTITY, String VEGETABLE_NAME_HINDI,
                           String VEGETABLE_TYPE_HINDI, String VEGETABLE_UNIT_HINDI){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col1, ID);
        contentValues.put(Col2, NAME);
        contentValues.put(Col3, CATEGORY);
        contentValues.put(Col5, PRICE);
        contentValues.put(Col6, PRICE_PER_UNIT);
        contentValues.put(Col7, QUANTITY);
        contentValues.put(Col8, TYPE);
        contentValues.put(Col9, STATUS);
        contentValues.put(Col10, ACTUAL_QUANTITY);
        contentValues.put(Col11, VEGETABLE_NAME_HINDI);
        contentValues.put(Col12, VEGETABLE_TYPE_HINDI);
        contentValues.put(Col13, VEGETABLE_UNIT_HINDI);
        long progress = db.insert(TABLE_NAME, null, contentValues);
        if (progress == -1){
            return false;
        }else {
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public int deleteData(String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, Col1 + " = ?", new String[]{ID});
    }

    public void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }

    public void updateData(String ID, String NAME, String CATEGORY, String PRICE, String PRICE_PER_UNIT,
                           String QUANTITY, String TYPE,  String STATUS, String  ACTUAL_QUANTITY, String VEGETABLE_NAME_HINDI,
                           String VEGETABLE_TYPE_HINDI, String VEGETABLE_UNIT_HINDI) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col1, ID);
        contentValues.put(Col2, NAME);
        contentValues.put(Col3, CATEGORY);
        contentValues.put(Col5, PRICE);
        contentValues.put(Col6, PRICE_PER_UNIT);
        contentValues.put(Col7, QUANTITY);
        contentValues.put(Col8, TYPE);
        contentValues.put(Col9, STATUS);
        contentValues.put(Col10, ACTUAL_QUANTITY);
        contentValues.put(Col11, VEGETABLE_NAME_HINDI);
        contentValues.put(Col12, VEGETABLE_TYPE_HINDI);
        contentValues.put(Col13, VEGETABLE_UNIT_HINDI);
        db.update(TABLE_NAME, contentValues, Col1 + " = ?", new String[]{ID});
    }
}
