package com.example.arouter.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.arouter.R;
import com.example.arouter_annotation.Route;

@Route(path = "/test/test1")
public class Test1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
    }
}
