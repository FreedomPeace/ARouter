package com.example.arouter.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.arouter_annotation.Route;

@Route(path="/test/test2")
public class Test2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView view = new TextView(this);
        view.setText("TEST2");
        view.setTextSize(60);
        setContentView(view);
    }
}
