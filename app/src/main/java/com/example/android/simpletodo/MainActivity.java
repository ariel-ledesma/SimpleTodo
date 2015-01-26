package com.example.android.simpletodo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class MainActivity extends ActionBarActivity {

    int request_code = 1;
    int  currentPos;
    public final static String EXTRA_MESSAGE = "com.example.android.simpletodo.MESSAGE";
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;
    //Position of highlighted elements
    Set<String> hlSet = new HashSet<String>();
    Set<String> extraSet = new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<String>();
        readItems();
        readHighlights();
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items) {
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View v =super.getView(position, convertView, parent);
                if(hlSet.contains(String.valueOf(position))) {
                    v.setBackgroundColor(Color.YELLOW);
                } else {
                    v.setBackgroundColor(Color.TRANSPARENT);
                }

                return v;
            }
        };
        lvItems.setAdapter(itemsAdapter);
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(getApplicationContext(), EditItem.class);
                intent.putExtra(EXTRA_MESSAGE, items.get(position));
                currentPos = position;
                startActivityForResult(intent,request_code);
            }
        });

        registerForContextMenu(lvItems);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == request_code){
            if(resultCode == RESULT_OK){
                items.set(currentPos, data.getData().toString());
                itemsAdapter.notifyDataSetChanged();
                writeItems();
            }
        }
    }


//    Creates drop down menus for items in the list
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextual_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.option1:
                items.remove(info.position);
                if(hlSet.contains(String.valueOf(info.position))) {
                    hlSet.remove(String.valueOf(info.position));
                }
                for(String s : hlSet) {
                    if(Integer.parseInt(s) > info.position) {
                        int temp = Integer.parseInt(s)-1;
                        extraSet.add(String.valueOf(temp));
                    }else {
                        extraSet.add(s);
                    }
                }
                hlSet.clear();
                hlSet.addAll(extraSet);
                extraSet.clear();
                itemsAdapter.notifyDataSetChanged();
                writeItems();
                writeHighlights();
                return true;
            case R.id.option2:
                lvItems.getChildAt(info.position).setBackgroundColor(Color.YELLOW);
                hlSet.add(String.valueOf(info.position));
                writeHighlights();
                return true;
            case R.id.option3:
                lvItems.getChildAt(info.position).setBackgroundColor(Color.TRANSPARENT);
                hlSet.remove(String.valueOf(info.position));
                writeHighlights();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
//        Textfield cannot be left blank when adding items
        if(itemText != "") {
            itemsAdapter.add(itemText);
            etNewItem.setText("");
            writeItems();
        }
    }

    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            items = new ArrayList<String>();
        }
    }

    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(todoFile, items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeHighlights() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "highlights.txt");
        try {
            FileUtils.writeLines(todoFile, hlSet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readHighlights() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "highlights.txt");
        try {
            hlSet = new HashSet<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            hlSet = new HashSet<String>();
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
}
