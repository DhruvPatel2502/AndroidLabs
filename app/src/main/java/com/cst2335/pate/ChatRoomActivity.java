package com.cst2335.pate;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

//This makes it a page in the application
public class ChatRoomActivity extends AppCompatActivity {

    boolean isTablet = false;
    MessageFragment chatFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_layout);
        isTablet = findViewById(R.id.detailsRoom) != null;
        //  getSupportFragmentManager().beginTransaction().add(R.id.fragmentRoom, new MessageListFragment()).commit();
        chatFragment = new MessageFragment();
        FragmentManager fMgr = getSupportFragmentManager();
        FragmentTransaction tx = fMgr.beginTransaction();
        tx.add(R.id.fragmentRoom, chatFragment);
        tx.commit();

    }

    public void userClickedMessage(MessageFragment.ChatMessage chatMessage, int position) {
        MessageDetails mdFragment = new MessageDetails(chatMessage, position);
        if (isTablet) {
            getSupportFragmentManager().beginTransaction().replace(R.id.detailsRoom, mdFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentRoom, mdFragment).commit();
        }
    }

    public void notifyManagerDeleted(MessageFragment.ChatMessage chosenMessage, int chosenPosition) {
        chatFragment.notifyMessageDeleted(chosenMessage, chosenPosition);
    }

}


