package com.example.shabnam.todolist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> todoItems;
    ArrayAdapter<String> aToDoAdapter;
    ListView lvItems;
    EditText etEditText;
    private final int REQUEST_CODE = 20;
    DatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myDB = new DatabaseHelper(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        populateArrayItems();
        lvItems=(ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(aToDoAdapter);
        etEditText = (EditText) findViewById(R.id.etEditText);
        //Long click listener for edit item
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                               @Override
                                               public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                                   todoItems.remove(position);
                                                   // Refresh the list
                                                   aToDoAdapter.notifyDataSetChanged();
                                                   writeItems();
                                                   return true;
                                               }
                                           }
        );

        //Short click listener for edit item
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                           @Override
                                           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                               //Here it calls launchEditItem function to launch the edititem activity. It passes the
                                               //position of the item being edited in the child activity
                                               launchEditItem(position);
                                           }
                                       }
        );

    }

    //In this function after setting the item content and position parameters,
    //edititem activity will be called.
    public void launchEditItem(int position) {
        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
        //Here the position and content of the item is being passed using extra
        i.putExtra("position", position);
        String itemContent=todoItems.get(position);
        i.putExtra("itemContent", itemContent);
        //Edititem activity being called here
        startActivityForResult(i, REQUEST_CODE);
    }

    //Time to handle the result of the edititem activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            String editedItem = data.getExtras().getString("editedItem");
            int itemPosition = data.getExtras().getInt("itemPosition", 0);
            todoItems.set(itemPosition,editedItem);
            aToDoAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Item Edited", Toast.LENGTH_SHORT).show();
            writeItems();
        }
    }

    //Read the items from file and populates them in the list
    public void populateArrayItems(){
        readItems();
        aToDoAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,todoItems) ;
    }

    //This function is used to read the items from the text file
    private void readItems(){

        Cursor allItems = myDB.getAllItems();
        todoItems = new ArrayList<String>(); 
        while (allItems.moveToNext()){
            todoItems.add(allItems.getString(1));
        }

    }

    //This function is used to write the items into the text file
    private void writeItems(){
        //Deletes and adds all the items again
        myDB.deleteAllItems();
        for (int i = 0; i < todoItems.size(); i++) {
            myDB.addItem(todoItems.get(i));
        }
    }

    //Defualt menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Default menu function
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //This function is being called when use clicks Add button to add the new item
    public void onAddItem(View view) {
        String currentString=etEditText.getText().toString();
        todoItems.add(currentString);
        etEditText.setText("");
        writeItems();

    }
}
