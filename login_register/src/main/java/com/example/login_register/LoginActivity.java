package com.example.login_register;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.arouter_annotation.BindPath;

@BindPath("/login/login")
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
