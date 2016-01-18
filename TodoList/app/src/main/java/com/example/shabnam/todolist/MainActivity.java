package com.example.shabnam.todolist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

    ArrayList<String> todoPriority;
    ArrayAdapter<String> aToDoAdapterP;
    ListView lvItemsP;

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

        lvItemsP=(ListView) findViewById(R.id.lvItems1);
        lvItemsP.setAdapter(aToDoAdapterP);

        //Long click listener for edit item
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                               @Override
                                               public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                                                   deleteItems(position);

                                                   todoItems.remove(position);
                                                   todoPriority.remove(position);

                                                   // Refresh the list
                                                   aToDoAdapter.notifyDataSetChanged();
                                                   aToDoAdapterP.notifyDataSetChanged();
                                                   int deleteItemPosition=position+1;

                                                   myDB.deleteItem(deleteItemPosition);

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

        Cursor editItem = myDB.getItem(position + 1);
        editItem.moveToNext();

        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
        //Here the position and content of the item is being passed using extra

        String itemContent=editItem.getString(1);
        String itemContentPriority=editItem.getString(2);
        String itemDay=editItem.getString(3);
        String itemMonth=editItem.getString(4);
        String itemYear=editItem.getString(5);


        i.putExtra("position", position);

        i.putExtra("itemContent", itemContent);
        i.putExtra("itemPriority", itemContentPriority);
        i.putExtra("itemMonth", itemMonth);
        i.putExtra("itemDay", itemDay);
        i.putExtra("itemYear", itemYear);

        //Edititem activity being called here
        startActivityForResult(i, 1);

    }

    //Time to handle the result of the edititem activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == 1) {
            // Extract name value from result extras
            String editedItem = data.getExtras().getString("editedItem");
            String itemPriority = data.getExtras().getString("itemPriority");
            String itemMonth = data.getExtras().getString("itemMonth");
            String itemDay = data.getExtras().getString("itemDay");
            String itemYear = data.getExtras().getString("itemYear");
            int itemPosition = data.getExtras().getInt("itemPosition", 0);

            todoItems.set(itemPosition, editedItem);
            todoPriority.set(itemPosition, itemPriority);

            aToDoAdapter.notifyDataSetChanged();
            aToDoAdapterP.notifyDataSetChanged();

            Toast.makeText(this, "Item Edited", Toast.LENGTH_SHORT).show();
            updateItems(itemPosition , itemDay,itemMonth, itemYear);
        } else  if (resultCode == RESULT_OK && requestCode == 2) {

            String addedItem = data.getExtras().getString("addedItem");
            String itemPriority = data.getExtras().getString("itemPriority");
            String itemMonth = data.getExtras().getString("itemMonth");
            String itemDay = data.getExtras().getString("itemDay");
            String itemYear = data.getExtras().getString("itemYear");

            todoItems.add(addedItem);
            todoPriority.add(itemPriority);

            aToDoAdapter.notifyDataSetChanged();
            aToDoAdapterP.notifyDataSetChanged();

            writeItem(itemDay, itemMonth, itemYear);


        }
    }

    //Read the items from file and populates them in the list
    public void populateArrayItems(){
        readItems();
        aToDoAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,todoItems) ;
        aToDoAdapterP = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,todoPriority) ;
    }

    //This function is used to read the items from the text file
    private void readItems(){

        Cursor allItems = myDB.getAllItems();

        todoItems = new ArrayList<String>();
        todoPriority = new ArrayList<String>();
        while (allItems.moveToNext()){
            todoItems.add(allItems.getString(1));
            todoPriority.add(allItems.getString(2));

        }

    }

    //This function is used to write the items into the text file
    private void updateItems(int idPosition, String chosenDay, String chosenMonth, String chosenYear){

        //Deletes and adds all the items again
        myDB.updateItemFields(idPosition+1, todoItems.get(idPosition), todoPriority.get(idPosition), chosenDay, chosenMonth, chosenYear);

    }

    //This function is used to write the items into the text file
    private void deleteItems(int position){
        //Actual id in DB
        position=position+1;

        myDB.deleteItem(position);

        for (int i = position+1; i <= todoItems.size(); i++) {
                myDB.updateItem(i-1);
        }
    }

    //This function is used to write single item into the text file
    private void writeItem(String chosenDay, String chosenMonth, String chosenYear){

        int idPosition=todoItems.size();
        int itemPosition=todoItems.size()-1;
        //Toast.makeText(this, chosenMonth , Toast.LENGTH_SHORT).show();
        //Toast.makeText(this,  todoPriority.get(itemPosition) , Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, todoItems.get(itemPosition) , Toast.LENGTH_SHORT).show();

        myDB.addItem(idPosition, todoItems.get(itemPosition), todoPriority.get(itemPosition), chosenDay, chosenMonth,chosenYear);

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

        if (id == R.id.add_item) {
            Intent i = new Intent(MainActivity.this, AddItemActivity.class);

            startActivityForResult(i, 2);
        }


        return super.onOptionsItemSelected(item);
    }

    //This function is being called when use clicks Add button to add the new item
    public void onAddItem(View view) {
        String currentString=etEditText.getText().toString();
        todoItems.add(currentString);
        todoPriority.add("hi");
        etEditText.setText("");
        //writeItems();
    }
}
