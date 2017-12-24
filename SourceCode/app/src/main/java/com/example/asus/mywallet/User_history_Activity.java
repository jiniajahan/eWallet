package com.example.asus.mywallet;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class User_history_Activity extends AppCompatActivity {

    private SearchView searchView;
    SQLiteDatabase sqLiteDatabase;
    private UserDbHelper userDbHelper;

    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        userDbHelper = new UserDbHelper(this);
        sqLiteDatabase = userDbHelper.getWritableDatabase();

        listView = (ListView)findViewById(R.id.listViewId);
        searchView =(SearchView)findViewById(R.id.searchViewId);




/**
 * ALL DATA LIST VIEW SHOW
 */
        ArrayList<String> listData = new ArrayList<>();

        Cursor cursor = userDbHelper.monthlyIandE();

        if (cursor.getCount()==0) {
            Toast.makeText(getApplicationContext(),"No data found",Toast.LENGTH_SHORT).show();
        }else
        {
            while (cursor.moveToNext()) {
                listData.add(cursor.getString(0)+" \t "+cursor.getString(1)+" \t "+cursor.getString(2));
            }
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.new_view_layout, R.id.userValue, listData);

        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectValue = (String)adapter.getItem(position);
                Toast.makeText(getApplicationContext(),"Selected value: "+selectValue,Toast.LENGTH_LONG).show();
            }
        });


/**
 * LIST VIEW FILTER BY ANY TEXT
 */
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

}
