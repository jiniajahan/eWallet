package com.example.asus.mywallet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    Context context = this;
    SQLiteDatabase sqLiteDatabase;
    UserDbHelper userDbHelper;
    private Spinner spinner;
    private static Button addNew,showAllItems,historyButton;
    private TextView income,expense,balance;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        userDbHelper = new UserDbHelper(this);
        sqLiteDatabase = userDbHelper.getWritableDatabase();

        income = (TextView)findViewById(R.id.totalIncomeId);
        expense = (TextView)findViewById(R.id.totalExpenseId);
        balance = (TextView)findViewById(R.id.totalBalanceId);
        spinner = (Spinner)findViewById(R.id.datePickerID);

        historyButton = (Button)findViewById(R.id.historyId);
        showAllItems = (Button)findViewById(R.id.showAllItem);
        addNew = (Button) findViewById(R.id.addItem);

        startAddingActivity();
        showListView();
        showUserHistory();
        loadSpinner();
        loadDailyBalance();

    }

/**
 * MENU VIEW
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

/**
 * START ADDING ACTIVITY
 */
    public void startAddingActivity() {
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =new Intent(getApplicationContext(),AddingNewElementActivity.class);
                startActivity(intent);
            }
        });

    }
/**
 * START SHOW ALL DATA LIST ACTIVITY
 */
    public void showListView(){
        showAllItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),DataListActivity.class);
                startActivity(intent);
                Log.e("datalistactivity","started");
            }
        });
    }

/**
 * START SHOW ALL DATA LIST ACTIVITY
 */
    public void showUserHistory(){
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),User_history_Activity.class);
                startActivity(intent);
                Log.e("User_history_Activity","started");
            }
        });
    }

/**
 * LOADS ALL INPUTTED DATE IN A SPINNER WITH LIST VIEW
 */
    public void loadSpinner() {
        ArrayList<String> datelist = new ArrayList<String>();
        Cursor c = userDbHelper.setDateForSummary();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String date = c.getString(c.getColumnIndex(userDbHelper.USER_DATE));
            datelist.add(date);
            c.moveToNext();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                R.layout.simple_list_item,R.id.filterDateId,datelist);
        spinner.setAdapter(adapter);
        userDbHelper.close();
    }
/**
 * COLLECT SELECTED DATE AND SUM INCOME EXPENSE
 */
    public void loadDailyBalance(){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = userDbHelper.dailyIandE(spinner.getSelectedItem().toString());
                if (cursor.getCount() != 0){
                    cursor.moveToNext();

                    income.setText("");
                    income.append(cursor.getString(0));
                    expense.setText("");
                    expense.append(cursor.getString(1));
                    balance.setText("");
                    Float bal = Float.valueOf(cursor.getString(2));
                    if (bal < 0) {
                        balance.append(cursor.getString(2) + " (Credit)");
                    } else {
                        balance.append(cursor.getString(2) + " (Debit)");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }


        });
    }

}
