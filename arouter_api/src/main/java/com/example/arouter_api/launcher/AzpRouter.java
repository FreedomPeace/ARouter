package com.example.arouter_api.launcher;

import android.content.Context;

import java.util.HashMap;

public class AzpRouter {
    //todo 把这个类做成单列
    private  static AzpRouter router = new AzpRouter();
    private static Context context;
    private HashMap<String,String> activitys = new HashMap<>();

    public static AzpRouter getInstance(Context context) {
        AzpRouter.context = context;
        return router;
    }

    public void build(String path) {
//        Intent intent = new Intent(context, LoginActivity.class);
    }
}
