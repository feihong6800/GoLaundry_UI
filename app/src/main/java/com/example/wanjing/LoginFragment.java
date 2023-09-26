package com.example.wanjing;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String userID = "";
    EditText userLoginEmail, userLoginPassword;
    TextView forgetPassword;
    Button btnLogin;
    FirebaseAuth mAuth;
    DatabaseReference rootRef;

    String userStatus = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();

        userLoginEmail = (EditText) view.findViewById(R.id.et_email);
        userLoginPassword = (EditText) view.findViewById(R.id.et_password);
        btnLogin = (Button) view.findViewById(R.id.btn_login);
        rootRef = FirebaseDatabase.getInstance().getReference("USERS");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email = userLoginEmail.getText().toString().trim();
                String password = userLoginPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    userLoginEmail.setError("Email require");
                    userLoginEmail.requestFocus();
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    userLoginEmail.setError("Please enter an valid email");
                    userLoginEmail.requestFocus();
                }

                if (password.isEmpty()) {
                    userLoginPassword.setError("Password require");
                    userLoginPassword.requestFocus();
                }

                if (password.length() < 6) {
                    userLoginPassword.setError("Password need to be more than 6 character");
                    userLoginPassword.requestFocus();

                } else {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                userID = mAuth.getCurrentUser().getUid();
                                rootRef = FirebaseDatabase.getInstance().getReference("USERS").child(userID);

                                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        userStatus = snapshot.child("status").getValue().toString();

                                        if(userStatus.equals("user")){
                                            Toast.makeText(getActivity(), "Welcome back!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            getActivity().finish();

                                        } else if (userStatus.equals("laundry")){
                                            Toast.makeText(getActivity(), "Welcome back!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getActivity(), RiderMainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            getActivity().finish();

                                        } else {
                                            Toast.makeText(getActivity(), "Welcome back!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getActivity(), RiderMainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            getActivity().finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                userLoginPassword.setError("Wrong password");
                                userLoginPassword.requestFocus();

                            } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                userLoginEmail.setError("Invalid user");
                                userLoginEmail.requestFocus();
                            }
                        }
                    });
                }
            }
        });

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            getActivity().finish();
            Intent intent = new Intent(getActivity(), RiderMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}