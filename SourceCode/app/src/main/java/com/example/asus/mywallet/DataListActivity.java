package com.example.asus.mywallet;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;

public class DataListActivity extends AppCompatActivity {

    private ListView listView;
    private SearchView searchView;
    SQLiteDatabase sqLiteDatabase;
    private UserDbHelper userDbHelper;
    Cursor cursor;
    Toolbar toolbar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_list_activity);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        listView = (ListView)findViewById(R.id.list_view_id);
        searchView =(SearchView)findViewById(R.id.searchViewId);
        userDbHelper = new UserDbHelper(this);

        ArrayList<String> listData = new ArrayList<>();

        Cursor cursor = userDbHelper.displayAllData();

        if (cursor.getCount()==0)
        {
            Toast.makeText(getApplicationContext(),"No data found",Toast.LENGTH_SHORT).show();
        }else
        {
            while (cursor.moveToNext())
            {
                listData.add(cursor.getString(0)+" \t "+cursor.getString(1)+" \t "+cursor.getString(2)+" \t "+cursor.getString(3)+" \t "+cursor.getString(4));
            }
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.row_layout, R.id.userAmount, listData);

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
