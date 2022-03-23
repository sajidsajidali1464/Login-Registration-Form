package com.example.myapplicationsemesterapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.myapplicationsemesterapp.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDoB, editTextRegisterMobile,
            editTextRegisterPwd, editTextRegisterConfirmPwd;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private DatePickerDialog picker;
    private static final String TAG = "RegisterActivity";
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle("Register");
        Toast.makeText(RegisterActivity.this, "You can register now", Toast.LENGTH_LONG).show();

        progressBar = findViewById(R.id.progressBar);
        editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterDoB = findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirm_password);

        // RadioButton for Gender
        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        //Setting up DatePicker on EditText
        editTextRegisterDoB.setOnClickListener(v -> {
            final Calendar calender = Calendar.getInstance();
            int day = calender.get(Calendar.DAY_OF_MONTH);
            int month = calender.get(Calendar.MONTH);
            int year = calender.get(Calendar.YEAR);

            //Date Picker Dialog
            picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    editTextRegisterDoB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                }
            }, year, month, day);
            picker.show();
        });

        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(v -> {
            int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
            radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

            // Obtain the entered data
            String textFullName = editTextRegisterFullName.getText().toString();
            String textEmail = editTextRegisterEmail.getText().toString();
            String textDoB = editTextRegisterDoB.getText().toString();
            String textMobile = editTextRegisterMobile.getText().toString();
            String textPwd = editTextRegisterPwd.getText().toString();
            String textConfirmPwd = editTextRegisterConfirmPwd.getText().toString();
            String textGender;   //Can' obtain the value before verifying if any button was selected or not

            //Validate Mobile Number using Matcher and Pattern (Regular Expression)
            String mobileRegex = "[0-9][0-9]{9}";   //First no. can be {6,8,9} and rest 9 nos. can be any no.
            Matcher mobileMatcher;
            Pattern mobilePattern = Pattern.compile(mobileRegex);
            mobileMatcher = mobilePattern.matcher(textMobile);

            if (TextUtils.isEmpty(textFullName)) {
                Toast.makeText(RegisterActivity.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
                editTextRegisterFullName.setError("Full name is required");
                editTextRegisterFullName.requestFocus();
            } else if (TextUtils.isEmpty(textEmail)) {
                Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                editTextRegisterEmail.setError("Valid email is required");
                editTextRegisterEmail.requestFocus();

            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                Toast.makeText(RegisterActivity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                editTextRegisterEmail.setError("Valid email is required");
                editTextRegisterEmail.requestFocus();
            } else if (TextUtils.isEmpty(textDoB)) {
                Toast.makeText(RegisterActivity.this, "Please your date of birth", Toast.LENGTH_SHORT).show();
                editTextRegisterDoB.setError("Date of birth is required");
                editTextRegisterDoB.requestFocus();
            } else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                Toast.makeText(RegisterActivity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                radioButtonRegisterGenderSelected.setError("Gender is required");
                radioButtonRegisterGenderSelected.requestFocus();
            } else if (TextUtils.isEmpty(textMobile)) {
                Toast.makeText(RegisterActivity.this, "Please enter your mobile no.", Toast.LENGTH_SHORT).show();
                editTextRegisterMobile.setError("Mobile No. is required");
                editTextRegisterMobile.requestFocus();
            } else if (textMobile.length() != 11) {
                Toast.makeText(RegisterActivity.this, "Please re-enter your mobile no.", Toast.LENGTH_SHORT).show();
                editTextRegisterMobile.setError("Mobile No. should be 11 digits");
                editTextRegisterMobile.requestFocus();
            } else if (!mobileMatcher.find()) {
                Toast.makeText(RegisterActivity.this, "Please re-enter your mobile no.", Toast.LENGTH_SHORT).show();
                editTextRegisterMobile.setError("Mobile No. is not valid");
                editTextRegisterMobile.requestFocus();
            } else if (TextUtils.isEmpty(textPwd)) {
                Toast.makeText(RegisterActivity.this, "Please Enter your password", Toast.LENGTH_SHORT).show();
                editTextRegisterPwd.setError("Password is required");
                editTextRegisterPwd.requestFocus();
            } else if (textPwd.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Password should be at least 6 digits", Toast.LENGTH_SHORT).show();
                editTextRegisterPwd.setError("Password to weak");
                editTextRegisterPwd.requestFocus();
            } else if (TextUtils.isEmpty(textConfirmPwd)) {
                Toast.makeText(RegisterActivity.this, "Please Confirm your password", Toast.LENGTH_SHORT).show();
                editTextRegisterConfirmPwd.setError("Password confirmation is required");
                editTextRegisterConfirmPwd.requestFocus();
            } else if (!textPwd.equals(textConfirmPwd)) {
                Toast.makeText(RegisterActivity.this, "Please same same password", Toast.LENGTH_SHORT).show();
                editTextRegisterConfirmPwd.setError("Password confirmation is required");
                editTextRegisterConfirmPwd.requestFocus();
                //Clear the entered passwords
                editTextRegisterPwd.clearComposingText();
                editTextRegisterConfirmPwd.clearComposingText();
            } else {
                textGender = radioButtonRegisterGenderSelected.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                registerUser(textFullName, textEmail, textDoB, textGender, textMobile, textPwd);
            }
        });
    }

    // Register user using the credentials given
    private void registerUser(String textFullName, String textEmail, String textDoB, String textGender, String textMobile, String textPwd) {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        //Create User Profile
        auth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(RegisterActivity.this,
                task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "registerUser: success");
                        FirebaseUser firebaseUser = auth.getCurrentUser();

                        //Update Display Name of User
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(textFullName).build();
                        firebaseUser.updateProfile(profileChangeRequest);

                        //Enter User data into the Firebase Realtime database.
                        User writeUserDetails = new User(textDoB, textGender, textMobile);

                        //Extracting user reference from Database for "Registered Users"
                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("users");
                        referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(task1 -> {

                            if (task1.isSuccessful()) {
                                Log.d(TAG, "registerUser: success");
                                //Send Verification Email
//                                firebaseUser.sendEmailVerification();
                                Toast.makeText(RegisterActivity.this, "User registered successfully. Please verify your email", Toast.LENGTH_SHORT).show();

                                //Open User Profile after successful registration
                                Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);

                                //To prevent user from returing back to register Activity on pressing back button after registration
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();  //to close register activity

                            } else {
                                Log.d(TAG, "registerUser: fail" + task1
                                        .getException());

                                Toast.makeText(RegisterActivity.this, "User registered failed. Please try again", Toast.LENGTH_SHORT).show();
                            }

                            //Hide progressBar whether User creation is successfully or failed
                            progressBar.setVisibility(View.GONE);
                        });

                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            editTextRegisterPwd.setError("Your password is too weak. Kindly use a mix of alphabets");
                            editTextRegisterPwd.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            editTextRegisterPwd.setError("Your email is invalid or already in use. Kindly re-enter .");
                            editTextRegisterPwd.requestFocus();
                        } catch (FirebaseAuthUserCollisionException e) {
                            editTextRegisterPwd.setError("User already registered with this email. Use another email");
                            editTextRegisterPwd.requestFocus();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                        //Hide progressBar whether User creation is successfully or failed
                        progressBar.setVisibility(View.GONE);

                    }
                });
    }
}