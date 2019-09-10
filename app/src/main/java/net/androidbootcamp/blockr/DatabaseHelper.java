package net.androidbootcamp.blockr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG ="DatabaseHelper";
    private static final String TABLE_NAME ="white_list_table";
    private static  final String COL1 ="ID";
    private static final String COL2="contactID";


    public DatabaseHelper(Context context) {
        super(context,TABLE_NAME,null,1);
    }
    //create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable ="CREATE TABLE "+ TABLE_NAME+ "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "+COL2 +" INT "+"NOT NULL UNIQUE)";
        db.execSQL(createTable);
    }
    //How to upgrade table, required method never used
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }
    //How to add a new record
    public boolean addData(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2,id);


        long result = db.insert(TABLE_NAME,null,contentValues);
        return result != -1;
    }
    //How to retrieve information, currently select * because the table is simple
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from white_list_table ",null);
        return cursor;
        }




}


