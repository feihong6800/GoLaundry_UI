package com.example.wanjing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class RepeatOrderActivity extends AppCompatActivity {

    AppCompatButton btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat_order);

        btn_next = (AppCompatButton) findViewById(R.id.btn_repeat_order_next);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RepeatOrderActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}