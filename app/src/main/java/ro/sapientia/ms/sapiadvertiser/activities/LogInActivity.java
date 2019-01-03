package ro.sapientia.ms.sapiadvertiser.activities;
// If you put your Activities files to another folder than the default one. You need to import the
// com.example.your project.R (this is your project R file NOT Android.R file) to ALL activities using R.

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.sapientia.ms.sapiadvertiser.R;


public class LogInActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    // Using ButterKnife it`s simplify the code
    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.login_progress)
    ProgressBar loginProgress;
    @BindView(R.id.verification_edittext)
    EditText verificationCode;
    @BindView(R.id.my_phone_input)
    IntlPhoneInput phoneInput;

    private enum BtnType {
        SIGNIN,
        VERIFYCODE
    }

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private BtnType mBtnType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        // Binding the id`s to the fields with butter knife
        ButterKnife.bind(LogInActivity.this);

        mBtnType = BtnType.SIGNIN;
        mAuth = FirebaseAuth.getInstance();

        // Works only on some phones, detects automatically the phone number and the phone language and sets the country as
        // your phone languages
        phoneInput.setDefault();

        initPhoneVerification();
    }

    private void initPhoneVerification() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request

                    Toast.makeText(LogInActivity.this, R.string.invalid_phone_number, Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Invalid credential: " + e.getLocalizedMessage());

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Log.d(TAG, "SMS Quota exceeded.");
                    Toast.makeText(LogInActivity.this, R.string.too_many_requests, Toast.LENGTH_LONG).show();

                }
                loginProgress.setVisibility(View.INVISIBLE);
                loginBtn.setEnabled(true);
                Log.d(TAG, "Failed to login.");
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                mBtnType = BtnType.VERIFYCODE;
                loginProgress.setVisibility(View.VISIBLE);
                loginBtn.setText(R.string.verify_code);
                loginBtn.setEnabled(true);
                verificationCode.setVisibility(View.VISIBLE);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // We will check on advertisementsListActivity if the user data is saved
            Intent advIntent = new Intent(LogInActivity.this, AdvertisementListActivity.class);
            startActivity(advIntent);
            // We use finish() to block the user to return  with back button
            finish();
        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loginProgress.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(LogInActivity.this, R.string.loading_advertisers, Toast.LENGTH_LONG).show();
                            loginProgress.setVisibility(View.INVISIBLE);
                            Intent advIntent = new Intent(LogInActivity.this, AdvertisementListActivity.class);
                            startActivity(advIntent);
                            finish();
                        } else {
                            // Sign in failed, display a message and update the UI

                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                Toast.makeText(LogInActivity.this, R.string.invalid_credential, Toast.LENGTH_LONG).show();
                                loginProgress.setVisibility(View.INVISIBLE);
                                resetVerificationCode();

                            }
                        }
                    }
                });
    }

    @OnClick(R.id.login_btn)
    public void login() {
        if (mBtnType == BtnType.SIGNIN) {
            String phoneNumber = phoneInput.getNumber();

            if (phoneNumber == null) {
                phoneInput.requestFocus();
                Toast.makeText(LogInActivity.this, R.string.empty_phone_number, Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (haveNetworkConnection()) {

                    if (phoneInput.isValid()) {
                        loginProgress.setVisibility(View.VISIBLE);
                        loginProgress.setSoundEffectsEnabled(true);
                        loginBtn.setEnabled(false);

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phoneNumber,        // Phone number to verify
                                60,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                this,               // Activity (for callback binding)
                                mCallbacks);        // OnVerificationStateChangedCallbacks
                    } else {
                        phoneInput.requestFocus();
                    }
                } else {

                    Toast.makeText(LogInActivity.this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        if (mBtnType == BtnType.VERIFYCODE) {
            String verCode = verificationCode.getText().toString();

            if (verCode.isEmpty()) {
                verificationCode.requestFocus();
                verificationCode.setSoundEffectsEnabled(true);
                loginBtn.setEnabled(true);


            } else {
                loginBtn.setEnabled(false);
                loginProgress.setVisibility(View.VISIBLE);
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verCode);
                signInWithPhoneAuthCredential(credential);
            }

        }

    }

    private void resetVerificationCode() {
        loginBtn.setEnabled(true);
        verificationCode.setText("");
        verificationCode.requestFocus();
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }




}
