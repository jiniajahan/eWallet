package com.example.asus.mywallet;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.Calendar;

public class AddingNewElementActivity extends AppCompatActivity implements View.OnClickListener{

    Toolbar toolbar;
    Context context = this;
    UserDbHelper userDbHelper;
    SQLiteDatabase sqLiteDatabase;

    EditText addAmount,addDescription,adddate,userId;
    private static Button updateData,selectDate,showLastData;
    private static RadioGroup contentType;
    private static RadioButton radiobutton1,radiobutton2,type;

    private int year_x;
    private int month_x;
    private int day_x;
    private Button deleteSavedDat,saveInputData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_new_element);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        userDbHelper = new UserDbHelper(this);
        sqLiteDatabase = userDbHelper.getWritableDatabase();

        userId = (EditText)findViewById(R.id.userInputId);
        addAmount = (EditText)findViewById(R.id.amount);
        addDescription = (EditText)findViewById(R.id.description);
        adddate = (EditText)findViewById(R.id.showDateButton);
        selectDate = (Button)findViewById(R.id.buttonDateDialog);
        contentType = (RadioGroup)findViewById(R.id.radioGroupType);
        radiobutton1 = (RadioButton)findViewById(R.id.incomeButton);
        radiobutton2 = (RadioButton) findViewById(R.id.expenseButton);
        updateData = (Button)findViewById(R.id.updateDataId);
        showLastData = (Button)findViewById(R.id.showlastDataId);
        saveInputData = (Button)findViewById(R.id.saveItemData);
        deleteSavedDat = (Button)findViewById(R.id.deleteDataId);

        userId.setEnabled(false);

        updateInputtedData();
        checkIncome();
        checkExpense();
        addInputData();
        ShowLastSavedData();
        deleteInputtedData();
        removeHintFromId();
        removeHintFromDes();
        removeHintFromAmount();
        selectDate.setOnClickListener(AddingNewElementActivity.this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }


/**
 * CHECKBOX INCOME CLICKED
 */
    public void checkIncome(){

        radiobutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((RadioButton) view).isChecked()){
                    Toast.makeText(AddingNewElementActivity.this,"INCOME is checked now!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

/**
 * CHECKBOX EXPENSE CLICKED
 */
    public void checkExpense(){

        radiobutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((RadioButton) view).isChecked()){
                    Toast.makeText(AddingNewElementActivity.this,"Expense is checked now!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

/**
 * DATE SELECTION
 */

    public void onClick(View v) {
        if (v == selectDate) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            year_x = c.get(Calendar.YEAR);
            month_x = c.get(Calendar.MONTH);
            day_x = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            adddate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, year_x, month_x, day_x);
            datePickerDialog.show();
        }
    }


/**
 * ADDING NEW ITEM
 */

    public void addInputData() {
        saveInputData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selected_id = contentType.getCheckedRadioButtonId();
                type = (RadioButton)findViewById(selected_id);
                Toast.makeText(AddingNewElementActivity.this,type.getText().toString(),Toast.LENGTH_SHORT).show();

                String id = userId.getText().toString();
                String amount = addAmount.getText().toString();
                String description = addDescription.getText().toString();
                String datatype = type.getText().toString();
                String date = adddate.getText().toString();


                if (v.getId()==R.id.saveItemData){
                    long rowId =-1;
                    Cursor chk = userDbHelper.getDataByDate(date);
                    int x = chk.getCount();
                    if(datatype.equals("Income") && x==0 ) {
                        userDbHelper.insertData("0","init","Expense",date);
                        rowId = userDbHelper.insertData(amount, description, datatype, date);
                    }else if (datatype.equals("Expense") && x==0){
                        userDbHelper.insertData("0","init","Income",date);
                        rowId = userDbHelper.insertData(amount, description, datatype, date);
                    }else{
                        rowId = userDbHelper.insertData(amount, description, datatype, date);
                    }

                    if (rowId==-1)
                    {

                        Toast.makeText(getApplicationContext()," Unsuccessful ", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"row "+rowId+" is successfully inserted : ", Toast.LENGTH_LONG).show();

                    }
                }
            }
        });
    }

/**
 * CHECKING LATEST INPUTTED DATA IN A DIALOG VIEW
 */
    public void ShowLastSavedData() {
        showLastData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor cursor = userDbHelper.displayLatestData();

                if (cursor.getCount()==-2){
                    showData("Error","no data found");
                    return;
                }
                StringBuffer stringBuffer = new StringBuffer();
                while (cursor.moveToNext()){
                    stringBuffer.append("ID : "+cursor.getString(0)+"\n");
                    stringBuffer.append("AMOUNT : "+cursor.getString(1)+"\n");
                    stringBuffer.append("DESCRIPTION : "+cursor.getString(2)+"\n");
                    stringBuffer.append("TYPE : "+cursor.getString(3)+"\n");
                    stringBuffer.append("DATE : "+cursor.getString(4)+"\n\n");
                }
                showData("ResultSet",stringBuffer.toString());
            }
        });
    }


/**
 * DATA UPDATE WITH ITEM ID
 */
    public void updateInputtedData() {
        updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userId.setEnabled(true);
                int selected_id = contentType.getCheckedRadioButtonId();
                type = (RadioButton)findViewById(selected_id);

                String id = userId.getText().toString();
                String amount = addAmount.getText().toString();
                String description = addDescription.getText().toString();
                String date = adddate.getText().toString();

              Boolean isUpdated = userDbHelper.updateData(id,amount,description,date);

              if (isUpdated==true) {
                  Toast.makeText(getApplicationContext(),"Data is updated",Toast.LENGTH_SHORT).show();
              }else
                  {
                      Toast.makeText(getApplicationContext(),"Data is updated",Toast.LENGTH_SHORT).show();
                  }
            }
        });
    }


/**
 * DELETE INPUTTED DATA WITH ITEM ID
 */
    public void deleteInputtedData() {
        deleteSavedDat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId.setEnabled(true);
                String id = userId.getText().toString();

              int value = userDbHelper.deleteData(id);

              if (value>0) {
                  Toast.makeText(getApplicationContext(),"Data is deleted",Toast.LENGTH_SHORT).show();
              }else
                  {
                      Toast.makeText(getApplicationContext(),"Data is not deleted",Toast.LENGTH_SHORT).show();
                  }
            }
        });
    }

    public void showData(String title,String message)
    {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.show();
    }


/**
 * EDIT TEXT AMOUNT HINT REMOVE IN CLICK
 */
    public void removeHintFromAmount(){
        addAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
             if (hasFocus){
                 addAmount.setHint("");
             }else {
                 addAmount.setHint("Amount");
             }
            }
        });
    }

/**
 * EDIT TEXT DESCRIPTION HINT REMOVE IN CLICK
 */
    public void removeHintFromDes() {
        addDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
             if (hasFocus){
                 addDescription.setHint("");
             }else {
                 addDescription.setHint("Description");
             }
            }
        });
    }

/**
 * EDIT TEXT ID HINT REMOVE IN CLICK
 */
    public void removeHintFromId(){
        userId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
             if (hasFocus){
                 userId.setHint("");
             }else {
                 userId.setHint("ID");
             }
            }
        });
    }


}


