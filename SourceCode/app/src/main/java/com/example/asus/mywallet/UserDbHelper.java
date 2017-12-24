package com.example.asus.mywallet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class UserDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PERSONSINFO.DB";
    public static final String TABLE_NAME="PERSON_DETAILS";

    public static final String ID="_id";
    public static final String USER_AMOUNT="amount";
    public static final String USER_DESCRIPTION="description";
    public static final String USER_DATATYPE="datatype";
    public static final String USER_DATE="date";


    private static final int DATABASE_VERSION =4;
    private static final String CREATION_QUERY = "CREATE TABLE "+TABLE_NAME+"( "+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+USER_AMOUNT+" VARCHAR(255), "+USER_DESCRIPTION+" VARCHAR(255), "+USER_DATATYPE+" VARCHAR(150), "+USER_DATE+" VARCHAR(100));";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;
    private static final String SELECTALL_QUERY ="SELECT * FROM " + TABLE_NAME;
    private static final String LATESTDATA_QUERY ="SELECT * FROM "+TABLE_NAME+ " ORDER BY "+ID+" DESC LIMIT 1";
    private static final String MONTHLY_SUMMARY ="Select * FROM  (SELECT sum("+USER_AMOUNT+") as income, strftime('%Y-%m', "+USER_DATE+") as Month FROM "+TABLE_NAME+" where "+USER_DATATYPE+"='Income' group by Month) NATURAL JOIN (SELECT sum("+USER_AMOUNT+") as expence, strftime('%Y-%m', "+USER_DATE+") as Month FROM  "+TABLE_NAME+" where "+USER_DATATYPE+"='Expense' group by Month)";
    private static final String SELECT_DATE_FOR_SUMMARY ="SELECT DISTINCT "+USER_DATE+" FROM " + TABLE_NAME;
    private Context context;



    public UserDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
        Log.e("DATABASE OPERATIONS","DATABASE CREATION/OPEN...");
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.e("inside on create","ON CREATE IS called");
        try {
            db.execSQL(CREATION_QUERY);
            Toast.makeText(context,"TABLE CREATED "+TABLE_NAME, Toast.LENGTH_LONG).show();
        }catch (Exception e){

            Toast.makeText(context,"Exception : "+e, Toast.LENGTH_LONG).show();
        }

    }


    public long insertData(String amount, String description, String datatype, String date){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues =new ContentValues();
        contentValues.put(USER_AMOUNT,amount);
        contentValues.put(USER_DESCRIPTION,description);
        contentValues.put(USER_DATATYPE,datatype);
        DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        String inputDateStr=date;
        Date ddate = null;
        try {
            ddate = inputFormat.parse(inputDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputDateStr = outputFormat.format(ddate);

        contentValues.put(USER_DATE,outputDateStr);

        long rowId = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        Toast.makeText(context,"data inserted "+TABLE_NAME, Toast.LENGTH_LONG).show();
        return rowId;
    }

    public Cursor displayAllData(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(SELECTALL_QUERY, null);
        return cursor;
    }

    public Cursor displayLatestData(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(LATESTDATA_QUERY, null);
        return cursor;
    }

    public boolean updateData(String id, String amount, String description, String date){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues =new ContentValues();
        contentValues.put(ID,id);
        contentValues.put(USER_AMOUNT,amount);
        contentValues.put(USER_DESCRIPTION,description);
//        contentValues.put(USER_DATATYPE,datatype);
        contentValues.put(USER_DATE,date);

        sqLiteDatabase.update(TABLE_NAME,contentValues,ID+ "=?",new String[]{id});
        return true;
    }

    public int deleteData(String id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        return sqLiteDatabase.delete(TABLE_NAME,ID+ "=?",new String[]{id});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("inside on upgrade","ON UPGRADE IS called");
        try {
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }catch (Exception e){
            Toast.makeText(context,"Exception : "+e, Toast.LENGTH_LONG).show();
        }
    }



    public Cursor monthlyIandE() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery( MONTHLY_SUMMARY, null);
        return cursor;
    }

    public Cursor getDataByDate(String date) {
        DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        String inputDateStr=date;
        Date ddate = null;
        try {
            ddate = inputFormat.parse(inputDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputDateStr = outputFormat.format(ddate);

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery( "SELECT * FROM PERSON_DETAILS WHERE date='"+outputDateStr+"' ", null);
        int x  = cursor.getCount();

        return cursor;
    }
    public Cursor setDateForSummary() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery( SELECT_DATE_FOR_SUMMARY, null);
        return cursor;
    }

    public Cursor dailyIandE(String date) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery( "SELECT income,expense,(income-expense) AS balance FROM (SELECT sum(CASE WHEN datatype='Income' AND date='"+date+"' THEN amount ELSE 0 END ) AS income ,sum(CASE WHEN datatype='Expense' AND date='"+date+"' THEN amount ELSE 0 END ) AS expense FROM PERSON_DETAILS)", null);
        return cursor;
    }


}
