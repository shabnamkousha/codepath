package com.example.shabnam.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditItemActivity extends AppCompatActivity {
    EditText etEditText1;
    int itemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String itemContent = getIntent().getStringExtra("itemContent");
        itemPosition = getIntent().getIntExtra("position", 0);

        etEditText1 = (EditText) findViewById(R.id.etEditText1);
        etEditText1.setText(itemContent);
        //cursor in the text field is at the end of the current text value and is in focus
        //by default
        etEditText1.setSelection(etEditText1.getText().length());

    }

    public void onSubmitEdit(View view) {
        //EditText editedItem = (EditText) findViewById(R.id.etEditText1);
        // Prepare data intent
        Intent data = new Intent();

        data.putExtra("editedItem", etEditText1.getText().toString());

        data.putExtra("itemPosition", itemPosition); // ints work too
        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }
}
