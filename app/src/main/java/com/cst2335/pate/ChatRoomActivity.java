package com.cst2335.pate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class ChatRoomActivity extends AppCompatActivity {

    private static final String TAG = "ChatroomActivity";

    //Message class

    public static class Messages {
        String messageInput;
        public  Boolean sendOrReceive;

        private  Messages (String messageIn, Boolean SorR){
            this.messageInput = messageIn;
            this.sendOrReceive = SorR;
        }




    }
    // Implementing ArrayList class using Messages

    ArrayList<Messages> element = new ArrayList<>();

    MyListAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        //Listview to adapter

        ListView myList = findViewById(R.id.listView);
        myList.setAdapter(myAdapter = new MyListAdapter());

        EditText editText = findViewById(R.id.editText);

        //When send button is pushed
        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener( click -> {
            String inputText = editText.getText().toString();
            Log.i(TAG, "Adding a sending row");
            element.add(new Messages(inputText, true));
            editText.setText("");
            myAdapter.notifyDataSetChanged();
        });

        //When receive button is pushed

        Button receiveButton = findViewById(R.id.receiveButton);
        receiveButton.setOnClickListener( click -> {
            String inputText = editText.getText().toString();
            Log.i(TAG, "Adding receiving row");
            element.add(new Messages(inputText, false));
            editText.setText("");
            myAdapter.notifyDataSetChanged();
        });

        myList.setOnItemLongClickListener( (p, b, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.MakeChoice))

                    //What is the message:
                    .setMessage(getString(R.string.delete))

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




