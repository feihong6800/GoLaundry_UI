package com.example.wanjing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserSignUpActivity extends AppCompatActivity {

    private EditText et_userName, et_NRIC, et_userNumber, et_userEmail, et_userPassword, etu_userConfirmPassword;
    private Button btn_signUp;
    private Spinner spinner_gender;
    private List<String> categories;
    private String category = "";
    private String item = "";
    private FirebaseAuth mAuth;
    private String userID = "";
    DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);

        et_userName = findViewById(R.id.et_name);
        et_NRIC = findViewById(R.id.et_birth_date);
        et_userNumber = findViewById(R.id.et_phonenumber);
        et_userEmail = findViewById(R.id.et_email);
        et_userPassword = findViewById(R.id.et_password);
        etu_userConfirmPassword = findViewById(R.id.et_confirm_password);
        btn_signUp = findViewById(R.id.btn_register);
        spinner_gender = findViewById(R.id.gender_type);
        mAuth = FirebaseAuth.getInstance();
        categories = new ArrayList<String>();
        rootRef = FirebaseDatabase.getInstance("https://golaundrytest-default-rtdb.firebaseio.com/").getReference();
//
        spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                item = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        List<String> categories = new ArrayList<String>();
        categories.add("Male");
        categories.add("Female");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner_gender.setAdapter(dataAdapter);

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = et_userName.getText().toString();
                String userNRIC = et_NRIC.getText().toString();
                String userPhoneNumber = et_userNumber.getText().toString();
                String userEmail = et_userEmail.getText().toString();
                String userPassword = et_userPassword.getText().toString();
                String userConfirmPassword = etu_userConfirmPassword.getText().toString();

                if (userName.isEmpty()) {
                    et_userName.setError("User Name not allow to empty");
                    et_userName.requestFocus();

                } else if (userNRIC.isEmpty()) {
                    et_NRIC.setError("NRIC not allow to empty");
                    et_NRIC.requestFocus();

                } else if (userPhoneNumber.isEmpty()) {
                    et_userNumber.setError("NRIC not allow to empty");
                    et_userNumber.requestFocus();

                } else if (userEmail.isEmpty()) {
                    et_userEmail.setError("NRIC not allow to empty");
                    et_userEmail.requestFocus();

                } else if (userPassword.isEmpty()) {
                    et_userPassword.setError("NRIC not allow to empty");
                    et_userPassword.requestFocus();

                } else if (userConfirmPassword.isEmpty()) {
                    etu_userConfirmPassword.setError("NRIC not allow to empty");
                    etu_userConfirmPassword.requestFocus();

                } else if (!userPassword.equals(userConfirmPassword)) {
                    etu_userConfirmPassword.setError("Password Not Match");
                    etu_userConfirmPassword.requestFocus();

                } else {
                    mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                userID = mAuth.getCurrentUser().getUid();
                                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!(snapshot.child("USERS").child(userID).exists())) {
                                            HashMap<String, Object> userdataMap = new HashMap<>();
                                            userdataMap.put("name", userName);
                                            userdataMap.put("email", userEmail);
                                            userdataMap.put("phone number", "+601" + userPhoneNumber);
                                            userdataMap.put("gender", item);
                                            userdataMap.put("NRIC", userNRIC);
                                            rootRef.child("USERS").child(userID).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(UserSignUpActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(UserSignUpActivity.this, HomeActivity.class);
                                                        startActivity(intent);

                                                    } else {
                                                        Toast.makeText(UserSignUpActivity.this, "Network Error. Please Try Again", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        } else {
                                            Toast.makeText(UserSignUpActivity.this, "This login email already exists. Please try a different email address, or log in to your account.", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(UserSignUpActivity.this, "Please try again", Toast.LENGTH_SHORT).show();

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                Toast.makeText(UserSignUpActivity.this, "This login email already exists. Please try a different email address.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });
    }
}