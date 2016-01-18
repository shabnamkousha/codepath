package com.example.shabnam.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

public class AddItemActivity extends AppCompatActivity {

    //NumberPicker monthPicker;
    NumberPicker monthPicker = null;
    NumberPicker dayPicker = null;
    NumberPicker yearPicker = null;

    EditText itemName;
    Spinner itemPriority;

    ArrayAdapter<CharSequence> aPriorityAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //monthPicker.setDisplayedValues(new String[]{"Jan", "Feb", "Mar"} );

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        monthPicker = (NumberPicker) findViewById(R.id.numberPicker1);
        dayPicker = (NumberPicker) findViewById(R.id.numberPicker2);
        yearPicker = (NumberPicker) findViewById(R.id.numberPicker3);

        itemName = (EditText) findViewById(R.id.etTextEdit1);

        itemPriority = (Spinner) findViewById(R.id.spinnerPriority);

        aPriorityAdapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, android.R.layout.simple_spinner_item);
        aPriorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemPriority.setAdapter(aPriorityAdapter);

        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String[] year = {"2016", "2017", "2018", "2019", "2020"};

        monthPicker.setMaxValue(12);
        dayPicker.setMaxValue(31);
        yearPicker.setMaxValue(4);

        monthPicker.setMinValue(1);
        dayPicker.setMinValue(1);
        yearPicker.setMinValue(1);

        monthPicker.setDisplayedValues(months);
        yearPicker.setDisplayedValues(year);

    }

    //This function is being called when use clicks Add button to add the new item
    public void onAddNewItem(View view) {
        String currentString=itemName.getText().toString();
        String chosenMonth =  String.valueOf(monthPicker.getValue());
        String chosenYear =  String.valueOf(yearPicker.getValue());
        String chosenDay =  String.valueOf(dayPicker.getValue());
        String chosenPriority = itemPriority.getSelectedItem().toString();

        // Prepare data intent

        Intent data = new Intent();

        data.putExtra("addedItem", currentString);
        data.putExtra("itemYear", chosenYear);
        data.putExtra("itemMonth", chosenMonth);
        data.putExtra("itemDay", chosenDay);
        data.putExtra("itemPriority", chosenPriority);

        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent, main activity


    }


}
