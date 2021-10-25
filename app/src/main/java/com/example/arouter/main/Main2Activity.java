package com.example.arouter.main;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arouter_annotation.Route;

@Route(path = "/main/main2")
public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView view = new TextView(this);
        view.setText("TEST2");
        view.setTextSize(60);
        setContentView(view);
    }
}
