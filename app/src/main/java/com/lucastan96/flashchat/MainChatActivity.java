package com.lucastan96.flashchat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainChatActivity extends AppCompatActivity {
    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;
    private DatabaseReference mDatabaseReference;
    private ChatListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        setupDisplayName();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mInputText = findViewById(R.id.messageInput);
        mSendButton = findViewById(R.id.sendButton);
        mChatListView = findViewById(R.id.chat_list_view);

        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sendMessage();
                return true;
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void setupDisplayName() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDisplayName = user.getDisplayName();

//        SharedPreferences pref = getSharedPreferences(RegisterActivity.CHAT_PREFS, MODE_PRIVATE);
//        mDisplayName = pref.getString(RegisterActivity.DISPLAY_NAME_KEY, null);
//        if (mDisplayName == null) {
//            mDisplayName = "Anonymous";
//        }
    }

    private void sendMessage() {
        String input = mInputText.getText().toString();
        if (!input.equals("")) {
            InstantMessage chat = new InstantMessage(input, mDisplayName);
            mDatabaseReference.child("messages").push().setValue(chat);
            mInputText.setText("");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter = new ChatListAdapter(this, mDatabaseReference, mDisplayName);
        mChatListView.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.cleanup();
    }
}