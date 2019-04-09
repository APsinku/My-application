package abhi.osmchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;





public class StartActivity extends AppCompatActivity {


    private DatabaseReference mDatabase;

    private LinearLayout mPhoneLayout;
    private LinearLayout mCodeLayout;
    private LinearLayout mNameLayout;

    private EditText mPhoneText;
    private EditText mCodeTextt;
    private EditText mNameText;

    private ProgressBar mPhoneBar;
    private ProgressBar mCodeBar;

    private Button mSendBtn;
    private int btnType = 0;

    private TextView mErrorText;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private  String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);

        mPhoneLayout = (LinearLayout) findViewById(R.id.phoneLayout);
        mCodeLayout = (LinearLayout) findViewById(R.id.codeLayout);
        mNameLayout = (LinearLayout) findViewById(R.id.NameLayout);


        mPhoneText = (EditText) findViewById(R.id.PhonEditText);
        mCodeTextt = (EditText) findViewById(R.id.codeEditText);
        mNameText = (EditText) findViewById(R.id.NameText);


        mPhoneBar = (ProgressBar) findViewById(R.id.phoneProgress);
        mCodeBar = (ProgressBar) findViewById(R.id.codeProgress);

        mErrorText = (TextView) findViewById(R.id.error);

        mSendBtn = (Button) findViewById(R.id.sendbtn);
        mAuth = FirebaseAuth.getInstance();



        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (btnType == 0) {


                    final String YourNameText = mNameText.getText().toString();


                    mPhoneBar.setVisibility(View.VISIBLE);
                    mPhoneText.setEnabled(false);
                    mSendBtn.setEnabled(false);

                    String phonenumber = mPhoneText.getText().toString();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phonenumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            StartActivity.this,               // Activity (for callback binding)
                            mCallbacks         // OnVerificationStateChangedCallbacks

                    );
                } else {

                    mSendBtn.setEnabled(false);
                    mCodeBar.setVisibility(View.VISIBLE);

                    String verificationCode = mCodeTextt.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);

                    final String YourNameText = mNameText.getText().toString();

                    signInWithPhoneAuthCredential(credential,YourNameText);

                }

            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {


                final String YourNameText = mNameText.getText().toString();

                signInWithPhoneAuthCredential(phoneAuthCredential,YourNameText);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                mErrorText.setText("There was some error in verification.");
                mErrorText.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {


                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                btnType = 1;

                mPhoneBar.setVisibility(View.INVISIBLE);
                mCodeLayout.setVisibility(View.VISIBLE);

                mSendBtn.setText("Verify Code");
                mSendBtn.setEnabled(true);
            }
        };


//    public void register_user(String YourNameText) {
//
//
//    }

}

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential, final String YourNameText) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            String mobilenumber = mPhoneText.getText().toString();

                            FirebaseUser user = task.getResult().getUser();

                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid();

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            String device_token = FirebaseInstanceId.getInstance().getToken();

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", YourNameText);
                            userMap.put("status", "Hey there! I am using AP Chat.");
                            userMap.put("image", "default");
                            userMap.put("mobile",mobilenumber);
                            userMap.put("thumb_image", "default");
                            userMap.put("device_token", device_token);

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){



                                        Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();

                                    }

                                }
                            });

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                           mErrorText.setText("There was some error in Login in.");
                           mErrorText.setVisibility(View.VISIBLE);

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }




    }


