package com.example.arouter.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.arouter.R;
import com.example.arouter_annotation.BindPath;
import com.example.arouter_api.launcher.ARouter;

@BindPath("/main/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void jumpToLogin(View view) {
        ARouter.init(getApplication());
        ARouter.getInstance().build("/login/login");
    }
}
