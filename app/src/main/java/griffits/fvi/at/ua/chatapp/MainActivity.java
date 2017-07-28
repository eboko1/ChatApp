package griffits.fvi.at.ua.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.auth.AuthUI;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final int RC_SIGN_IN = 1;
    public static final String ANONYMOUS = "anonymous";
    //UI
    private ListView mMessageListView;
    private EditText mMessageEditText;
    private ImageButton mSendButton, mPhotoPickerButton;
    private String mUsername;
    private MessageAdapter mMessageAdapter;
    private Calendar calendar;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    // FirebaseUI for Authentication
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "onCreate");

        mUsername = ANONYMOUS;


        // initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        calendar = Calendar.getInstance();

        mDatabaseReference = mFirebaseDatabase.getReference().child("messages");
        init();

        // initialize message ListView and its adapter
        List<Message> messages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, messages);
        mMessageListView.setAdapter(mMessageAdapter);

        //Authenticating Users
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if(user != null){
                        //user sing in
                        // Toast.makeText(MainActivity.this, "Welcome!!!", Toast.LENGTH_LONG).show();
                        onSignedInInitialize(user.getDisplayName());
                        Log.i(TAG, "user name = "+ user.getDisplayName());
                    } else {
                        // user sing out
                        onSignedOutCleanup();

                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setIsSmartLockEnabled(false)
                                        .setAvailableProviders(
                                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                        // new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                                        new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                                                        new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build()))
                                        .build(),
                                RC_SIGN_IN);
                    }
                }
            };
        }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN)
            if(resultCode == RESULT_OK){
                Toast.makeText(MainActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "Please Sing In", Toast.LENGTH_SHORT).show();
                finish();
            }
    }
    // init UI
    protected void init(){
        mMessageListView = (ListView)findViewById(R.id.messageListView);
        mMessageEditText = (EditText) findViewById(R.id.mesageEditText);
        mSendButton = (ImageButton)findViewById(R.id.sendImageButton);
        mPhotoPickerButton = (ImageButton)findViewById(R.id.photoPickerImageButton);


    }

    // send button sends a message
    public void sendMessage(View view){
       Calendar calendar = Calendar.getInstance();
       String mTime =  ""+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);

        Message message = new Message(mMessageEditText.getText().toString(), mUsername, mTime, null);

        if (mMessageEditText.getText().length() > 0) {
            mDatabaseReference.push().setValue(message);
            Log.i(TAG, "it enter edit text "+mMessageEditText.getText());
            Log.i(TAG, "push massage-> "+message.getText()+ " user name-> "+message.getUserName() +" date-> " + message.getTime()+ " photo picker->" + message.getPhotoURL() );

            // сlear input box
            mMessageEditText.setText("");
        } else {
            Toast.makeText(this, "Please enter text", Toast.LENGTH_LONG).show();
        }
    }

    // image PickerButton shows an img picker to upload a image for message
    public void onClickPhotoPickerButton(View v){

    }
   @Override
    protected void onPause() {
        super.onPause();
       if (mAuthStateListener != null) {
           mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
       }
       detachChildEventListener();
       mMessageAdapter.clear();
       Log.i(TAG, "onPause()" );
   }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        Log.i(TAG, "onResume()" );
    }

    private void onSignedInInitialize(String username) {
        mUsername = username;
        attachChildEventListener();
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        mMessageAdapter.clear();
        detachChildEventListener();

    }


    public void attachChildEventListener() {
        if (mChildEventListener == null){
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Message message = dataSnapshot.getValue(Message.class);
                    mMessageAdapter.add(message);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

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
    }

    public void detachChildEventListener() {
        if (mChildEventListener != null){
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }



 /*   @Override
    protected void onDestroy() {
        super.onDestroy();
        // remove listener for FireBase
        if (mChildEventListener != null){
            Log.i(TAG, "onDestroy() mChildEventListener = " +mChildEventListener );
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
            Log.i(TAG, "onDestroy() mChildEventListener =" + mChildEventListener);
        }
    }*/
}
