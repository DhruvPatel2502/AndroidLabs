package com.cst2335.pate;

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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

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
//        EditText recieve = findViewById(R.id.receiveText);
//        EditText send = findViewById(R.id.sendText);
        EditText editText = findViewById(R.id.editText);

        //loadDataFromDatabase(); //get any previously saved Contact objects
        myAdapter = new MyListAdapter();
        myList.setAdapter(myAdapter);


        myList.setOnItemClickListener(( parent,  view,  position,  id) -> {
            showMessage( position );
        });

        //When send button is pushed

        sendButton.setOnClickListener( click -> {
            String inputText = editText.getText().toString();
            Log.i(TAG, "Adding a sending row");
            ContentValues newRowValues = new ContentValues();
            newRowValues.put(DatabaseHelper.COL_NAME, inputText);
          long newId = db.insert(DatabaseHelper.TABLE_NAME, null, newRowValues);
            element.add(new Messages(inputText, true,0));
            myAdapter.notifyDataSetChanged();
            editText.setText("");


            Toast.makeText(this, "Inserted item id:"+0, Toast.LENGTH_LONG).show();

        });

        //When receive button is pushed


        receiveButton.setOnClickListener( click -> {
            String inputText = editText.getText().toString();
            Log.i(TAG, "Adding a recieving row");
            ContentValues newRowValues = new ContentValues();
            newRowValues.put(DatabaseHelper.COL_NAME, inputText);
            long newId = db.insert(DatabaseHelper.TABLE_NAME, null, newRowValues);
            Messages messages= new Messages(inputText, true,newId);
            element.add(messages);
            myAdapter.notifyDataSetChanged();

            editText.setText("");



        });

        myList.setOnItemLongClickListener( (p, b, pos, id) -> {

            String message = String.valueOf(myAdapter.getItem(pos));
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.MakeChoice))

                    //What is the message:
                    .setMessage(getString(R.string.delete)+"\nThe message is at row number:"+(pos+1)+"\n"+"And the message is:"+message)


                    //what the Yes button does:
                    .setPositiveButton(getString(R.string.yes), (click, arg) -> {

                        element.remove(pos);
                        myAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton(getString(R.string.no), (click, arg) -> {

                        myAdapter.notifyDataSetChanged();
                    })
                    //Show the dialog
                    .show();
            return true;
        });




    }

        protected void showMessage(int position) {
            Messages selectedContact = element.get(position);

//            View contact_view = getLayoutInflater().inflate(R.layout.contact_edit, null);
//            //get the TextViews
//            EditText rowName = contact_view.findViewById(R.id.row_name);
//            EditText rowEmail = contact_view.findViewById(R.id.row_email);
//            TextView rowId = contact_view.findViewById(R.id.row_id);
//
//            //set the fields for the alert dialog
//            rowName.setText(selectedContact.getName());
//            rowEmail.setText(selectedContact.getEmail());
//            rowId.setText("id:" + selectedContact.getId());
            LayoutInflater inflater = getLayoutInflater();
            if (!element.get(position).sendOrReceive){
                View newView1 = inflater.inflate(R.layout.send, null);
                //set what the text should be for this row:
                EditText editText1 = newView1.findViewById(R.id.sendText);
                editText1.setText( element.get(position).toString() );

                //return it to be put in the table
            }
            else  {
                View newView2 = inflater.inflate(R.layout.receive,null);
                //set what the text should be for this row:
                EditText editText2 = newView2.findViewById(R.id.receiveText);
                editText2.setText( element.get(position).toString() );


            }
        }


    private void loadDataFromDatabase() {
        DatabaseHelper dataBaseOpener = new DatabaseHelper(this);
        db = dataBaseOpener.getWritableDatabase();
        String [] columns = {DatabaseHelper.COL_ID, DatabaseHelper.COL_NAME};
        Cursor results = db.query(false, DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null, null);
        int messageColIndex = results.getColumnIndex(DatabaseHelper.COL_NAME);
        int idColIndex = results.getColumnIndex(DatabaseHelper.COL_ID);
        while(results.moveToNext())
        {
            String message = results.getString(messageColIndex);

            long id = results.getLong(idColIndex);

            //add the new Contact to the array list:
            element.add(new Messages(message, false,id));
        }

        //At this point, the contactsList array has loaded every row from the cursor.

    }

    public class MyListAdapter extends BaseAdapter {

        public int getCount() { return element.size();}

        public Object getItem(int position) { return element.get(position).messageInput; }

        public long getItemId(int position) { return (long) position; }

        public View getView(int position, View old, ViewGroup parent)
        {
            LayoutInflater inflater = getLayoutInflater();

            //New row:
            if (!element.get(position).sendOrReceive){
                View newView1 = inflater.inflate(R.layout.send, parent, false);
                //set what the text should be for this row:
                EditText editText1 = newView1.findViewById(R.id.sendText);
                editText1.setText( getItem(position).toString() );

                //return it to be put in the table
                return newView1;
            }
            else  {
                View newView2 = inflater.inflate(R.layout.receive, parent, false);
                //set what the text should be for this row:
                EditText editText2 = newView2.findViewById(R.id.receiveText);
                editText2.setText(getItem(position).toString());

                //return it to be put in the table
                return newView2;
            }
        }



    }

}





