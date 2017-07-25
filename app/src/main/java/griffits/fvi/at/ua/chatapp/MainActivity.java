package griffits.fvi.at.ua.chatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //UI
    private ListView mMessageListView;
    private EditText mMessageEditText;
    private ImageButton mSendButton;
    private MessageAdapter mMessageAdapter;

    //Firebase
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();


        // init FireBase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("message");

        // create Listener for Firebase

        if (mChildEventListener == null){
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                mMessageAdapter.add(message);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            mDatabaseReference.addChildEventListener(mChildEventListener);

        }

        List<Message> messages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, messages);
        mMessageListView.setAdapter(mMessageAdapter);
        }

    // init UI
    protected void init(){
        mMessageListView = (ListView)findViewById(R.id.messageListView);
        mMessageEditText = (EditText) findViewById(R.id.mesageEditText);
        mSendButton = (ImageButton)findViewById(R.id.sendImageButton);
    }

    public void sendMessage(View view){
        Message message = new Message(mMessageEditText.getText().toString());

        if (mMessageEditText.getText().length() > 0) {
            mDatabaseReference.push().setValue(message);
            mMessageEditText.setText("");
        } else {
            Toast.makeText(this, "Please enter text", Toast.LENGTH_LONG).show();
        }
    }



    // remove listener with FireBase

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChildEventListener != null){
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }
}
