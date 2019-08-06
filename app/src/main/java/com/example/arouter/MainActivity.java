package com.example.arouter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.arouter_annotation.BindPath;

@BindPath("main/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
