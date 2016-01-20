package com.example.shabnam.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;

public class EditItemActivity extends AppCompatActivity {
    EditText etEditText1;
    int itemPosition;
    //NumberPicker monthPicker;
    NumberPicker monthPicker = null;
    NumberPicker dayPicker = null;
    NumberPicker yearPicker = null;
    Spinner itemPriority;
    ArrayAdapter<CharSequence> PriorityAdapter;



    //Here the item position and item content being read and displayed in edit text input
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        monthPicker = (NumberPicker) findViewById(R.id.numberPicker1);
        dayPicker = (NumberPicker) findViewById(R.id.numberPicker2);
        yearPicker = (NumberPicker) findViewById(R.id.numberPicker3);

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

        itemPriority = (Spinner) findViewById(R.id.spinnerPriority);
        PriorityAdapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, android.R.layout.simple_spinner_item);
        PriorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemPriority.setAdapter(PriorityAdapter);



        String addedItem = getIntent().getStringExtra("itemContent");
        String itemPriorityItem = getIntent().getStringExtra("itemPriority");
        String itemMonth = getIntent().getStringExtra("itemMonth");
        String itemDay = getIntent().getStringExtra("itemDay");
        String itemYear = getIntent().getStringExtra("itemYear");
        itemPosition = getIntent().getIntExtra("position", 0);

        monthPicker.setValue(Integer.parseInt(itemMonth));
        dayPicker.setValue(Integer.parseInt(itemDay));
        yearPicker.setValue(1);

        int spinnerPosition = PriorityAdapter.getPosition(itemPriorityItem);

        //set the default according to value
        itemPriority.setSelection(spinnerPosition);

        etEditText1 = (EditText) findViewById(R.id.etTextEdit1);
        etEditText1.setText(addedItem);
        etEditText1.setSelection(etEditText1.getText().length());

    }

    //This function is being called when user clicks on Edit button
    public void onSubmitEdit(View view) {
        String currentString=etEditText1.getText().toString();
        String chosenMonth =  String.valueOf(monthPicker.getValue());
        String chosenYear =  String.valueOf(yearPicker.getValue());
        String chosenDay =  String.valueOf(dayPicker.getValue());
        String chosenPriority = itemPriority.getSelectedItem().toString();

        // Prepare data intent

        Intent data = new Intent();

        data.putExtra("editedItem", currentString);
        data.putExtra("itemYear", chosenYear);
        data.putExtra("itemMonth", chosenMonth);
        data.putExtra("itemDay", chosenDay);
        data.putExtra("itemPriority", chosenPriority);
        data.putExtra("itemPosition", itemPosition);

        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent, main activity

    }


}
