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
    ArrayAdapter<String> TodoListAdapter;
    ListView listViewItems;

    ArrayList<String> todoPriority;
    ArrayAdapter<String> TodoAdapterPriority;
    ListView listItemsPriority;

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
        listViewItems=(ListView) findViewById(R.id.lvItems);
        listViewItems.setAdapter(TodoListAdapter);

        listItemsPriority=(ListView) findViewById(R.id.lvItems1);
        listItemsPriority.setAdapter(TodoAdapterPriority);

        //Long click listener for edit item
        listViewItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                                     @Override
                                                     public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                                                         deleteItems(position);

                                                         todoItems.remove(position);
                                                         todoPriority.remove(position);

                                                         // Refresh the list
                                                         TodoListAdapter.notifyDataSetChanged();
                                                         TodoAdapterPriority.notifyDataSetChanged();
                                                         int deleteItemPosition = position + 1;

                                                         myDB.deleteItem(deleteItemPosition);

                                                         return true;
                                                     }
                                                 }
        );

        //Short click listener for edit item
        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
    public void launchEditItem(int aPositionIndex) {

        Cursor editItem = myDB.getItem(aPositionIndex + 1);
        editItem.moveToNext();

        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
        //Here the position and content of the item is being passed using extra

        String itemContent=editItem.getString(1);
        String itemContentPriority=editItem.getString(2);
        String itemDay=editItem.getString(3);
        String itemMonth=editItem.getString(4);
        String itemYear=editItem.getString(5);


        i.putExtra("position", aPositionIndex);

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
    protected void onActivityResult(int aRequestCode, int aResultCode, Intent aDataSet) {
        // REQUEST_CODE is defined above
        if (aResultCode == RESULT_OK && aRequestCode == 1) {
            // Extract name value from result extras
            String editedItem = aDataSet.getExtras().getString("editedItem");
            String itemPriority = aDataSet.getExtras().getString("itemPriority");
            String itemMonth = aDataSet.getExtras().getString("itemMonth");
            String itemDay = aDataSet.getExtras().getString("itemDay");
            String itemYear = aDataSet.getExtras().getString("itemYear");
            int itemPosition = aDataSet.getExtras().getInt("itemPosition", 0);

            todoItems.set(itemPosition, editedItem);
            todoPriority.set(itemPosition, itemPriority);

            TodoListAdapter.notifyDataSetChanged();
            TodoAdapterPriority.notifyDataSetChanged();

            Toast.makeText(this, "Item Edited", Toast.LENGTH_SHORT).show();
            updateItems(itemPosition , itemDay,itemMonth, itemYear);
        } else  if (aResultCode == RESULT_OK && aRequestCode == 2) {

            String addedItem = aDataSet.getExtras().getString("addedItem");
            String itemPriority = aDataSet.getExtras().getString("itemPriority");
            String itemMonth = aDataSet.getExtras().getString("itemMonth");
            String itemDay = aDataSet.getExtras().getString("itemDay");
            String itemYear = aDataSet.getExtras().getString("itemYear");

            todoItems.add(addedItem);
            todoPriority.add(itemPriority);

            TodoListAdapter.notifyDataSetChanged();
            TodoAdapterPriority.notifyDataSetChanged();

            writeItem(itemDay, itemMonth, itemYear);


        }
    }

    //Read the items from file and populates them in the list
    public void populateArrayItems(){
        readItems();
        TodoListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,todoItems) ;
        TodoAdapterPriority = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,todoPriority) ;
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
    private void updateItems(int aPositionIndex, String aChosenDay, String aChosenMonth, String aChosenYear){

        //Deletes and adds all the items again
        myDB.updateItemFields(aPositionIndex + 1, todoItems.get(aPositionIndex), todoPriority.get(aPositionIndex), aChosenDay, aChosenMonth, aChosenYear);

    }

    //This function is used to write the items into the text file
    private void deleteItems(int aPositionIndex){
        //Actual id in DB
        aPositionIndex=aPositionIndex+1;

        myDB.deleteItem(aPositionIndex);

        for (int i = aPositionIndex+1; i <= todoItems.size(); i++) {
                myDB.updateItem(i-1);
        }
    }

    //This function is used to write single item into the text file
    private void writeItem(String aChosenDay, String aChosenMonth, String aChosenYear){

        int idPosition=todoItems.size();
        int itemPosition=todoItems.size()-1;

        myDB.addItem(idPosition, todoItems.get(itemPosition), todoPriority.get(itemPosition), aChosenDay, aChosenMonth, aChosenYear);

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
    public boolean onOptionsItemSelected(MenuItem anItem) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = anItem.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.add_item) {
            Intent i = new Intent(MainActivity.this, AddItemActivity.class);

            startActivityForResult(i, 2);
        }


        return super.onOptionsItemSelected(anItem);
    }

}
