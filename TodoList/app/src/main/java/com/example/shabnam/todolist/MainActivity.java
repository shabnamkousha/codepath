package com.example.shabnam.todolist;

import android.content.Intent;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        populateArrayItems();
        lvItems=(ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(aToDoAdapter);
        etEditText = (EditText) findViewById(R.id.etEditText);
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                               @Override
                                               public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                                   todoItems.remove(position);
                                                   aToDoAdapter.notifyDataSetChanged();
                                                   writeItems();
                                                   return true;
                                               }
                                           }
        );


       lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                          @Override
                                          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                              //todoItems.remove(position);
                                              //aToDoAdapter.notifyDataSetChanged();
                                              //writeItems();
                                              //return true;
                                              launchEditItem(position);
                                          }
                                      }
       );

    }

    public void launchEditItem(int position) {
        // first parameter is the context, second is the class of the activity to launch
        Intent i = new Intent(MainActivity.this, EditItemActivity.class);

        i.putExtra("position", position);
        String itemContent=todoItems.get(position);
        i.putExtra("itemContent", itemContent);
        //startActivity(i);
        startActivityForResult(i, REQUEST_CODE);
    }

    // ActivityOne.java, time to handle the result of the sub-activity
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

    public void populateArrayItems(){
        readItems();
        aToDoAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,todoItems) ;
    }
    private void readItems(){
        File filesDir = getFilesDir();
        File file = new File(filesDir,"todo.txt");
        try{
            todoItems = new ArrayList<String>(FileUtils.readLines(file));
        } catch (IOException e){

        }
    }

    private void writeItems(){
        File filesDir = getFilesDir();
        File file = new File(filesDir,"todo.txt");
        try{
            FileUtils.writeLines(file,todoItems);
        } catch (IOException e){

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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

    public void onAddItem(View view) {
        todoItems.add(etEditText.getText().toString());
        etEditText.setText("");
        writeItems();
    }
}
