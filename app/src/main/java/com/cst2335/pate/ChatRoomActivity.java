package com.cst2335.pate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class ChatRoomActivity extends AppCompatActivity {

    SQLiteDatabase db;


    private static final String TAG = "ChatroomActivity";
    ArrayList<Messages> element = new ArrayList<>();
    MyListAdapter myAdapter;
    ListView myList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        //Listview to adapter
        Button receiveButton = findViewById(R.id.receiveButton);
        Button sendButton = findViewById(R.id.sendButton);
        myList = findViewById(R.id.listView);
        EditText editText = findViewById(R.id.editText);

        loadDataFromDatabase(); //get any previously saved Contact objects

        myAdapter = new MyListAdapter();
        myList.setAdapter(myAdapter);


        sendButton.setOnClickListener(click -> {
            String inputText = editText.getText().toString();
            Log.i(TAG, "Adding a sending row");
            ContentValues newRowValues = new ContentValues();

            newRowValues.put(DatabaseHelper.COL2, inputText);
            newRowValues.put(DatabaseHelper.COL3, 1);
            long newId = db.insert(DatabaseHelper.TABLE_NAME, null, newRowValues);
            element.add(new Messages(inputText, true, newId));
            myAdapter.notifyDataSetChanged();
            editText.setText("");


            Toast.makeText(this, "Inserted item id:" + newId, Toast.LENGTH_LONG).show();

        });

        //When receive button is pushed


        receiveButton.setOnClickListener(click -> {
            String inputText = editText.getText().toString();
            Log.i(TAG, "Adding a recieving row");
            ContentValues newRowValues = new ContentValues();
            newRowValues.put(DatabaseHelper.COL2, inputText);
            newRowValues.put(DatabaseHelper.COL3, 0);
            long newId = db.insert(DatabaseHelper.TABLE_NAME, null, newRowValues);
            Messages messages = new Messages(inputText, false, newId);
            element.add(messages);

            myAdapter.notifyDataSetChanged();

            editText.setText("");

            Toast.makeText(this, "Inserted item id:" + newId, Toast.LENGTH_LONG).show();


        });

        myList.setOnItemLongClickListener((p, b, pos, id) -> {

            Messages selectedMessage = element.get(pos);

            String message = String.valueOf(myAdapter.getItem(pos).messageInput);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.MakeChoice))

                    //What is the message:
                    .setMessage(getString(R.string.delete) + "\nThe message is at row number: " + (pos + 1) + "\n" + "And the message is: " + message)


                    //what the Yes button does:
                    .setPositiveButton(getString(R.string.yes), (click, arg) -> {

                        element.remove(pos);
                        deleteMessages(selectedMessage);
                        myAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton(getString(R.string.no), (click, arg) -> {

                        myAdapter.notifyDataSetChanged();
                    })
                    //Show the dialog
                    .show();
            return true;
        });

printCursor(results, db.getVersion());
    }

    public void printCursor(Cursor c, int version) {
        int dbVersion = version;
        int numberCursorColumns = c.getColumnCount();
        String[] nameCursorColumns = c.getColumnNames();
        int numberCursorResults = c.getCount();



        Log.i("DATABASE VERSION", Integer.toString(dbVersion));
        Log.i("NUMBER OF COLUMNS", Integer.toString(numberCursorColumns));
        Log.i("COLUMN NAMES", Arrays.toString(nameCursorColumns));
        Log.i("NUMBER OF RESULTS", Integer.toString(numberCursorResults));

       }


    Cursor results;
    private void loadDataFromDatabase() {
        DatabaseHelper dataBaseOpener = new DatabaseHelper(this);
        db = dataBaseOpener.getWritableDatabase();

        String[] columns = {DatabaseHelper.COL1, DatabaseHelper.COL2, DatabaseHelper.COL3};
        results = db.query(false, DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null, null);

        int messageColIndex = results.getColumnIndex(DatabaseHelper.COL2);
        int idColIndex = results.getColumnIndex(DatabaseHelper.COL1);
        int sOrR = results.getColumnIndex(DatabaseHelper.COL3);


        while (results.moveToNext()) {
            String message = results.getString(messageColIndex);
            long id = results.getLong(idColIndex);
            boolean value = (results.getString(sOrR).equals("1"));

            element.add(new Messages(message, value, id));

        }

    }        //At this point, the contactsList array has loaded every row from the cursor.


    public class MyListAdapter extends BaseAdapter {

        public int getCount() {
            return element.size();
        }

        public Messages getItem(int position) {
            return element.get(position);
        }

        public long getItemId(int position) {
            return getItem(position).getId();
        }

        public View getView(int position, View old, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();

            //New row:
            if (!element.get(position).sendOrReceive) {
                View newView1 = inflater.inflate(R.layout.send, parent, false);
                //set what the text should be for this row:
                EditText editText1 = newView1.findViewById(R.id.sendText);
                editText1.setText(getItem(position).getMessageInput());

                //return it to be put in the table
                return newView1;
            } else {
                View newView2 = inflater.inflate(R.layout.receive, parent, false);
                //set what the text should be for this row:
                EditText editText2 = newView2.findViewById(R.id.receiveText);
                editText2.setText(getItem(position).getMessageInput());

                //return it to be put in the table
                return newView2;
            }
        }


    }

    protected void deleteMessages(Messages c) {
        db.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COL1 + "= ?", new String[]{Long.toString(c.getId())});
    }

}





