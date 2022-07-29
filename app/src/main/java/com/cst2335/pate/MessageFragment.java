package com.cst2335.pate;



import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;

public class MessageFragment extends Fragment {
    ArrayList<ChatMessage> messages = new ArrayList<>();
    RecyclerView chatList;
    MyChatAdapter adt;
    Date date = new Date();
    SQLiteDatabase db;
    Button send;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View chatLayout = inflater.inflate(R.layout.chatlayout, container, false);
        send = chatLayout.findViewById(R.id.sendButton);
        MyOpenHelper opener  = new MyOpenHelper(getContext());
        db = opener.getWritableDatabase();

        EditText messageTyped = chatLayout.findViewById(R.id.messageEdit);


        send = chatLayout.findViewById(R.id.sendButton);
        Button receive = chatLayout.findViewById(R.id.receiveButton);
        chatList = chatLayout.findViewById(R.id.myrecycler);

        Cursor results = db.rawQuery("Select * from " + MyOpenHelper.TABLE_NAME + ";", null);

        int _idCol = results.getColumnIndex("_id");
        int messageCol = results.getColumnIndex(MyOpenHelper.col_message);
        int sendCol = results.getColumnIndex(MyOpenHelper.col_send_receive);
        int timeCol = results.getColumnIndex(MyOpenHelper.col_time_sent);
        while(results.moveToNext()) {
            long id = results.getInt(_idCol);
            String message = results.getString(messageCol);
            String time = results.getString(timeCol);
            int sendOrReceive = results.getInt(sendCol);
            messages.add(new ChatMessage(message, sendOrReceive, time, id));
        }
        adt = new MyChatAdapter();
        // LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        chatList.setAdapter(adt);
        chatList.setLayoutManager (new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false));

        send.setOnClickListener(click ->
                {
                    ChatMessage thisMessage = new ChatMessage( messageTyped.getText().toString(), 1, date);

                    ContentValues newRow = new ContentValues();
                    newRow.put(MyOpenHelper.col_message, thisMessage.getMessage());
                    newRow.put(MyOpenHelper.col_send_receive, thisMessage.getSendOrReceive());
                    newRow.put(MyOpenHelper.col_time_sent, thisMessage.getTimeSent());
                    long newId = db.insert(MyOpenHelper.TABLE_NAME, MyOpenHelper.col_message, newRow);
                    thisMessage.setId(newId);
                    messages.add( thisMessage );
                    messageTyped.setText(" ");
                    adt.notifyItemInserted( messages.size()-1);

                }
        );

        receive.setOnClickListener(click ->
                {
                    ChatMessage thisMessage = new ChatMessage(messageTyped.getText().toString(), 2, date);
                    ContentValues newRow = new ContentValues();
                    newRow.put(MyOpenHelper.col_message, thisMessage.getMessage());
                    newRow.put(MyOpenHelper.col_send_receive, thisMessage.getSendOrReceive());
                    newRow.put(MyOpenHelper.col_time_sent, thisMessage.getTimeSent());
                    long newId = db.insert(MyOpenHelper.TABLE_NAME, MyOpenHelper.col_message, newRow);
                    thisMessage.setId(newId);
                    messages.add( thisMessage );
                    messageTyped.setText(" ");
                    adt.notifyItemInserted( messages.size()-1);

                }
        );



        return  chatLayout;
    }

    public void notifyMessageDeleted(ChatMessage chosenMessage, int chosenPosition) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("Do you want to delete the message: " + chosenMessage.getMessage())
                .setTitle("Danger")
                .setNegativeButton( "Cancel", (dialog, c )-> {})
                .setPositiveButton("Delete",(dialog, cl )-> {
                    //position = getAbsoluteAdapterPosition();
                    ChatMessage removedMessage = messages.get(chosenPosition);
                    messages.remove(chosenPosition);
                    adt.notifyItemRemoved(chosenPosition);

                    db.delete(MyOpenHelper.TABLE_NAME, "_id=?" , new String [] { Long.toString(removedMessage.getId())});

                    Snackbar.make(send , " You deleted message # " + chosenPosition, Snackbar.LENGTH_LONG)
                            .setAction("Undo", clk ->{
                                messages.add(chosenPosition, removedMessage);
                                adt.notifyItemInserted(chosenPosition);
                                db.execSQL("Insert into " + MyOpenHelper.TABLE_NAME + " values('" + removedMessage.getId() +
                                        "','" + removedMessage.getMessage() +
                                        "','" + removedMessage.getSendOrReceive() +
                                        "','" + removedMessage.getTimeSent() + "');");

                            })
                            .show();
                })
                .create().show();
    }

    private class MyRowViews extends RecyclerView.ViewHolder{
        TextView messageText;
        TextView timeText;
        int position = -1;
        public MyRowViews( View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.message);
            timeText = itemView.findViewById(R.id.time);
            itemView.setOnClickListener(click-> {

                ChatRoomActivity parentActivity = (ChatRoomActivity) getContext();
                position = getAdapterPosition();
                parentActivity.userClickedMessage(messages.get(position), position);


            });

        }

        public void setPosition(int p) {
            position = p;
        }
    }

    private class MyChatAdapter extends RecyclerView.Adapter<MyRowViews>{

        @Override
        public MyRowViews onCreateViewHolder( ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            int layoutID;
            if(viewType == 1)
                layoutID = R.layout.send;
            else
                layoutID = R.layout.receive;
            View loadedRow = inflater.inflate(layoutID, parent, false);
            return new MyRowViews(loadedRow);
        }

        @Override
        public void onBindViewHolder(MyRowViews holder, int position) {
            holder.messageText.setText(messages.get(position).getMessage());
            holder.timeText.setText(messages.get(position).getTimeSent());
            holder.setPosition(position);
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        @Override
        public int getItemViewType(int position) {
            ChatMessage thisRow = messages.get(position);
            return thisRow.getSendOrReceive();
        }
    }

    class ChatMessage{
        String message;
        int sendOrReceive;
        Date timeSent;
        String currentDateAndTime;
        long id;

        public void setId(long l){ id = l;}
        public long getId(){ return id ;}

        public ChatMessage(String message, int sendOrReceive, Date timeSent) {

            /*
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a", Locale.getDefault());
            currentDateAndTime = sdf.format(timeSent);
            */


            this.message = message;
            this.sendOrReceive = sendOrReceive;
            //this.timeSent = timeSent;
        }
        public ChatMessage(String message, int sendOrReceive, String timeSent, long id){
            this.message = message;
            this.sendOrReceive = sendOrReceive;
            //this.currentDateAndTime = timeSent;
            setId(id);
        }

        public String getTimeSent() {

            return currentDateAndTime;
        }

        public String getMessage() {

            return message;
        }

        public int getSendOrReceive() {

            return sendOrReceive;
        }

        public long getID() {
            return id;
        }
    }
}

